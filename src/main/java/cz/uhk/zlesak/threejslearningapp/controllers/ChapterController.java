package cz.uhk.zlesak.threejslearningapp.controllers;

import cz.uhk.zlesak.threejslearningapp.clients.ChapterApiClient;
import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickTextureEntity;
import cz.uhk.zlesak.threejslearningapp.models.records.SubChapterForSelectRecord;
import cz.uhk.zlesak.threejslearningapp.models.entities.ChapterEntity;
import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickModelEntity;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Controller for managing chapters in the application.
 * This class provides methods to create chapters, retrieve chapter details, and manage sub-chapters.
 * Provides the connector to the ChapterApiClient for performing operations related to chapters on BE side.
 */
@Slf4j
@Component
@Scope("prototype")
public class ChapterController {
    private final ChapterApiClient chapterApiClient;
    private ChapterEntity chapterEntity = null;
    @Setter
    private QuickModelEntity uploadedModel = null;

    /**
     * Constructor for ChapterController that initializes the ChapterApiClient.
     * @param chapterApiClient The API client used to interact with chapter-related operations.
     */
    @Autowired
    public ChapterController(ChapterApiClient chapterApiClient) {
        this.chapterApiClient = chapterApiClient;
    }

    /**
     * Creates a new chapter with the specified name and content.
     * Validates the inputs to ensure that the chapter name and content are not empty, and that a model is uploaded.
     * If any validation fails, an ApplicationContextException is thrown with an appropriate message.
     * If the chapter is successfully created, it returns the created ChapterEntity.
     * @param name the name of the chapter
     * @param content  the content of the chapter in JSON format
     * @return the created ChapterEntity coming back from BE as proof of successful creation
     * @throws Exception if there is an error during chapter creation or if validation fails
     */
    public ChapterEntity createChapter(String name, String content) throws Exception {

        if (name == null || name.isEmpty()) {
            log.debug("Název kapitoly je prázdný.");
            throw new ApplicationContextException("Název kapitoly nesmí být prázdný.");
        }
        if (content == null || content.isEmpty()) {
            log.debug("Obsah kapitoly je prázdný.");
            throw new ApplicationContextException("Obsah kapitoly nesmí být prázdný.");
        }
        if (uploadedModel == null) {
            log.debug("Kapitola nemá přiřazen žádný model.");
            throw new ApplicationContextException("Kapitola musí mít alespoň jeden model.");
        }

        ChapterEntity chapter = ChapterEntity.builder()
                .Name(name)
                .Content(content)
                .Models(List.of(uploadedModel))
                .build();
        try {
            return chapterApiClient.createChapter(chapter);
        } catch (Exception e) {
            log.error("Chyba při vytváření kapitoly: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Retrieves a chapter by its ID via chapterApiClient from the BE.
     * This method fetches the chapter details from the ChapterApiClient and stores it in the chapterEntity field.
     * If an error occurs during the retrieval, it logs the error and throws an Exception with a message indicating the failure.
     * @param chapterId the ID of the chapter to be retrieved
     * @throws Exception if there is an error retrieving the chapter or if the chapter does not exist
     * @see ChapterApiClient#getChapterById(String)
     */
    private void getChapter(String chapterId) throws Exception {
        try {
            chapterEntity = chapterApiClient.getChapterById(chapterId);
        } catch (Exception e) {
            log.error("Chyba při získávání kapitoly: {}", e.getMessage(), e);
            throw new Exception("Chyba při získávání kapitoly: " + e.getMessage());
        }
    }

    /**
     * Retrieves the name of a chapter by its ID.
     * If the chapterEntity is not set or does not match the provided chapterId, it fetches the chapter details using the getChapter method.
     * This method saves network calls by caching the chapterEntity after the first retrieval.
     * @param chapterId the ID of the chapter whose name is to be retrieved
     * @return the name of the chapter
     * @throws Exception if there is an error retrieving the chapter or if the chapter does not exist
     */
    public String getChapterName(String chapterId) throws Exception {
        if(chapterEntity == null || !Objects.equals(chapterEntity.getId(), chapterId)) {
            getChapter(chapterId);
        }
        return chapterEntity.getName();
    }

    /**
     * Retrieves the content of a chapter.
     * If the chapterEntity is not set or does not match the provided chapterId, it fetches the chapter details using the getChapter method.
     * @return the content of the chapter as a JSON string
     */
    public String getChapterContent(String chapterId) throws Exception {
        if(chapterEntity == null || !Objects.equals(chapterEntity.getId(), chapterId)) {
            getChapter(chapterId);
        }
        return chapterEntity.getContent();
    }

    /**
     * Retrieves the names of sub-chapters from the chapter content.
     * It parses the chapter content to extract sub-chapter headers (level 1 headers).
     * Each sub-chapter is represented by its ID and text.
     * If an error occurs during the parsing, it logs the error and throws an Exception.
     * @return a list of SubChapterForComboBoxRecord objects containing sub-chapter IDs and names
     * @throws Exception if there is an error retrieving the sub-chapter names or if the chapter does not exist
     * @see SubChapterForSelectRecord
     */
    public List<SubChapterForSelectRecord> getSubChaptersNames(String chapterId) throws Exception {
        if(chapterEntity == null || !Objects.equals(chapterEntity.getId(), chapterId)) {
            getChapter(chapterId);
        }

        List<SubChapterForSelectRecord> subChapters = new ArrayList<>();
        try {
            JsonArray blocks = Json.parse(chapterEntity.getContent()).getArray("blocks");

            for (int i = 0; i < blocks.length(); i++) {
                JsonObject block = blocks.getObject(i);
                if ("header".equals(block.getString("type")) && block.getObject("data").getNumber("level") == 1) {
                    String id = block.hasKey("id") ? block.getString("id") : "fallback-" + java.util.UUID.randomUUID().toString().substring(0, 7);
                    String text = block.getObject("data").getString("text");
                    subChapters.add(new SubChapterForSelectRecord(id, text));
                }
            }
            return subChapters;
        } catch (Exception e) {
            log.error("Error getting subchapter names: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Retrieves the content of sub-chapters from the chapter content.
     * It parses the chapter content to extract sub-chapter headers (level 1 headers)
     * @return a JsonArray containing sub-chapter content, where each sub-chapter is represented by its header and content blocks
     * @throws Exception if there is an error retrieving the sub-chapter content or if the chapter does not exist
     */
    public JsonArray getSubChaptersContent(String chapterId) throws Exception {
        if(chapterEntity == null || !Objects.equals(chapterEntity.getId(), chapterId)) {
            getChapter(chapterId);
        }

        try {
            JsonArray blocks = Json.parse(chapterEntity.getContent()).getArray("blocks");
            JsonArray result = Json.createArray();
            int resultIndex = 0;
            int objectIndex = 0;

            JsonObject oldHeaderBlock;
            JsonObject newHeaderBlock = null;

            JsonArray blocksArray = Json.createArray();

            while (objectIndex < blocks.length()) {
                JsonObject block = blocks.getObject(objectIndex++);
                if ("header".equals(block.getString("type")) && block.getObject("data").getNumber("level") == 1) {
                    oldHeaderBlock = newHeaderBlock;
                    newHeaderBlock = block;
                    JsonObject obj = Json.createObject();
                    if (oldHeaderBlock != null) {
                        obj.put("h1", oldHeaderBlock);
                    } else {
                        JsonObject noHeader = Json.createObject();
                        noHeader.put("id", "fallback-" + java.util.UUID.randomUUID().toString().substring(0, 7));
                        noHeader.put("type", "header");
                        JsonObject data = Json.createObject();
                        data.put("text", "Obsah bez hlavního nadpisu");
                        data.put("level", 1);
                        noHeader.put("data", data);
                        obj.put("h1", noHeader);
                    }
                    obj.put("content", blocksArray);
                    result.set(resultIndex++, obj);
                    blocksArray = Json.createArray();
                } else if ("header".equals(block.getString("type"))) {
                    blocksArray.set(blocksArray.length(), block);
                }
                JsonObject previousBlock = blocks.getObject(objectIndex - 1);

                if (objectIndex == blocks.length() && "header".equals(previousBlock.getString("type")) && previousBlock.getObject("data").getNumber("level") == 1) {
                    oldHeaderBlock = newHeaderBlock;
                    newHeaderBlock = block;
                    JsonObject obj = Json.createObject();
                    if (oldHeaderBlock != null) {
                        obj.put("h1", oldHeaderBlock);
                    } else {
                        JsonObject noHeader = Json.createObject();
                        noHeader.put("id", "fallback-" + java.util.UUID.randomUUID().toString().substring(0, 7));
                        noHeader.put("type", "header");
                        JsonObject data = Json.createObject();
                        data.put("text", "Obsah bez hlavního nadpisu");
                        data.put("level", 1);
                        noHeader.put("data", data);
                        obj.put("h1", noHeader);
                    }
                    obj.put("content", blocksArray);
                    result.set(resultIndex++, obj);
                    blocksArray = Json.createArray();
                }
            }
            if (blocksArray.length() > 0) {
                oldHeaderBlock = newHeaderBlock;
                JsonObject obj = Json.createObject();
                if (oldHeaderBlock != null) {
                    obj.put("h1", oldHeaderBlock);
                } else {
                    JsonObject noHeader = Json.createObject();
                    noHeader.put("id", "fallback-" + java.util.UUID.randomUUID().toString().substring(0, 7));
                    noHeader.put("type", "header");
                    JsonObject data = Json.createObject();
                    data.put("text", "Obsah bez hlavního nadpisu");
                    data.put("level", 1);
                    noHeader.put("data", data);
                    obj.put("h1", noHeader);
                }
                obj.put("content", blocksArray);
                result.set(resultIndex, obj);
            }
            return result;
        } catch (Exception e) {
            log.error("Error getting subchapters content: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Retrieves the content of a selected sub-chapter by its ID.
     * It parses the chapter content to find the blocks associated with the specified sub-chapter header (level 1 header).
     * If the header with the specified ID does not exist, it returns the entire chapter content.
     * If the header exists, it collects all blocks until the next header of the same level (level 1) is found.
     * @param id the ID of the sub-chapter header to retrieve content for
     * @return the content of the selected sub-chapter as a JSON string
     */
    public String getSelectedSubChapterContent(String id) {
        JsonArray blocks = Json.parse(chapterEntity.getContent()).getArray("blocks");
        boolean headerExists = false;
        for (int i = 0; i < blocks.length(); i++) {
            JsonObject block = blocks.getObject(i);
            if ("header".equals(block.getString("type")) && block.getObject("data").getNumber("level") == 1 && block.hasKey("id") && block.getString("id").equals(id)) {
                headerExists = true;
                break;
            }
        }
        if (!headerExists) {
            return blocks.toJson();
        }
        boolean found = false;
        JsonArray content = Json.createArray();
        int contentIndex = 0;
        for (int i = 0; i < blocks.length(); i++) {
            JsonObject block = blocks.getObject(i);
            if (!found) {
                if ("header".equals(block.getString("type")) && block.getObject("data").getNumber("level") == 1 && block.hasKey("id") && block.getString("id").equals(id)) {
                    found = true;
                    content.set(contentIndex++, block);
                }
            } else if ("header".equals(block.getString("type")) && block.getObject("data").getNumber("level") == 1) {
                break;
            } else {
                content.set(contentIndex++, block);
            }
        }
        return content.toJson();
    }

    /**
     * Retrieves the first QuickModelEntity from the chapter's models (there is just one model as of this time for each chapter).
     * If the chapterEntity is not set or does not match the provided chapterId, it fetches the chapter details using the getChapter method.
     * @param chapterId the ID of the chapter from which to retrieve the first QuickModelEntity
     * @return the first QuickModelEntity of the chapter
     * @throws Exception if there is an error retrieving the model data or if the chapter does not exist
     */
    public QuickModelEntity getChapterFirstQuickModelEntity(String chapterId) throws Exception {
        if(chapterEntity == null || !Objects.equals(chapterEntity.getId(), chapterId)) {
            getChapter(chapterId);
        }
        try {
            return chapterEntity.getModels().getFirst();
        } catch (Exception e) {
            log.error("Chyba při čtení dat modelu kapitoly: {}", e.getMessage(), e);
            throw new Exception("Chyba při čtení dat modelu kapitoly: " + e.getMessage());
        }
    }

    public List<QuickTextureEntity> getAllChapterTextures(String chapterId) throws Exception {
        if(chapterEntity == null || !Objects.equals(chapterEntity.getId(), chapterId)) {
            getChapter(chapterId);
        }
        QuickModelEntity quickModelEntity = getChapterFirstQuickModelEntity(chapterId);

        List<QuickTextureEntity> allModelTextures = new ArrayList<>(quickModelEntity.getOtherTextures());
        QuickTextureEntity mainTexture = quickModelEntity.getMainTexture();
        if (mainTexture != null) {
            allModelTextures.addFirst(mainTexture);
        }
        return allModelTextures;
    }

    public Map<String, String> getTextureIdCsvMap(String chapterId) throws Exception {
        List<QuickTextureEntity> allModelTextures = getAllChapterTextures(chapterId);
        Map<String, String> textureIdCsvMap = new HashMap<>();
        for(QuickTextureEntity textureEntity : allModelTextures) {
            String textureId = textureEntity.getTextureFileId();
            if (!textureEntity.getCsvContent().isEmpty()) {
                textureIdCsvMap.put(textureId, textureEntity.getCsvContent());
            }
        }
        return textureIdCsvMap;
    }

    public Map<String, String> getOtherTextures(String chapterId, TextureController textureController) throws Exception {
        QuickModelEntity quickModelEntity = getChapterFirstQuickModelEntity(chapterId);
        Map<String, String> otherTextures = new HashMap<>();
        for (var texture : quickModelEntity.getOtherTextures()) {
            String otherTextureBase64 = textureController.getTextureBase64(texture.getTextureFileId());
            otherTextures.put(texture.getTextureFileId(), "data:application/octet-stream;base64," + otherTextureBase64);
        }
        return otherTextures;
    }
}
