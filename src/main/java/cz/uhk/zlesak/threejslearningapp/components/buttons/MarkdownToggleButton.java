package cz.uhk.zlesak.threejslearningapp.components.buttons;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import cz.uhk.zlesak.threejslearningapp.events.editor.MarkdownModeToggleEvent;
import cz.uhk.zlesak.threejslearningapp.i18n.I18nAware;

/**
 * A toggle button to switch between Markdown mode and Block Editor mode.
 * Fires a MarkdownModeToggleEvent when toggled.
 */
public class MarkdownToggleButton extends Button implements I18nAware {
    private boolean markdownMode = false;

    public MarkdownToggleButton() {
        super(new Icon(VaadinIcon.CODE));
        setText(text("markdownToggleButton.label.md"));
        addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL);
        getElement().setProperty("title", text("markdownToggleButton.mdModeTooltip"));
        addClickListener(e -> {
            markdownMode = !markdownMode;
            ComponentUtil.fireEvent(UI.getCurrent(), new MarkdownModeToggleEvent(UI.getCurrent(), markdownMode));
            if (markdownMode) {
                setText(text("markdownToggleButton.label.editorjs"));
                setIcon(new Icon(VaadinIcon.FILE_TEXT_O));
                getElement().setProperty("title", text("markdownToggleButton.editorjsToolTip"));
            } else {
                setText(text("markdownToggleButton.label.md"));
                setIcon(new Icon(VaadinIcon.CODE));
                getElement().setProperty("title", text("markdownToggleButton.mdModeTooltip"));
            }
        });
    }
}
