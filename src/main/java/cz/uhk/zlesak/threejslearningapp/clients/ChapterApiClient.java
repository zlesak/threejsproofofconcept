package cz.uhk.zlesak.threejslearningapp.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.uhk.zlesak.threejslearningapp.clients.interfaces.IApiClient;
import cz.uhk.zlesak.threejslearningapp.clients.interfaces.IChapterApiClient;
import cz.uhk.zlesak.threejslearningapp.exceptions.ApiCallException;
import cz.uhk.zlesak.threejslearningapp.models.entities.ChapterEntity;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * ChapterApiClient provides connection to the backend service for managing chapters.
 * It implements the IChapterApiClient interface and provides methods for creating, updating, deleting, and retrieving chapters.
 * It uses RestTemplate for making HTTP requests to the backend service.
 * The base URL for the API is determined by the IApiClient interface
 */
@Component
public class ChapterApiClient implements IChapterApiClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    /**
     * Constructor for ChapterApiClient.
     * Initializes the RestTemplate and ObjectMapper, and sets the base URL for API requests.
     *
     * @param restTemplate the RestTemplate used for making HTTP requests
     * @param objectMapper the ObjectMapper used for JSON serialization/deserialization
     */
    @Autowired
    public ChapterApiClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.baseUrl = IApiClient.getBaseUrl() + "chapter/";
    }

    /**
     * API call function to endpoint create to create chapter coming from frontend view
     *
     * @param chapter Chapter entity to save via API
     * @return Returns saved chapter, as the behavior is to redirect user to the created chapter
     * @throws Exception Throws exception if anything goes bad when saving the chapter via this API call
     */
    @Override
    public ChapterEntity createChapter(ChapterEntity chapter) throws Exception {
        String url = baseUrl + "create";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ChapterEntity> request = new HttpEntity<>(chapter, headers);

        try {
            ResponseEntity<ChapterEntity> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    ChapterEntity.class);
            return objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), ChapterEntity.class);
        } catch (HttpStatusCodeException ex) {
            throw new ApiCallException("Chyba při nahrávání kapitoly", null, request.toString(), ex.getStatusCode(), ex.getResponseBodyAsString(), ex);

        } catch (Exception e) {
            throw new Exception("Neočekávaná chyba při volání API pro nahrání kapitoly: " + e.getMessage(), e);
        }
    }

    /**
     * API call function to update chapter coming from frontend view
     * This method is not implemented yet and will throw NotImplementedException.
     *
     * @param chapterId     ID of the chapter to update
     * @param chapterEntity ChapterEntity containing updated chapter data
     * @throws NotImplementedException Throws NotImplementedException as of this moment
     */
    @Override
    public void updateChapter(String chapterId, ChapterEntity chapterEntity) throws NotImplementedException {
        throw new NotImplementedException("Update chapter method is not implemented yet.");
    }

    /**
     * API call function to delete chapter by its ID
     * This method is not implemented yet and will throw NotImplementedException.
     *
     * @param chapterId ID of the chapter to delete
     * @throws NotImplementedException Throws NotImplementedException as of this moment
     */
    @Override
    public void deleteChapter(String chapterId) throws NotImplementedException {
        throw new NotImplementedException("Delete chapter method is not implemented yet.");
    }

    /**
     * API call function to get chapter by its ID
     * This method retrieves a chapter entity by its ID from the backend service.
     * It uses the RestTemplate to make a GET request to the backend service.
     * If the request is successful, it returns the chapter entity.
     * If there is an error during the request, it throws an ApiCallException with details about the error.
     *
     * @param chapterId ID of the chapter to retrieve
     * @return Returns the ChapterEntity corresponding to the provided chapterId
     * @throws Exception Throws an exception if there is an error during the API call
     */
    @Override
    public ChapterEntity getChapterById(String chapterId) throws Exception {
        String url = baseUrl + chapterId;
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ChapterEntity> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    ChapterEntity.class
            );
            return objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), ChapterEntity.class);
        } catch (HttpStatusCodeException ex) {
            throw new ApiCallException("Chyba při získávání kapitoly dle jejího ID", chapterId, requestEntity.toString(), ex.getStatusCode(), ex.getResponseBodyAsString(), ex);

        } catch (Exception e) {
            throw new Exception("Neočekávaná chyba při volání API pro získání kapitoly: " + e.getMessage(), e);
        }
    }

    /**
     * API call function to get chapters by author ID
     * This method is not implemented yet and will throw NotImplementedException.
     *
     * @param authorId ID of the author whose chapters are to be retrieved
     * @return List of chapter IDs authored by the specified author
     * @throws NotImplementedException Throws NotImplementedException as of this moment
     */
    @Override
    public List<String> getChaptersByAuthor(String authorId) throws NotImplementedException {
        throw new NotImplementedException("Get chapters by author method is not implemented yet.");
    }

    /**
     * API call function to get all chapters
     * This method is not implemented yet and will throw NotImplementedException.
     *
     * @return List of all chapter IDs
     * @throws NotImplementedException Throws NotImplementedException as of this moment
     */
    @Override
    public List<String> getAllChapters() throws NotImplementedException {
        throw new NotImplementedException("Get all chapters method is not implemented yet.");
    }
}