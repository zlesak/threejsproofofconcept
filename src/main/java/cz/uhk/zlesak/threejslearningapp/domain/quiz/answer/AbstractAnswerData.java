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
public abstract class AbstractAnswerData {
    String questionId;
    QuestionTypeEnum type;
}

