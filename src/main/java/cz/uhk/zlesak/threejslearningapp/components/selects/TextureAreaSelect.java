package cz.uhk.zlesak.threejslearningapp.components.selects;

import com.vaadin.flow.component.ComponentEventListener;
import cz.uhk.zlesak.threejslearningapp.events.TextureAreaChangeEvent;
import cz.uhk.zlesak.threejslearningapp.models.records.TextureAreaForSelectRecord;
import org.springframework.context.annotation.Scope;

import java.util.List;

//TODO finish the logic based on the BE implementation

/**
 * TextureAreaSelect is a custom select implementation for selecting texture areas to be shown in the renderer.
 * It extends GenericSelect to provide functionality for handling texture area selection changes.
 */
@Scope("prototype")
public class TextureAreaSelect extends GenericSelect<TextureAreaForSelectRecord, TextureAreaChangeEvent> {
    /**
     * Constructor for TextureAreaSelect.
     * It initializes the select with an empty label, a text generator for items, and sets up the event handling for texture area changes.
     */
    public TextureAreaSelect() {
        super("", TextureAreaForSelectRecord::areaName,
                TextureAreaChangeEvent.class,
                (select, event) -> new TextureAreaChangeEvent((TextureAreaSelect) select, event.isFromClient(), event.getOldValue(), event.getValue()));
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
     * This method is used to populate the select with texture area records.
     * Calls the initialize method from the parent class to set the items.
     *
     * @param textureAreas the list of texture area records to be displayed in the select
     */
    public void initializeTextureAreaSelect(List<TextureAreaForSelectRecord> textureAreas) {
        initialize(textureAreas);
    }
}
