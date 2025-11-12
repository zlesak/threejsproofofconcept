package cz.uhk.zlesak.threejslearningapp.events.model;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;

/**
 * Event representing the clearing of a model in the application.
 * This event is fired when a model is cleared from the view or context.
 * This event is broadcast at the UI level to decouple components.
 */
public class ModelClearEvent extends ComponentEvent<UI> {
    /**
     * Constructor for ModelClearEvent.
     * @param source the UI that fired the event
     */
    public ModelClearEvent(UI source) {
        super(source, false);
    }
}

