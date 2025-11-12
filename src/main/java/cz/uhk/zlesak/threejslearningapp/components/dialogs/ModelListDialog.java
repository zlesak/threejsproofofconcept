package cz.uhk.zlesak.threejslearningapp.components.dialogs;

import com.vaadin.flow.component.dialog.Dialog;
import cz.uhk.zlesak.threejslearningapp.domain.model.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.views.model.ModelListView;
import lombok.Setter;

import java.util.function.Consumer;

/**
 * ModelListDialog Class - A dialog component that displays a list of 3D models for selection.
 * It utilizes the ModelListView to present the models and allows users to select one.
 * Upon selection, a listener is triggered to handle the selected model.
 */
@Setter
public class ModelListDialog extends Dialog {
    private Consumer<QuickModelEntity> modelSelectedListener;
    private final ModelListView modelListView;

    /**
     * Constructor for ModelListDialog.
     * @param modelListView the ModelListView component to be displayed in the dialog
     */
    public ModelListDialog(ModelListView modelListView) {
        this.modelListView = modelListView;
        setWidth("800px");
        setHeight("600px");
        add(modelListView);
    }

    /**
     * Handles the event when a model is selected from the list.
     * @param entity the selected QuickModelEntity
     */
    private void onModelSelected(QuickModelEntity entity) {
        if (modelSelectedListener != null) {
            modelSelectedListener.accept(entity);
        }
        close();
    }

    /**
     * Opens the dialog and initializes the model list view.
     */
    public void open() {
        this.setOpened(true);
        modelListView.listModels(false);
        modelListView.setModelSelectedListener(this::onModelSelected);
    }
}

