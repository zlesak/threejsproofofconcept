package cz.uhk.zlesak.threejslearningapp.events.texture;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import cz.uhk.zlesak.threejslearningapp.components.forms.ModelUploadForm;
import lombok.Getter;

/**
 * Event representing the removal of an additional texture.
 * Contains the name of the texture that was removed.
 *
 * @see ModelUploadForm for usage
 */
@Getter
public class OtherTextureRemovedEvent extends ComponentEvent<Component> {
    private final String name;

    public OtherTextureRemovedEvent(Component source, String name) {
        super(source, false);
        this.name = name;
    }
}
