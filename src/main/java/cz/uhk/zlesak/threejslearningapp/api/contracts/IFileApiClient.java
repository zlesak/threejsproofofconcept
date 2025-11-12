package cz.uhk.zlesak.threejslearningapp.api.contracts;

import cz.uhk.zlesak.threejslearningapp.common.InputStreamMultipartFile;
import cz.uhk.zlesak.threejslearningapp.domain.common.*;

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
    PageResult<QuickFile> getFileEntities(int page, int limit, String orderBy, SortDirectionEnum sortDirection) throws Exception;
    QuickFile uploadFileEntity(InputStreamMultipartFile inputStream, IEntity entity) throws Exception;
    void deleteFileEntity(String modelId) throws Exception;
}
