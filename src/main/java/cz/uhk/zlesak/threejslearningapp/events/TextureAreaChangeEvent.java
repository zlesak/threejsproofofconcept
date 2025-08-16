package cz.uhk.zlesak.threejslearningapp.events;

import com.vaadin.flow.component.ComponentEvent;
import cz.uhk.zlesak.threejslearningapp.components.Selects.TextureAreaSelect;
import cz.uhk.zlesak.threejslearningapp.models.records.TextureAreaForSelectRecord;

/**
 * Event that is fired when the selected texture area in the TextureAreaSelect changes.
 * This event is used to trigger actions in the UI after the texture area selection is changed.
 */
public class TextureAreaChangeEvent extends ComponentEvent<TextureAreaSelect> {
    private final TextureAreaForSelectRecord oldValue;
    private final TextureAreaForSelectRecord newValue;

    public TextureAreaChangeEvent(TextureAreaSelect source, boolean fromClient, TextureAreaForSelectRecord oldValue, TextureAreaForSelectRecord newValue) {
        super(source, fromClient);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}
