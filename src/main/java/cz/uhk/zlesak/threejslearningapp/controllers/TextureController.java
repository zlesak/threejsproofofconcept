package cz.uhk.zlesak.threejslearningapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.server.StreamResource;
import cz.uhk.zlesak.threejslearningapp.clients.TextureApiClient;
import cz.uhk.zlesak.threejslearningapp.data.files.InputStreamMultipartFile;
import cz.uhk.zlesak.threejslearningapp.models.entities.TextureEntity;
import cz.uhk.zlesak.threejslearningapp.models.entities.TextureUploadEntity;
import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickFileEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@Scope("prototype")
public class TextureController {
    protected final TextureApiClient textureApiClient;
    protected final ObjectMapper objectMapper;
    private TextureEntity textureEntity = null;

    @Autowired
    public TextureController(TextureApiClient textureApiClient, ObjectMapper objectMapper) {
        this.textureApiClient = textureApiClient;
        this.objectMapper = objectMapper;
    }

    public String uploadTexture(InputStreamMultipartFile inputStream, boolean isPrimary, String modelId, InputStream csv) throws ApplicationContextException {

        if (inputStream.isEmpty()) {
            throw new ApplicationContextException("Soubor pro nahrání textury nesmí být prázdný.");
        }
        if (inputStream.getDisplayName().isEmpty()) {
            throw new ApplicationContextException("Název textury nesmí být prázdný.");
        }

        try {
            String csvString = csv == null ? null : new String(csv.readAllBytes(), StandardCharsets.UTF_8);
            TextureEntity textureEntity = TextureEntity.builder()
                    .Name(inputStream.getDisplayName())
                    .CSV(csvString)
                    .build();
            TextureUploadEntity uploadedEntity = TextureUploadEntity.builder()
                    .modelId(modelId)
                    .isPrimary(isPrimary)
                    .texture(textureEntity)
                    .build();
            return textureApiClient.uploadFileEntity(inputStream, uploadedEntity);
        } catch (Exception e) {
            throw new RuntimeException("Chyba při nahrávání textury: " + e.getMessage(), e);
        }
    }

    public List<QuickFileEntity> uploadOtherTextures(List<InputStreamMultipartFile> textureInputStream, String modelId, List<InputStreamMultipartFile> csvInputStream) throws ApplicationContextException {
        List<QuickFileEntity> otherTextureEntities = new ArrayList<>();
        for (var entry : textureInputStream) {
            if (!entry.isEmpty()) {
                InputStream csvStream = null;
                String prefix;
                if (csvInputStream != null) {
                    prefix = entry.getName().substring(0, entry.getName().lastIndexOf('.'));
                    for (InputStreamMultipartFile csvFile : csvInputStream) {
                        if (csvFile.getName().equals(prefix + ".csv")) {
                            csvStream = csvFile.getInputStream();
                            break;
                        }
                    }
                }

                String uploadedTextureId = uploadTexture(entry, false, modelId, csvStream);
                otherTextureEntities.add(QuickFileEntity.builder()
                        .id(uploadedTextureId)
                        .name(entry.getDisplayName())
                        .build());
            }
        }
        return otherTextureEntities;
    }

    private void getTexture(String textureId) {
        try {
            this.textureEntity = textureApiClient.getFileEntityById(textureId);
        } catch (Exception e) {
            log.error("Chyba při získávání textury: {}", e.getMessage(), e);
            throw new RuntimeException("Chyba při získávání textury: " + e.getMessage(), e);
        }
    }

    public StreamResource getTextureImage(String textureId) {
        if (this.textureEntity == null || !Objects.equals(this.textureEntity.getId(), textureId)) {
            this.getTexture(textureId);
        }
        InputStreamMultipartFile texture = this.textureEntity.getFile();
        InputStream textureStream = texture.getInputStream();
        return new StreamResource(texture.getName(), () -> textureStream);
    }

    public String getTextureName(String textureId) {
        if (this.textureEntity == null || !Objects.equals(this.textureEntity.getId(), textureId)) {
            this.getTexture(textureId);
        }
        return this.textureEntity.getName();
    }

    public String getTextureBase64(String textureId) throws IOException {
        if (this.textureEntity == null || !Objects.equals(this.textureEntity.getId(), textureId)) {
            this.getTexture(textureId);
        }
        return textureEntity.getBase64File();
    }
}

