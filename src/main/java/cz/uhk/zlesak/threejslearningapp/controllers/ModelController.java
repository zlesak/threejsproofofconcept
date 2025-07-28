package cz.uhk.zlesak.threejslearningapp.controllers;

import cz.uhk.zlesak.threejslearningapp.clients.ModelApiClient;
import cz.uhk.zlesak.threejslearningapp.models.FileEntity;
import cz.uhk.zlesak.threejslearningapp.models.InputStreamMultipartFile;
import cz.uhk.zlesak.threejslearningapp.models.ModelEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ModelController {
    private final ModelApiClient modelApiClient;

    @Autowired
    public ModelController(ModelApiClient modelApiClient) {
        this.modelApiClient = modelApiClient;
    }

    public String uploadModel(String modelName, InputStreamMultipartFile inputStream) {
        try {
            FileEntity fileEntity = ModelEntity.builder()
                    .Name(modelName)
//                        .Creator()
//                        .Metadata()
//                        .MainTextureEntity()
//                        .TextureEntities()
                    .build();
            return modelApiClient.uploadFileEntity(inputStream, fileEntity);
        } catch (Exception e) {
            throw new RuntimeException("Chyba při nahrávání modelu: " + e.getMessage(), e);
        }
    }

    public ModelEntity getModel(String modelId) {

        try {
            return modelApiClient.getFileEntityById(modelId);
        } catch (Exception e) {
            throw new RuntimeException("Chyba při získávání modelu: " + e.getMessage(), e);
        }
    }


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
//public Resource downloadModel(String chapterId) throws Exception {
//    String url = baseUrl + "download/" + chapterId;
//    HttpHeaders headers = new HttpHeaders();
//    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
//    HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
//    try {
//        return restTemplate.exchange(
//                url,
//                HttpMethod.GET,
//                requestEntity,
//                Resource.class
//        ).getBody();
//    } catch (HttpStatusCodeException ex) {
//        throw new ApiCallException("Chyba při získávání modelu pro kapitolu", chapterId, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
//    } catch (Exception e) {
//        throw new Exception("Neočekávaná chyba při volání API pro stažení modelu: " + e.getMessage(), e);
//    }
//}
}
