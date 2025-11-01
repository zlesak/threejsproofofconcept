package cz.uhk.zlesak.threejslearningapp.application.components.selects;

import com.vaadin.flow.component.ComponentEventListener;
import cz.uhk.zlesak.threejslearningapp.application.events.ModelListingChangeEvent;
import cz.uhk.zlesak.threejslearningapp.application.models.entities.quickEntities.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.application.models.records.ModelForSelectRecord;
import cz.uhk.zlesak.threejslearningapp.application.models.records.parsers.ModelListingDataParser;
import org.springframework.context.annotation.Scope;

import java.util.Map;

/**
 * ModelListingSelect is a custom select component for choosing 3D models.
 * It extends GenericSelect with ModelForSelectRecord as the item type and ModelListingChangeEvent as the event type.
 * It provides functionality to initialize the select with a list of models and handle model selection changes.
 */
@Scope("prototype")
public class ModelListingSelect extends GenericSelect<ModelForSelectRecord, ModelListingChangeEvent> {
    /**
     * Constructor to initialize the ModelListingSelect component.
     * Sets up the select with appropriate item label generator and change event handling.
     */
    public ModelListingSelect() {
        super("", ModelForSelectRecord::modelName,
                ModelListingChangeEvent.class,
                (select, event) -> new ModelListingChangeEvent((ModelListingSelect) select, event.isFromClient(), event.getOldValue(), event.getValue()));
        setEmptySelectionAllowed(false);
        setWidthFull();
    }

    /**
     * Adds a listener for model change events.
     *
     * @param listener the listener to be added
     */
    public void addModelChangeListener(ComponentEventListener<ModelListingChangeEvent> listener) {
        addGenericChangeListener(listener);
    }

    /**
     * Initializes the select component with a map of QuickModelEntity objects.
     *
     * @param models the map of model IDs to QuickModelEntity objects
     */
    public void initializeModelSelect(Map<String, QuickModelEntity> models) {
        initialize(ModelListingDataParser.modelForSelectDataParser(models), true);
    }

    /**
     * Sets the selected model in the select component by its ID.
     *
     * @param modelId the ID of the model to be selected
     */
    public void setSelectedModelById(String modelId) {
        if (modelId == null) return;
        for (ModelForSelectRecord item : getItems()) {
            if (item.id().equals(modelId)) {
                setValue(item);
                return;
            }
        }
    }
}
