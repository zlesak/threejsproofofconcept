package cz.uhk.zlesak.threejslearningapp.application.models.records.parsers;

import cz.uhk.zlesak.threejslearningapp.application.models.entities.quickEntities.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.application.models.records.TextureListingForSelectRecord;

import java.util.List;
import java.util.Map;

/**
 * TextureListingDataParser class - utility class for parsing texture data into records for select.
 * This class provides a method to convert a list of QuickFileEntity objects into a list of TextureListingForSelectRecord objects.
 * Used for populating select with texture listings in the application.
 */
public abstract class TextureListingDataParser {
    /**
     * Parses texture listing data from a map of QuickModelEntity objects.
     *
     * @param models      Map of model IDs to QuickModelEntity objects.
     * @param allTextures Flag indicating whether to include all textures or only other textures.
     * @return List of TextureListingForSelectRecord objects.
     */
    public static List<TextureListingForSelectRecord> textureListingForSelectDataParser(Map<String, QuickModelEntity> models, boolean allTextures) {
        if (models == null || models.isEmpty()) {
            return List.of();
        }
        if (allTextures) {
            return models.values().stream()
                    .flatMap(model -> model.getAllTextures().stream()
                            .map(texture -> new TextureListingForSelectRecord(
                                    texture.getTextureFileId(),
                                    model.getModel().getId(),
                                    texture.getName())))
                    .toList();
        } else {
            return models.values().stream()
                    .flatMap(model -> model.getOtherTextures().stream()
                            .map(texture -> new TextureListingForSelectRecord(
                                    texture.getTextureFileId(),
                                    model.getModel().getId(),
                                    texture.getName())))
                    .toList();
        }

    }
}
