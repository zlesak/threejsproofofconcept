package cz.uhk.zlesak.threejslearningapp.api.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.uhk.zlesak.threejslearningapp.api.contracts.IApiClient;
import cz.uhk.zlesak.threejslearningapp.api.contracts.IFileApiClient;
import cz.uhk.zlesak.threejslearningapp.common.InputStreamMultipartFile;
import cz.uhk.zlesak.threejslearningapp.domain.common.PageResult;
import cz.uhk.zlesak.threejslearningapp.domain.texture.QuickTextureEntity;
import cz.uhk.zlesak.threejslearningapp.domain.texture.TextureEntity;
import cz.uhk.zlesak.threejslearningapp.domain.texture.TextureFilter;
import cz.uhk.zlesak.threejslearningapp.domain.texture.TextureUploadEntity;
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

/**
 * TextureApiClient provides connection to the backend service for managing textures.
 * It implements the IFileApiClient interface and provides methods for creating, retrieving, uploading, downloading, and deleting texture entities.
 * It uses RestTemplate for making HTTP requests to the backend service.
 * The base URL for the API is determined by the IApiClient interface.
 */
@Component
public class TextureApiClient extends AbstractApiClient<TextureEntity, QuickTextureEntity, TextureFilter> implements IFileApiClient<TextureUploadEntity, QuickTextureEntity> {
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

    //region CRUD operations from IApiClient

    /**
     * Creates a new texture.
     *
     * @param textureEntity Texture entity to create
     * @return Created texture entity
     * @throws Exception if API call fails
     */
    @Override
    public TextureEntity create(TextureEntity textureEntity) throws Exception {
        throw new NotImplementedException("Vytváření textur není zatím implementováno.");
//        return sendPostRequest(baseUrl + "create", textureEntity, TextureEntity.class, "Chyba při vytváření textury", null);
    }

    /**
     * Gets a texture by ID.
     *
     * @param textureId ID of the texture to retrieve
     * @return Texture entity
     * @throws Exception if API call fails
     */
    @Override
    public TextureEntity read(String textureId) throws Exception {
        InputStreamMultipartFile file = downloadFileEntity(textureId);
        return TextureEntity.builder()
                .Id(textureId)
                .Name(file.getName())
                .File(file)
                .build();
//        throw new NotImplementedException("Získávání textur dle ID není zatím implementováno.");
//        return sendGetRequest(baseUrl + textureId, TextureEntity.class, "Chyba při získávání textury dle ID", textureId);
    }

    /**
     * Gets paginated list of textures.
     *
     * @param pageRequest PageRequest object containing pagination info
     * @return PageResult of QuickTextureEntity
     * @throws Exception if API call fails
     */
    @Override
    public PageResult<QuickTextureEntity> readEntities(PageRequest pageRequest) throws Exception {
        return readEntitiesFiltered(pageRequest, null);
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
//        String url = pageRequestToQueryParams(pageRequest) + filterToQueryParams(filter);
//        ResponseEntity<String> response = sendGetRequestRaw(url, "Chyba při získávání seznamu textur", null);
//        JavaType type = objectMapper.getTypeFactory().constructParametricType(PageResult.class, QuickTextureEntity.class);
//        return parseResponse(response, type, "Chyba při získávání seznamu textur", null);
    }

    /**
     * Updates an existing texture.
     *
     * @param textureId     ID of the model to update
     * @param textureEntity Texture entity to update
     * @return Updated texture entity
     * @throws Exception if API call fails
     */
    @Override
    public TextureEntity update(String textureId, TextureEntity textureEntity) throws Exception {
        throw new NotImplementedException("Aktualizace textur není zatím implementováno.");
//        return sendPutRequest(baseUrl + "update/" + textureId, textureEntity, TextureEntity.class, "Chyba při aktualizaci textury", textureId);

    }

    /**
     * Deletes a texture by ID.
     *
     * @param textureId ID of the model to delete
     * @return True if deletion was successful, false otherwise
     * @throws Exception if API call fails
     */
    @Override
    public boolean delete(String textureId) throws Exception {
        throw new NotImplementedException("Mazání textur není zatím implementováno.");
//        sendDeleteRequest(baseUrl + "delete/" + textureId, "Chyba při mazání textury", textureId);
//        return true;
    }
//endregion

    /**
     *
     */
    @Override
    public QuickTextureEntity uploadFileEntity(InputStreamMultipartFile inputStreamMultipartFile, TextureUploadEntity textureEntity) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("texture", inputStreamMultipartFile.getResource());

        String metadataJson = objectMapper.writeValueAsString(textureEntity);
        HttpHeaders metadataHeaders = new HttpHeaders();
        metadataHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> metadataPart = new HttpEntity<>(metadataJson, metadataHeaders);
        body.add("metadata", metadataPart);

        return sendPostRequest(baseUrl + "upload", body, QuickTextureEntity.class, "Chyba při nahrávání textury", null, headers);
    }


    /**
     *
     */
    @Override
    public InputStreamMultipartFile downloadFileEntity(String fileEntityId) throws Exception {
        String url = baseUrl + "download/" + fileEntityId;
        ResponseEntity<byte[]> response = sendGetRequestRaw(url, byte[].class, "Chyba při stahování modelu dle ID", fileEntityId, false);
        return parseFileResponse(response, "Textura nalezena nebo chyba při stahování.", fileEntityId);
    }

    /**
     * Generates the URL for downloading a texture by its ID.
     *
     * @param textureId The ID of the texture to download.
     * @return The URL for downloading the texture.
     */
    public String getTextureStreamBeEndpointUrl(String textureId) {
        return IApiClient.getLocalBaseBeUrl() + "texture/download/" + textureId;
    }
}
