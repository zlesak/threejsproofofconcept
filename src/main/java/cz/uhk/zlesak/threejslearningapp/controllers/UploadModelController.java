package cz.uhk.zlesak.threejslearningapp.controllers;

import cz.uhk.zlesak.threejslearningapp.clients.ModelApiClient;
import cz.uhk.zlesak.threejslearningapp.models.IFileEntity;
import cz.uhk.zlesak.threejslearningapp.models.InputStreamMultipartFile;
import cz.uhk.zlesak.threejslearningapp.models.ModelEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UploadModelController {
    private final ModelApiClient modelApiClient;

    @Autowired
    public UploadModelController(ModelApiClient modelApiClient) {
        this.modelApiClient = modelApiClient;
    }

    public void uploadModel(String modelName, InputStreamMultipartFile inputStream) {
        try {
            IFileEntity fileEntity = ModelEntity.builder()
                    .Name(modelName)
//                        .Creator()
//                        .CreationDate()
//                        .LastUpdateDate()
//                        .Metadata()
                    .File(inputStream)
//                        .MainTextureEntity()
//                        .TextureEntities()
                    .build();
            modelApiClient.uploadFileEntity(fileEntity);
        } catch (Exception e) {
            throw new RuntimeException("Chyba při nahrávání modelu: " + e.getMessage(), e);
        }
    }
}
