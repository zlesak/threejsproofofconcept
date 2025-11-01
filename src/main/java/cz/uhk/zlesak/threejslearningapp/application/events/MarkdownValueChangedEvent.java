package cz.uhk.zlesak.threejslearningapp.application.events;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;
import lombok.Getter;

/**
 * Event fired when the markdown value is changed.
 * Includes the new markdown value and the current markdown mode state.
 */
@Getter
public class MarkdownValueChangedEvent extends ComponentEvent<UI> {
    private final String markdownValue;
    private final boolean markdownMode;

    public MarkdownValueChangedEvent(UI source, String markdownValue, boolean markdownMode) {
        super(source, false);
        this.markdownValue = markdownValue;
        this.markdownMode = markdownMode;
    }

}