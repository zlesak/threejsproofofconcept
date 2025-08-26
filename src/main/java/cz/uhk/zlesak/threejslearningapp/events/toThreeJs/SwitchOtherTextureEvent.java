package cz.uhk.zlesak.threejslearningapp.events.toThreeJs;

import com.vaadin.flow.component.Component;
import lombok.Getter;

/**
 * Event to switch the texture of a model in a Three.js component.
 * This event carries the ID of the new texture to be applied.
 */
@Getter
public class SwitchOtherTextureEvent extends ThreeJsInEvent {
    private final String textureId;

    /**
     * Constructor for SwitchOtherTextureEvent.
     *
     * @param source    the component that is the source of the event
     * @param textureId the ID of the texture to switch to
     */
    public SwitchOtherTextureEvent(Component source, String textureId) {
        super(source);
        this.textureId = textureId;
    }

}
