package cz.uhk.zlesak.threejslearningapp.application.models.records;

/**
 * Record class that represents a texture listing for a select item.
 * Used for hte selection of the applied texture in the ThreeJS renderer.
 *
 * @param id for the texture identifier, typically a unique identifier for the texture area.
 * @param textureName for the name of the texture area, which is displayed in the combo box.
 */
public record TextureListingForSelectRecord(String id, String textureName) {
}
