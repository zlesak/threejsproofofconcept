package cz.uhk.zlesak.threejslearningapp.components.ComboBoxes;

import cz.uhk.zlesak.threejslearningapp.events.TextureListingChangeEvent;
import cz.uhk.zlesak.threejslearningapp.models.records.TextureListingForComboBoxRecord;
import com.vaadin.flow.component.ComponentEventListener;
import org.springframework.context.annotation.Scope;

import java.util.List;

/**
 * TextureListingComboBox is a custom combo box for selecting texture listings.
 * It extends GenericSelectionComboBox to provide functionality for handling texture listing selection changes.
 */
@Scope("prototype")
public class TextureListingComboBox extends GenericSelectionComboBox<TextureListingForComboBoxRecord, TextureListingChangeEvent> {
    /**
     * Constructor for TextureListingComboBox.
     * It initializes the combo box with an empty label, a text generator for items, and sets up the event handling for texture listing changes.
     * Calls the parent class constructor with the appropriate parameters.
     */
    public TextureListingComboBox() {
        super("", TextureListingForComboBoxRecord::textureName,
              TextureListingChangeEvent.class,
              (combo, event) -> new TextureListingChangeEvent((TextureListingComboBox) combo, event.isFromClient(), event.getOldValue(), event.getValue()));
    }

    /**
     * Adds a listener for texture listing change events.
     * Calls the addGenericChangeListener method from the parent class to register the listener.
     *
     * @param listener the listener to be added
     */
    public void addTextureListingChangeListener(ComponentEventListener<TextureListingChangeEvent> listener) {
        addGenericChangeListener(listener);
    }

    /**
     * This method is used to populate the combo box with texture listing records.
     * Calls the initialize method from the parent class to set the items.
     *
     * @param textureListings the list of texture listing records to be displayed in the combo box
     */
    public void initializeTextureListingComboBox(List<TextureListingForComboBoxRecord> textureListings) {
        initialize(textureListings);
    }
}
