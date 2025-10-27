package cz.uhk.zlesak.threejslearningapp.application.models.records;

/**
 * Record representing a texture area for a select component.
 * This record is used to store the name of the area and its corresponding hex color.
 *
 * @param textureId The identifier of the texture.
 * @param hexColor The hex color associated with the texture area.
 * @param areaName The name of the texture area.
 */
public record TextureAreaForSelectRecord(String textureId, String hexColor, String areaName, String modelId) {
}
