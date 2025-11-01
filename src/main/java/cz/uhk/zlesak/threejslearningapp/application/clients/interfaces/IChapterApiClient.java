package cz.uhk.zlesak.threejslearningapp.application.clients.interfaces;

import cz.uhk.zlesak.threejslearningapp.application.models.entities.ChapterEntity;
import cz.uhk.zlesak.threejslearningapp.application.models.records.PageResult;
import cz.uhk.zlesak.threejslearningapp.application.models.records.SortDirectionEnum;

import java.util.List;

/**
 * Interface for Chapter API Client
 * This interface defines methods for creating, updating, deleting, and retrieving chapters.
 * It extends the IApiClient interface to be able to get the address of the backend service based on the environment.
 */
public interface IChapterApiClient extends IApiClient {
    ChapterEntity createChapter(ChapterEntity chapterEntity) throws Exception;
    void updateChapter(String chapterId, ChapterEntity chapterEntity) throws Exception;
    void deleteChapter(String chapterId) throws Exception;
    ChapterEntity getChapterById(String chapterId) throws Exception;
    PageResult<ChapterEntity> getChapters(int page, int limit, String orderBy, SortDirectionEnum sortDirection) throws Exception;
    List<String> getChaptersByAuthor(String authorId) throws Exception;
}
