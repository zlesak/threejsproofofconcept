package cz.uhk.zlesak.threejslearningapp.api.clients;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.uhk.zlesak.threejslearningapp.api.contracts.IApiClient;
import cz.uhk.zlesak.threejslearningapp.api.contracts.IFileApiClient;
import cz.uhk.zlesak.threejslearningapp.common.InputStreamMultipartFile;
import cz.uhk.zlesak.threejslearningapp.domain.common.PageResult;
import cz.uhk.zlesak.threejslearningapp.domain.model.ModelEntity;
import cz.uhk.zlesak.threejslearningapp.domain.model.ModelFilter;
import cz.uhk.zlesak.threejslearningapp.domain.model.QuickModelEntity;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * ModelApiClient provides connection to the backend service for managing models.
 * It implements the IFileApiClient interface and provides methods for creating, retrieving, uploading, downloading, and deleting model entities.
 * It uses RestTemplate for making HTTP requests to the backend service.
 * The base URL for the API is determined by the IApiClient interface.
 */
@Component
public class ModelApiClient extends AbstractApiClient<ModelEntity, QuickModelEntity, ModelFilter> implements IFileApiClient<ModelEntity, QuickModelEntity> {

    /**
     * Constructor for ModelApiClient.
     *
     * @param restTemplate the RestTemplate used for making HTTP requests
     * @param objectMapper the ObjectMapper used for JSON serialization/deserialization
     */
    @Autowired
    public ModelApiClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        super(restTemplate, objectMapper, "model/");
    }

    //region CRUD operations from IApiClient

    /**
     * Creates a new model.
     *
     * @param modelEntity Model entity to create
     * @return Created model entity
     * @throws Exception if API call fails
     */
    @Override
    public ModelEntity create(ModelEntity modelEntity) throws Exception {
        return sendPostRequest(baseUrl + "create", modelEntity, ModelEntity.class, "Chyba při vytváření modelu", null, null);
    }

    /**
     * Gets a model by ID.
     *
     * @param modelId ID of the model to retrieve
     * @return Model entity
     * @throws Exception if API call fails
     */
    @Override
    public ModelEntity read(String modelId) throws Exception {

        InputStreamMultipartFile fileEntity = downloadFileEntity(modelId);
        return ModelEntity.builder()
                .Id(modelId)
                .Name(fileEntity.getName())
                .MainTextureEntity(null)
                .TextureEntities(List.of())
                .File(fileEntity)
                .build();
//        return sendGetRequest(baseUrl + modelId, ModelEntity.class, "Chyba při získávání modelu dle ID", modelId);
    }

    /**
     * Gets paginated list of models.
     *
     * @param pageRequest PageRequest object containing pagination info
     * @return PageResult of QuickModelEntity
     * @throws Exception if API call fails
     */
    @Override
    public PageResult<QuickModelEntity> readEntities(PageRequest pageRequest) throws Exception {
        return readEntitiesFiltered(pageRequest, null);
    }

    /**
     * Gets paginated list of models with filtering.
     *
     * @param pageRequest PageRequest object containing pagination info
     * @param filter      ModelFilter object containing filter criteria
     * @return PageResult of QuickModelEntity
     * @throws Exception if API call fails
     */
    @Override
    public PageResult<QuickModelEntity> readEntitiesFiltered(PageRequest pageRequest, ModelFilter filter) throws Exception {
        String url = pageRequestToQueryParams(pageRequest, "list-by") + filterToQueryParams(filter);
        ResponseEntity<String> response = sendGetRequestRaw(url, String.class, "Chyba při získávání seznamu modelů", null, true);
        JavaType type = objectMapper.getTypeFactory().constructParametricType(PageResult.class, QuickModelEntity.class);
        return parseResponse(response, type, "Chyba při získávání seznamu modelů", null);
    }

    /**
     * Updates an existing model.
     *
     * @param modelId     ID of the model to update
     * @param modelEntity Model entity to update
     * @return Updated chapter entity
     * @throws Exception if API call fails
     */
    @Override
    public ModelEntity update(String modelId, ModelEntity modelEntity) throws Exception {
        throw new NotImplementedException("Aktualizace modelů není zatím implementováno.");
//        return sendPutRequest(baseUrl + "update/" + modelId, modelEntity, ModelEntity.class, "Chyba při aktualizaci modelu", modelId);

    }

    /**
     * Deletes a model by ID.
     *
     * @param modelId ID of the model to delete
     * @return True if deletion was successful, false otherwise
     * @throws Exception if API call fails
     */
    @Override
    public boolean delete(String modelId) throws Exception {
        throw new NotImplementedException("Mazání modelů není zatím implementováno.");
//        sendDeleteRequest(baseUrl + "delete/" + modelId, "Chyba při mazání kvízu", modelId);
//        return true;
    }
//endregion

    /**
     *
     */
    @Override
    public QuickModelEntity uploadFileEntity(InputStreamMultipartFile inputStreamMultipartFile, ModelEntity modelEntity) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("model", inputStreamMultipartFile.getResource());

        String metadataJson = objectMapper.writeValueAsString(modelEntity);
        HttpHeaders metadataHeaders = new HttpHeaders();
        metadataHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> metadataPart = new HttpEntity<>(metadataJson, metadataHeaders);
        body.add("metadata", metadataPart);

        return sendPostRequest(baseUrl + "upload", body, QuickModelEntity.class, "Chyba při nahrávání modelu", null, headers);
    }

    /**
     *
     */
    @Override
    public InputStreamMultipartFile downloadFileEntity(String fileEntityId) throws Exception {
        String url = baseUrl + "download/" + fileEntityId;
        ResponseEntity<byte[]> response = sendGetRequestRaw(url, byte[].class, "Chyba při stahování modelu dle ID", fileEntityId, false);
        return parseFileResponse(response, "Model nenalezen nebo chyba při stahování.", fileEntityId);
    }

    /**
     * Generates the backend endpoint URL for downloading a model file by its ID.
     *
     * @param modelId the ID of the model
     * @return the complete URL to download the model file
     */
    public String getModelFileBeEndpointUrl(String modelId) {
        return IApiClient.getLocalBaseBeUrl() + "model/download/" + modelId;
    }
}
