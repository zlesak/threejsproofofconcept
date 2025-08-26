package cz.uhk.zlesak.threejslearningapp.events.toTextureSelectComposition;

import lombok.Getter;

/**
 * Event to set a texture by its ID in the texture selection component.
 * This event is used to specify which texture should be selected based on its unique identifier.
 */
@Getter
public class SetTextureByIdEvent extends TextureSelectsInEvent {
    private final String textureId;

    /**
     * Constructor for SetTextureByIdEvent.
     *
     * @param source    the component that is the source of the event
     * @param textureId the ID of the texture to be set
     */
    public SetTextureByIdEvent(Object source, String textureId) {
        super(source);
        this.textureId = textureId;
    }
}
