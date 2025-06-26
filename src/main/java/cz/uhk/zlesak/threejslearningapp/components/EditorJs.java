package cz.uhk.zlesak.threejslearningapp.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import elemental.json.JsonValue;

import java.util.concurrent.CompletableFuture;

@Tag("editor-js")
@JsModule("./js/editor-js.ts")
@NpmPackage(value = "@editorjs/editorjs", version = "2.30.8")
public class EditorJs extends Component implements HasSize, HasStyle {
    public EditorJs() {
    }

    public CompletableFuture<String> getData() {
        return getElement()
                .callJsFunction("getDataAsString")
                .toCompletableFuture()
                .thenApply(JsonValue::asString);
    }

    public CompletableFuture<Void> setData(String jsonData) {
        return getElement()
                .callJsFunction("setData", jsonData)
                .toCompletableFuture()
                .exceptionally(error -> {
                    throw new RuntimeException("Chyba při nastavování kapitoly: " + error.getMessage());
                })
                .thenApply(ignore -> null);
    }

    public void toggleReadOnlyMode(boolean readOnly) {
        getElement().callJsFunction("toggleReadOnlyMode", readOnly);
    }

    public void clear() {
        getElement().callJsFunction("clear");
    }

    public CompletableFuture<JsonValue> getSubChaptersNames() {
        return getElement().callJsFunction("getSubChaptersNames").toCompletableFuture();
    }

    public CompletableFuture<JsonValue> getSubchaptersContent() {
        return getElement().callJsFunction("getSubchaptersContent").toCompletableFuture();
    }

    public CompletableFuture<String> getChapterContentData() {
        return getElement()
                .callJsFunction("getChapterContentData")
                .toCompletableFuture()
                .thenApply(JsonValue::asString);
    }

    public CompletableFuture<Void> setChapterContentData(String jsonData) {
        return getElement()
                .callJsFunction("setChapterContentData", jsonData)
                .toCompletableFuture()
                .exceptionally(error -> {
                    throw new RuntimeException("Chyba při nastavování chapterContentData: " + error.getMessage());
                })
                .thenApply(ignore -> null);
    }

    public CompletableFuture<Void> selectedSubChapterContentSet(String id) {
        return getElement()
                .callJsFunction("selectedSubChapterContentSet", id)
                .toCompletableFuture()
                .exceptionally(error -> {
                    throw new RuntimeException("Chyba při nastavování selectedSubChapterContentSet: " + error.getMessage());
                })
                .thenApply(ignore -> null);
    }
}