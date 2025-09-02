package cz.uhk.zlesak.threejslearningapp.application.models.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * TextureUploadEntity data class - holds data about a texture upload for a model.
 * This is used to represent the relationship the texture has to the model it is beeing uploaded to.
 * isPrimary marks whether the texture is the primary texture for the model.
 */
@Data
@AllArgsConstructor
@Builder
public class TextureUploadEntity implements IEntity {
    String modelId;
    Boolean isPrimary;
    TextureEntity texture;
}
