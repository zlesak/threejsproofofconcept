package cz.uhk.zlesak.threejslearningapp.events.texture;

import com.vaadin.flow.component.ComponentEvent;
import cz.uhk.zlesak.threejslearningapp.components.selects.TextureAreaSelect;
import cz.uhk.zlesak.threejslearningapp.domain.texture.TextureAreaForSelect;
import lombok.Getter;

/**
 * Event that is fired when the selected texture area in the TextureAreaSelect changes.
 * This event is used to trigger actions in the UI after the texture area selection is changed.
 */
@Getter
public class TextureAreaChangeEvent extends ComponentEvent<TextureAreaSelect> {
    private final TextureAreaForSelect oldValue;
    private final TextureAreaForSelect newValue;

    public TextureAreaChangeEvent(TextureAreaSelect source, boolean fromClient, TextureAreaForSelect oldValue, TextureAreaForSelect newValue) {
        super(source, fromClient);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}
