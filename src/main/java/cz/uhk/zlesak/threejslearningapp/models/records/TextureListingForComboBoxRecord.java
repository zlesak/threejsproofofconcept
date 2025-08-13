package cz.uhk.zlesak.threejslearningapp.models.records;

/**
 * Record class that represents a texture listing for a combo box.
 * Used for hte selection of the applied texture in the ThreeJS renderer.
 *
 * @param id for the texture identifier, typically a unique identifier for the texture area.
 * @param textureName for the name of the texture area, which is displayed in the combo box.
 */
public record TextureListingForComboBoxRecord(String id, String textureName) {
}
