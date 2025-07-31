package cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder

public class QuickFileEntity {
    String id;
    String name;
}
