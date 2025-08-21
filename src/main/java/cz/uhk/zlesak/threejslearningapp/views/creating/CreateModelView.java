package cz.uhk.zlesak.threejslearningapp.views.creating;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.Route;
import cz.uhk.zlesak.threejslearningapp.components.BeforeLeaveActionDialog;
import cz.uhk.zlesak.threejslearningapp.components.notifications.ErrorNotification;
import cz.uhk.zlesak.threejslearningapp.components.notifications.InfoNotification;
import cz.uhk.zlesak.threejslearningapp.controllers.ModelController;
import cz.uhk.zlesak.threejslearningapp.data.enums.ViewTypeEnum;
import cz.uhk.zlesak.threejslearningapp.i18n.CustomI18NProvider;
import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.views.scaffolds.ModelScaffold;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

@Slf4j
@Route("createModel")
@Tag("create-model")
@Scope("prototype")
public class CreateModelView extends ModelScaffold {
    private boolean skipBeforeLeaveDialog = false;

    @Autowired
    public CreateModelView(ModelController modelController, CustomI18NProvider i18nProvider) {
        super(i18nProvider, ViewTypeEnum.CREATE);

        Button createButton = new Button("Vytvořit model");

        createButton.addClickListener(event -> {
            try {
                QuickModelEntity quickModelEntity;
                if (isAdvanced.getValue()) {
                    quickModelEntity = modelController.uploadModel(modelName.getValue().trim(), objUploadComponent.getUploadedFiles().getFirst(), mainTextureUploadComponent.getUploadedFiles().getFirst(), otherTexturesUploadComponent.getUploadedFiles(), csvUploadComponent.getUploadedFiles());
                    new InfoNotification("Model úspěšně nahrán.");
                } else {
                    quickModelEntity = modelController.uploadModel(modelName.getValue().trim(), objUploadComponent.getUploadedFiles().getFirst());
                    new InfoNotification("Model a textury úspěšně nahrány.");
                }
                skipBeforeLeaveDialog = true;
                UI.getCurrent().navigate("model/" + quickModelEntity.getModel().getId());
            } catch (Exception e) {
                log.error("Error uploading model", e);
                new ErrorNotification("Chyba při nahrávání modelu: " + e.getMessage());
            }
        });
        modelProperties.add(createButton);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        if (!skipBeforeLeaveDialog) {
            BeforeLeaveActionDialog.leave(event);
        }
    }

    @Override
    public String getPageTitle() {
        try {
            return this.i18nProvider.getTranslation("page.title.createChapterView", UI.getCurrent().getLocale());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

    }
}
