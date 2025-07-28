package cz.uhk.zlesak.threejslearningapp.clients;

import cz.uhk.zlesak.threejslearningapp.models.ChapterEntity;

import java.util.List;

public interface IChapterApiClient extends IApiClient {
    ChapterEntity createChapter(ChapterEntity chapterEntity) throws Exception;
    void updateChapter(String chapterId, ChapterEntity chapterEntity) throws Exception;
    void deleteChapter(String chapterId) throws Exception;
    ChapterEntity getChapterById(String chapterId) throws Exception;
    List<String> getChaptersByAuthor(String authorId) throws Exception;
    List<String> getAllChapters() throws Exception;

    ChapterEntity getChapter(String chapterId) throws Exception;
}
