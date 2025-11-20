package cz.uhk.zlesak.threejslearningapp.api.clients;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.uhk.zlesak.threejslearningapp.api.contracts.IModelApiClient;
import cz.uhk.zlesak.threejslearningapp.common.InputStreamMultipartFile;
import cz.uhk.zlesak.threejslearningapp.domain.common.PageResult;
import cz.uhk.zlesak.threejslearningapp.domain.model.ModelEntity;
import cz.uhk.zlesak.threejslearningapp.domain.model.ModelFilter;
import cz.uhk.zlesak.threejslearningapp.domain.model.QuickModelEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * ModelApiClient provides connection to the backend service for managing models.
 * It implements the IFileApiClient interface and provides methods for creating, retrieving, uploading, downloading, and deleting model entities.
 * It uses RestTemplate for making HTTP requests to the backend service.
 * The base URL for the API is determined by the IApiClient interface.
 */
@Component
public class ModelApiClient extends AbstractFileApiClient<ModelEntity, QuickModelEntity, ModelFilter, ModelEntity> implements IModelApiClient {

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

    //region Overridden CRUD operations from IApiClient


    /**
     * Gets a model by ID.
     *
     * @param modelId ID of the model to retrieve
     * @return Model entity
     * @throws Exception if API call fails
     */
    @Override
    public ModelEntity read(String modelId) throws Exception { //TODO BE implementation to provide this structure directly

        InputStreamMultipartFile fileEntity = downloadFileEntity(modelId);
        return ModelEntity.builder()
                .Id(modelId)
                .Name(fileEntity.getName())
                .MainTextureEntity(null)
                .TextureEntities(List.of())
                .File(fileEntity)
                .build();
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
        String url = pageRequestToQueryParams(pageRequest, "list-by") + filterToQueryParams(filter); //TODO make list on BE to be use the method from AbstractApiClient
        ResponseEntity<String> response = sendGetRequestRaw(url, String.class, "Chyba při získávání seznamu modelů", null, true);
        JavaType type = objectMapper.getTypeFactory().constructParametricType(PageResult.class, QuickModelEntity.class);
        return parseResponse(response, type, "Chyba při získávání seznamu modelů", null);
    }
    //endregion

    //region Overridden operations from AbstractApiClient

    /**
     * Gets the class type of the entity.
     * @return Class of ModelEntity
     */
    @Override
    protected Class<ModelEntity> getEntityClass() {
        return ModelEntity.class;
    }

    /**
     * Gets the class type of the quick entity.
     * @return Class of QuickModelEntity
     */
    @Override
    protected Class<QuickModelEntity> getQuicEntityClass() {
        return QuickModelEntity.class;
    }
    //endregion
}
