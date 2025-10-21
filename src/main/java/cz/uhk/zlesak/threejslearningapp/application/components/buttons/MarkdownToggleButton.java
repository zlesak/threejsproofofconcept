package cz.uhk.zlesak.threejslearningapp.application.components.buttons;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import cz.uhk.zlesak.threejslearningapp.application.events.MarkdownModeToggleEvent;

/**
 * A toggle button to switch between Markdown mode and Block Editor mode.
 * Fires a MarkdownModeToggleEvent when toggled.
 */
public class MarkdownToggleButton extends Button {
    private boolean markdownMode = false;

    public MarkdownToggleButton() {
        super("Markdown", new Icon(VaadinIcon.CODE));
        addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL);
        getElement().setProperty("title", "Přepnout na Markdown režim");
        addClickListener(e -> {
            markdownMode = !markdownMode;
            ComponentUtil.fireEvent(UI.getCurrent(), new MarkdownModeToggleEvent(UI.getCurrent(), markdownMode));
            if (markdownMode) {
                setText("Blokový editor");
                setIcon(new Icon(VaadinIcon.FILE_TEXT_O));
                getElement().setProperty("title", "Přepnout zpět na blokový Editor.js");
            } else {
                setText("Markdown");
                setIcon(new Icon(VaadinIcon.CODE));
                getElement().setProperty("title", "Přepnout na Markdown režim");
            }
        });
    }
}
