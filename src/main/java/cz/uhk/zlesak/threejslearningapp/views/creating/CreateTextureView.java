package cz.uhk.zlesak.threejslearningapp.views.creating;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.*;
import cz.uhk.zlesak.threejslearningapp.components.UploadComponent;
import cz.uhk.zlesak.threejslearningapp.controllers.TextureController;
import cz.uhk.zlesak.threejslearningapp.data.ViewTypeEnum;
import cz.uhk.zlesak.threejslearningapp.models.InputStreamMultipartFile;
import cz.uhk.zlesak.threejslearningapp.views.scaffolds.TextureScaffold;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Scope;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.List;

@Slf4j
@PageTitle("Nahrát texturu")
@Route("createTexture")
@Menu(order = 3, icon = LineAwesomeIconUrl.IMAGE_SOLID)
@Tag("create-texture")
@Scope("prototype")
public class CreateTextureView extends TextureScaffold {

    @Autowired
    public CreateTextureView(TextureController textureController) {
        super(ViewTypeEnum.CREATE);

        UploadComponent uploadComponent = new UploadComponent(new MultiFileMemoryBuffer(), List.of(".jpg"), true);
        uploadComponent.setUploadListener((fileName, inputStream) -> {
            try {
                String name = textureName.getValue().trim();
                InputStreamMultipartFile multipartFile = new InputStreamMultipartFile(inputStream, fileName);
                String textureEntityId = textureController.uploadTexture(name, multipartFile, isPrimary.getValue(), modelId.getValue());
                uploadComponent.clearFileList();
                textureName.clear();
                Notification.show("Textura úspěšně nahrána.", 3000, Notification.Position.MIDDLE);
                UI.getCurrent().navigate("texture/" + textureEntityId);
            } catch (ApplicationContextException ex) {
                Notification.show("Chyba: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
                log.warn(ex.getMessage(), ex);
            } catch (Exception ex) {
                Notification.show("Chyba při nahrávání textury: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
                log.error(ex.getMessage(), ex);
            }
        });
        texture.add(uploadComponent);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {

    }
}
