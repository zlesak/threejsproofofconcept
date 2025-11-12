package cz.uhk.zlesak.threejslearningapp.services;

import cz.uhk.zlesak.threejslearningapp.api.clients.ModelApiClient;
import cz.uhk.zlesak.threejslearningapp.common.InputStreamMultipartFile;
import cz.uhk.zlesak.threejslearningapp.domain.common.Entity;
import cz.uhk.zlesak.threejslearningapp.domain.model.ModelEntity;
import cz.uhk.zlesak.threejslearningapp.domain.common.QuickFile;
import cz.uhk.zlesak.threejslearningapp.domain.model.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.domain.texture.QuickTextureEntity;
import cz.uhk.zlesak.threejslearningapp.domain.common.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Controller for managing 3D models, including uploading and retrieving model files and textures.
 * This class handles the interaction with the model API client to upload models and textures,
 * and provides methods to retrieve model files, names, and base64 representations.
 * It also integrates with the TextureController to manage textures associated with the models as the textures are an integral part of the model data.
 *
 * @see TextureService
 */
@Slf4j
@Service
@Scope("prototype")
public class ModelService implements IService {
    private final TextureService textureController;
    private final ModelApiClient modelApiClient;
    private ModelEntity modelEntity = null;

    /**
     * Constructor for ModelController.
     * Initializes controller with dependencies for texture management, model API client, and JSON processing.
     *
     * @param textureController the controller for managing textures associated with models.
     * @param modelApiClient    the API client for interacting with model-related endpoints.
     */
    @Autowired
    public ModelService(TextureService textureController, ModelApiClient modelApiClient) {
        this.textureController = textureController;
        this.modelApiClient = modelApiClient;
    }

    /**
     * Uploads a 3D model with the specified name and input streams.
     * The method checks for valid model name and input streams, then uploads the model using the model API client.
     * It returns a QuickModelEntity containing the uploaded model's details as a proof of successful upload.
     *
     * @param modelName   as of the whole object with possible textures and CSVs.
     * @param inputStream the input stream representing the model file to be uploaded.
     * @return QuickModelEntity containing the details of the uploaded model.
     * @throws RuntimeException if the model name is empty or the input streams are empty, or if an error occurs during the upload process.
     */
    public QuickModelEntity uploadModel(String modelName, InputStreamMultipartFile inputStream) throws RuntimeException {

        if (modelName.isEmpty()) {
            throw new ApplicationContextException("Název modelu nesmí být prázdný.");
        }
        if (inputStream.isEmpty()) {
            throw new ApplicationContextException("Soubor pro nahrání modelu nesmí být prázdný.");
        }

        try {
            Entity entity = ModelEntity.builder()
                    .Name(modelName)
                    .build();
            return modelApiClient.uploadFileEntity(inputStream, entity);
        } catch (Exception e) {
            throw new RuntimeException("Chyba při nahrávání modelu: " + e.getMessage(), e);
        }
    }

    /**
     * Uploads a 3D model along with its textures and CSV files.
     * This method handles the upload of the model file, main texture, other textures, and CSV files.
     * It validates the inputs and uses the TextureController to manage texture uploads.
     * If any of the required inputs are empty, it throws an ApplicationContextException.
     *
     * @param modelName                    the name of the model to be uploaded.
     * @param modelInputStream             a map of input streams representing the model file to be uploaded, where the key is the file name and the value is the InputStream of the file.
     * @param mainTextureInputStream       a map of input streams representing the main texture file to be uploaded, where the key is the file name and the value is the InputStream of the file.
     * @param otherTexturesInputStreamList a map of input streams representing other texture files to be uploaded, where the key is the file name and the value is the InputStream of the file.
     * @param csvInputStreamList           a map of input streams representing CSV files to be uploaded, where the key is the file name and the value is the InputStream of the file.
     * @return QuickModelEntity containing the details of the uploaded model and its textures.
     * @throws ApplicationContextException if the model name is empty, the model input stream is empty, or the main texture input stream is empty.
     * @throws RuntimeException            if there is an error during the upload of the main texture or other textures.
     * @see TextureService
     */
    public QuickModelEntity uploadModel(String modelName, InputStreamMultipartFile modelInputStream, InputStreamMultipartFile mainTextureInputStream, List<InputStreamMultipartFile> otherTexturesInputStreamList, List<InputStreamMultipartFile> csvInputStreamList) throws ApplicationContextException, RuntimeException {
        if (modelName.isEmpty()) {
            throw new ApplicationContextException("Název modelu nesmí být prázdný.");
        }
        if (modelInputStream.isEmpty()) {
            throw new ApplicationContextException("Soubor pro nahrání modelu nesmí být prázdný.");
        }
        if (mainTextureInputStream.isEmpty()) {
            throw new ApplicationContextException("Hlavní textura nesmí být prázdná.");
        }
        QuickModelEntity uploadedModel = uploadModel(modelName, modelInputStream);

        try {
            QuickTextureEntity mainTextureQuickFileEntity = textureController.uploadTexture(mainTextureInputStream, true, uploadedModel.getModel().getId(), null);
            uploadedModel.setMainTexture(mainTextureQuickFileEntity);
        } catch (Exception e) {
            log.error("Chyba při nahrávání hlavní textury: {}", e.getMessage(), e);
            throw new RuntimeException("Chyba při nahrávání hlavní textury: " + e.getMessage(), e);
        }

        try {
            List<QuickTextureEntity> otherTexturesUploadedList = textureController.uploadOtherTextures(otherTexturesInputStreamList, uploadedModel.getModel().getId(), csvInputStreamList);
            uploadedModel.setOtherTextures(otherTexturesUploadedList);
        } catch (Exception e) {
            log.error("Chyba při nahrávání vedlejších textur: {}", e.getMessage(), e);
            throw new RuntimeException("Chyba při nahrávání vedlejších textur: " + e.getMessage(), e);
        }
        return uploadedModel;
    }

    /**
     * Retrieves a model entity by its ID.
     * This method uses the model API client to fetch the model entity from the BE.
     *
     * @param modelId the ID of the model to be retrieved.
     * @throws RuntimeException if there is an error during the retrieval of the model entity.
     * @see ModelApiClient#getFileEntityById(String)
     */
    private void getModel(String modelId) throws RuntimeException {
        try {
            this.modelEntity = modelApiClient.getFileEntityById(modelId);
        } catch (Exception e) {
            log.error("Chyba při získávání modelu: {}", e.getMessage(), e);
            throw new RuntimeException("Chyba při získávání modelu: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves models saved in the BE.
     * Currently, it retrieves only the first 10 models due to pagination.
     *
     * @return List of QuickModelEntity representing the models.
     * @throws RuntimeException if there is an error during the retrieval of the models.
     */
    public PageResult<QuickFile> getModels(int page, int limit) throws RuntimeException {
        try {
            return modelApiClient.getFileEntities(page, limit);
        } catch (Exception e) {
            log.error("Chyba při získávání stránkování modelů pro page {}, limit {}, error message: {}", page, limit, e.getMessage(), e);
            throw new RuntimeException("Chyba při získávání modelu: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves the InputStream of a model file by its ID.
     * If the model entity is not already loaded or if the loaded entity does not match the requested ID,
     * it fetches the model entity using the getModel method.
     *
     * @param modelId the ID of the model whose InputStream is to be retrieved.
     * @return the InputStream of the model file.
     */
    public InputStreamResource getInputStream(String modelId) {
        if (this.modelEntity == null) {
            this.getModel(modelId);
        } else if (this.modelEntity.getId() == null || !this.modelEntity.getId().equals(modelId)) {
            this.getModel(modelId);
        }
        return new InputStreamResource(modelEntity.getFile().getInputStream());
    }

    /**
     * Constructs the endpoint URL for streaming the model file by its ID.
     * If the model entity is not already loaded or if the loaded entity does not match the requested ID,
     * it fetches the model entity using the getModel method.
     *
     * @param modelId the ID of the model whose stream endpoint URL is to be constructed.
     * @return the endpoint URL for streaming the model file.
     */
    public String getModelStreamEndpoint(String modelId, boolean advanced) {
        return "/api/model/" + modelId + "/stream" + (advanced ? "?advanced=true" : "?advanced=false");
    }

    /**
     * Retrieves the name of a model by its ID.
     * If the model entity is not already loaded or if the loaded entity does not match the requested ID,
     * it fetches the model entity using the getModel method.
     *
     * @param modelId the ID of the model whose name is to be retrieved.
     * @return the name of the model.
     */
    public String getModelName(String modelId) {
        if (this.modelEntity == null) {
            this.getModel(modelId);
        } else if (this.modelEntity.getId() == null || !this.modelEntity.getId().equals(modelId)) {
            this.getModel(modelId);
        }
        return this.modelEntity.getName();
    }

    /**
     * Retrieves the URL for texture file BE endpoint by texture ID.
     *
     * @param textureId the ID of the texture whose URL is to be retrieved.
     * @return the URL for the texture file BE endpoint.
     */
    public String getModelFileBeEndpointUrl(String textureId) {
        return modelApiClient.getModelFileBeEndpointUrl(textureId);
    }
}
