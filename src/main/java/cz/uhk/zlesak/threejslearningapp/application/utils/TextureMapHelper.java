package cz.uhk.zlesak.threejslearningapp.application.utils;

import cz.uhk.zlesak.threejslearningapp.application.controllers.TextureController;
import cz.uhk.zlesak.threejslearningapp.application.models.entities.quickEntities.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.application.models.entities.quickEntities.QuickTextureEntity;

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
            if(textureEntity != null) {
                String textureId = textureEntity.getTextureFileId();
                if (textureEntity.getCsvContent() != null && !textureEntity.getCsvContent().isEmpty()) {
                    textureIdCsvMap.put(textureId, textureEntity.getCsvContent());
                }
            }
        }
        return textureIdCsvMap;
    }
    public static Map<String, String> createCsvMap(Map<String, QuickModelEntity> quickModelEntityMap) {
        Map<String, String> textureIdCsvMap = new HashMap<>();
        for (QuickModelEntity modelEntity : quickModelEntityMap.values()) {
            if (modelEntity != null && modelEntity.getOtherTextures() != null) {
                for (QuickTextureEntity textureEntity : modelEntity.getOtherTextures()) {
                    if (textureEntity != null) {
                        String textureId = textureEntity.getTextureFileId();
                        if (textureEntity.getCsvContent() != null && !textureEntity.getCsvContent().isEmpty()) {
                            textureIdCsvMap.put(textureId, textureEntity.getCsvContent());
                        }
                    }
                }
            }
        }
        return textureIdCsvMap;
    }

    /**
     * Creates a map of texture file IDs to their corresponding texture stream endpoint URLs.
     * Uses the provided TextureController to generate the URLs.
     * @param quickTextureEntityList the list of QuickTextureEntity objects
     * @param textureController the TextureController used to get the texture stream endpoint URLs
     * @return a map where the key is the texture file ID and the value is the texture stream endpoint URL
     * @throws IOException if an I/O error occurs while retrieving the texture stream endpoint URL
     */
    public static Map<String, String> otherTexturesMap(List<QuickTextureEntity> quickTextureEntityList, TextureController textureController) throws IOException {
        Map<String, String> otherTextures = new HashMap<>();
        for (QuickTextureEntity texture : quickTextureEntityList) {
            String textureUrl = textureController.getTextureStreamEndpointUrl(texture.getTextureFileId());
            otherTextures.put(texture.getTextureFileId(), textureUrl);
        }
        return otherTextures;
    }
}
