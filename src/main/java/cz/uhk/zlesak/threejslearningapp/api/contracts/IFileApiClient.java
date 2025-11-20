package cz.uhk.zlesak.threejslearningapp.api.contracts;

import cz.uhk.zlesak.threejslearningapp.common.InputStreamMultipartFile;

/**
 * Interface for file-related API client operations.
 * Defined generic methods for uploading and downloading files.
 * Files represent both models and its textures, thus this interface is used in both model and texture api clients.
 *
 * @param <E> The type of the entity associated with the file.
 * @param <Q> The type of the response after uploading the file.
 */
public interface IFileApiClient<E, Q> {
    Q uploadFileEntity(InputStreamMultipartFile inputStreamMultipartFile, E entity) throws Exception;

    InputStreamMultipartFile downloadFileEntity(String fileId) throws Exception;
}
