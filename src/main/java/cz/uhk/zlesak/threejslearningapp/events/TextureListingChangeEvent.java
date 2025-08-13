package cz.uhk.zlesak.threejslearningapp.events;

import com.vaadin.flow.component.ComponentEvent;
import cz.uhk.zlesak.threejslearningapp.components.ComboBoxes.TextureListingComboBox;
import cz.uhk.zlesak.threejslearningapp.models.records.TextureListingForComboBoxRecord;

/**
 * Event that is fired when the selected texture listing in the TextureListingComboBox changes.
 * This event is used to trigger actions in the UI after the texture listing selection is changed.
 */
public class TextureListingChangeEvent extends ComponentEvent<TextureListingComboBox> {
    private final TextureListingForComboBoxRecord oldValue;
    private final TextureListingForComboBoxRecord newValue;

    public TextureListingChangeEvent(TextureListingComboBox source, boolean fromClient, TextureListingForComboBoxRecord oldValue, TextureListingForComboBoxRecord newValue) {
        super(source, fromClient);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}
