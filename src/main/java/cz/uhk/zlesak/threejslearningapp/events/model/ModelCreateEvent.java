package cz.uhk.zlesak.threejslearningapp.events.model;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;
import lombok.Getter;

/**
 * Event fired when a model should be created/uploaded.
 * This event is broadcast at the UI level to decouple the button from the upload logic.
 */
@Getter
public class ModelCreateEvent extends ComponentEvent<UI> {
    private final String modelName;
    private final boolean isAdvanced;

    /**
     * Constructor for ModelCreateEvent.
     *
     * @param source      the UI that fired the event
     * @param modelName   the name of the model to create
     * @param isAdvanced  whether this is an advanced upload (with textures)
     */
    public ModelCreateEvent(UI source, String modelName, boolean isAdvanced) {
        super(source, false);
        this.modelName = modelName;
        this.isAdvanced = isAdvanced;
    }
}

