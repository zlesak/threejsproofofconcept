package cz.uhk.zlesak.threejslearningapp.components.dialogs;

import com.vaadin.flow.component.dialog.Dialog;
import cz.uhk.zlesak.threejslearningapp.domain.model.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.views.model.ModelListView;
import lombok.Setter;

import java.util.function.Consumer;

@Setter
public class ModelListDialog extends Dialog {
    private Consumer<QuickModelEntity> modelSelectedListener;
    private final ModelListView modelListView;

    public ModelListDialog(ModelListView modelListView) {
        this.modelListView = modelListView;
        setWidth("800px");
        setHeight("600px");
        add(modelListView);
    }

    private void onModelSelected(QuickModelEntity entity) {
        if (modelSelectedListener != null) {
            modelSelectedListener.accept(entity);
        }
        close();
    }

    public void open() {
        this.setOpened(true);
        modelListView.listModels(1, 6, false);
        modelListView.setModelSelectedListener(this::onModelSelected);
    }
}

