package cz.uhk.zlesak.threejslearningapp.application.events;

import com.vaadin.flow.component.ComponentEvent;
import cz.uhk.zlesak.threejslearningapp.application.components.selects.TextureAreaSelect;
import cz.uhk.zlesak.threejslearningapp.application.models.records.TextureAreaForSelectRecord;
import lombok.Getter;

/**
 * Event that is fired when the selected texture area in the TextureAreaSelect changes.
 * This event is used to trigger actions in the UI after the texture area selection is changed.
 */
@Getter
public class TextureAreaChangeEvent extends ComponentEvent<TextureAreaSelect> {
    private final TextureAreaForSelectRecord oldValue;
    private final TextureAreaForSelectRecord newValue;

    public TextureAreaChangeEvent(TextureAreaSelect source, boolean fromClient, TextureAreaForSelectRecord oldValue, TextureAreaForSelectRecord newValue) {
        super(source, fromClient);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}
