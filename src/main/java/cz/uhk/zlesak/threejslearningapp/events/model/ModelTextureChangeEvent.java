package cz.uhk.zlesak.threejslearningapp.events.model;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import cz.uhk.zlesak.threejslearningapp.components.forms.ModelUploadForm;
import cz.uhk.zlesak.threejslearningapp.domain.texture.QuickTextureEntity;
import lombok.Getter;

import java.util.Map;

/**
 * Event representing a change in model textures.
 * Contains a map of QuickTextureEntity objects that represent the new textures.
 * @see ModelUploadForm for usage
 */
@Getter
public class ModelTextureChangeEvent extends ComponentEvent<Component> {
    private final Map<String, QuickTextureEntity> quickTextureEntity;
    public ModelTextureChangeEvent(Component source, Map<String, QuickTextureEntity> quickTextureEntity) {
        super(source, false);
        this.quickTextureEntity = quickTextureEntity;
    }
}
