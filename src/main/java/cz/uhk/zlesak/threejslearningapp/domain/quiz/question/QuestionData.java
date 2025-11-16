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
public abstract class QuestionData {
    /**
     * Unique identifier for this question within the quiz
     */
    String questionId;

    /**
     * Question text
     */
    String questionText;

    /**
     * Type of question
     */
    QuestionTypeEnum type;

    /**
     * Points for correct answer
     */
    Integer points;
}

