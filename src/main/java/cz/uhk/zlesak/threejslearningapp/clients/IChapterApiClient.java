package cz.uhk.zlesak.threejslearningapp.clients;

import cz.uhk.zlesak.threejslearningapp.models.ChapterEntity;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IChapterApiClient {
    ChapterEntity createChapter(ChapterEntity chapter) throws Exception;

    ChapterEntity getChapter(String chapterId) throws Exception;

    //    List<ChapterListingEntity> getChapters() throws Exception; TODO implement based on the API on the BE
    void uploadModel(MultipartFile file, String chapterId) throws Exception;
    Resource downloadModel(String modelId) throws Exception;
}

