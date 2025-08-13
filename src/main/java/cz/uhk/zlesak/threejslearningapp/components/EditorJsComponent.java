package cz.uhk.zlesak.threejslearningapp.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import elemental.json.JsonValue;
import org.springframework.context.annotation.Scope;

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
     * Sets the selected subchapter data in the Editor.js instance.
     * This method expects a JSON string that represents the subchapter data.
     *
     * @param jsonData JSON string containing the subchapter data.
     */
    public void setSelectedSubchapterData(String jsonData) {
        getElement().callJsFunction("setSelectedSubchapterData", jsonData);
    }
}