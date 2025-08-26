package cz.uhk.zlesak.threejslearningapp.events.toThreeJs;

import org.springframework.context.ApplicationEvent;

/**
 * Abstract base class for events sent to Three.js components.
 * These events are used to communicate with Three.js components in the application.
 */
public abstract class ThreeJsInEvent extends ApplicationEvent {
    /**
     * Constructor for ThreeJsInEvent.
     *
     * @param source the component that is the source of the event
     */
    public ThreeJsInEvent(Object source) {
        super(source);
    }
}
