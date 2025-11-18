package cz.uhk.zlesak.threejslearningapp.api.clients;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.uhk.zlesak.threejslearningapp.api.contracts.IApiClient;
import cz.uhk.zlesak.threejslearningapp.common.InputStreamMultipartFile;
import cz.uhk.zlesak.threejslearningapp.exceptions.ApiCallException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract base class for API clients.
 * Provides common functionality for HTTP requests, headers, and response handling.
 *
 * @param <T> Entity type that this API client works with
 */
public abstract class AbstractApiClient<T, S, F> implements IApiClient<T, S, F> {

    protected final RestTemplate restTemplate;
    protected final ObjectMapper objectMapper;
    protected final String baseUrl;

    public AbstractApiClient(RestTemplate restTemplate, ObjectMapper objectMapper, String endpoint) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.baseUrl = IApiClient.getBaseUrl() + endpoint;
    }

    /**
     * Sends a POST request and returns the response body as the specified type.
     *
     * @param url          URL to send request to
     * @param body         Request body
     * @param responseType Response type class
     * @param errorMessage Error message prefix for exceptions
     * @param entityId     Optional entity ID for error reporting
     * @return Response body
     * @throws Exception if request fails
     */
    protected <R> R sendPostRequest(String url, Object body, Class<R> responseType, String errorMessage, String entityId, HttpHeaders headers) throws Exception {
        headers = headers == null ? createJsonHeaders() : headers;
        HttpEntity<?> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<R> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    responseType
            );
            return objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), responseType);
        } catch (HttpStatusCodeException ex) {
            throw new ApiCallException(errorMessage, entityId, request.toString(), ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
        } catch (Exception e) {
            throw new Exception("Neočekávaná chyba při volání API: " + errorMessage + " - " + e.getMessage(), e);
        }
    }

    /**
     * Sends a GET request and returns the response body as the specified type.
     *
     * @param url          URL to send request to
     * @param responseType Response type class
     * @param errorMessage Error message prefix for exceptions
     * @param entityId     Optional entity ID for error reporting
     * @return Response body
     * @throws Exception if request fails
     */
    protected <R> R sendGetRequest(String url, Class<R> responseType, String errorMessage, String entityId) throws Exception {
        HttpHeaders headers = createJsonHeaders();
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<R> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    responseType
            );
            return objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), responseType);
        } catch (HttpStatusCodeException ex) {
            throw new ApiCallException(errorMessage, entityId, requestEntity.toString(), ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
        } catch (Exception e) {
            throw new Exception("Neočekávaná chyba při volání API: " + errorMessage + " - " + e.getMessage(), e);
        }
    }

    /**
     * Sends a GET request and returns raw String response.
     * Useful for generic JSON that needs custom parsing.
     *
     * @param url          URL to send request to
     * @param errorMessage Error message prefix for exceptions
     * @param entityId     Optional entity ID for error reporting
     * @return Response body as String
     * @throws Exception if request fails
     */
    protected <R> ResponseEntity<R> sendGetRequestRaw(String url, Class<R> responseType, String errorMessage, String entityId, boolean includeHeaders) throws Exception {
        HttpHeaders headers = includeHeaders ? createAcceptJsonHeaders() : null;
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            return restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    responseType
            );
        } catch (HttpStatusCodeException ex) {
            throw new ApiCallException(errorMessage, entityId, requestEntity.toString(), ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
        } catch (Exception e) {
            throw new Exception("Neočekávaná chyba při volání API: " + errorMessage + " - " + e.getMessage(), e);
        }
    }

    /**
     * Sends a PUT request.
     *
     * @param url          URL to send request to
     * @param body         Request body
     * @param responseType Response type class
     * @param errorMessage Error message prefix for exceptions
     * @param entityId     Optional entity ID for error reporting
     * @return Response body
     * @throws Exception if request fails
     */
    protected <R> R sendPutRequest(String url, Object body, Class<R> responseType, String errorMessage, String entityId) throws Exception {
        HttpHeaders headers = createJsonHeaders();
        HttpEntity<?> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<R> response = restTemplate.exchange(url, HttpMethod.PUT, request, responseType);
            return objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), responseType);
        } catch (HttpStatusCodeException ex) {
            throw new ApiCallException(errorMessage, entityId, request.toString(), ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
        } catch (Exception e) {
            throw new Exception("Neočekávaná chyba při volání API: " + errorMessage + " - " + e.getMessage(), e);
        }
    }

    /**
     * Sends a DELETE request.
     *
     * @param url          URL to send request to
     * @param errorMessage Error message prefix for exceptions
     * @param entityId     Optional entity ID for error reporting
     * @throws Exception if request fails
     */
    protected void sendDeleteRequest(String url, String errorMessage, String entityId) throws Exception {
        try {
            restTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);
        } catch (HttpStatusCodeException ex) {
            throw new ApiCallException(errorMessage, entityId, null, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
        } catch (Exception e) {
            throw new Exception("Neočekávaná chyba při volání API: " + errorMessage + " - " + e.getMessage(), e);
        }
    }

    /**
     * Parses a raw JSON response to the specified JavaType.
     *
     * @param response     ResponseEntity with raw JSON
     * @param javaType     Jackson JavaType for target type
     * @param errorMessage Error message for exception
     * @param entityId     Optional entity ID for error reporting
     * @return Parsed object of type R
     * @throws Exception if parsing or status fails
     */
    protected <R> R parseResponse(ResponseEntity<String> response, JavaType javaType, String errorMessage, String entityId) throws Exception {
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return objectMapper.readValue(response.getBody(), javaType);
        } else {
            throw new ApiCallException(errorMessage, entityId, null, response.getStatusCode(), response.getBody(), null);
        }
    }

    /**
     * Parses a file response from the API.
     *
     * @param response     ResponseEntity with file bytes
     * @param errorMessage Error message for exception
     * @param entityId     Optional entity ID for error reporting
     * @return InputStreamMultipartFile representing the downloaded file
     * @throws Exception if parsing or status fails
     */
    protected InputStreamMultipartFile parseFileResponse(ResponseEntity<byte[]> response, String errorMessage, String entityId) throws Exception {
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            String contentDisposition = response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
            String filename = null;
            if (contentDisposition != null && contentDisposition.contains("filename=")) {
                filename = contentDisposition.substring(contentDisposition.indexOf("filename=") + 9).replace("\"", "");
            }
            return new InputStreamMultipartFile(new ByteArrayInputStream(response.getBody()), filename, filename);

        } else {
            throw new ApiCallException(errorMessage, entityId, null, response.getStatusCode(), Arrays.toString(response.getBody()), null);
        }
    }

    /**
     * Creates HTTP headers with JSON content type.
     *
     * @return HttpHeaders with JSON content type
     */
    protected HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    /**
     * Creates HTTP headers with JSON accept type.
     *
     * @return HttpHeaders with JSON accept type
     */
    protected HttpHeaders createAcceptJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    /**
     * Converts a filter object to URL query parameters
     * Ignores null values and uses field names as parameter keys
     * Supports class inheritance by processing superclass fields as well
     * Needs to be appended after pageRequestToQueryParams
     *
     * @param filter filter object (např. QuizFilter)
     * @return query string (bez počátečního ?)
     * @see #pageRequestToQueryParams(PageRequest)
     */
    protected String filterToQueryParams(F filter) {
        if (filter == null) return "";
        StringBuilder sb = new StringBuilder();
        Class<?> clazz = filter.getClass();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    Object value = field.get(filter);
                    if (value != null) {
                        sb.append("&")
                                .append(URLEncoder.encode(field.getName(), StandardCharsets.UTF_8))
                                .append("=")
                                .append(URLEncoder.encode(value.toString(), StandardCharsets.UTF_8));
                    }
                } catch (IllegalAccessException ignored) {
                }
            }
            clazz = clazz.getSuperclass();
        }
        return !sb.isEmpty() ? sb.substring(1) : "";
    }

    /**
     * Converts a PageRequest to URL query parameters.
     *
     * @param pageRequest PageRequest object
     * @return query string
     */
    protected String pageRequestToQueryParams(PageRequest pageRequest, String customBaseUrl) {
        customBaseUrl = customBaseUrl == null ? "list" : customBaseUrl;
        String orderBy = pageRequest.getSort().isSorted()
                ? pageRequest.getSort().iterator().next().getProperty()
                : "id";
        String sortDirection = pageRequest.getSort().isSorted()
                ? pageRequest.getSort().iterator().next().getDirection().name()
                : "ASC";
        return baseUrl + customBaseUrl +
                "?limit=" + pageRequest.getPageSize() +
                "&page=" + pageRequest.getPageNumber() +
                "&orderBy=" + orderBy +
                "&sortDirection=" + sortDirection;
    }
}
