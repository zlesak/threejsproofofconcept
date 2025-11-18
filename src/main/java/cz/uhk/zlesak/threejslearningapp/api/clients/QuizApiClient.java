package cz.uhk.zlesak.threejslearningapp.api.clients;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.uhk.zlesak.threejslearningapp.api.contracts.IQuizApiClient;
import cz.uhk.zlesak.threejslearningapp.domain.common.PageResult;
import cz.uhk.zlesak.threejslearningapp.domain.quiz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * QuizApiClient provides connection to the backend service for managing quizzes.
 * It implements the IQuizApiClient interface and provides methods for creating, updating, deleting, and retrieving quizzes.
 * It uses RestTemplate for making HTTP requests to the backend service.
 */
@Component
public class QuizApiClient extends AbstractApiClient<QuizEntity, QuickQuizEntity, QuizFilter> implements IQuizApiClient {

    @Autowired
    public QuizApiClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        super(restTemplate, objectMapper, "quiz/");
    }

//region CRUD operations from IApiClient

    /**
     * Creates a new quiz.
     *
     * @param quizEntity Quiz entity to create
     * @return Created quiz entity
     * @throws Exception if API call fails
     */
    @Override
    public QuizEntity create(QuizEntity quizEntity) throws Exception {
        return sendPostRequest(baseUrl + "create", quizEntity, QuizEntity.class, "Chyba při vytváření kvízu", null, null);
    }

    /**
     * Gets a quiz by ID.
     *
     * @param quizId ID of the quiz to retrieve
     * @return Quiz entity
     * @throws Exception if API call fails
     */
    @Override
    public QuizEntity read(String quizId) throws Exception {
        return sendGetRequest(baseUrl + quizId, QuizEntity.class, "Chyba při získávání kvízu dle ID", quizId);
    }

    /**
     * Gets paginated list of quizzes.
     *
     * @param pageRequest PageRequest object containing pagination info
     * @return PageResult of QuickQuizEntity
     * @throws Exception if API call fails
     */
    @Override
    public PageResult<QuickQuizEntity> readEntities(PageRequest pageRequest) throws Exception {
        return readEntitiesFiltered(pageRequest, null);
    }

    /**
     * Gets paginated and filtered list of quizzes.
     *
     * @param pageRequest PageRequest object containing pagination info
     * @param filter      QuizFilter object containing filter criteria
     * @return PageResult of QuickQuizEntity
     * @throws Exception if API call fails
     */
    @Override
    public PageResult<QuickQuizEntity> readEntitiesFiltered(PageRequest pageRequest, QuizFilter filter) throws Exception {
        String url = pageRequestToQueryParams(pageRequest, null) + filterToQueryParams(filter);
        ResponseEntity<String> response = sendGetRequestRaw(url, String.class, "Chyba při získávání seznamu kvízů", null, true);
        JavaType type = objectMapper.getTypeFactory().constructParametricType(PageResult.class, QuickQuizEntity.class);
        return parseResponse(response, type, "Chyba při získávání seznamu kvízů", null);
    }

    /**
     * Updates an existing quiz.
     *
     * @param quizId     ID of the quiz to update
     * @param quizEntity Updated quiz entity
     * @return Updated quiz entity
     * @throws Exception if API call fails
     */
    @Override
    public QuizEntity update(String quizId, QuizEntity quizEntity) throws Exception {
        return sendPutRequest(baseUrl + "update/" + quizId, quizEntity, QuizEntity.class, "Chyba při aktualizaci kvízu", quizId);
    }

    /**
     * Deletes a quiz by ID.
     *
     * @param quizId ID of the quiz to delete
     * @return boolean indicating success
     * @throws Exception if API call fails
     */
    @Override
    public boolean delete(String quizId) throws Exception {
        sendDeleteRequest(baseUrl + "delete/" + quizId, "Chyba při mazání kvízu", quizId);
        return true;
    }
//endregion

    /**
     * Validates user's quiz answers.
     * Sends answers to backend for validation without exposing correct answers to frontend.
     *
     * @param submissionRequest User's submitted answers
     * @return Validation results with score and per-question correctness
     * @throws Exception if API call fails
     */
    @Override
    public QuizValidationResult validateAnswers(QuizSubmissionRequest submissionRequest) throws Exception {
        return sendPostRequest(baseUrl + "validate", submissionRequest, QuizValidationResult.class, "Chyba při validaci odpovědí kvízu", submissionRequest.getQuizId(), null);
    }

    /**
     * Gets a quiz without answers for student.
     *
     * @param quizId Quiz ID
     * @return Quiz entity without answers
     * @throws Exception if API call fails
     */
    @Override
    public QuizEntity readQuizStudent(String quizId) throws Exception {
        QuizEntity quiz = read(quizId);
        quiz.setAnswersJson(null);
        return quiz;

    }
}
