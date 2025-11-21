package cz.uhk.zlesak.threejslearningapp.components.dialogs.listDialogs;

import cz.uhk.zlesak.threejslearningapp.domain.model.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.views.model.ModelListView;

/**
 * Dialog for listing and selecting QuickModelEntity instances.
 * It extends the AbstractListDialog with QuickModelEntity type.
 */
public class ModelListDialog extends AbstractListDialog<QuickModelEntity> {
    /**
     * Constructor for ModelListDialog.
     * @param modelListView the ModelListView to be used in the dialog
     */
    public ModelListDialog(ModelListView modelListView) {
        super(modelListView);
    }
}
