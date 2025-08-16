package cz.uhk.zlesak.threejslearningapp.views.creating;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.theme.lumo.Lumo;
import cz.uhk.zlesak.threejslearningapp.components.Notifications.ErrorNotification;
import cz.uhk.zlesak.threejslearningapp.components.Notifications.InfoNotification;
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
    private final ModelScaffold scaffold;
    @Getter
    private QuickModelEntity createdModel;
    @Setter
    private ModelCreatedListener modelCreatedListener;

    public CreateModelDialog(ModelController modelController, I18NProvider i18nProvider) {
        scaffold = new DialogModelScaffold(i18nProvider, ViewTypeEnum.CREATE);
        Button createButton = new Button("Vytvořit model");
        createButton.addClickListener(event -> {
            try {
                QuickModelEntity quickModelEntity;
                if (scaffold.getIsAdvanced().getValue()) {
                    quickModelEntity = modelController.uploadModel(
                            scaffold.getModelName().getValue().trim(),
                            scaffold.getObjUploadComponent().getUploadedFiles().getFirst(),
                            scaffold.getMainTextureUploadComponent().getUploadedFiles().getFirst(),
                            scaffold.getOtherTexturesUploadComponent().getUploadedFiles(),
                            scaffold.getCsvUploadComponent().getUploadedFiles()
                    );
                    new InfoNotification("Model úspěšně nahrán.");
                } else {
                    quickModelEntity = modelController.uploadModel(
                            scaffold.getModelName().getValue().trim(),
                            scaffold.getObjUploadComponent().getUploadedFiles().getFirst()
                    );
                    new InfoNotification("Model a textury úspěšně nahrány.");
                }
                this.createdModel = quickModelEntity;
                if (modelCreatedListener != null) {
                    modelCreatedListener.modelCreated(quickModelEntity);
                }
                this.close();
            } catch (Exception e) {
                log.error("Error uploading model", e);
                new ErrorNotification("Chyba při nahrávání modelu: " + e.getMessage());
            }
        });
        scaffold.getModelProperties().add(createButton);
        add(scaffold);

        setWidthFull();
        setHeight("auto");
    }

    public boolean isAdvanced() {
        return scaffold.getIsAdvanced().getValue();
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

    public interface ModelCreatedListener {
        void modelCreated(QuickModelEntity entity) throws IOException;
    }

    private static class DialogModelScaffold extends ModelScaffold {
        public DialogModelScaffold(I18NProvider i18nProvider, ViewTypeEnum viewTypeEnum) {
            super(i18nProvider, viewTypeEnum);
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
    }
}
