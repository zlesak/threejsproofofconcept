package cz.uhk.zlesak.threejslearningapp.views.creating;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.uhk.zlesak.threejslearningapp.components.UploadComponent;
import cz.uhk.zlesak.threejslearningapp.controllers.ModelController;
import cz.uhk.zlesak.threejslearningapp.data.ViewTypeEnum;
import cz.uhk.zlesak.threejslearningapp.models.InputStreamMultipartFile;
import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.views.scaffolds.ModelScaffold;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Scope;

import java.util.List;

@Slf4j
@PageTitle("Nahrát 3D model")
@Route("createModel")
@Tag("create-model")
@Scope("prototype")
public class CreateModelView extends ModelScaffold {

    @Autowired
    public CreateModelView(ModelController modelController) {
        super(ViewTypeEnum.CREATE);

        UploadComponent uploadComponent = new UploadComponent(new MultiFileMemoryBuffer(), List.of(".glb"));
        uploadComponent.setUploadListener((fileName, inputStream) -> {
            try {
                String name = modelName.getValue().trim();
                InputStreamMultipartFile multipartFile = new InputStreamMultipartFile(inputStream, fileName);
                QuickModelEntity quickModelEntity = modelController.uploadModel(name, multipartFile);
                uploadComponent.clearFileList();
                modelName.clear();
                Notification.show("Model úspěšně nahrán.", 3000, Notification.Position.MIDDLE);
                UI.getCurrent().navigate("model/" + quickModelEntity.getModel().getId());
            } catch (ApplicationContextException ex) {
                Notification.show("Chyba: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
                log.warn(ex.getMessage(), ex);
            } catch (Exception ex) {
                Notification.show("Chyba při nahrávání modelu: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
                log.error(ex.getMessage(), ex);
            }
        });
        modelDiv.add(uploadComponent);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {

    }
}
