package cz.uhk.zlesak.threejslearningapp.events;

import com.vaadin.flow.component.ComponentEvent;
import cz.uhk.zlesak.threejslearningapp.components.ComboBoxes.TextureListingComboBox;
import cz.uhk.zlesak.threejslearningapp.models.records.TextureListingForComboBoxRecord;

public class TextureListingChangeEvent extends ComponentEvent<TextureListingComboBox> {
    private final TextureListingForComboBoxRecord oldValue;
    private final TextureListingForComboBoxRecord newValue;

    public TextureListingChangeEvent(TextureListingComboBox source, boolean fromClient, TextureListingForComboBoxRecord oldValue, TextureListingForComboBoxRecord newValue) {
        super(source, fromClient);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}
