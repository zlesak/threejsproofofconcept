package cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities;

import lombok.*;

@Data
@AllArgsConstructor
@Getter
@Setter
@Builder
public class QuickTextureEntity implements IQuickFile{
    String textureFileId;
    String name;
    String csvContent;
}
