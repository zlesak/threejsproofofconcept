package cz.uhk.zlesak.threejslearningapp.models.entities;

import cz.uhk.zlesak.threejslearningapp.models.IEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TextureUploadEntity implements IEntity {
    String targetFileId;
    Boolean isPrimary;
    TextureEntity texture;

}
