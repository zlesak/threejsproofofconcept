package cz.uhk.zlesak.threejslearningapp.models.records.parsers;

import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickTextureEntity;
import cz.uhk.zlesak.threejslearningapp.models.records.TextureListingForSelectRecord;

import java.util.List;

/**
 * TextureListingDataParser class - utility class for parsing texture data into records for select.
 * This class provides a method to convert a list of QuickFileEntity objects into a list of TextureListingForSelectRecord objects.
 * Used for populating select with texture listings in the application.
 */
public abstract class TextureListingDataParser
{
    public static List<TextureListingForSelectRecord> textureListingForSelectDataParser(List<QuickTextureEntity> textures) {
        if(textures == null || textures.isEmpty()) {
            return List.of();
        }
        return textures.stream()
                .map(texture -> new TextureListingForSelectRecord(
                        texture.getTextureFileId(),
                        texture.getName()))
                .toList();
    }
}
