package cz.uhk.zlesak.threejslearningapp.domain.quiz.question;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Open text question - user provides text answer.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OpenTextQuestionData extends QuestionData {
    /**
     * Placeholder text for the input field
     */
    String placeholder;

    /**
     * Maximum length of the answer
     */
    Integer maxLength;
}

