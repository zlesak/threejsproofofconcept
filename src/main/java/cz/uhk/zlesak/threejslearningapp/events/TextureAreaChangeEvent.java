package cz.uhk.zlesak.threejslearningapp.events;

import com.vaadin.flow.component.ComponentEvent;
import cz.uhk.zlesak.threejslearningapp.components.ComboBoxes.TextureAreaComboBox;
import cz.uhk.zlesak.threejslearningapp.models.records.TextureAreaForComboBoxRecord;

/**
 * Event that is fired when the selected texture area in the TextureAreaComboBox changes.
 * This event is used to trigger actions in the UI after the texture area selection is changed.
 */
public class TextureAreaChangeEvent extends ComponentEvent<TextureAreaComboBox> {
    private final TextureAreaForComboBoxRecord oldValue;
    private final TextureAreaForComboBoxRecord newValue;

    public TextureAreaChangeEvent(TextureAreaComboBox source, boolean fromClient, TextureAreaForComboBoxRecord oldValue, TextureAreaForComboBoxRecord newValue) {
        super(source, fromClient);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}
