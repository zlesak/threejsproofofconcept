package cz.uhk.zlesak.threejslearningapp.components;

import com.vaadin.flow.component.dialog.Dialog;
import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.views.listing.ModelListView;
import lombok.Setter;

import java.util.function.Consumer;

@Setter
public class ModelListDialog extends Dialog {
    private Consumer<QuickModelEntity> modelSelectedListener;

    public ModelListDialog(ModelListView modelListView) {
        setWidth("800px");
        setHeight("600px");
        add(modelListView);
        modelListView.listModels(1, 6, false);
        modelListView.setModelSelectedListener(this::onModelSelected);
    }

    private void onModelSelected(QuickModelEntity entity) {
        if (modelSelectedListener != null) {
            modelSelectedListener.accept(entity);
        }
        close();
    }
}

