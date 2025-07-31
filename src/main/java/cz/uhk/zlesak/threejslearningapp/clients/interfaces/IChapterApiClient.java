package cz.uhk.zlesak.threejslearningapp.clients.interfaces;

import cz.uhk.zlesak.threejslearningapp.models.entities.ChapterEntity;

import java.util.List;

public interface IChapterApiClient extends IApiClient {
    ChapterEntity createChapter(ChapterEntity chapterEntity) throws Exception;
    void updateChapter(String chapterId, ChapterEntity chapterEntity) throws Exception;
    void deleteChapter(String chapterId) throws Exception;
    ChapterEntity getChapterById(String chapterId) throws Exception;
    List<String> getChaptersByAuthor(String authorId) throws Exception;
    List<String> getAllChapters() throws Exception;
}
