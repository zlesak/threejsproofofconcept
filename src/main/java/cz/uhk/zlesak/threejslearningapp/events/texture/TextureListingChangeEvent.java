package cz.uhk.zlesak.threejslearningapp.events.texture;

import com.vaadin.flow.component.ComponentEvent;
import cz.uhk.zlesak.threejslearningapp.components.selects.TextureListingSelect;
import cz.uhk.zlesak.threejslearningapp.domain.texture.TextureListingForSelect;
import lombok.Getter;

/**
 * Event that is fired when the selected texture listing in the TextureListingSelect changes.
 * This event is used to trigger actions in the UI after the texture listing selection is changed.
 */
@Getter
public class TextureListingChangeEvent extends ComponentEvent<TextureListingSelect> {
    private final TextureListingForSelect oldValue;
    private final TextureListingForSelect newValue;

    public TextureListingChangeEvent(TextureListingSelect source, boolean fromClient, TextureListingForSelect oldValue, TextureListingForSelect newValue) {
        super(source, fromClient);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}
