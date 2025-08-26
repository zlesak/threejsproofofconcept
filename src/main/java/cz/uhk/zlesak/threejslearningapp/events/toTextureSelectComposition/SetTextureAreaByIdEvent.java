package cz.uhk.zlesak.threejslearningapp.events.toTextureSelectComposition;

import lombok.Getter;

/**
 * Event to set a texture area by its ID and color in the texture selection component.
 * This event is used to specify which texture area should be selected based on its unique identifier and color.
 */
@Getter
public class SetTextureAreaByIdEvent extends TextureSelectsInEvent {
    private final String textureId;
    private final String hexColor;

    /**
     * Constructor for SetTextureAreaByIdEvent.
     *
     * @param source    the component that is the source of the event
     * @param textureId the ID of the texture area to be set
     * @param hexColor  the hex color of the texture area to be set
     */
    public SetTextureAreaByIdEvent(Object source, String textureId, String hexColor) {
        super(source);
        this.textureId = textureId;
        this.hexColor = hexColor;
    }
}
