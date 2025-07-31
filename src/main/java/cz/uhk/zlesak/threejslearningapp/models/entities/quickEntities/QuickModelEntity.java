package cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@Getter
@Setter
@Builder

public class QuickModelEntity {
    QuickFileEntity model;
    QuickFileEntity mainTexture;
    List<QuickFileEntity> otherTextures;
}
