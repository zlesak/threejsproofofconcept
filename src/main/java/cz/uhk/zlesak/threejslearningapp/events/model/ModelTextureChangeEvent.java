package cz.uhk.zlesak.threejslearningapp.events.model;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;
import cz.uhk.zlesak.threejslearningapp.domain.texture.QuickTextureEntity;
import lombok.Getter;

import java.util.Map;

/**
 * Event representing a change in model textures.
 * Contains a map of QuickTextureEntity objects that represent the new textures.
 * This event is broadcast at the UI level to decouple components.
 */
@Getter
public class ModelTextureChangeEvent extends ComponentEvent<UI> {
    private final Map<String, QuickTextureEntity> quickTextureEntity;
    public ModelTextureChangeEvent(UI source, Map<String, QuickTextureEntity> quickTextureEntity) {
        super(source, false);
        this.quickTextureEntity = quickTextureEntity;
    }
}
