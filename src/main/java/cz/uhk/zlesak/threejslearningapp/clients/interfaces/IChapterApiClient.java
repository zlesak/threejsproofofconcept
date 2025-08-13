package cz.uhk.zlesak.threejslearningapp.clients.interfaces;

import cz.uhk.zlesak.threejslearningapp.models.entities.ChapterEntity;

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
    List<String> getChaptersByAuthor(String authorId) throws Exception;
    List<String> getAllChapters() throws Exception;
}
