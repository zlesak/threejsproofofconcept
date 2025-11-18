package cz.uhk.zlesak.threejslearningapp.api.contracts;

import cz.uhk.zlesak.threejslearningapp.common.InputStreamMultipartFile;

/**
 * Interface for file-related API client operations.
 * Defined generic methods for uploading and downloading files.
 * Files represent both models and its textures, thus this interface is used in both model and texture api clients.
 */
public interface IFileApiClient<E, R> {
    R uploadFileEntity(InputStreamMultipartFile inputStreamMultipartFile, E textureEntity) throws Exception;

    InputStreamMultipartFile downloadFileEntity(String fileId) throws Exception;
}
