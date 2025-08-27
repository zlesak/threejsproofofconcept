package cz.uhk.zlesak.threejslearningapp.utils;

import cz.uhk.zlesak.threejslearningapp.controllers.TextureController;
import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickTextureEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TextureMapHelper is a utility class that provides methods for creating maps of texture data.
 * It includes methods for generating CSV maps and other texture maps from a list of QuickTextureEntity objects.
 */
public abstract class TextureMapHelper {

    /**
     * Creates a map of texture IDs to their corresponding CSV content from a list of QuickTextureEntity objects.
     * Only includes entries where the CSV content is not empty.
     * @param quickTextureEntityList the list of QuickTextureEntity objects
     * @return a map where the key is the texture ID and the value is the CSV content
     */
    public static Map<String, String> createCsvMap(List<QuickTextureEntity> quickTextureEntityList) {
        Map<String, String> textureIdCsvMap = new HashMap<>();
        for(QuickTextureEntity textureEntity : quickTextureEntityList) {
            String textureId = textureEntity.getTextureFileId();
            if (textureEntity.getCsvContent() != null && !textureEntity.getCsvContent().isEmpty()) {
                textureIdCsvMap.put(textureId, textureEntity.getCsvContent());
            }
        }
        return textureIdCsvMap;
    }

    /**
     * Creates a map of texture IDs to their corresponding base64-encoded texture data URIs from a list of QuickTextureEntity objects.
     * Uses the TextureController to retrieve the base64-encoded texture data.
     * @see TextureController#getTextureBase64(String)
     * @param quickTextureEntityList the list of QuickTextureEntity objects
     * @param textureController the TextureController used to retrieve texture data
     * @return a map where the key is the texture ID and the value is the base64-encoded texture data URI
     * @throws IOException if an I/O error occurs while retrieving texture data
     */
    public static Map<String, String> otherTexturesMap(List<QuickTextureEntity> quickTextureEntityList, TextureController textureController) throws IOException {
        Map<String, String> otherTextures = new HashMap<>();
        for (QuickTextureEntity texture : quickTextureEntityList) {
            String otherTextureBase64 = textureController.getTextureBase64(texture.getTextureFileId());
            otherTextures.put(texture.getTextureFileId(), "data:application/octet-stream;base64," + otherTextureBase64);
        }
        return otherTextures;
    }
}
