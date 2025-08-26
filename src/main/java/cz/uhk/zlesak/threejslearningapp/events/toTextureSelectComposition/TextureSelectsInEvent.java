package cz.uhk.zlesak.threejslearningapp.events.toTextureSelectComposition;

import org.springframework.context.ApplicationEvent;

/**
 * Event indicating that the user has selected a texture in the texture selection component.
 * This is an abstract class to allow for different types of texture selection events.
 */
public abstract class TextureSelectsInEvent extends ApplicationEvent {
    /**
     * Constructor for the event.
     *
     * @param source the component that is the source of the event
     */
    public TextureSelectsInEvent(Object source) {
        super(source);
    }
}
