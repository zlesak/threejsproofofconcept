package cz.uhk.zlesak.threejslearningapp.components.selects;

import com.vaadin.flow.component.ComponentEventListener;
import cz.uhk.zlesak.threejslearningapp.events.TextureListingChangeEvent;
import cz.uhk.zlesak.threejslearningapp.models.records.TextureListingForSelectRecord;
import org.springframework.context.annotation.Scope;

import java.util.List;

/**
 * TextureListingSelect is a custom select for selecting texture listings.
 * It extends GenericSelect to provide functionality for handling texture listing selection changes.
 */
@Scope("prototype")
public class TextureListingSelect extends GenericSelect<TextureListingForSelectRecord, TextureListingChangeEvent> {
    /**
     * Constructor for TextureListingSelect.
     * It initializes the select with an empty label, a text generator for items, and sets up the event handling for texture listing changes.
     * Calls the parent class constructor with the appropriate parameters.
     */
    public TextureListingSelect() {
        super("", TextureListingForSelectRecord::textureName,
                TextureListingChangeEvent.class,
                (select, event) -> new TextureListingChangeEvent((TextureListingSelect) select, event.isFromClient(), event.getOldValue(), event.getValue()));
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
     * This method is used to populate the select with texture listing records.
     * Calls the initialize method from the parent class to set the items.
     *
     * @param textureListings the list of texture listing records to be displayed in the select
     */
    public void initializeTextureListingSelect(List<TextureListingForSelectRecord> textureListings) {
        initialize(textureListings);
    }
}
