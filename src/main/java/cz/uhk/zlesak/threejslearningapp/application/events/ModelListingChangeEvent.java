package cz.uhk.zlesak.threejslearningapp.application.events;

import com.vaadin.flow.component.ComponentEvent;
import cz.uhk.zlesak.threejslearningapp.application.components.selects.ModelListingSelect;
import cz.uhk.zlesak.threejslearningapp.application.models.records.ModelForSelectRecord;
import lombok.Getter;

/**
 * Event that is fired when the selected model listing in the ModelListingSelect changes.
 * This event is used to trigger actions in the UI after the model listing selection is changed.
 * @see cz.uhk.zlesak.threejslearningapp.application.components.selects.ModelListingSelect
 */
@Getter
public class ModelListingChangeEvent extends ComponentEvent<ModelListingSelect> {
    private final ModelForSelectRecord oldValue;
    private final ModelForSelectRecord newValue;

    public ModelListingChangeEvent(ModelListingSelect source, boolean fromClient, ModelForSelectRecord oldValue, ModelForSelectRecord newValue) {
        super(source, fromClient);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}
