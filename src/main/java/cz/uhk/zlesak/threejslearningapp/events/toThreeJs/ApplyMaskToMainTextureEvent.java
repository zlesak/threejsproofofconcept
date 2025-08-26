package cz.uhk.zlesak.threejslearningapp.events.toThreeJs;

import com.vaadin.flow.component.Component;
import lombok.Getter;

/**
 * Event to apply a mask to the main texture of a model in a Three.js component.
 * This event carries the ID of the texture and the hex color for the mask.
 */
@Getter
public class ApplyMaskToMainTextureEvent extends ThreeJsInEvent {
    private final String textureId;
    private final String hexColor;

    /**
     * Constructor for ApplyMaskToMainTextureEvent.
     *
     * @param source    the component that is the source of the event
     * @param textureId the ID of the texture to which the mask will be applied
     * @param hexColor  the hex color of the mask to be applied
     */
    public ApplyMaskToMainTextureEvent(Component source, String textureId, String hexColor) {
        super(source);
        this.textureId = textureId;
        this.hexColor = hexColor;
    }

}

