package cz.uhk.zlesak.threejslearningapp.domain.quiz;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * QuizEntity Class - Represents a quiz entity with questions, answers, and time limit.
 */
@Data
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class QuizEntity extends QuickQuizEntity {
    String questionsJson;
    String answersJson;
    Integer timeLimit;
}
