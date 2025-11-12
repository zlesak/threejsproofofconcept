package cz.uhk.zlesak.threejslearningapp.events.texture;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;
import lombok.Getter;

/**
 * Event representing the removal of an additional texture.
 * Contains the name of the texture that was removed.
 * This event is broadcast at the UI level to decouple components.
 */
@Getter
public class OtherTextureRemovedEvent extends ComponentEvent<UI> {
    private final String name;

    public OtherTextureRemovedEvent(UI source, String name) {
        super(source, false);
        this.name = name;
    }
}
