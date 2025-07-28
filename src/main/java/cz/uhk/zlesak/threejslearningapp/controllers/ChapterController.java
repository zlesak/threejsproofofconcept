package cz.uhk.zlesak.threejslearningapp.controllers;

import cz.uhk.zlesak.threejslearningapp.clients.ChapterApiClient;
import cz.uhk.zlesak.threejslearningapp.clients.ModelApiClient;
import cz.uhk.zlesak.threejslearningapp.data.SubChapterForComboBox;
import cz.uhk.zlesak.threejslearningapp.models.ChapterEntity;
import cz.uhk.zlesak.threejslearningapp.models.ModelEntity;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChapterController {
    private static final Logger logger = LoggerFactory.getLogger(ChapterController.class);
    private final ChapterApiClient chapterApiClient;
    private final ModelApiClient modelApiClient;
    private ChapterEntity chapterEntity = null;

    @Autowired
    public ChapterController(ChapterApiClient chapterApiClient, ModelApiClient modelApiClient) {
        this.chapterApiClient = chapterApiClient;
        this.modelApiClient = modelApiClient;
    }

    public ChapterEntity createChapter(String name, String content) throws Exception {
        ChapterEntity chapter = ChapterEntity.builder()
                .ChapterEntityName(name)
                .ChapterEntityContent(content)
                .build();
        try {
            return chapterApiClient.createChapter(chapter);
        } catch (Exception e) {
            logger.error("Chyba při vytváření kapitoly: {}", e.getMessage(), e);
            throw e;
        }
    }

    public String getChapterName() throws Exception {
        return chapterEntity.getChapterEntityName();
    }

    public String getChapterEntityContent() throws Exception {
        return chapterEntity.getChapterEntityContent();
    }

    public List<SubChapterForComboBox> getSubChaptersNames() throws Exception {
        List<SubChapterForComboBox> subChapters = new ArrayList<>();
        try {
            JsonArray blocks = Json.parse(chapterEntity.getChapterEntityContent()).getArray("blocks");

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
            logger.error("Error getting subchapter names: {}", e.getMessage(), e);
            throw e;
        }
    }

    public JsonArray getSubChaptersContent() throws Exception {
        try {
            JsonArray blocks = Json.parse(chapterEntity.getChapterEntityContent()).getArray("blocks");
            JsonArray result = Json.createArray();
            JsonObject oldH1Text = null;
            JsonObject currentH1Text = null;
            JsonArray subchapterData = Json.createArray();
            int resultIndex = 0;
            for (int i = 0; i < blocks.length(); i++) {
                JsonObject block = blocks.getObject(i);
                if ("header".equals(block.getString("type")) && block.getObject("data").getNumber("level") == 1) {
                    oldH1Text = currentH1Text;
                    currentH1Text = block;
                    if (subchapterData.length() != 0 && oldH1Text != null) {
                        JsonObject obj = Json.createObject();
                        obj.put("h1", oldH1Text);
                        obj.put("content", subchapterData);
                        result.set(resultIndex++, obj);
                    } else if (oldH1Text != null) {
                        JsonObject obj = Json.createObject();
                        obj.put("h1", oldH1Text);
                        obj.put("content", Json.createArray());
                        result.set(resultIndex++, obj);
                    } else if (i > 0) {
                        JsonObject noHeader = Json.createObject();
                        noHeader.put("id", "fallback-" + java.util.UUID.randomUUID().toString().substring(0, 7));
                        noHeader.put("type", "header");
                        JsonObject data = Json.createObject();
                        data.put("text", "Obsah bez hlavního nadpisu");
                        data.put("level", 1);
                        noHeader.put("data", data);
                        JsonObject obj = Json.createObject();
                        obj.put("h1", noHeader);
                        obj.put("content", subchapterData);
                        result.set(resultIndex++, obj);
                    }
                    subchapterData = Json.createArray();
                    if (i == blocks.length() - 1) {
                        JsonObject obj = Json.createObject();
                        obj.put("h1", currentH1Text);
                        obj.put("content", Json.createArray());
                        result.set(resultIndex++, obj);
                    }
                } else {
                    if ("header".equals(block.getString("type"))) {
                        subchapterData.set(subchapterData.length(), block);
                    }
                }
            }
            return result;
        } catch (Exception e) {
            logger.error("Error getting subchapters content: {}", e.getMessage(), e);
            throw e;
        }
    }

    public String getSelectedSubChapterContent(String id) throws Exception {
        JsonArray blocks = Json.parse(chapterEntity.getChapterEntityContent()).getArray("blocks");

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
            } else {
                if ("header".equals(block.getString("type")) && block.getObject("data").getNumber("level") == 1) {
                    break;
                }
                content.set(contentIndex++, block);
            }
        }
        return content.toJson();
    }

    public void getChapter(String chapterId) throws Exception {
        if (chapterEntity == null) {
            try {
                chapterEntity = chapterApiClient.getChapter(chapterId);
            } catch (Exception e) {
                logger.error("Chyba při získávání kapitoly: {}", e.getMessage(), e);
                throw new Exception("Chyba při získávání kapitoly: " + e.getMessage());
            }
        }
    }

    public ModelEntity getChapterModel() throws Exception {
        try{
            return (ModelEntity) modelApiClient.downloadFileEntityById(chapterEntity.getChapterEntityModelEntities().getFirst()); //TODO apply to the main model instead of the first one
        } catch (Exception e) {
            logger.error("Chyba při čtení dat modelu kapitoly: {}", e.getMessage(), e);
            throw new Exception("Chyba při čtení dat modelu kapitoly: " + e.getMessage());
        }
    }
}
