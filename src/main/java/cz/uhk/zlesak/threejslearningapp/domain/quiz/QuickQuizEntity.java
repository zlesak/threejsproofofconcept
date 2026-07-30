package cz.uhk.zlesak.threejslearningapp.domain.quiz;

import cz.uhk.zlesak.threejslearningapp.domain.common.AbstractEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * A quiz as it appears in listings: identity, the chapter it belongs to and its time limit.
 * {@link QuizEntity} adds the questions and answers.
 *
 * @see AbstractEntity for inherited properties.
 */
@Data
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class QuickQuizEntity extends AbstractEntity {

    String chapterId;

    /** Minutes available for the quiz; zero means no limit. */
    Integer timeLimit;
}
