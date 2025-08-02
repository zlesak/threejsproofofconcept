package cz.uhk.zlesak.threejslearningapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.server.StreamResource;
import cz.uhk.zlesak.threejslearningapp.clients.TextureApiClient;
import cz.uhk.zlesak.threejslearningapp.models.InputStreamMultipartFile;
import cz.uhk.zlesak.threejslearningapp.models.entities.TextureEntity;
import cz.uhk.zlesak.threejslearningapp.models.entities.TextureUploadEntity;
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
import java.util.Map;
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

    public String uploadTexture(String textureName, InputStreamMultipartFile inputStream, boolean isPrimary, String modelId) throws ApplicationContextException {
        return uploadTexture(textureName, inputStream, isPrimary, modelId, null);
    }

    public String uploadTexture(String textureName, InputStreamMultipartFile inputStream, boolean isPrimary, String modelId, InputStreamMultipartFile csv) throws ApplicationContextException {

        if (textureName.isEmpty()) {
            throw new ApplicationContextException("Název textury nesmí být prázdný.");
        }
        if (inputStream.isEmpty()) {
            throw new ApplicationContextException("Soubor pro nahrání textury nesmí být prázdný.");
        }

        try {
            String csvString = csv == null ? null : new String(csv.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            TextureEntity textureEntity = TextureEntity.builder()
                    .Name(textureName)
                    .CSV(csvString)
                    .build();
            TextureUploadEntity uploadedEntity = TextureUploadEntity.builder()
                    .targetFileId(modelId)
                    .isPrimary(isPrimary)
                    .texture(textureEntity)
                    .build();
            return textureApiClient.uploadFileEntity(inputStream, uploadedEntity);
        } catch (Exception e) {
            throw new RuntimeException("Chyba při nahrávání textury: " + e.getMessage(), e);
        }
    }

    public List<String> uploadTexture(Map<String, InputStream> textureInputStream, boolean isPrimary, String modelId, Map<String, InputStream> csvInputStream) throws ApplicationContextException {
        List<String> uploadedTextureIds = new ArrayList<>();
        for (var entry : textureInputStream.entrySet()) {
            String fileName = entry.getKey();
            InputStream inputStream = entry.getValue();
            InputStream csvStream = null;
            String prefix ="";
            if (csvInputStream != null) {
                prefix = fileName.substring(0, fileName.lastIndexOf('.'));
                csvStream = csvInputStream.get(prefix + ".csv");
            }

            InputStreamMultipartFile otherTexture = new InputStreamMultipartFile(inputStream, fileName);
            if (!otherTexture.isEmpty()) {
                if (csvStream == null) {
                    uploadedTextureIds.add(uploadTexture(fileName, otherTexture, isPrimary, modelId));
                } else {
                    InputStreamMultipartFile csv = new InputStreamMultipartFile(csvStream, prefix + ".csv");
                    uploadedTextureIds.add(uploadTexture(fileName, otherTexture, isPrimary, modelId, csv));
                }
            }
        }
        return uploadedTextureIds;
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

