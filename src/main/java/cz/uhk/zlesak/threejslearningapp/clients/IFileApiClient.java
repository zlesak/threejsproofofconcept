package cz.uhk.zlesak.threejslearningapp.clients;

import cz.uhk.zlesak.threejslearningapp.models.IFileEntity;

import java.util.List;

public interface IFileApiClient {
    void createFileEntity(IFileEntity fileEntity) throws Exception;
    IFileEntity getFileEntityById(String modelId) throws Exception;
    List<IFileEntity> getFileEntitiesByAuthor(String authorId) throws Exception;
    void uploadFileEntity(IFileEntity fileEntity) throws Exception;
    void deleteFileEntity(String modelId) throws Exception;
}
