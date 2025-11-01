package cz.uhk.zlesak.threejslearningapp.application.components.compositions;

import com.flowingcode.vaadin.addons.markdown.MarkdownEditor;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import cz.uhk.zlesak.threejslearningapp.application.components.MarkdownUploadComponent;
import cz.uhk.zlesak.threejslearningapp.application.components.UploadComponent;
import cz.uhk.zlesak.threejslearningapp.application.components.buttons.CreateChapterButton;
import cz.uhk.zlesak.threejslearningapp.application.components.buttons.MarkdownToggleButton;
import cz.uhk.zlesak.threejslearningapp.application.components.editors.EditorJsComponent;
import lombok.Getter;

/**
 * A toolbar composition for creating chapters with markdown support.
 * Includes buttons for creating chapters, toggling markdown view, and uploading markdown files.
 */
public class CreateChapterToolBarComposition extends HorizontalLayout {
    @Getter
    private CreateChapterButton createChapterButton;

    public CreateChapterToolBarComposition(EditorJsComponent editorjs, MarkdownEditor mdEditor) {
        super();
        createChapterButton = new CreateChapterButton();
        Button markdownToggleButton = new MarkdownToggleButton();
        Button mdUploadedButton = new Button();
        mdUploadedButton.setVisible(false);
        UploadComponent getMdUploadComponent = new MarkdownUploadComponent(editorjs, mdEditor, mdUploadedButton);

        add(markdownToggleButton, mdUploadedButton, getMdUploadComponent, createChapterButton);
        setWidthFull();
        setSpacing(true);
        setPadding(false);
        setAlignItems(FlexComponent.Alignment.STRETCH);
        setFlexGrow(0, markdownToggleButton);
        setFlexGrow(0, getMdUploadComponent);
        setFlexGrow(1, createChapterButton);
    }
}
