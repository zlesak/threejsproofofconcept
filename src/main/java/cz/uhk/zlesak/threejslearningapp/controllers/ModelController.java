package cz.uhk.zlesak.threejslearningapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.uhk.zlesak.threejslearningapp.clients.ModelApiClient;
import cz.uhk.zlesak.threejslearningapp.models.entities.Entity;
import cz.uhk.zlesak.threejslearningapp.models.InputStreamMultipartFile;
import cz.uhk.zlesak.threejslearningapp.models.entities.ModelEntity;
import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickModelEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope("prototype")
public class ModelController {
    private final ModelApiClient modelApiClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public ModelController(ModelApiClient modelApiClient, ObjectMapper objectMapper) {
        this.modelApiClient = modelApiClient;
        this.objectMapper = objectMapper;
    }

    public QuickModelEntity uploadModel(String modelName, InputStreamMultipartFile inputStream) throws ApplicationContextException {

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
            String response = modelApiClient.uploadFileEntity(inputStream,entity);
            return objectMapper.readValue(response, QuickModelEntity.class);
        } catch (Exception e) {
            throw new RuntimeException("Chyba při nahrávání modelu: " + e.getMessage(), e);
        }
    }

    public ModelEntity getModel(String modelId) {
        try {
            return modelApiClient.getFileEntityById(modelId);
        } catch (Exception e) {
            log.error("Chyba při získávání modelu: {}", e.getMessage(), e);
            throw new RuntimeException("Chyba při získávání modelu: " + e.getMessage(), e);
        }
    }
}

