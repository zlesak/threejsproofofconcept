package cz.uhk.zlesak.threejslearningapp.domain.quiz.answer;

import cz.uhk.zlesak.threejslearningapp.domain.quiz.QuestionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Base class for all answer types.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class AnswerData {
    /**
     * Question ID this answer belongs to
     */
    String questionId;

    /**
     * Type of answer
     */
    QuestionTypeEnum type;
}

