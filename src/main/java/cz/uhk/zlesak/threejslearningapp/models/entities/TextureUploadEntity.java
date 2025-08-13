package cz.uhk.zlesak.threejslearningapp.models.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TextureUploadEntity implements IEntity {
    String modelId;
    Boolean isPrimary;
    TextureEntity texture;
}
