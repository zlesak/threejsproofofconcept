package cz.uhk.zlesak.threejslearningapp.domain.quiz.question;

import cz.uhk.zlesak.threejslearningapp.domain.quiz.QuestionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Base class for all question types.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class AbstractQuestionData {
    String questionId;
    String questionText;
    QuestionTypeEnum type;
    Integer points;
}

