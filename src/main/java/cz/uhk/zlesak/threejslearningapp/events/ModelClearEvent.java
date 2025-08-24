package cz.uhk.zlesak.threejslearningapp.events;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;

/**
 * Event representing the clearing of a model in the application.
 * This event is fired when a model is cleared from the view or context.
 */
public class ModelClearEvent extends ComponentEvent<Component> {
    /**
     * Constructor for ModelClearEvent.
     * @param source the source component that fired the event
     */
    public ModelClearEvent(Component source) {
        super(source, false);
    }
}

