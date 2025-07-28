package cz.uhk.zlesak.threejslearningapp.clients;

import cz.uhk.zlesak.threejslearningapp.data.ApiCallException;
import cz.uhk.zlesak.threejslearningapp.models.ChapterEntity;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChapterApiClient implements IChapterApiClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ChapterApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
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
            return response.getBody();
        }catch (HttpStatusCodeException ex) {
            throw new ApiCallException("Chyba při nahrávání kapitoly", null, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);

        } catch (Exception e) {
            throw new Exception("Neočekávaná chyba při volání API pro nahrání kapitoly: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateChapter(String chapterId, ChapterEntity chapterEntity) throws Exception {

    }

    @Override
    public void deleteChapter(String chapterId) throws Exception {

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
            return response.getBody();

        } catch (HttpStatusCodeException ex) {
            throw new ApiCallException("Chyba při získávání kapitoly dle jejího ID", chapterId, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);

        } catch (Exception e) {
            throw new Exception("Neočekávaná chyba při volání API pro získání kapitoly: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> getChaptersByAuthor(String authorId) throws Exception {
        return new ArrayList<>();

    }

    @Override
    public List<String> getAllChapters() throws Exception {
        return List.of();
    }

    /**
     * API call function to endpoint chapter with parameter of ID of the selected chapter
     *
     * @param chapterId ChapterId of wanted chapter to be requested from API
     * @return Returns chapter data
     * @throws Exception Throws exception when any trouble happens along the get chapter API call
     */
    @Override
    public ChapterEntity getChapter(String chapterId) throws Exception {
        return ChapterEntity.builder().build();
    }

    /**
     * API call function to endpoint upload to upload the model for the selected chapter
     * @param file File of the model to upload
     * @param chapterId ChapterId of the chapter that the model belongs to
     * @throws Exception Throws exception when anything bad happens along the uploading of the model
     */
//
//    public void uploadModel(MultipartFile file, String chapterId) throws Exception {
//        String url = baseUrl + "upload";
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        body.add("model", file.getResource());
//        body.add("chapterId", chapterId);
//
//        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//        try {
//            restTemplate.exchange(
//                    url,
//                    HttpMethod.POST,
//                    requestEntity,
//                    String.class
//            );
//        } catch (HttpStatusCodeException ex) {
//            throw new ApiCallException("Chyba při nahrávání modelu pro kapitolu", chapterId, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
//        } catch (Exception e) {
//            throw new Exception("Neočekávaná chyba při volání API pro upload modelu: " + e.getMessage(), e);
//        }
//    }

    /**
     * API call function to endpoint download to download model for selected chapter
     *
     * @param chapterId ChapterId to get the model for
     * @return Returns model for the chapter
     * @throws Exception Throws exception, when any problem along the path of getting the model, has occurred
     */
//    public Resource downloadModel(String chapterId) throws Exception {
//        String url = baseUrl + "download/" + chapterId;
//        HttpHeaders headers = new HttpHeaders();
//        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
//        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
//        try {
//            return restTemplate.exchange(
//                    url,
//                    HttpMethod.GET,
//                    requestEntity,
//                    Resource.class
//            ).getBody();
//        } catch (HttpStatusCodeException ex) {
//            throw new ApiCallException("Chyba při získávání modelu pro kapitolu", chapterId, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
//        } catch (Exception e) {
//            throw new Exception("Neočekávaná chyba při volání API pro stažení modelu: " + e.getMessage(), e);
//        }
//    }
}