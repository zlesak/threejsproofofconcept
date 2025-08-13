package cz.uhk.zlesak.threejslearningapp.models.records.parsers;

import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickFileEntity;
import cz.uhk.zlesak.threejslearningapp.models.records.TextureListingForComboBoxRecord;

import java.util.List;

public abstract class TextureListingDataParser
{
    public static List<TextureListingForComboBoxRecord> textureListingForComboBoxDataParser(List<QuickFileEntity> textures) {
        return textures.stream()
                .map(texture -> new TextureListingForComboBoxRecord(
                        texture.getId(),
                        texture.getName()))
                .toList();
    }
}
