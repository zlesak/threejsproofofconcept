package cz.uhk.zlesak.threejslearningapp.api.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.uhk.zlesak.threejslearningapp.api.contracts.IApiClient;
import cz.uhk.zlesak.threejslearningapp.api.contracts.IQuizApiClient;
import cz.uhk.zlesak.threejslearningapp.domain.common.PageResult;
import cz.uhk.zlesak.threejslearningapp.domain.common.SortDirectionEnum;
import cz.uhk.zlesak.threejslearningapp.domain.quiz.QuizEntity;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * QuizApiClient provides connection to the backend service for managing quizzes.
 */
@Component
public class QuizApiClient implements IQuizApiClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    @Autowired
    public QuizApiClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.baseUrl = IApiClient.getBaseUrl() + "quiz/";
    }

    @Override
    public QuizEntity createQuiz(QuizEntity quizEntity) throws Exception {
        throw new NotImplementedException("Create quiz method is not implemented yet.");
    }

    @Override
    public void updateQuiz(String quizId, QuizEntity quizEntity) throws Exception {
        throw new NotImplementedException("Update quiz method is not implemented yet.");
    }

    @Override
    public void deleteQuiz(String quizId) throws Exception {
        throw new NotImplementedException("Delete quiz method is not implemented yet.");
    }

    @Override
    public QuizEntity getQuizById(String quizId) throws Exception {
        throw new NotImplementedException("Create quiz method is not implemented yet.");

    }

    @Override
    public PageResult<QuizEntity> getQuizzes(int page, int limit, String orderBy, SortDirectionEnum sortDirection) throws Exception {
        throw new NotImplementedException("Create quiz method is not implemented yet.");
    }

    @Override
    public List<String> getQuizzesByAuthor(String authorId) throws Exception {
        throw new NotImplementedException("Get quizzes by author method is not implemented yet.");
    }
}
