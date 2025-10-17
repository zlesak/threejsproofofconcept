package cz.uhk.zlesak.threejslearningapp.application.components.buttons;

import com.flowingcode.vaadin.addons.markdown.MarkdownEditor;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import cz.uhk.zlesak.threejslearningapp.application.components.editors.EditorJsComponent;

public class MarkdownToggleButton extends Button {

    public MarkdownToggleButton(MarkdownEditor mdEditor, EditorJsComponent editorjs) {
        super("Markdown", new Icon(VaadinIcon.CODE));
        addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL);
        getElement().setProperty("title", "Přepnout na Markdown režim (nebo zpět)");
        addClickListener(e -> editorjs.toggleMarkdownMode());
        editorjs.addMarkdownModeChangedListener(mode -> {
            if (mode) {
                setText("Blokový editor");
                setIcon(new Icon(VaadinIcon.FILE_TEXT_O));
                getElement().setProperty("title", "Přepnout zpět na blokový Editor.js");
                editorjs.getMarkdown().thenAccept(mdEditor::setValue);
                mdEditor.setVisible(true);
            } else {
                setText("Markdown");
                setIcon(new Icon(VaadinIcon.CODE));
                getElement().setProperty("title", "Přepnout na Markdown režim");
                editorjs.loadMarkdown(mdEditor.getValue());
                mdEditor.setVisible(false);
            }
        });
    }
}
