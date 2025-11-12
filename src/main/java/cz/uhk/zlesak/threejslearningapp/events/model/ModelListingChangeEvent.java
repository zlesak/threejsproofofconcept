package cz.uhk.zlesak.threejslearningapp.events.model;

import com.vaadin.flow.component.ComponentEvent;
import cz.uhk.zlesak.threejslearningapp.components.selects.ModelListingSelect;
import cz.uhk.zlesak.threejslearningapp.domain.model.ModelForSelect;
import lombok.Getter;

/**
 * Event that is fired when the selected model listing in the ModelListingSelect changes.
 * This event is used to trigger actions in the UI after the model listing selection is changed.
 * @see ModelListingSelect
 */
@Getter
public class ModelListingChangeEvent extends ComponentEvent<ModelListingSelect> {
    private final ModelForSelect oldValue;
    private final ModelForSelect newValue;

    public ModelListingChangeEvent(ModelListingSelect source, boolean fromClient, ModelForSelect oldValue, ModelForSelect newValue) {
        super(source, fromClient);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}
