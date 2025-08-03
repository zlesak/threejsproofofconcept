package cz.uhk.zlesak.threejslearningapp.views.creating;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.theme.lumo.Lumo;
import cz.uhk.zlesak.threejslearningapp.controllers.ModelController;
import cz.uhk.zlesak.threejslearningapp.data.enums.ViewTypeEnum;
import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.views.scaffolds.ModelScaffold;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class CreateModelDialog extends Dialog {
    @Getter
    private QuickModelEntity createdModel;
    private final ModelScaffold scaffold;

    public boolean isAdvanced() {
        return scaffold.getIsAdvanced().getValue();
    }

    private static class DialogModelScaffold extends ModelScaffold {
        public DialogModelScaffold(ViewTypeEnum viewTypeEnum) {
            super(viewTypeEnum);
        }
        @Override
        public void beforeEnter(com.vaadin.flow.router.BeforeEnterEvent event) {}
        @Override
        public void beforeLeave(com.vaadin.flow.router.BeforeLeaveEvent event) {}

        @Override
        public String getPageTitle() {
            return "";
        }
    }

    public interface ModelCreatedListener {
        void modelCreated(QuickModelEntity entity) throws IOException;
    }

    @Setter
    private ModelCreatedListener modelCreatedListener;

    public CreateModelDialog(ModelController modelController) {
        scaffold = new DialogModelScaffold(ViewTypeEnum.CREATE);
        Button createButton = new Button("Vytvořit model");
        createButton.addClickListener(event -> {
            try {
                QuickModelEntity quickModelEntity;
                if (scaffold.getIsAdvanced().getValue()) {
                    quickModelEntity = modelController.uploadModel(
                        scaffold.getModelName().getValue().trim(),
                        scaffold.getObjUploadComponent().getInputStreams(),
                        scaffold.getMainTextureUploadComponent().getInputStreams(),
                        scaffold.getOtherTexturesUploadComponent().getInputStreams(),
                        scaffold.getCsvUploadComponent().getInputStreams()
                    );
                    log.info("Model a textruy pokročilého modelu nahrány");
                    Notification.show("Model úspěšně nahrán.", 3000, Notification.Position.MIDDLE);
                } else {
                    quickModelEntity = modelController.uploadModel(
                        scaffold.getModelName().getValue().trim(),
                        scaffold.getObjUploadComponent().getInputStreams()
                    );
                    log.info("Model a textury jednoduchého modelu nahrány");
                    Notification.show("Model a textury úspěšně nahrány.", 3000, Notification.Position.MIDDLE);
                }
                this.createdModel = quickModelEntity;
                if (modelCreatedListener != null) {
                    modelCreatedListener.modelCreated(quickModelEntity);
                }
                this.close();
            } catch (Exception e) {
                log.error("Error uploading model", e);
                Notification.show("Chyba při nahrávání modelu: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        });
        scaffold.getModelProperties().add(createButton);
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

}
