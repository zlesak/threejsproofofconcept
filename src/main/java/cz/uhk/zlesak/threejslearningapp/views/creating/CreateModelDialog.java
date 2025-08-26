package cz.uhk.zlesak.threejslearningapp.views.creating;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.theme.lumo.Lumo;
import cz.uhk.zlesak.threejslearningapp.components.notifications.ErrorNotification;
import cz.uhk.zlesak.threejslearningapp.components.notifications.InfoNotification;
import cz.uhk.zlesak.threejslearningapp.controllers.ModelController;
import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.views.scaffolds.ModelScaffold;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@Getter
public class CreateModelDialog extends Dialog {
    private final DialogModelScaffold scaffold;

    public CreateModelDialog(ModelController modelController) {
        scaffold = new DialogModelScaffold(modelController);
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
        return scaffold.isAdvanced;
    }

    public interface ModelCreatedListener {
        void modelCreated(QuickModelEntity entity) throws IOException;
    }

    @Getter
    private class DialogModelScaffold extends ModelScaffold {
        private QuickModelEntity createdModel;
        @Setter
        private ModelCreatedListener modelCreatedListener;
        private boolean isAdvanced;

        public DialogModelScaffold(ModelController modelController) {
            super();

            Button createButton = new Button("Vytvořit model");
            createButton.addClickListener(event -> {
                try {
                    isAdvanced = modelUploadFormScrollerComposition.getIsAdvanced().getValue();
                    if (isAdvanced) {
                        this.createdModel = modelController.uploadModel(
                                modelUploadFormScrollerComposition.getModelName().getValue().trim(),
                                modelUploadFormScrollerComposition.getObjUploadComponent().getUploadedFiles().getFirst(),
                                modelUploadFormScrollerComposition.getMainTextureUploadComponent().getUploadedFiles().getFirst(),
                                modelUploadFormScrollerComposition.getOtherTexturesUploadComponent().getUploadedFiles(),
                                modelUploadFormScrollerComposition.getCsvUploadComponent().getUploadedFiles()
                        );
                        new InfoNotification("Model úspěšně nahrán.");
                    } else {
                        this.createdModel = modelController.uploadModel(
                                modelUploadFormScrollerComposition.getModelName().getValue().trim(),
                                modelUploadFormScrollerComposition.getObjUploadComponent().getUploadedFiles().getFirst()
                        );
                        new InfoNotification("Model a textury úspěšně nahrány.");
                    }
                    if (modelCreatedListener != null) {
                        modelCreatedListener.modelCreated(this.createdModel);
                        CreateModelDialog.this.close();
                    }
                } catch (Exception e) {
                    log.error("Error uploading model", e);
                    new ErrorNotification("Chyba při nahrávání modelu: " + e.getMessage());
                }
            });
            modelUploadFormScrollerComposition.getVl().add(createButton);
        }

        @Override
        public void beforeEnter(com.vaadin.flow.router.BeforeEnterEvent event) {
        }

        @Override
        public void beforeLeave(com.vaadin.flow.router.BeforeLeaveEvent event) {
        }

        @Override
        public String getPageTitle() {
            return "";
        }

        @Override
        public void afterNavigation(AfterNavigationEvent event) {

        }
    }
}
