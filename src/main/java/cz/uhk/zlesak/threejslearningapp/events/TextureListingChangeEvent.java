package cz.uhk.zlesak.threejslearningapp.events;

import com.vaadin.flow.component.ComponentEvent;
import cz.uhk.zlesak.threejslearningapp.components.selects.TextureListingSelect;
import cz.uhk.zlesak.threejslearningapp.models.records.TextureListingForSelectRecord;

/**
 * Event that is fired when the selected texture listing in the TextureListingSelect changes.
 * This event is used to trigger actions in the UI after the texture listing selection is changed.
 */
public class TextureListingChangeEvent extends ComponentEvent<TextureListingSelect> {
    private final TextureListingForSelectRecord oldValue;
    private final TextureListingForSelectRecord newValue;

    public TextureListingChangeEvent(TextureListingSelect source, boolean fromClient, TextureListingForSelectRecord oldValue, TextureListingForSelectRecord newValue) {
        super(source, fromClient);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}
