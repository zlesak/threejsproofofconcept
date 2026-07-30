package cz.uhk.zlesak.threejslearningapp.backend.service;

import cz.uhk.zlesak.threejslearningapp.backend.BackendException;
import cz.uhk.zlesak.threejslearningapp.backend.persistence.ListingQueries;
import cz.uhk.zlesak.threejslearningapp.backend.persistence.MongoCollections;
import cz.uhk.zlesak.threejslearningapp.backend.persistence.QuizRepository;
import cz.uhk.zlesak.threejslearningapp.domain.common.FilterParameters;
import cz.uhk.zlesak.threejslearningapp.domain.common.PageResult;
import cz.uhk.zlesak.threejslearningapp.domain.quiz.QuickQuizEntity;
import cz.uhk.zlesak.threejslearningapp.domain.quiz.QuizEntity;
import cz.uhk.zlesak.threejslearningapp.domain.quiz.QuizFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Owns quizzes. The correct answers are left out unless the caller is allowed to see them, so a
 * student loading a quiz never receives the answer key.
 */
@Service
@RequiredArgsConstructor
public class QuizBackendService {

    private final QuizRepository quizRepository;
    private final MongoTemplate mongoTemplate;
    private final QuizAuthoringValidator authoringValidator;
    private final ChapterBackendService chapterBackendService;
    private final ListingQueries listingQueries;
    private final CurrentUserProvider currentUserProvider;

    /**
     * Stores a new quiz.
     *
     * @param quiz quiz to store.
     * @return the stored quiz.
     */
    @PreAuthorize("hasRole('CREATE_QUIZ')")
    public QuizEntity create(QuizEntity quiz) {
        validate(quiz);
        quiz.setId(null);
        quiz.setCreatorId(currentUserProvider.requireUserId());
        Instant now = Instant.now();
        quiz.setCreated(quiz.getCreated() != null ? quiz.getCreated() : now);
        quiz.setUpdated(now);
        return quizRepository.save(quiz);
    }

    /**
     * Updates an existing quiz, keeping its original author and creation time.
     *
     * @param quiz quiz carrying the new content; its id must be set.
     * @return the stored quiz.
     */
    @PreAuthorize("hasRole('CREATE_QUIZ')")
    public QuizEntity update(QuizEntity quiz) {
        if (quiz.getId() == null || quiz.getId().isBlank()) {
            throw new BackendException.Validation("Kvíz nelze aktualizovat bez ID.");
        }
        QuizEntity existing = require(quiz.getId(), true);
        validate(quiz);

        quiz.setCreatorId(existing.getCreatorId() != null ? existing.getCreatorId() : currentUserProvider.requireUserId());
        quiz.setCreated(existing.getCreated());
        quiz.setUpdated(Instant.now());
        return quizRepository.save(quiz);
    }

    /**
     * @param quizId      id of the quiz.
     * @param showAnswers whether the correct answers should be included.
     * @return the quiz.
     * @throws BackendException.NotFound when no such quiz exists.
     */
    public QuizEntity require(String quizId, boolean showAnswers) {
        if (quizId == null || quizId.isBlank()) {
            throw new BackendException.Validation("ID kvízu nesmí být prázdné.");
        }

        Query query = new Query(Criteria.where("_id").is(quizId));
        if (!showAnswers) {
            query.fields().exclude("answers");
        }

        QuizEntity quiz = mongoTemplate.findOne(query, QuizEntity.class, MongoCollections.QUIZ);
        if (quiz == null) {
            throw new BackendException.NotFound("Kvíz s ID " + quizId + " nebyl nalezen.");
        }
        return quiz;
    }

    /**
     * @param quizId id of the quiz.
     */
    @PreAuthorize("hasRole('CREATE_QUIZ')")
    public void delete(String quizId) {
        quizRepository.deleteById(quizId);
    }

    /**
     * @param filterParameters paging and filtering requested by the UI.
     * @return one page of quizzes, without their questions and answers.
     */
    public PageResult<QuickQuizEntity> list(FilterParameters<QuizFilter> filterParameters) {
        Query query = listingQueries.baseQuery(filterParameters.getFilter());
        query.fields().exclude("questions").exclude("answers");
        return listingQueries.page(query, filterParameters.getPageRequest(), QuickQuizEntity.class,
                MongoCollections.QUIZ);
    }

    /**
     * @param chapterId id of a chapter.
     * @return the quizzes belonging to that chapter, for the chapter detail screen.
     */
    public List<QuizEntity> byChapter(String chapterId) {
        if (chapterId == null || chapterId.isBlank()) {
            return List.of();
        }
        return quizRepository.findByChapterId(chapterId);
    }

    private void validate(QuizEntity quiz) {
        if (quiz.getName() == null || quiz.getName().isBlank()) {
            throw new BackendException.Validation("Název kvízu nesmí být prázdný.");
        }
        if (quiz.getTimeLimit() != null && quiz.getTimeLimit() < 0) {
            throw new BackendException.Validation("Časový limit kvízu nesmí být záporný.");
        }
        if (quiz.getChapterId() != null && !quiz.getChapterId().isBlank()) {
            chapterBackendService.require(quiz.getChapterId());
        }
        authoringValidator.validate(quiz.getQuestions(), quiz.getAnswers());
    }
}
