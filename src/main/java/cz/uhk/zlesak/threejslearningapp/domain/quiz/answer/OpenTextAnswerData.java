package cz.uhk.zlesak.threejslearningapp.domain.quiz.answer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Answer data for open text question.
 * Contains list of acceptable answers and exact match option boolean.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OpenTextAnswerData extends AnswerData {
    /**
     * List of acceptable answers
     */
    List<String> acceptableAnswers;

    /**
     * Whether to perform exact match or allow partial match
     */
    Boolean exactMatch;
}

