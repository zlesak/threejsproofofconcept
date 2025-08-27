package cz.uhk.zlesak.threejslearningapp.events;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import cz.uhk.zlesak.threejslearningapp.components.compositions.ModelUploadFormScrollerComposition;
import lombok.Getter;

/**
 * Event representing the removal of an additional texture.
 * Contains the name of the texture that was removed.
 *
 * @see ModelUploadFormScrollerComposition for usage
 */
@Getter
public class OtherTextureRemovedEvent extends ComponentEvent<Component> {
    private final String name;

    public OtherTextureRemovedEvent(Component source, String name) {
        super(source, false);
        this.name = name;
    }
}
