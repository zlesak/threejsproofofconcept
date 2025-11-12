package cz.uhk.zlesak.threejslearningapp.events.editor;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;
import lombok.Getter;

/**
 * Event fired when the markdown mode is toggled.
 * Includes information about the new state of markdown mode.
 */
@Getter
public class MarkdownModeToggleEvent extends ComponentEvent<UI> {
    private final boolean markdownMode;

    public MarkdownModeToggleEvent(UI source, boolean markdownMode) {
        super(source, false);
        this.markdownMode = markdownMode;
    }

}
