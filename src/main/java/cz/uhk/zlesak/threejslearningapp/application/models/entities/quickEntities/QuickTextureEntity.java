package cz.uhk.zlesak.threejslearningapp.application.models.entities.quickEntities;

import lombok.*;

/**
 * QuickTextureEntity is a data class that extends QuickFile to represent a texture entity with additional properties.
 * It includes fields for texture file ID, name, and CSV content.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Getter
@Setter
@Builder
public class QuickTextureEntity extends QuickFile {
    String textureFileId;
    String name;
    String csvContent;
}
