package cz.uhk.zlesak.threejslearningapp.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.uhk.zlesak.threejslearningapp.clients.interfaces.IApiClient;
import cz.uhk.zlesak.threejslearningapp.clients.interfaces.IFileApiClient;
import cz.uhk.zlesak.threejslearningapp.data.files.InputStreamMultipartFile;
import cz.uhk.zlesak.threejslearningapp.exceptions.ApiCallException;
import cz.uhk.zlesak.threejslearningapp.models.entities.Entity;
import cz.uhk.zlesak.threejslearningapp.models.entities.IEntity;
import cz.uhk.zlesak.threejslearningapp.models.entities.TextureEntity;
import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickFile;
import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickTextureEntity;
import cz.uhk.zlesak.threejslearningapp.models.records.PageResult;
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
 * TextureApiClient provides connection to the backend service for managing textures.
 * It implements the IFileApiClient interface and provides methods for creating, retrieving, uploading, downloading, and deleting texture entities.
 * It uses RestTemplate for making HTTP requests to the backend service.
 * The base URL for the API is determined by the IApiClient interface.
 */
@Component
public class TextureApiClient implements IFileApiClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    /**
     * Constructor for TextureApiClient.
     * Initializes the RestTemplate and ObjectMapper, and sets the base URL for API requests.
     *
     * @param restTemplate the RestTemplate used for making HTTP requests
     * @param objectMapper the ObjectMapper used for JSON serialization/deserialization
     */
    @Autowired
    public TextureApiClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        this.baseUrl = IApiClient.getBaseUrl() + "texture/";
    }

    /**
     * This method is not implemented as the textures upload is handled by uploadFileEntity.
     *
     * @param entity The entity to create.
     * @throws NotImplementedException Always thrown as this method is not implemented for textures.
     */
    @Override
    public void createFileEntity(Entity entity) throws NotImplementedException {
        throw new NotImplementedException("Tato metoda není implementována pro textury.");
    }

    /**
     * API call function to retrieve a texture entity by its ID.
     * This method retrieves a texture file from the backend service using its ID.
     *
     * @param fileEntityId The ID of the texture entity to retrieve.
     * @return Returns the TextureEntity if found, otherwise throws an exception.
     * @throws Exception Throws exception if anything goes wrong when retrieving the texture via this API call.
     */
    @Override
    public TextureEntity getFileEntityById(String fileEntityId) throws Exception {
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
                InputStreamMultipartFile file = InputStreamMultipartFile.builder()
                        .fileName(filename)
                        .displayName(filename)
                        .inputStream(new ByteArrayInputStream(response.getBody()))
                        .build();
                return TextureEntity.builder()
                        .Name(filename)
                        .File(file)
                        .build();
            } else {
                throw new Exception("Textura nalezena nebo chyba při stahování.");
            }
        } catch (HttpStatusCodeException ex) {
            throw new ApiCallException("Nepodařilo se stáhnout texturu", null, null, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
        }
    }

    /**
     * This method is not implemented as the textures are not retrieved by author but the model that they belong to.
     *
     * @param authorId The ID of the author.
     * @throws NotImplementedException Always thrown as this method is not implemented for textures.
     */
    @Override
    public List<Entity> getFileEntitiesByAuthor(String authorId) throws NotImplementedException {
        throw new NotImplementedException("Tato metoda není implementována pro textury.");
    }

    /**
     * This method is not implemented as the textures are not needed to be retrieved in a paginated way, or other.
     *
     * @param page  page number.
     * @param limit number of items per page.
     * @return an empty list as this method is not implemented for textures.
     */
    @Override
    public PageResult<QuickFile> getFileEntities(int page, int limit) {
        throw new NotImplementedException();
    }

    /**
     * API call function to upload a texture file along with its metadata.
     * This method uploads a texture file and its associated metadata to the backend.
     *
     * @param inputStreamMultipartFile The InputStreamMultipartFile containing the texture file.
     * @param textureEntity            The IEntity containing metadata for the texture.
     * @return Returns ID of uploaded texture proving its successful upload.
     * @throws Exception Throws exception if anything goes wrong when uploading the texture via this API call.
     */
    @Override
    public QuickTextureEntity uploadFileEntity(InputStreamMultipartFile inputStreamMultipartFile, IEntity textureEntity) throws Exception {
        String url = baseUrl + "upload";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);


        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("texture", inputStreamMultipartFile.getResource());

        String metadataJson = objectMapper.writeValueAsString(textureEntity);
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
            String responseBody = response.getBody();
            return objectMapper.readValue(responseBody, QuickTextureEntity.class);
        } catch (HttpStatusCodeException ex) {
            throw new ApiCallException("Chyba při nahrávání textury", null, request.toString(), ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
        }
    }

    /**
     * API call function to download a texture file by its ID.
     * This method retrieves a texture file from the backend service using its ID.
     *
     * @param fileEntityId The ID of the texture entity to download.
     * @return Returns the TextureEntity containing the downloaded file and its metadata.
     * @throws Exception Throws exception if anything goes wrong when downloading the texture via this API call.
     */
    @Override
    public Entity downloadFileEntityById(String fileEntityId) throws Exception {
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
                InputStreamMultipartFile file = InputStreamMultipartFile.builder()
                        .fileName(filename)
                        .displayName(filename) //todo change after i wil get the name back from the BE
                        .inputStream(new ByteArrayInputStream(response.getBody()))
                        .build();
                return TextureEntity.builder().Name(filename).File(file).build();
            } else {
                throw new Exception("Soubor nebyl nalezen nebo došlo k chybě při stahování.");
            }
        } catch (HttpStatusCodeException ex) {
            throw new ApiCallException("Chyba při stahování souboru", null, null, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
        }
    }

    /**
     * This method is not implemented at this moment as the textures should be deleted on BE side after their aro no longer associated with any model.
     *
     * @param modelId The ID of the model.
     * @throws NotImplementedException Always thrown as this method is not implemented for textures.
     */
    @Override
    public void deleteFileEntity(String modelId) throws NotImplementedException {
        throw new NotImplementedException("Tato metoda není implementována pro textury.");
    }
}
