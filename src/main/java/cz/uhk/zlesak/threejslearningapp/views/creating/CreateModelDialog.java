package cz.uhk.zlesak.threejslearningapp.views.creating;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.theme.lumo.Lumo;
import cz.uhk.zlesak.threejslearningapp.controllers.ModelController;
import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.views.scaffolds.DialogModelScaffold;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@Getter
public class CreateModelDialog extends Dialog {
    private final DialogModelScaffold scaffold;

    public CreateModelDialog(ModelController modelController) {
        scaffold = new DialogModelScaffold(modelController, this);
        add(scaffold);
        setWidthFull();
        setHeight("auto");
    }

    @Override
    public void open() {
        super.open();
        UI.getCurrent().getPage().executeJs(
                "const match = document.cookie.match('(^|;) ?themeMode=([^;]*)(;|$)'); return match ? match[2] : null;"
        ).then(String.class, value -> {
            if ("dark".equals(value)) {
                getElement().getThemeList().add(Lumo.DARK);
            } else {
                getElement().getThemeList().remove(Lumo.DARK);
            }
        });
    }

    public void setModelCreatedListener(ModelCreatedListener listener) {
        scaffold.setModelCreatedListener(listener);
    }

    public boolean isAdvanced() {
        return scaffold.isAdvanced();
    }

    public interface ModelCreatedListener {
        void modelCreated(QuickModelEntity entity) throws IOException;
    }
}
