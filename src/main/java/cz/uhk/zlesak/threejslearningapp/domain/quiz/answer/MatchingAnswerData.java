package cz.uhk.zlesak.threejslearningapp.domain.quiz.answer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * Answer data for matching question.
 * Contains mapping of left items to right items.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MatchingAnswerData extends AnswerData {
    /**
     * Correct matches: key = left item index, value = right item index
     */
    Map<Integer, Integer> correctMatches;
}

