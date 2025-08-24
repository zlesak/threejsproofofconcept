package cz.uhk.zlesak.threejslearningapp.clients.interfaces;

import cz.uhk.zlesak.threejslearningapp.models.entities.IEntity;
import cz.uhk.zlesak.threejslearningapp.models.entities.Entity;
import cz.uhk.zlesak.threejslearningapp.data.files.InputStreamMultipartFile;
import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickFile;

import java.util.List;

/**
 * Interface for file-related API client operations.
 * This interface defines methods for creating, retrieving, uploading, downloading, and deleting file entities.
 * Files represent both models and its textures, thus this interface is used in both model and texture api clients.
 * It extends the IApiClient interface to be able to get the address of the backend service based on the environment.
 */
public interface IFileApiClient extends IApiClient {
    void createFileEntity(Entity entity) throws Exception;
    Entity getFileEntityById(String fileEntityId) throws Exception;
    List<Entity> getFileEntitiesByAuthor(String authorId) throws Exception;
    List<QuickFile> getFileEntities(int page, int limit) throws Exception;
    QuickFile uploadFileEntity(InputStreamMultipartFile inputStream, IEntity entity) throws Exception;
    Entity downloadFileEntityById(String fileEntityId) throws Exception;
    void deleteFileEntity(String modelId) throws Exception;
}
