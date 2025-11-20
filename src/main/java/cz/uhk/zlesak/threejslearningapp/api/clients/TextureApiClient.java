package cz.uhk.zlesak.threejslearningapp.api.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.uhk.zlesak.threejslearningapp.api.contracts.ITextureApiClient;
import cz.uhk.zlesak.threejslearningapp.common.InputStreamMultipartFile;
import cz.uhk.zlesak.threejslearningapp.domain.common.PageResult;
import cz.uhk.zlesak.threejslearningapp.domain.texture.QuickTextureEntity;
import cz.uhk.zlesak.threejslearningapp.domain.texture.TextureEntity;
import cz.uhk.zlesak.threejslearningapp.domain.texture.TextureFilter;
import cz.uhk.zlesak.threejslearningapp.domain.texture.TextureUploadEntity;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * TextureApiClient provides connection to the backend service for managing textures.
 * It implements the ITextureApiClient interface and provides methods for creating, retrieving, uploading, downloading, and deleting texture entities.
 * It uses RestTemplate for making HTTP requests to the backend service.
 * The base URL for the API is determined by the IApiClient interface.
 */
@Component
public class TextureApiClient extends AbstractFileApiClient<TextureEntity, QuickTextureEntity, TextureFilter, TextureUploadEntity> implements ITextureApiClient {
    /**
     * Constructor for TextureApiClient.
     *
     * @param restTemplate the RestTemplate used for making HTTP requests
     * @param objectMapper the ObjectMapper used for JSON serialization/deserialization
     */
    @Autowired
    public TextureApiClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        super(restTemplate, objectMapper, "texture/");
    }

    //region Overridden CRUD operations from IApiClient

    /**
     * Creates a new texture.
     *
     * @param textureEntity Texture entity to create
     * @return Created texture entity
     * @throws Exception if API call fails
     */
    @Override
    public TextureEntity create(TextureEntity textureEntity) throws Exception { //TODO BE implementation
        throw new NotImplementedException("Vytváření textur není zatím implementováno.");
    }

    /**
     * Gets a texture by ID.
     *
     * @param textureId ID of the texture to retrieve
     * @return Texture entity
     * @throws Exception if API call fails
     */
    @Override
    public TextureEntity read(String textureId) throws Exception {//TODO BE implementation to provide this structure directly
        InputStreamMultipartFile file = downloadFileEntity(textureId);
        return TextureEntity.builder()
                .Id(textureId)
                .Name(file.getName())
                .File(file)
                .build();
    }

    /**
     * Gets paginated list of textures with filtering.
     *
     * @param pageRequest PageRequest object containing pagination info
     * @param filter      TextureFilter object containing filter criteria
     * @return PageResult of QuickTextureEntity
     * @throws Exception if API call fails
     */
    @Override
    public PageResult<QuickTextureEntity> readEntitiesFiltered(PageRequest pageRequest, TextureFilter filter) throws Exception {
        throw new NotImplementedException("Filtrování textur není zatím implementováno.");
    }

    //endregion

    //region Overridden operations from AbstractApiClient

    /**
     * Gets the class type of the entity.
     *
     * @return Class of TextureEntity
     */
    @Override
    protected Class<TextureEntity> getEntityClass() {
        return TextureEntity.class;
    }

    /**
     * Gets the class type of the quick entity.
     *
     * @return Class of QuickTextureEntity
     */
    @Override
    protected Class<QuickTextureEntity> getQuicEntityClass() {
        return QuickTextureEntity.class;
    }

    //endregion
}
