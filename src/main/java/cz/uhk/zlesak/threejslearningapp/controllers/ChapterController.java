package cz.uhk.zlesak.threejslearningapp.controllers;

import cz.uhk.zlesak.threejslearningapp.clients.ChapterApiClient;
import cz.uhk.zlesak.threejslearningapp.models.SubChapterForComboBox;
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

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Scope("prototype")
public class ChapterController {
    private final ChapterApiClient chapterApiClient;
    private ChapterEntity chapterEntity = null;
    @Setter
    private QuickModelEntity uploadedModel = null;

    @Autowired
    public ChapterController(ChapterApiClient chapterApiClient) {
        this.chapterApiClient = chapterApiClient;
    }

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
        log.info("Vytváření kapitoly s názvem: {}", chapter.getName());
        log.info("Obsah kapitoly: {}", chapter.getContent());
        log.info("Přiřazený model: {}", chapter.getModels().getFirst().getModel().getName());
        try {
            return chapterApiClient.createChapter(chapter);
        } catch (Exception e) {
            log.error("Chyba při vytváření kapitoly: {}", e.getMessage(), e);
            throw e;
        }
    }

    public String getChapterName() {
        return chapterEntity.getName();
    }

    public String getChapterEntityContent() {
        return chapterEntity.getContent();
    }

    public List<SubChapterForComboBox> getSubChaptersNames() {
        List<SubChapterForComboBox> subChapters = new ArrayList<>();
        try {
            JsonArray blocks = Json.parse(chapterEntity.getContent()).getArray("blocks");

            subChapters.add(new SubChapterForComboBox("", "Vyberte podkapitolu"));
            for (int i = 0; i < blocks.length(); i++) {
                JsonObject block = blocks.getObject(i);
                if ("header".equals(block.getString("type")) && block.getObject("data").getNumber("level") == 1) {
                    String id = block.hasKey("id") ? block.getString("id") : "fallback-" + java.util.UUID.randomUUID().toString().substring(0, 7);
                    String text = block.getObject("data").getString("text");
                    subChapters.add(new SubChapterForComboBox(id, text));
                }
            }
            return subChapters;
        } catch (Exception e) {
            log.error("Error getting subchapter names: {}", e.getMessage(), e);
            throw e;
        }
    }

    public JsonArray getSubChaptersContent() {
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

    public void getChapter(String chapterId) throws Exception {
        try {
            chapterEntity = chapterApiClient.getChapterById(chapterId);
        } catch (Exception e) {
            log.error("Chyba při získávání kapitoly: {}", e.getMessage(), e);
            throw new Exception("Chyba při získávání kapitoly: " + e.getMessage());
        }
    }

    public QuickModelEntity getChapterFirstQuickModelEntity() throws Exception {
        try {
            return chapterEntity.getModels().getFirst();
        } catch (Exception e) {
            log.error("Chyba při čtení dat modelu kapitoly: {}", e.getMessage(), e);
            throw new Exception("Chyba při čtení dat modelu kapitoly: " + e.getMessage());
        }
    }
}
