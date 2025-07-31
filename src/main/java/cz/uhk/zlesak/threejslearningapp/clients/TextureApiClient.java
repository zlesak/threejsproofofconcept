package cz.uhk.zlesak.threejslearningapp.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.uhk.zlesak.threejslearningapp.clients.interfaces.IApiClient;
import cz.uhk.zlesak.threejslearningapp.clients.interfaces.IFileApiClient;
import cz.uhk.zlesak.threejslearningapp.data.ApiCallException;
import cz.uhk.zlesak.threejslearningapp.models.IEntity;
import cz.uhk.zlesak.threejslearningapp.models.InputStreamMultipartFile;
import cz.uhk.zlesak.threejslearningapp.models.entities.Entity;
import cz.uhk.zlesak.threejslearningapp.models.entities.TextureEntity;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.util.List;

@Service
public class TextureApiClient implements IFileApiClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    @Autowired
    public TextureApiClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        this.baseUrl = IApiClient.getBaseUrl() + "texture/";
    }

    @Override
    public void createFileEntity(Entity entity) throws NotImplementedException {
        throw new NotImplementedException("Tato metoda není implementována pro textury.");
    }

    @Override
    public TextureEntity getFileEntityById(String fileEntityId) throws Exception {
        String url = baseUrl + "download/" + fileEntityId;
        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    null,
                    byte[].class
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String contentDisposition = response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
                String filename = null;
                if (contentDisposition != null && contentDisposition.contains("filename=")) {
                    filename = contentDisposition.substring(contentDisposition.indexOf("filename=") + 9).replace("\"", "");
                }
                InputStreamMultipartFile file = new InputStreamMultipartFile(new ByteArrayInputStream(response.getBody()), filename);
                return TextureEntity.builder()
                        .Name(filename)
                        .File(file)
                        .build();
            } else {
                throw new Exception("Textura nalezena nebo chyba při stahování.");
            }
        }catch(HttpStatusCodeException ex){
            throw new ApiCallException("Nepodařilo se stáhnout texturu", null, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
        }
    }

    @Override
    public List<Entity> getFileEntitiesByAuthor(String authorId) throws NotImplementedException {
        throw new NotImplementedException("Tato metoda není implementována pro textury.");
    }

    @Override
    public String uploadFileEntity(InputStreamMultipartFile inputStreamMultipartFile, IEntity textureEntity) throws Exception {
        String url = baseUrl + "upload";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);


        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("texture", inputStreamMultipartFile.getResource());

        String metadataJson = objectMapper.writeValueAsString(textureEntity);
        HttpHeaders metadataHeaders = new HttpHeaders();
        metadataHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> metadataPart = new HttpEntity<>(metadataJson, metadataHeaders);
        body.add("metadata", metadataPart);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    String.class
            );
            return response.getBody();
        } catch (HttpStatusCodeException ex) {
            throw new ApiCallException("Chyba při nahrávání textury", null, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
        }
    }

    @Override
    public Entity downloadFileEntityById(String fileEntityId) throws Exception {
        String url = baseUrl + "download/" + fileEntityId;
        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    null,
                    byte[].class
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {

                String contentDisposition = response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
                String filename = null;
                if (contentDisposition != null && contentDisposition.contains("filename=")) {
                    filename = contentDisposition.substring(contentDisposition.indexOf("filename=") + 9).replace("\"", "");
                }
                return TextureEntity.builder().Name(filename).File(new InputStreamMultipartFile(new ByteArrayInputStream(response.getBody()), filename)).build();
            } else {
                throw new Exception("Soubor nebyl nalezen nebo došlo k chybě při stahování.");
            }
        } catch (HttpStatusCodeException ex) {
            throw new ApiCallException("Chyba při stahování souboru", null, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
        }
    }

    @Override
    public void deleteFileEntity(String modelId) throws NotImplementedException {
        throw new NotImplementedException("Tato metoda není implementována pro textury.");
    }
}
