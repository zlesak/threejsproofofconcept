package cz.uhk.zlesak.threejslearningapp.api;

import cz.uhk.zlesak.threejslearningapp.api.contracts.IQuizApiClient;
import cz.uhk.zlesak.threejslearningapp.backend.service.QuizAttemptService;
import cz.uhk.zlesak.threejslearningapp.backend.service.QuizBackendService;
import cz.uhk.zlesak.threejslearningapp.domain.common.FilterParameters;
import cz.uhk.zlesak.threejslearningapp.domain.common.PageResult;
import cz.uhk.zlesak.threejslearningapp.domain.quiz.QuickQuizEntity;
import cz.uhk.zlesak.threejslearningapp.domain.quiz.QuizEntity;
import cz.uhk.zlesak.threejslearningapp.domain.quiz.QuizFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Quiz access for the UI.
 */
@Component
@RequiredArgsConstructor
public class QuizApiClient implements IQuizApiClient {

    private final QuizBackendService quizBackendService;
    private final QuizAttemptService quizAttemptService;

    @Override
    public QuickQuizEntity create(QuizEntity entity) {
        return quizBackendService.create(entity);
    }

    /**
     * Reads a quiz without its answers. Teachers use {@link #readAll(String)} when they need the
     * answer key.
     */
    @Override
    public QuizEntity read(String id) {
        return quizBackendService.require(id, false);
    }

    @Override
    public QuickQuizEntity readQuick(String id) {
        return quizBackendService.require(id, false);
    }

    @Override
    public PageResult<QuickQuizEntity> readEntities(FilterParameters<QuizFilter> filterParameters) {
        return quizBackendService.list(filterParameters);
    }

    @Override
    public QuizEntity update(String id, QuizEntity entity) {
        entity.setId(id);
        return quizBackendService.update(entity);
    }

    @Override
    public boolean delete(String id) {
        quizBackendService.delete(id);
        return true;
    }

    @Override
    public QuizEntity readQuizStudent(String quizId) {
        QuizEntity quiz = quizBackendService.require(quizId, false);
        quizAttemptService.start(quizId, quiz.getTimeLimit() == null ? 0 : quiz.getTimeLimit());
        return quiz;
    }

    @Override
    public QuizEntity readAll(String quizId) {
        return quizBackendService.require(quizId, true);
    }
}
