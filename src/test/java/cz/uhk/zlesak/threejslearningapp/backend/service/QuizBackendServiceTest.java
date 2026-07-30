package cz.uhk.zlesak.threejslearningapp.backend.service;

import cz.uhk.zlesak.threejslearningapp.backend.BackendException;
import cz.uhk.zlesak.threejslearningapp.common.logging.AuditLog;
import cz.uhk.zlesak.threejslearningapp.backend.persistence.ListingQueries;
import cz.uhk.zlesak.threejslearningapp.backend.persistence.MongoCollections;
import cz.uhk.zlesak.threejslearningapp.backend.persistence.QuizRepository;
import cz.uhk.zlesak.threejslearningapp.domain.quiz.QuizEntity;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonInt64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Covers the two properties of quiz reads that are not visible from the outside: which read hands
 * back the answer key, and what happens to a quiz when its chapter is removed.
 */
class QuizBackendServiceTest {

    private MongoTemplate mongoTemplate;
    private QuizBackendService quizBackendService;

    @BeforeEach
    void setUp() {
        mongoTemplate = Mockito.mock(MongoTemplate.class);
        quizBackendService = new QuizBackendService(
                Mockito.mock(QuizRepository.class),
                mongoTemplate,
                Mockito.mock(QuizAuthoringValidator.class),
                Mockito.mock(ChapterBackendService.class),
                Mockito.mock(ListingQueries.class),
                Mockito.mock(CurrentUserProvider.class),
                Mockito.mock(AuditLog.class));

        Mockito.when(mongoTemplate.findOne(Mockito.any(), Mockito.eq(QuizEntity.class), Mockito.anyString()))
                .thenReturn(QuizEntity.builder().name("Kvíz").build());
    }

    @Test
    void theStudentReadLeavesTheAnswerKeyOut() {
        quizBackendService.require("quiz-1");

        assertThat(capturedQuery().getFieldsObject().toJson()).contains("\"answers\": 0");
    }

    @Test
    void theAuthoringReadKeepsTheAnswerKey() {
        quizBackendService.requireWithAnswers("quiz-1");

        assertThat(capturedQuery().getFieldsObject().isEmpty()).isTrue();
    }

    @Test
    void rejectsABlankQuizId() {
        assertThatThrownBy(() -> quizBackendService.require(" "))
                .isInstanceOf(BackendException.Validation.class);
    }

    @Test
    void reportsAMissingQuizAsNotFound() {
        Mockito.when(mongoTemplate.findOne(Mockito.any(), Mockito.eq(QuizEntity.class), Mockito.anyString()))
                .thenReturn(null);

        assertThatThrownBy(() -> quizBackendService.require("gone"))
                .isInstanceOf(BackendException.NotFound.class);
    }

    @Test
    void detachingFromAChapterClearsTheReferenceOnEveryQuizOfThatChapter() {
        Mockito.when(mongoTemplate.updateMulti(Mockito.any(), Mockito.any(Update.class),
                        Mockito.eq(QuizEntity.class), Mockito.anyString()))
                .thenReturn(UpdateResult.acknowledged(2, 2L, new BsonInt64(0)));

        assertThat(quizBackendService.detachFromChapter("ch-1")).isEqualTo(2);

        ArgumentCaptor<Query> query = ArgumentCaptor.forClass(Query.class);
        ArgumentCaptor<Update> update = ArgumentCaptor.forClass(Update.class);
        Mockito.verify(mongoTemplate).updateMulti(query.capture(), update.capture(),
                Mockito.eq(QuizEntity.class), Mockito.eq(MongoCollections.QUIZ));

        assertThat(query.getValue().getQueryObject().toJson()).contains("ch-1");
        assertThat(update.getValue().getUpdateObject().toJson()).contains("$unset").contains("chapterId");
    }

    @Test
    void detachingIgnoresABlankChapterId() {
        assertThat(quizBackendService.detachFromChapter(" ")).isZero();
        Mockito.verifyNoInteractions(mongoTemplate);
    }

    private Query capturedQuery() {
        ArgumentCaptor<Query> captor = ArgumentCaptor.forClass(Query.class);
        Mockito.verify(mongoTemplate).findOne(captor.capture(), Mockito.eq(QuizEntity.class),
                Mockito.eq(MongoCollections.QUIZ));
        return captor.getValue();
    }
}
