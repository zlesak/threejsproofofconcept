package cz.uhk.zlesak.threejslearningapp.views.creating;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.Route;
import cz.uhk.zlesak.threejslearningapp.controllers.ModelController;
import cz.uhk.zlesak.threejslearningapp.data.enums.ViewTypeEnum;
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

    @Autowired
    public CreateModelView(ModelController modelController) {
        super(ViewTypeEnum.CREATE);

        Button createButton = new Button("Vytvořit model");

        createButton.addClickListener(event -> {
            try {
                QuickModelEntity quickModelEntity;
                if (isAdvanced.getValue()) {
                    quickModelEntity = modelController.uploadModel(modelName.getValue().trim(), objUploadComponent.getInputStreams(), mainTextureUploadComponent.getInputStreams(), otherTexturesUploadComponent.getInputStreams(), csvUploadComponent.getInputStreams());
                    Notification.show("Model úspěšně nahrán.", 3000, Notification.Position.MIDDLE);
                } else {
                    quickModelEntity = modelController.uploadModel(modelName.getValue().trim(), objUploadComponent.getInputStreams());
                    Notification.show("Model a textury úspěšně nahrány.", 3000, Notification.Position.MIDDLE);
                }
                UI.getCurrent().navigate("model/" + quickModelEntity.getModel().getId());
            } catch (Exception e) {
                log.error("Error uploading model", e);
                Notification.show("Chyba při nahrávání modelu: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        });
        modelProperties.add(createButton);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {

    }

    @Override
    public String getPageTitle() {
        return "";
    }
}
