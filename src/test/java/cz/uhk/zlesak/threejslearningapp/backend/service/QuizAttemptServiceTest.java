package cz.uhk.zlesak.threejslearningapp.backend.service;

import cz.uhk.zlesak.threejslearningapp.backend.BackendException;
import cz.uhk.zlesak.threejslearningapp.backend.persistence.QuizAttemptDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QuizAttemptServiceTest {

    private MongoTemplate mongoTemplate;
    private QuizAttemptService quizAttemptService;

    @BeforeEach
    void setUp() {
        mongoTemplate = Mockito.mock(MongoTemplate.class);
        CurrentUserProvider currentUserProvider = Mockito.mock(CurrentUserProvider.class);
        Mockito.when(currentUserProvider.requireUserId()).thenReturn("bart");
        quizAttemptService = new QuizAttemptService(mongoTemplate, currentUserProvider);
    }

    @Test
    void keepsAttemptsOfDifferentQuizzesApart() {
        quizAttemptService.start("quiz-1", 10);
        quizAttemptService.start("quiz-2", 5);

        ArgumentCaptor<QuizAttemptDocument> saved = ArgumentCaptor.forClass(QuizAttemptDocument.class);
        Mockito.verify(mongoTemplate, Mockito.times(2)).save(saved.capture());

        assertThat(saved.getAllValues()).extracting(QuizAttemptDocument::getId)
                .containsExactly("bart:quiz-1", "bart:quiz-2");
    }

    @Test
    void recordsTheDeadlineForATimedQuiz() {
        Instant before = Instant.now();
        quizAttemptService.start("quiz-1", 15);

        ArgumentCaptor<QuizAttemptDocument> saved = ArgumentCaptor.forClass(QuizAttemptDocument.class);
        Mockito.verify(mongoTemplate).save(saved.capture());

        assertThat(saved.getValue().isHasTimeLimit()).isTrue();
        assertThat(saved.getValue().getDeadline()).isAfterOrEqualTo(before.plus(14, ChronoUnit.MINUTES));
    }

    @Test
    void marksAQuizWithoutALimitAsUntimed() {
        quizAttemptService.start("quiz-1", 0);

        ArgumentCaptor<QuizAttemptDocument> saved = ArgumentCaptor.forClass(QuizAttemptDocument.class);
        Mockito.verify(mongoTemplate).save(saved.capture());

        assertThat(saved.getValue().isHasTimeLimit()).isFalse();
    }

    @Test
    void acceptsASubmissionThatFollowsAStart() {
        Mockito.when(mongoTemplate.findAndRemove(Mockito.any(Query.class), Mockito.eq(QuizAttemptDocument.class)))
                .thenReturn(QuizAttemptDocument.builder()
                        .id("bart:quiz-1").hasTimeLimit(true)
                        .deadline(Instant.now().plus(5, ChronoUnit.MINUTES)).build());

        quizAttemptService.consume("quiz-1");
    }

    @Test
    void rejectsASubmissionForAQuizThatWasNeverStarted() {
        Mockito.when(mongoTemplate.findAndRemove(Mockito.any(Query.class), Mockito.eq(QuizAttemptDocument.class)))
                .thenReturn(null);

        assertThatThrownBy(() -> quizAttemptService.consume("quiz-1"))
                .isInstanceOf(BackendException.Validation.class)
                .hasMessageContaining("nebyl řádně spuštěn");
    }

    @Test
    void rejectsASecondSubmissionOfTheSameAttempt() {
        Mockito.when(mongoTemplate.findAndRemove(Mockito.any(Query.class), Mockito.eq(QuizAttemptDocument.class)))
                .thenReturn(QuizAttemptDocument.builder().id("bart:quiz-1").build())
                .thenReturn(null);

        quizAttemptService.consume("quiz-1");

        assertThatThrownBy(() -> quizAttemptService.consume("quiz-1"))
                .isInstanceOf(BackendException.Validation.class);
    }

    @Test
    void rejectsASubmissionThatArrivesAfterTheTimeLimit() {
        Mockito.when(mongoTemplate.findAndRemove(Mockito.any(Query.class), Mockito.eq(QuizAttemptDocument.class)))
                .thenReturn(QuizAttemptDocument.builder()
                        .id("bart:quiz-1").hasTimeLimit(true)
                        .deadline(Instant.now().minusSeconds(1)).build());

        assertThatThrownBy(() -> quizAttemptService.consume("quiz-1"))
                .isInstanceOf(BackendException.Validation.class)
                .hasMessageContaining("Časový limit");
    }

    @Test
    void acceptsALateSubmissionWhenTheQuizHasNoTimeLimit() {
        Mockito.when(mongoTemplate.findAndRemove(Mockito.any(Query.class), Mockito.eq(QuizAttemptDocument.class)))
                .thenReturn(QuizAttemptDocument.builder()
                        .id("bart:quiz-1").hasTimeLimit(false)
                        .deadline(Instant.now().minusSeconds(3600)).build());

        quizAttemptService.consume("quiz-1");
    }
}
