package cz.uhk.zlesak.threejslearningapp.api.clients;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.uhk.zlesak.threejslearningapp.api.contracts.IChapterApiClient;
import cz.uhk.zlesak.threejslearningapp.domain.chapter.ChapterEntity;
import cz.uhk.zlesak.threejslearningapp.domain.chapter.ChapterFilter;
import cz.uhk.zlesak.threejslearningapp.domain.common.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * ChapterApiClient provides connection to the backend service for managing chapters.
 * Extends AbstractApiClient and implements IChapterApiClient interface.
 */
@Component
public class ChapterApiClient extends AbstractApiClient<ChapterEntity, ChapterEntity, ChapterFilter> implements IChapterApiClient { //TODO QUICKCHAPTERENTITY
    /**
     * Constructor for ChapterApiClient.
     *
     * @param restTemplate RestTemplate for making HTTP requests
     * @param objectMapper ObjectMapper for JSON serialization/deserialization
     */
    @Autowired
    public ChapterApiClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        super(restTemplate, objectMapper, "chapter/");
    }
//region CRUD operations from IApiClient

    /**
     * Creates a new chapter.
     *
     * @param chapterEntity Chapter entity to create
     * @return Created chapter entity
     * @throws Exception if API call fails
     */
    @Override
    public ChapterEntity create(ChapterEntity chapterEntity) throws Exception {
        return sendPostRequest(baseUrl + "create", chapterEntity, ChapterEntity.class, "Chyba při vytváření kapitoly", null, null);
    }

    /**
     * Gets a chapter by ID.
     *
     * @param chapterId ID of the chapter to retrieve
     * @return Chapter entity
     * @throws Exception if API call fails
     */
    @Override
    public ChapterEntity read(String chapterId) throws Exception {
        return sendGetRequest(baseUrl + chapterId, ChapterEntity.class, "Chyba při získávání kapitoly dle ID", chapterId);
    }

    /**
     * Gets paginated list of chapters.
     *
     * @param pageRequest PageRequest object containing pagination info
     * @return PageResult of QuickChapterEntity
     * @throws Exception if API call fails
     */
    @Override
    public PageResult<ChapterEntity> readEntities(PageRequest pageRequest) throws Exception {
        return readEntitiesFiltered(pageRequest, null);
    }

    /**
     * Gets paginated list of chapters with filtering.
     *
     * @param pageRequest PageRequest object containing pagination info
     * @param filter      ChapterFilter object containing filter criteria
     * @return PageResult of QuickChapterEntity
     * @throws Exception if API call fails
     */
    @Override
    public PageResult<ChapterEntity> readEntitiesFiltered(PageRequest pageRequest, ChapterFilter filter) throws Exception { //TODO QUICKCHAPTERENTITY
        String url = pageRequestToQueryParams(pageRequest, null) + filterToQueryParams(filter);
        ResponseEntity<String> response = sendGetRequestRaw(url, String.class, "Chyba při získávání seznamu kapitol", null, true);
        JavaType type = objectMapper.getTypeFactory().constructParametricType(PageResult.class, ChapterEntity.class);
        return parseResponse(response, type, "Chyba při získávání seznamu kapitol", null);
    }

    /**
     * Updates an existing chapter.
     *
     * @param chapterId     ID of the chapter to update
     * @param chapterEntity Chapter entity to update
     * @return Updated chapter entity
     * @throws Exception if API call fails
     */
    @Override //TODO implement into MISH (endpoint není s ID zatím)
    public ChapterEntity update(String chapterId, ChapterEntity chapterEntity) throws Exception {
        return sendPutRequest(baseUrl + "update/" + chapterId, chapterEntity, ChapterEntity.class, "Chyba při aktualizaci kapitoly", chapterId);

    }

    /**
     * Deletes a chapter by ID.
     *
     * @param chapterId ID of the chapter to delete
     * @return True if deletion was successful, false otherwise
     * @throws Exception if API call fails
     */
    @Override
    public boolean delete(String chapterId) throws Exception {
        sendDeleteRequest(baseUrl + "delete/" + chapterId, "Chyba při mazání kapitoly", chapterId);
        return true;
    }
//endregion
}