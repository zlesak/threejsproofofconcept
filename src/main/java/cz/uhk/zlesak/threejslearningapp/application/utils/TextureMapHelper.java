package cz.uhk.zlesak.threejslearningapp.application.utils;

import cz.uhk.zlesak.threejslearningapp.application.controllers.TextureController;
import cz.uhk.zlesak.threejslearningapp.application.models.entities.quickEntities.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.application.models.entities.quickEntities.QuickTextureEntity;
import cz.uhk.zlesak.threejslearningapp.application.models.records.TextureAreaForSelectRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TextureMapHelper is a utility class that provides methods for creating maps of texture data.
 */
public abstract class TextureMapHelper {

    /**
     * Creates a list of TextureAreaForSelectRecord from the provided map of QuickModelEntity objects.
     * Removes duplicate texture areas - if the same model is used for multiple sub-chapters, its areas appear only once.
     *
     * @param quickModelEntityMap the map of QuickModelEntity objects
     * @return a list of TextureAreaForSelectRecord objects (without duplicates)
     * @throws IllegalArgumentException if the CSV content format is invalid
     */
    public static List<TextureAreaForSelectRecord> createTextureAreaForSelectRecordList(Map<String, QuickModelEntity> quickModelEntityMap) {
        List<TextureAreaForSelectRecord> records = new ArrayList<>();

        Map<String, QuickModelEntity> uniqueModels = new java.util.LinkedHashMap<>();
        quickModelEntityMap.values().forEach(model -> {
            if (model != null) {
                uniqueModels.putIfAbsent(model.getModel().getId(), model);
            }
        });

        for (QuickModelEntity modelEntity : uniqueModels.values()) {
            if (modelEntity.getOtherTextures() != null) {
                for (QuickTextureEntity textureEntity : modelEntity.getAllTextures()) {
                    if (textureEntity != null) {
                        String textureId = textureEntity.getTextureFileId();
                        if (textureEntity.getCsvContent() != null && !textureEntity.getCsvContent().isEmpty()) {
                            String[] rows = textureEntity.getCsvContent().split("\\r?\\n|\\r");
                            for (String row : rows) {
                                row = row.trim();
                                if (row.isEmpty()) continue;
                                String[] parts = row.split(";");
                                if (parts.length == 2) {
                                    records.add(new TextureAreaForSelectRecord(textureId, parts[0].trim(), parts[1].trim(), modelEntity.getModel().getId()));
                                } else {
                                    throw new IllegalArgumentException("Invalid CSV format for TextureAreaForComboBoxRecord: " + row);
                                }
                            }
                        }
                    }
                }
            }
        }
        return records;
    }

    /**
     * Creates a map of texture file IDs to their corresponding texture stream endpoint URLs.
     * Uses the provided TextureController to generate the URLs.
     *
     * @param quickTextureEntityList the list of QuickTextureEntity objects
     * @param textureController      the TextureController used to get the texture stream endpoint URLs
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
