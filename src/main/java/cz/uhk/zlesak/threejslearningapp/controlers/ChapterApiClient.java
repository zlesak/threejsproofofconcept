package cz.uhk.zlesak.threejslearningapp.controlers;

import cz.uhk.zlesak.threejslearningapp.models.ChapterEntity;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ChapterApiClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ChapterApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        // Detekce Hotswap Agent podle argumentů JVM
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

    public ChapterEntity createChapter(ChapterEntity chapter) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ChapterEntity> request = new HttpEntity<>(chapter, headers);
        ResponseEntity<ChapterEntity> response = restTemplate.postForEntity(
                baseUrl + "/create",
                request,
                ChapterEntity.class);
        return response.getBody();
    }

    public ChapterEntity getChapter(String chapterId) {
        ResponseEntity<ChapterEntity> response = restTemplate.getForEntity(
                baseUrl + "/" + chapterId,
                ChapterEntity.class);
        return response.getBody();
    }

    public void uploadModel(MultipartFile file, String chapterId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("model", file.getResource());
        body.add("chapterId", chapterId);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        restTemplate.postForEntity(
                baseUrl + "/upload",
                requestEntity,
                String.class);
    }

    public Resource downloadModel(String chapterId) {
        ResponseEntity<Resource> response = restTemplate.getForEntity(
                baseUrl + "/download/" + chapterId,
                Resource.class);
        return response.getBody();
    }
}