package cz.uhk.zlesak.threejslearningapp.api.contracts;

import cz.uhk.zlesak.threejslearningapp.domain.chapter.ChapterEntity;
import cz.uhk.zlesak.threejslearningapp.domain.common.PageResult;
import cz.uhk.zlesak.threejslearningapp.domain.common.SortDirectionEnum;

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
