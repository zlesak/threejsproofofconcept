package cz.uhk.zlesak.threejslearningapp.models.records;

/**
 * Record representing a texture area for a combo box.
 * This record is used to store the name of the area and its corresponding hex color.
 *
 * @param areaName The name of the texture area.
 * @param hexColor The hex color associated with the texture area.
 */
public record TextureAreaForComboBoxRecord(String areaName, String hexColor) {
}
