package cz.uhk.zlesak.threejslearningapp.events.texture;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;
import lombok.Getter;

import java.util.Map;

/**
 * Event representing the loading of additional textures.
 * Contains a map of texture names to their corresponding base64-encoded strings.
 * This event is broadcast at the UI level to decouple components.
 */
@Getter
public class OtherTextureLoadedEvent extends ComponentEvent<UI> {
    private final Map<String, String> base64Textures;

    public OtherTextureLoadedEvent(UI source, Map<String, String> base64Textures) {
        super(source, false);
        this.base64Textures = base64Textures;
    }
}
