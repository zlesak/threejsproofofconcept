package cz.uhk.zlesak.threejslearningapp.application.components;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import cz.uhk.zlesak.threejslearningapp.application.models.entities.quickEntities.QuickTextureEntity;
import cz.uhk.zlesak.threejslearningapp.application.models.records.TextureAreaForSelectRecord;
import cz.uhk.zlesak.threejslearningapp.application.models.records.TextureListingForSelectRecord;
import cz.uhk.zlesak.threejslearningapp.application.models.records.parsers.TextureAreaDataParser;
import cz.uhk.zlesak.threejslearningapp.application.models.records.parsers.TextureListingDataParser;
import cz.uhk.zlesak.threejslearningapp.application.utils.TextureMapHelper;
import elemental.json.JsonValue;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Custom component for Editor.js integration in Vaadin.
 * This component allows interaction with Editor.js, including getting data,
 * toggling read-only mode, and setting chapter content data.
 * It uses JavaScript interop to call methods defined in the Editor.js JavaScript module.
 * This is the heart of the Editor.js integration, allowing for rich text editing capabilities within a Vaadin application.
 */
@Tag("editor-js")
@JsModule("./js/editor-js.ts")
@NpmPackage(value = "@editorjs/editorjs", version = "2.30.8")
@Scope("prototype")
public class EditorJsComponent extends Component implements HasSize, HasStyle {
    /**
     * Default constructor for EditorJsComponent.
     * Does not take any parameters as they are not needed at the time of instantiation.
     */
    public EditorJsComponent() {
    }

    /**
     * Retrieves data from the Editor.js instance as a JSON string.
     *
     * @return JSON as string with data retrieved from Editor.js.
     */
    public CompletableFuture<String> getData() {
        return getElement()
                .callJsFunction("getDataAsString")
                .toCompletableFuture()
                .thenApply(JsonValue::asString);
    }

    /**
     * Toggles the read-only mode of the Editor.js instance.
     * When in read-only mode, users cannot edit the content.
     *
     * @param readOnly true to enable read-only mode, false to disable it.
     */
    public void toggleReadOnlyMode(boolean readOnly) {
        getElement().callJsFunction("toggleReadOnlyMode", readOnly);
    }

    /**
     * Sets the chapter content data in the Editor.js instance.
     * This method expects a JSON string that represents the chapter content.
     *
     * @param jsonData JSON string containing the chapter content data.
     */
    public void setChapterContentData(String jsonData) {
        getElement()
                .callJsFunction("setChapterContentData", jsonData)
                .toCompletableFuture()
                .exceptionally(error -> {
                    throw new RuntimeException("Chyba při nastavování chapterContentData: " + error.getMessage());
                })
                .thenApply(ignore -> null);
    }

    /**
     * Shows the whole chapter data in the Editor.js instance.
     * This method is used to display all the content of the chapter, including subchapters.
     * Used when no specific subchapter is selected.
     */
    public void showWholeChapterData() {
        getElement().callJsFunction("showWholeChapterData")
                .toCompletableFuture()
                .exceptionally(error -> {
                    throw new RuntimeException("Chyba při zobrazování dat celé kapitoly " + error.getMessage());
                })
                .thenApply(ignore -> null);
    }

    /**
     * Sets the selected subchapter data in the Editor.js instance.
     * This method expects a JSON string that represents the subchapter data.
     *
     * @param jsonData JSON string containing the subchapter data.
     */
    public void setSelectedSubchapterData(String jsonData) {
        getElement().callJsFunction("setSelectedSubchapterData", jsonData);
    }

    /**
     * Initializes texture selection options in the Editor.js instance.
     * This method takes a list of QuickTextureEntity objects, processes them,
     * and passes the relevant data to the JavaScript side for initializing custom TextureColorLinkTool inline tool.
     *
     * @param quickModelEntityList list of QuickTextureEntity objects to be processed for texture selection.
     */
    public void initializeTextureSelects(List<QuickTextureEntity> quickModelEntityList) {
        List<TextureListingForSelectRecord> otherTexturesMap = TextureListingDataParser.textureListingForSelectDataParser(quickModelEntityList);
        List<TextureAreaForSelectRecord> textureAreaForSelectRecord = TextureAreaDataParser.csvParse(TextureMapHelper.createCsvMap(quickModelEntityList));
        ObjectMapper mapper = new ObjectMapper();
        try {
            String texturesJson = mapper.writeValueAsString(otherTexturesMap);
            String areasJson = mapper.writeValueAsString(textureAreaForSelectRecord);
            getElement().callJsFunction("initializeTextureSelects", texturesJson, areasJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Chyba při serializaci texture dat: " + e.getMessage());
        }
    }

    /**
     * Adds a listener for texture color area click events.
     * When a texture color area is clicked in the Editor.js instance, this listener will be triggered.
     * The listener receives the texture ID, hex color, and associated text as parameters.
     *
     * @param listener the listener to be added
     */
    public void addTextureColorAreaClickListener(TextureColorAreaClickListener listener) {
        getElement().addEventListener("texturecolorareaclick", event -> {
                    String textureId = event.getEventData().getString("event.detail.textureId");
                    String hexColor = event.getEventData().getString("event.detail.hexColor");
                    String text = event.getEventData().getString("event.detail.text");
                    listener.onTextureColorAreaClick(textureId, hexColor, text);
                }).addEventData("event.detail.textureId")
                .addEventData("event.detail.hexColor")
                .addEventData("event.detail.text");
    }

    /**
     * Searches for the given text in the Editor.js instance.
     * This method triggers a search operation within the Editor.js content.
     * @param searchText the text to search for within the Editor.js content.
     * @see SearchTextField
     */
    public void search(String searchText){
        getElement().callJsFunction("search", searchText)
                .toCompletableFuture()
                .exceptionally(error -> {
                    throw new RuntimeException("Chyba při vyhledávání " + error.getMessage());
                })
                .thenApply(ignore -> null);
    }

    /**
     * Listener interface for handling texture color area click events.
     * Implement this interface to define custom behavior when a texture color area is clicked.
     */
    public interface TextureColorAreaClickListener {
        void onTextureColorAreaClick(String textureId, String hexColor, String text);
    }
}