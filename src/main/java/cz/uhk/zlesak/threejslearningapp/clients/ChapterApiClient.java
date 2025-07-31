package cz.uhk.zlesak.threejslearningapp.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.uhk.zlesak.threejslearningapp.clients.interfaces.IApiClient;
import cz.uhk.zlesak.threejslearningapp.clients.interfaces.IChapterApiClient;
import cz.uhk.zlesak.threejslearningapp.data.ApiCallException;
import cz.uhk.zlesak.threejslearningapp.models.entities.ChapterEntity;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ChapterApiClient implements IChapterApiClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    @Autowired
    public ChapterApiClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.baseUrl = IApiClient.getBaseUrl() + "chapter/";
    }

    /**
     * API call function to endpoint create to create chapter coming from frontend view
     * @param chapter Chapter entity to save via API
     * @return Returns saved chapter, as the behavior is to redirect user to the created chapter
     * @throws Exception Throws exception if anything goes bad when saving the chapter via this API call
     */
    @Override
    public ChapterEntity createChapter(ChapterEntity chapter) throws Exception {
        String url = baseUrl + "create";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ChapterEntity> request = new HttpEntity<>(chapter, headers);

        try{
            ResponseEntity<ChapterEntity> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    ChapterEntity.class);
            return objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), ChapterEntity.class);
        }catch (HttpStatusCodeException ex) {
            throw new ApiCallException("Chyba při nahrávání kapitoly", null, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);

        } catch (Exception e) {
            throw new Exception("Neočekávaná chyba při volání API pro nahrání kapitoly: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateChapter(String chapterId, ChapterEntity chapterEntity) throws NotImplementedException {
        throw new NotImplementedException("Update chapter method is not implemented yet.");
    }

    @Override
    public void deleteChapter(String chapterId) throws NotImplementedException {
        throw new NotImplementedException("Delete chapter method is not implemented yet.");
    }

    @Override
    public ChapterEntity getChapterById(String chapterId) throws Exception {
        String url = baseUrl + chapterId;
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ChapterEntity> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    ChapterEntity.class
            );
            return objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), ChapterEntity.class);
        } catch (HttpStatusCodeException ex) {
            throw new ApiCallException("Chyba při získávání kapitoly dle jejího ID", chapterId, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);

        } catch (Exception e) {
            throw new Exception("Neočekávaná chyba při volání API pro získání kapitoly: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> getChaptersByAuthor(String authorId) throws NotImplementedException {
        throw new NotImplementedException("Get chapters by author method is not implemented yet.");
    }

    @Override
    public List<String> getAllChapters() throws NotImplementedException {
        throw new NotImplementedException("Get all chapters method is not implemented yet.");
    }
}