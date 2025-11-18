package cz.uhk.zlesak.threejslearningapp.domain.quiz;

import cz.uhk.zlesak.threejslearningapp.domain.common.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Quiz entity data class - holds data about quiz on frontend side or when communicating with backend API endpoints.
 * Questions and answers are stored separately to prevent cheating - only questions are sent to frontend,
 * answers are validated on backend.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
public class QuizEntity extends Entity {
    String Description;
    String QuestionsJson;
    String AnswersJson;
    Integer TimeLimit;
    String ChapterId;
}
