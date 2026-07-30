package cz.uhk.zlesak.threejslearningapp.backend.service;

import cz.uhk.zlesak.threejslearningapp.backend.BackendException;
import cz.uhk.zlesak.threejslearningapp.backend.persistence.QuizAttemptDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Tracks in-flight quiz attempts so a submission can be tied to a start and checked against the
 * time limit. Attempts are keyed per user and quiz, so starting one quiz never disturbs another,
 * and the marker is removed atomically on submission, which is what stops a single attempt from
 * being graded twice.
 */
@Service
@RequiredArgsConstructor
public class QuizAttemptService {

    private final MongoTemplate mongoTemplate;
    private final CurrentUserProvider currentUserProvider;

    /**
     * Records that the current user has started a quiz.
     *
     * @param quizId    id of the quiz.
     * @param timeLimit minutes allowed; zero or less means no limit.
     */
    public void start(String quizId, int timeLimit) {
        String userId = currentUserProvider.requireUserId();
        Instant now = Instant.now();

        mongoTemplate.save(QuizAttemptDocument.builder()
                .id(QuizAttemptDocument.key(userId, quizId))
                .userId(userId)
                .quizId(quizId)
                .hasTimeLimit(timeLimit > 0)
                .deadline(now.plus(Math.max(timeLimit, 0), ChronoUnit.MINUTES))
                .startedAt(now)
                .build());
    }

    /**
     * Consumes the attempt marker for a quiz.
     *
     * @param quizId id of the quiz being submitted.
     * @throws BackendException.Validation when the quiz was never started, was already submitted,
     *                                     or the time limit has passed.
     */
    public void consume(String quizId) {
        String userId = currentUserProvider.requireUserId();
        QuizAttemptDocument attempt = mongoTemplate.findAndRemove(
                new Query(Criteria.where("_id").is(QuizAttemptDocument.key(userId, quizId))),
                QuizAttemptDocument.class);

        if (attempt == null) {
            throw new BackendException.Validation("Kvíz nebyl řádně spuštěn, nelze jej odevzdat.");
        }
        if (attempt.isHasTimeLimit() && attempt.getDeadline() != null && attempt.getDeadline().isBefore(Instant.now())) {
            throw new BackendException.Validation("Časový limit kvízu vypršel.");
        }
    }
}
