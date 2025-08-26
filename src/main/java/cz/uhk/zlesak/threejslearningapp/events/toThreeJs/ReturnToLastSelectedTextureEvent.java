package cz.uhk.zlesak.threejslearningapp.events.toThreeJs;

import com.vaadin.flow.component.Component;

/**
 * Event to return to the last selected texture in a Three.js component.
 * This event is used to revert to the previously selected texture.
 */
public class ReturnToLastSelectedTextureEvent extends ThreeJsInEvent {
    /**
     * Constructor for ReturnToLastSelectedTextureEvent.
     *
     * @param source the component that is the source of the event
     */
    public ReturnToLastSelectedTextureEvent(Component source) {
        super(source);
    }
}
