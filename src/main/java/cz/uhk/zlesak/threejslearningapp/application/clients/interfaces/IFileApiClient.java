package cz.uhk.zlesak.threejslearningapp.application.clients.interfaces;

import cz.uhk.zlesak.threejslearningapp.application.data.files.InputStreamMultipartFile;
import cz.uhk.zlesak.threejslearningapp.application.models.entities.IEntity;
import cz.uhk.zlesak.threejslearningapp.application.models.entities.Entity;
import cz.uhk.zlesak.threejslearningapp.application.models.entities.quickEntities.QuickFile;
import cz.uhk.zlesak.threejslearningapp.application.models.records.PageResult;

import java.util.List;

/**
 * Interface for file-related API client operations.
 * This interface defines methods for creating, retrieving, uploading, downloading, and deleting file entities.
 * Files represent both models and its textures, thus this interface is used in both model and texture api clients.
 * It extends the IApiClient interface to be able to get the address of the backend service based on the environment.
 */
public interface IFileApiClient extends IApiClient {
    Entity getFileEntityById(String fileEntityId) throws Exception;
    List<Entity> getFileEntitiesByAuthor(String authorId) throws Exception;
    PageResult<QuickFile> getFileEntities(int page, int limit) throws Exception;
    QuickFile uploadFileEntity(InputStreamMultipartFile inputStream, IEntity entity) throws Exception;
    void deleteFileEntity(String modelId) throws Exception;
}
