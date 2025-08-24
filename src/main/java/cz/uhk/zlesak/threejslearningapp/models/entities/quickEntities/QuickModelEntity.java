package cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities;

import lombok.*;

import java.util.List;

/**
 * QuickModelEntity data class - holds data about a model, its main texture, and other textures.
 * This is used for quick access to model-related data in the application.
 * It includes the model file, the main texture file, and a list of other texture files.
 * @see QuickFileEntity
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Getter
@Setter
@Builder
public class QuickModelEntity extends QuickFile {
    String metadataId;
    QuickFileEntity model;
    QuickTextureEntity mainTexture;
    List<QuickTextureEntity> otherTextures;
}
