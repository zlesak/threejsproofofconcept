package cz.uhk.zlesak.threejslearningapp.application.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.uhk.zlesak.threejslearningapp.application.clients.interfaces.IApiClient;
import cz.uhk.zlesak.threejslearningapp.application.clients.interfaces.IFileApiClient;
import cz.uhk.zlesak.threejslearningapp.application.files.InputStreamMultipartFile;
import cz.uhk.zlesak.threejslearningapp.application.exceptions.ApiCallException;
import cz.uhk.zlesak.threejslearningapp.application.models.entities.Entity;
import cz.uhk.zlesak.threejslearningapp.application.models.entities.IEntity;
import cz.uhk.zlesak.threejslearningapp.application.models.entities.ModelEntity;
import cz.uhk.zlesak.threejslearningapp.application.models.entities.quickEntities.QuickFile;
import cz.uhk.zlesak.threejslearningapp.application.models.entities.quickEntities.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.application.models.records.PageResult;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * ModelApiClient provides connection to the backend service for managing models.
 * It implements the IFileApiClient interface and provides methods for creating, retrieving, uploading, downloading, and deleting model entities.
 * It uses RestTemplate for making HTTP requests to the backend service.
 * The base URL for the API is determined by the IApiClient interface.
 */
@Component
public class ModelApiClient implements IFileApiClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    /**
     * Constructor for ModelApiClient.
     * Initializes the RestTemplate and ObjectMapper, and sets the base URL for API requests.
     *
     * @param restTemplate the RestTemplate used for making HTTP requests
     * @param objectMapper the ObjectMapper used for JSON serialization/deserialization
     */
    @Autowired
    public ModelApiClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        this.baseUrl = IApiClient.getBaseUrl() + "model/";
    }

    /**
     * API call function to retrieve a model entity by its ID.
     * This method downloads the model file and returns a ModelEntity containing the file and its metadata.
     * It uses the RestTemplate to make a GET request to the backend service.
     *
     * @param fileEntityId The ID of the model entity to retrieve.
     * @return ModelEntity containing the file and its metadata.
     * @throws Exception if the model is not found or there is an error during the download process.
     */
    @Override
    public ModelEntity getFileEntityById(String fileEntityId) throws Exception {
        String url = baseUrl + "download/" + fileEntityId;
        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    byte[].class
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String contentDisposition = response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
                String filename = null;
                if (contentDisposition != null && contentDisposition.contains("filename=")) {
                    filename = contentDisposition.substring(contentDisposition.indexOf("filename=") + 9).replace("\"", "");
                }
                InputStreamMultipartFile file = new InputStreamMultipartFile(new ByteArrayInputStream(response.getBody()), filename, filename);
                return ModelEntity.builder()
                        .Id(fileEntityId)
                        .Name(filename)
                        .MainTextureEntity(null)
                        .TextureEntities(List.of())
                        .File(file)
                        .build();
            } else {
                throw new Exception("Model nenalezen nebo chyba při stahování.");
            }
        } catch (HttpStatusCodeException ex) {
            throw new ApiCallException("Chyba při stahování modelu", null, null, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
        }
    }

    /**
     * This method is not implemented as of this moment.
     *
     * @param authorId ID of the author whose file entities are to be retrieved.
     * @return List of Entity objects representing the file entities authored by the specified author.
     * @throws NotImplementedException as of now
     */
    @Override
    public List<Entity> getFileEntitiesByAuthor(String authorId) throws NotImplementedException {
        throw new NotImplementedException("Tato metoda není implementována pro modely.");
    }

    /**
     * API call function to retrieve a paginated list of model entities.
     * This method fetches a list of models from the backend service based on the specified page and limit.
     * It uses the RestTemplate to make a GET request and returns a list of QuickFile objects.
     *
     * @param page  the page number to retrieve
     * @param limit the maximum number of items per page
     * @return a list of QuickFile objects representing the model entities
     * @throws Exception if there is an error during the retrieval process or if the response is not successful.
     */
    @Override
    public PageResult<QuickFile> getFileEntities(int page, int limit) throws Exception {
        String url = baseUrl + "list-by?limit=" + limit + "&page=" + page;
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    String.class
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return objectMapper.readValue(response.getBody(), objectMapper.getTypeFactory().constructParametricType(PageResult.class, QuickModelEntity.class));
            } else {
                throw new ApiCallException("Chyba při získávání seznamu modelů", null, null, response.getStatusCode(), response.getBody(), null);
            }
        } catch (HttpStatusCodeException ex) {
            throw new ApiCallException("Chyba při získávání seznamu modelů", null, null, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
        }
    }

    /**
     * API call function to upload a file entity.
     * This method uploads a file to the backend service and associates it with a file entity.
     * It uses the RestTemplate to make a POST request with multipart/form-data content type.
     * The file is sent as a resource, and the metadata of the file entity is sent as a JSON part.
     *
     * @param inputStreamMultipartFile the file to be uploaded, wrapped in an InputStreamMultipartFile
     * @param fileEntity               the file entity containing metadata about the file
     * @return the ID of the uploaded file entity as a String, proving the correct upload
     * @throws Exception if there is an error during the upload process or if the response is not successful.
     */
    @Override
    public QuickModelEntity uploadFileEntity(InputStreamMultipartFile inputStreamMultipartFile, IEntity fileEntity) throws Exception {
        String url = baseUrl + "upload";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("model", inputStreamMultipartFile.getResource());

        String metadataJson = objectMapper.writeValueAsString(fileEntity);
        HttpHeaders metadataHeaders = new HttpHeaders();
        metadataHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> metadataPart = new HttpEntity<>(metadataJson, metadataHeaders);
        body.add("metadata", metadataPart);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    String.class
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return objectMapper.readValue(response.getBody(), QuickModelEntity.class);
            } else {
                throw new ApiCallException("Chyba při nahrávání modelu", null, request.toString(), response.getStatusCode(), response.getBody(), null);
            }

        } catch (HttpStatusCodeException ex) {
            throw new ApiCallException("Chyba při nahrávání modelu", null, request.toString(), ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
        }
    }

    /**
     * This method is not implemented as of this moment.
     *
     * @param modelId ID of the model to be deleted.
     * @throws NotImplementedException as of now
     */
    @Override
    public void deleteFileEntity(String modelId) throws NotImplementedException {
        throw new NotImplementedException("Metoda deleteFileEntity není implementována pro ModelApiClient.");
    }

    /**
     * Generates the backend endpoint URL for downloading a model file by its ID.
     * @param modelId the ID of the model
     * @return the complete URL to download the model file
     */
    public String getModelFileBeEndpointUrl(String modelId) {
        return IApiClient.getLocalBaseBeUrl() + "model/download/" + modelId;
    }
}
