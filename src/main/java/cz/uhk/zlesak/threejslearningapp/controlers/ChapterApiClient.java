package cz.uhk.zlesak.threejslearningapp.controlers;

import cz.uhk.zlesak.threejslearningapp.data.ApiCallException;
import cz.uhk.zlesak.threejslearningapp.models.ChapterEntity;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ChapterApiClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ChapterApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        // Detekce Hotswap Agent podle argumentů JVM //TODO this needs to be removed if ever wanted to get into real production to prevent from any not wanted or unexpected behaviour that may be dirty
        boolean isHotswap = java.lang.management.ManagementFactory.getRuntimeMXBean()
                .getInputArguments().stream()
                .anyMatch(arg -> arg.contains("hotswap-agent.jar"));
        if (isHotswap) {
            System.out.println("Hotswap Agent detected, using local base URL");
            this.baseUrl = "http://localhost:8080/api/chapter";
        } else {
            this.baseUrl = "http://kotlin-backend:8080/api/chapter";
        }
    }

    /**
     * API call function to endpoint create to create chapter coming from frontend view
     * @param chapter Chapter entity to save via API
     * @return Returns saved chapter, as the behavior is to redirect user to the created chapter
     * @throws Exception Throws exception if anything goes bad when saving the chapter via this API call
     */
    public ChapterEntity createChapter(ChapterEntity chapter) throws Exception {
        String url = baseUrl + "/create";
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

    /**
     * API call function to endpoint chapter with parameter of ID of the selected chapter
     *
     * @param chapterId ChapterId of wanted chapter to be requested from API
     * @return Returns chapter data
     * @throws Exception Throws exception when any trouble happens along the get chapter API call
     */
    public ChapterEntity getChapter(String chapterId) throws Exception {
        String url = baseUrl + "/" + chapterId;
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
            throw new ApiCallException("Chyba při získávání kapitoly", chapterId, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);

        } catch (Exception e) {
            throw new Exception("Neočekávaná chyba při volání API pro získání kapitoly: " + e.getMessage(), e);
        }
    }

    /**
     * API call function to endpoint upload to upload the model for the selected chapter
     * @param file File of the model to upload
     * @param chapterId ChapterId of the chapter that the model belongs to
     * @throws Exception Throws exception when anything bad happens along the uploading of the model
     */
    public void uploadModel(MultipartFile file, String chapterId) throws Exception {
        String url = baseUrl + "/upload";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("model", file.getResource());
        body.add("chapterId", chapterId);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        try {
            restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
        } catch (HttpStatusCodeException ex) {
            throw new ApiCallException("Chyba při nahrávání modelu pro kapitolu", chapterId, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
        } catch (Exception e) {
            throw new Exception("Neočekávaná chyba při volání API pro upload modelu: " + e.getMessage(), e);
        }
    }

    /**
     * API call function to endpoint download to download model for selected chapter
     *
     * @param chapterId ChapterId to get the model for
     * @return Returns model for the chapter
     * @throws Exception Throws exception, when any problem along the path of getting the model, has occurred
     */
    public Resource downloadModel(String chapterId) throws Exception {
        String url = baseUrl + "/download/" + chapterId;
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        try {
            ResponseEntity<Resource> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    Resource.class
            );
            return response.getBody();
        } catch (HttpStatusCodeException ex) {
            throw new ApiCallException("Chyba při získávání modelu pro kapitolu", chapterId, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
        } catch (Exception e) {
            throw new Exception("Neočekávaná chyba při volání API pro stažení modelu: " + e.getMessage(), e);
        }
    }
}