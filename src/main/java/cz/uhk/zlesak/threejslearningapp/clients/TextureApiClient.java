package cz.uhk.zlesak.threejslearningapp.clients;

import cz.uhk.zlesak.threejslearningapp.models.IFileEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TextureApiClient implements IFileApiClient{
    @Override
    public void createFileEntity(IFileEntity fileEntity) throws Exception {

    }

    @Override
    public IFileEntity getFileEntityById(String modelId) throws Exception {
        return null;
    }

    @Override
    public List<IFileEntity> getFileEntitiesByAuthor(String authorId) throws Exception {
        return List.of();
    }

    @Override
    public void uploadFileEntity(IFileEntity fileEntity) throws Exception {

    }

    @Override
    public void deleteFileEntity(String modelId) throws Exception {

    }
}
