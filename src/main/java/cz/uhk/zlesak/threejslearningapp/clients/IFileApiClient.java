package cz.uhk.zlesak.threejslearningapp.clients;

import cz.uhk.zlesak.threejslearningapp.models.FileEntity;
import cz.uhk.zlesak.threejslearningapp.models.InputStreamMultipartFile;

import java.util.List;

public interface IFileApiClient extends IApiClient {
    void createFileEntity(FileEntity fileEntity) throws Exception;
    FileEntity getFileEntityById(String fileEntityId) throws Exception;
    List<FileEntity> getFileEntitiesByAuthor(String authorId) throws Exception;
    String uploadFileEntity(InputStreamMultipartFile inputStream, FileEntity fileEntity) throws Exception;
    FileEntity downloadFileEntityById(String fileEntityId) throws Exception;
    void deleteFileEntity(String modelId) throws Exception;
}
