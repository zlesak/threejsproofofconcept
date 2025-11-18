package cz.uhk.zlesak.threejslearningapp.api.contracts;

import cz.uhk.zlesak.threejslearningapp.domain.common.PageResult;
import cz.uhk.zlesak.threejslearningapp.domain.common.SortDirectionEnum;
import cz.uhk.zlesak.threejslearningapp.domain.quiz.QuizEntity;
import cz.uhk.zlesak.threejslearningapp.domain.quiz.QuizSubmissionRequest;
import cz.uhk.zlesak.threejslearningapp.domain.quiz.QuizValidationResult;

import java.util.List;

/**
 * Interface for Quiz API Client
 * Defines CRUD operations and listing for quizzes.
 */
public interface IQuizApiClient extends IApiClient {
    QuizEntity createQuiz(QuizEntity quizEntity) throws Exception;
    void updateQuiz(String quizId, QuizEntity quizEntity) throws Exception;
    void deleteQuiz(String quizId) throws Exception;
    QuizEntity getQuizById(String quizId) throws Exception;
    PageResult<QuizEntity> getQuizzes(int page, int limit, String orderBy, SortDirectionEnum sortDirection) throws Exception;
    List<String> getQuizzesByAuthor(String authorId) throws Exception;
    QuizValidationResult validateAnswers(QuizSubmissionRequest submissionRequest) throws Exception;
}
