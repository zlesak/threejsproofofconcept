package cz.uhk.zlesak.threejslearningapp.application.events;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import cz.uhk.zlesak.threejslearningapp.application.components.compositions.ModelUploadFormScrollerComposition;
import cz.uhk.zlesak.threejslearningapp.application.models.entities.quickEntities.QuickTextureEntity;
import lombok.Getter;

import java.util.Map;

/**
 * Event representing a change in model textures.
 * Contains a map of QuickTextureEntity objects that represent the new textures.
 * @see ModelUploadFormScrollerComposition for usage
 */
@Getter
public class ModelTextureChangeEvent extends ComponentEvent<Component> {
    private final Map<String, QuickTextureEntity> quickTextureEntity;
    public ModelTextureChangeEvent(Component source, Map<String, QuickTextureEntity> quickTextureEntity) {
        super(source, false);
        this.quickTextureEntity = quickTextureEntity;
    }
}
