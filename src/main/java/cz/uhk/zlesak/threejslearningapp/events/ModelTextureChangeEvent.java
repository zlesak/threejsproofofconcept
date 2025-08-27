package cz.uhk.zlesak.threejslearningapp.events;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import cz.uhk.zlesak.threejslearningapp.components.compositions.ModelUploadFormScrollerComposition;
import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickTextureEntity;
import lombok.Getter;

import java.util.List;

/**
 * Event representing a change in model textures.
 * Contains a list of QuickTextureEntity objects that represent the new textures.
 * @see ModelUploadFormScrollerComposition fo usage
 */
@Getter
public class ModelTextureChangeEvent extends ComponentEvent<Component> {
    private final List<QuickTextureEntity> quickTextureEntity;
    public ModelTextureChangeEvent(Component source, List<QuickTextureEntity> quickTextureEntity) {
        super(source, false);
        this.quickTextureEntity = quickTextureEntity;
    }
}
