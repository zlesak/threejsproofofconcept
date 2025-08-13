package cz.uhk.zlesak.threejslearningapp.components.ComboBoxes;

import cz.uhk.zlesak.threejslearningapp.events.TextureAreaChangeEvent;
import cz.uhk.zlesak.threejslearningapp.models.records.TextureAreaForComboBoxRecord;
import com.vaadin.flow.component.ComponentEventListener;
import org.springframework.context.annotation.Scope;

import java.util.List;

//TODO finish the logic based on the BE implementation
/**
 * TextureAreaComboBox is a custom combo box for selecting texture areas to be shown in the renderer.
 * It extends GenericSelectionComboBox to provide functionality for handling texture area selection changes.
 */
@Scope("prototype")
public class TextureAreaComboBox extends GenericSelectionComboBox<TextureAreaForComboBoxRecord, TextureAreaChangeEvent> {
    /**
     * Constructor for TextureAreaComboBox.
     * It initializes the combo box with an empty label, a text generator for items, and sets up the event handling for texture area changes.
     * Calls the parent class constructor with the appropriate parameters.
     */
    public TextureAreaComboBox() {
        super("", TextureAreaForComboBoxRecord::areaName,
              TextureAreaChangeEvent.class,
              (combo, event) -> new TextureAreaChangeEvent((TextureAreaComboBox) combo, event.isFromClient(), event.getOldValue(), event.getValue()));
    }

    /**
     * Adds a listener for texture area change events.
     * Calls the addGenericChangeListener method from the parent class to register the listener.
     *
     * @param listener the listener to be added
     */
    public void addTextureAreaChangeListener(ComponentEventListener<TextureAreaChangeEvent> listener) {
        addGenericChangeListener(listener);
    }

    /**
     * This method is used to populate the combo box with texture area records.
     * Calls the initialize method from the parent class to set the items.
     *
     * @param textureAreas the list of texture area records to be displayed in the combo box
     */
    public void initializeTextureAreaComboBox(List<TextureAreaForComboBoxRecord> textureAreas) {
        initialize(textureAreas);
    }
}
