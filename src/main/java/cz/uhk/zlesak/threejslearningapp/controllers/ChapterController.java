package cz.uhk.zlesak.threejslearningapp.controllers;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.dom.DomEventListener;
import cz.uhk.zlesak.threejslearningapp.clients.IChapterApiClient;
import cz.uhk.zlesak.threejslearningapp.data.SubChapterForComboBox;
import cz.uhk.zlesak.threejslearningapp.models.ChapterEntity;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextException;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class ChapterController {
    private String chapterId;
    private final IChapterApiClient chapterApiClient;
    private ChapterEntity chapterEntity = null;
    private static final Logger logger = LoggerFactory.getLogger(ChapterController.class);

    public ChapterController(IChapterApiClient chapterApiClient, String chapterId) {
        this.chapterApiClient = chapterApiClient;
        this.chapterId = chapterId;
    }

    public static Anchor getAnchor(JsonObject contentData, String contentDataId, DomEventListener scrollClickListener) {
        Anchor contentLocationAnchor = new Anchor("#", contentData.getString("text"));
        contentLocationAnchor.setWidthFull();
        contentLocationAnchor.getStyle().set("display", "block");
        contentLocationAnchor.getElement().setAttribute("data-target-id", contentDataId);
        contentLocationAnchor.getElement().addEventListener("click", scrollClickListener)
                .addEventData("event.preventDefault()");
        return contentLocationAnchor;
    }

    public String getChapterName() throws Exception {
        return this.getChapter().getChapterEntityName();
    }
    public String getChapterEntityContent() throws Exception {
        return this.getChapter().getChapterEntityContent();
    }

    public List<SubChapterForComboBox> getSubChaptersNames() throws Exception {
        List<SubChapterForComboBox> subChapters = new ArrayList<>();
        try {
            JsonArray blocks = Json.parse(this.getChapter().getChapterEntityContent()).getArray("blocks");

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
            JsonArray blocks = Json.parse(this.getChapter().getChapterEntityContent()).getArray("blocks");
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
        JsonArray blocks = Json.parse(this.getChapter().getChapterEntityContent()).getArray("blocks");

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

    private ChapterEntity getChapter() throws Exception {
        if (chapterEntity == null) {
            try{
                chapterEntity = chapterApiClient.getChapter(chapterId);
            }catch(Exception e){
                logger.error("Chyba při získávání kapitoly: {}", e.getMessage(), e);
                throw new Exception("Chyba při získávání kapitoly: " + e.getMessage());
            }
        }
        return chapterEntity;
    }
    public String getChapterModel() throws Exception {
        try{
            Resource modelResource =  chapterApiClient.downloadModel(chapterId);
            if (modelResource != null) {
                InputStream inputStream = modelResource.getInputStream();
                byte[] bytes = inputStream.readAllBytes();
                return java.util.Base64.getEncoder().encodeToString(bytes);
            }else{
                throw new ApplicationContextException("Model resource is null for chapter ID: " + chapterId);
            }
        }catch (ApplicationContextException e) {
            logger.error("Chyba při získávání modelu kapitoly: {}", e.getMessage(), e);
            throw new Exception("Chyba při získávání modelu kapitoly: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Chyba při čtení dat modelu kapitoly: {}", e.getMessage(), e);
            throw new Exception("Chyba při čtení dat modelu kapitoly: " + e.getMessage());
        }
    }

    // TODO implement searchInChapterTextField.addValueChangeListener
}
