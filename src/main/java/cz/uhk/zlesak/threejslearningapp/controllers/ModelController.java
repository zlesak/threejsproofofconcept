package cz.uhk.zlesak.threejslearningapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.uhk.zlesak.threejslearningapp.clients.ModelApiClient;
import cz.uhk.zlesak.threejslearningapp.models.InputStreamMultipartFile;
import cz.uhk.zlesak.threejslearningapp.models.entities.Entity;
import cz.uhk.zlesak.threejslearningapp.models.entities.ModelEntity;
import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickFileEntity;
import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickModelEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Scope("prototype")
public class ModelController {
    @Autowired
    private final TextureController textureController;

    private final ModelApiClient modelApiClient;
    private final ObjectMapper objectMapper;
    private ModelEntity modelEntity = null;

    @Autowired
    public ModelController(TextureController textureController, ModelApiClient modelApiClient, ObjectMapper objectMapper) {
        this.textureController = textureController;
        this.modelApiClient = modelApiClient;
        this.objectMapper = objectMapper;
    }

    public QuickModelEntity uploadModel(String modelName, Map<String, InputStream> inputStreams) throws ApplicationContextException {

        if (modelName.isEmpty()) {
            throw new ApplicationContextException("Název modelu nesmí být prázdný.");
        }
        if (inputStreams.isEmpty()) {
            throw new ApplicationContextException("Soubor pro nahrání modelu nesmí být prázdný.");
        }

        var entry = inputStreams.entrySet().iterator().next();
        String fileName = entry.getKey();
        InputStream objInputStream = entry.getValue();
        InputStreamMultipartFile multipartFile = new InputStreamMultipartFile(objInputStream, fileName);

        try {
            Entity entity = ModelEntity.builder()
                    .Name(modelName)
                    .build();
            String response = modelApiClient.uploadFileEntity(multipartFile, entity);
            return objectMapper.readValue(response, QuickModelEntity.class);
        } catch (Exception e) {
            throw new RuntimeException("Chyba při nahrávání modelu: " + e.getMessage(), e);
        }
    }

    public QuickModelEntity uploadModel(String modelName, Map<String, InputStream> modelInputStream, Map<String, InputStream> mainTextureInputStream, Map<String, InputStream> otherTexturesInputStreamList) throws ApplicationContextException {

        //TODO checks
        QuickModelEntity uploadedModel = uploadModel(modelName, modelInputStream);

        if (mainTextureInputStream.isEmpty()) {
            throw new ApplicationContextException("Hlavní textura nesmí být prázdná.");
        }
        try {
            List<String> mainTextureUploadedList = textureController.uploadTexture(mainTextureInputStream, true, uploadedModel.getModel().getId());
            if (!mainTextureUploadedList.isEmpty()) {
                QuickFileEntity mainTextureQuickFileEntity = QuickFileEntity.builder().id(mainTextureUploadedList.getFirst()).name(mainTextureInputStream.entrySet().iterator().next().getKey()).build();
                uploadedModel.setMainTexture(mainTextureQuickFileEntity);
            }
        } catch (Exception e) {
            log.error("Chyba při nahrávání hlavní textury: {}", e.getMessage(), e);
            throw new RuntimeException("Chyba při nahrávání hlavní textury: " + e.getMessage(), e);
        }
        try {
            textureController.uploadTexture(otherTexturesInputStreamList, true, uploadedModel.getModel().getId());
        } catch (Exception e) {
            log.error("Chyba při nahrávání vedlejších textur: {}", e.getMessage(), e);
            throw new RuntimeException("Chyba při nahrávání vedlejších textur: " + e.getMessage(), e);
        }

        return uploadedModel;
    }

    public void getModel(String modelId) {
        try {
            this.modelEntity = modelApiClient.getFileEntityById(modelId);
        } catch (Exception e) {
            log.error("Chyba při získávání modelu: {}", e.getMessage(), e);
            throw new RuntimeException("Chyba při získávání modelu: " + e.getMessage(), e);
        }
    }

    public InputStreamMultipartFile getModelFile(String modelId) {
        if (this.modelEntity == null || !this.modelEntity.getId().equals(modelId)) {
            this.getModel(modelId);
        }
        return this.modelEntity.getFile();
    }

    public String getModelName(String modelId) {
        if (this.modelEntity == null || !this.modelEntity.getId().equals(modelId)) {
            this.getModel(modelId);
        }
        return this.modelEntity.getName();
    }

    public String getModelBase64(String modelId) throws IOException {
        if (this.modelEntity == null || !this.modelEntity.getId().equals(modelId)) {
            this.getModel(modelId);
        }
        return modelEntity.getBase64File();
    }
}
