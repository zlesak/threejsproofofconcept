package cz.uhk.zlesak.threejslearningapp.components.buttons;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import cz.uhk.zlesak.threejslearningapp.components.forms.ModelUploadForm;
import cz.uhk.zlesak.threejslearningapp.events.model.ModelCreateEvent;
import cz.uhk.zlesak.threejslearningapp.i18n.I18nAware;

/**
 * A button that triggers model creation/upload.
 * Fires a ModelCreateEvent when clicked.
 * Requires ModelUploadForm to read form data.
 */
public class CreateModelButton extends Button implements I18nAware {
    private final ModelUploadForm modelUploadForm;

    public CreateModelButton(ModelUploadForm modelUploadForm) {
        super();
        this.modelUploadForm = modelUploadForm;
        setText(text("button.createModel"));
        addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addClickListener(event -> fireCreateEvent());
    }

    private void fireCreateEvent() {
        String modelName = modelUploadForm.getModelName().getValue().trim();
        boolean isAdvanced = modelUploadForm.getIsAdvanced().getValue();
        ComponentUtil.fireEvent(UI.getCurrent(), new ModelCreateEvent(UI.getCurrent(), modelName, isAdvanced));
    }
}

