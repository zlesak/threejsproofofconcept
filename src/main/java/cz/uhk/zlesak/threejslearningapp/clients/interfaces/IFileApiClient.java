package cz.uhk.zlesak.threejslearningapp.clients.interfaces;

import cz.uhk.zlesak.threejslearningapp.models.entities.IEntity;
import cz.uhk.zlesak.threejslearningapp.models.entities.Entity;
import cz.uhk.zlesak.threejslearningapp.data.files.InputStreamMultipartFile;

import java.util.List;

public interface IFileApiClient extends IApiClient {
    void createFileEntity(Entity entity) throws Exception;
    Entity getFileEntityById(String fileEntityId) throws Exception;
    List<Entity> getFileEntitiesByAuthor(String authorId) throws Exception;
    String uploadFileEntity(InputStreamMultipartFile inputStream, IEntity entity) throws Exception;
    Entity downloadFileEntityById(String fileEntityId) throws Exception;
    void deleteFileEntity(String modelId) throws Exception;
}
