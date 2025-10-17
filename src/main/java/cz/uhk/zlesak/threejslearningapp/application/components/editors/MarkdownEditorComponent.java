package cz.uhk.zlesak.threejslearningapp.application.components.editors;

import com.flowingcode.vaadin.addons.markdown.MarkdownEditor;

public class MarkdownEditorComponent extends MarkdownEditor {
    public MarkdownEditorComponent() {
        super();
        setSizeFull();
        setPlaceholder("Začněte tvořit pomocí syntaxe markdown...");
        setVisible(false);
    }
}
