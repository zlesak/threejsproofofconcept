package cz.uhk.zlesak.threejslearningapp.events;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import cz.uhk.zlesak.threejslearningapp.components.compositions.ModelUploadFormScrollerComposition;
import lombok.Getter;

import java.util.Map;

/**
 * Event representing the loading of additional textures.
 * Contains a map of texture names to their corresponding base64-encoded strings.
 *
 * @see ModelUploadFormScrollerComposition for usage
 */
@Getter
public class OtherTextureLoadedEvent extends ComponentEvent<Component> {
    private final Map<String, String> base64Textures;

    public OtherTextureLoadedEvent(Component source, Map<String, String> base64Textures) {
        super(source, false);
        this.base64Textures = base64Textures;
    }
}
