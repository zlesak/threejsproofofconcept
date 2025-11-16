package cz.uhk.zlesak.threejslearningapp.domain.quiz.question;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Matching question - user matches items from left list to items in right list.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MatchingQuestionData extends QuestionData {
    /**
     * Left column items (keys)
     */
    List<String> leftItems;

    /**
     * Right column items (values to match)
     */
    List<String> rightItems;
}

