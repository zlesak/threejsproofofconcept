package cz.uhk.zlesak.threejslearningapp.domain.quiz;

import cz.uhk.zlesak.threejslearningapp.domain.common.Entity;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Getter
@Setter
@Builder
public class QuickQuizEntity extends Entity {
    String Description;
}
