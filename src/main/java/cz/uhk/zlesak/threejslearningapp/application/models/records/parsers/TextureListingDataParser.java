package cz.uhk.zlesak.threejslearningapp.application.models.records.parsers;

import cz.uhk.zlesak.threejslearningapp.application.models.entities.quickEntities.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.application.models.entities.quickEntities.QuickTextureEntity;
import cz.uhk.zlesak.threejslearningapp.application.models.records.TextureListingForSelectRecord;

import java.util.List;
import java.util.Map;

/**
 * TextureListingDataParser class - utility class for parsing texture data into records for select.
 * This class provides a method to convert a list of QuickFileEntity objects into a list of TextureListingForSelectRecord objects.
 * Used for populating select with texture listings in the application.
 */
public abstract class TextureListingDataParser {
    public static List<TextureListingForSelectRecord> textureListingForSelectListDataParser(List<QuickTextureEntity> textures) {
        if (textures == null || textures.isEmpty()) {
            return List.of();
        }
        return textures.stream()
                .map(texture -> new TextureListingForSelectRecord(
                        texture.getTextureFileId(),
                        "",
                        texture.getName()))
                .toList();
    }

    public static List<TextureListingForSelectRecord> textureListingForSelectDataParser(Map<String, QuickModelEntity> models) {
        if (models == null || models.isEmpty()) {
            return List.of();
        }

        return models.values().stream()
                .flatMap(model -> model.getOtherTextures().stream()
                        .map(texture -> new TextureListingForSelectRecord(
                                texture.getTextureFileId(),
                                model.getModel().getId(),
                                texture.getName())))
                .toList();
    }
}
