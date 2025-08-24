package cz.uhk.zlesak.threejslearningapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.server.StreamResource;
import cz.uhk.zlesak.threejslearningapp.clients.TextureApiClient;
import cz.uhk.zlesak.threejslearningapp.data.files.InputStreamMultipartFile;
import cz.uhk.zlesak.threejslearningapp.models.entities.TextureEntity;
import cz.uhk.zlesak.threejslearningapp.models.entities.TextureUploadEntity;
import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickTextureEntity;
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

/**
 * TextureController is responsible for handling texture-related operations such as uploading textures,
 * retrieving texture images, and managing texture data.
 * It interacts with the TextureApiClient to perform these operations.
 */
@Slf4j
@Component
@Scope("prototype")
public class TextureController {
    protected final TextureApiClient textureApiClient;
    protected final ObjectMapper objectMapper;
    private TextureEntity textureEntity = null;

    /**
     * Constructor for TextureController.
     * Initializes the controller with the provided TextureApiClient and ObjectMapper.
     * @param textureApiClient client for interacting with the texture API
     * @param objectMapper object mapper for JSON serialization and deserialization
     */
    @Autowired
    public TextureController(TextureApiClient textureApiClient, ObjectMapper objectMapper) {
        this.textureApiClient = textureApiClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Uploads a texture file along with its metadata to the server.
     * Validates the input file and constructs a TextureUploadEntity to send to the TextureApiClient.
     * @param inputStream the texture file to be uploaded
     * @param isPrimary indicates if the texture is the primary texture
     * @param modelId the ID of the model to which the texture belongs
     * @param csv an optional CSV file containing additional texture metadata
     * @return a QuickTextureEntity representing the uploaded texture
     * @throws ApplicationContextException if the input file is empty or has no name
     */
    public QuickTextureEntity uploadTexture(InputStreamMultipartFile inputStream, boolean isPrimary, String modelId, InputStream csv) throws ApplicationContextException {
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
                    .CsvContent(csvString)
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

    /**
     * Uploads multiple non-primary textures along with their associated CSV metadata files.
     * Matches each texture file with its corresponding CSV file based on the filename prefix.
     * For this to work, the files of the csv needs to mathc the texture file names.
     * @param textureInputStream a list of texture files to be uploaded
     * @param modelId the ID of the model to which the textures belong
     * @param listCsvInputStream a list of CSV files containing additional texture metadata
     * @return a list of QuickTextureEntity objects representing the uploaded textures
     * @throws ApplicationContextException if any of the input files are invalid
     */
    public List<QuickTextureEntity> uploadOtherTextures(List<InputStreamMultipartFile> textureInputStream, String modelId, List<InputStreamMultipartFile> listCsvInputStream) throws ApplicationContextException {
        List<QuickTextureEntity> otherTextureEntities = new ArrayList<>();
        for (var entry : textureInputStream) {
            if (!entry.isEmpty()) {
                InputStreamMultipartFile csv = null;
                InputStream csvStream = null;
                String prefix;
                prefix = entry.getName().substring(0, entry.getName().lastIndexOf('.'));
                for (InputStreamMultipartFile csvFile : listCsvInputStream) {
                    if (csvFile.getName().equals(prefix + ".csv")) {
                        csv = csvFile;
                        csvStream = csv.getInputStream();
                        break;
                    }
                }
                listCsvInputStream.remove(csv);

                QuickTextureEntity quickTextureEntity = uploadTexture(entry, false, modelId, csvStream);
                otherTextureEntities.add(quickTextureEntity);
            }
        }
        return otherTextureEntities;
    }

    /**
     * Retrieves a texture entity by its ID using the TextureApiClient.
     * Caches the retrieved texture entity for future use.
     * @param textureId the ID of the texture to be retrieved
     */
    private void getTexture(String textureId) {
        try {
            this.textureEntity = textureApiClient.getFileEntityById(textureId);
        } catch (Exception e) {
            log.error("Chyba při získávání textury: {}", e.getMessage(), e);
            throw new RuntimeException("Chyba při získávání textury: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves the texture image as a StreamResource by its ID.
     * If the texture entity is not already cached or if the cached entity does not match the requested ID,
     * it fetches the texture entity from the server.
     * @param textureId the ID of the texture to be retrieved
     * @return a StreamResource representing the texture image
     */
    public StreamResource getTextureImage(String textureId) {
        if (this.textureEntity == null || !Objects.equals(this.textureEntity.getId(), textureId)) {
            this.getTexture(textureId);
        }
        InputStreamMultipartFile texture = this.textureEntity.getFile();
        InputStream textureStream = texture.getInputStream();
        return new StreamResource(texture.getName(), () -> textureStream);
    }

    /**
     * Retrieves the name of the texture by its ID.
     * If the texture entity is not already cached or if the cached entity does not match the requested ID,
     * it fetches the texture entity from the server.
     * @param textureId the ID of the texture whose name is to be retrieved
     * @return the name of the texture
     */
    public String getTextureName(String textureId) {
        if (this.textureEntity == null || !Objects.equals(this.textureEntity.getId(), textureId)) {
            this.getTexture(textureId);
        }
        return this.textureEntity.getName();
    }

    /**
     * Retrieves the base64 representation of the texture file by its ID.
     * If the texture entity is not already cached or if the cached entity does not match the requested ID,
     * it fetches the texture entity from the server.
     * @param textureId the ID of the texture whose base64 representation is to be retrieved
     * @return the base64 representation of the texture file
     * @throws IOException if an I/O error occurs while reading the texture file
     */
    public String getTextureBase64(String textureId) throws IOException {
        if (this.textureEntity == null || !Objects.equals(this.textureEntity.getId(), textureId)) {
            this.getTexture(textureId);
        }
        return textureEntity.getBase64File();
    }
}

