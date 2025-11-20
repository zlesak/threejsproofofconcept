package cz.uhk.zlesak.threejslearningapp.api.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.uhk.zlesak.threejslearningapp.api.contracts.IApiClient;
import cz.uhk.zlesak.threejslearningapp.api.contracts.IFileApiClient;
import cz.uhk.zlesak.threejslearningapp.common.InputStreamMultipartFile;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Abstract file api client provides common functionality for file-related API clients.
 * @param <E> entity type
 * @param <Q> quick entity type
 * @param <F> filter type
 * @param <U> upload entity type
 */
abstract class AbstractFileApiClient<E, Q, F, U> extends AbstractApiClient<E, Q, F> implements IFileApiClient<U, Q> {

    private final String type;

    /**
     * Constructor for AbstractFileApiClient.
     * @param restTemplate rest template
     * @param objectMapper object mapper
     * @param endpoint API endpoint
     */
    public AbstractFileApiClient(RestTemplate restTemplate, ObjectMapper objectMapper, String endpoint) {
        super(restTemplate, objectMapper, endpoint);
        this.type = endpoint.equals("model/") ? "model" : endpoint.equals("texture/") ? "texture" : "file";
    }

    //region Overridden operations from IApiClient

    /**
     * Overridden update method to throw not implemented exception.
     * @param textureId ID of the texture to update
     * @param textureEntity texture entity
     * @return updated texture entity
     * @throws Exception throws exception when update fails
     */
    @Override
    public E update(String textureId, E textureEntity) throws Exception {
        throw new NotImplementedException("Aktualizace textur není zatím implementováno.");
    }

    /**
     * Overridden delete method to throw not implemented exception.
     * @param id ID of the entity to delete
     * @return deleted entity
     * @throws Exception throws exception when delete fails
     */
    @Override
    public boolean delete(String id) throws Exception {
        throw new NotImplementedException("Mazání textur není zatím implementováno.");
    }
    //endregion

    //region Methods from IFileApiClient
    /**
     * @param inputStreamMultipartFile file to upload
     * @param uploadEntity             metadata entity
     * @return uploaded entity
     * @throws Exception throws exception when upload fails
     */
    @Override
    public Q uploadFileEntity(InputStreamMultipartFile inputStreamMultipartFile, U uploadEntity) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add(type, inputStreamMultipartFile.getResource());

        String metadataJson = objectMapper.writeValueAsString(uploadEntity);
        HttpHeaders metadataHeaders = new HttpHeaders();
        metadataHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> metadataPart = new HttpEntity<>(metadataJson, metadataHeaders);
        body.add("metadata", metadataPart);

        return sendPostRequest(baseUrl + "upload", body, getQuicEntityClass(), "Chyba při nahrávání " + type + " " + getEntityClass().getSimpleName(), null, headers);
    }

    /**
     * @param fileId ID of the file to download
     * @return downloaded file
     * @throws Exception throws exception when download fails
     */
    @Override
    public InputStreamMultipartFile downloadFileEntity(String fileId) throws Exception {
        String url = baseUrl + "download/" + fileId;
        ResponseEntity<byte[]> response = sendGetRequestRaw(url, byte[].class, "Chyba při stahování " + type + " dle ID", fileId, false);
        return parseFileResponse(response, "Nenalezeno nebo chyba při stahování." + getEntityClass().getSimpleName(), fileId);
    }
    //endregion

    /**
     * Constructs the streaming backend endpoint URL for downloading a file by ID.
     * @param id ID of the file
     * @return streaming backend endpoint URL
     */
    public String getStreamBeEndpointUrl(String id) {
        return IApiClient.getLocalBaseBeUrl() + type + "/download/" + id;
    }
}
