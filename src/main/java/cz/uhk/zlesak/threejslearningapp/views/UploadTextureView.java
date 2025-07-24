package cz.uhk.zlesak.threejslearningapp.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.uhk.zlesak.threejslearningapp.clients.TextureApiClient;
import cz.uhk.zlesak.threejslearningapp.models.IFileEntity;
import cz.uhk.zlesak.threejslearningapp.models.InputStreamMultipartFile;
import cz.uhk.zlesak.threejslearningapp.models.TextureEntity;
import org.springframework.web.multipart.MultipartFile;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.io.InputStream;

@PageTitle("Nahrát texturu")
@Route("uploadTexture")
@Menu(order = 3, icon = LineAwesomeIconUrl.IMAGE_SOLID)
@Tag("upload-texture")
public class UploadTextureView extends Composite<VerticalLayout> {
    private InputStream inputStream = null;
    private String fileName = null;

    public UploadTextureView() {
        TextureApiClient textureApiClient = new TextureApiClient();
        TextField header = new TextField("Název textury");
        header.setMaxLength(255);
        header.setRequired(true);
        header.setRequiredIndicatorVisible(true);
        header.setWidthFull();
        Upload upload = getUpload();
        Button uploadButton = new Button("Nahrát texturu");
        uploadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        uploadButton.setWidthFull();
        uploadButton.addClickListener(e -> {
            String name = header.getValue().trim();
            if (name.isEmpty()) {
                Notification.show("Vyplňte název textury.");
                return;
            }
            if (inputStream == null || fileName == null || !(fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".png"))) {
                Notification.show("Musíte nahrát texturu s příponou .jpg nebo .png.");
                return;
            }
            try {
                MultipartFile multipartFile = new InputStreamMultipartFile(inputStream, fileName); //TODO FINISH
                    IFileEntity fileEntity = TextureEntity.builder()
//                        .Name()
//                        .Creator()
//                        .CreationDate()
//                        .LastUpdateDate()
//                        .Metadata()
//                        .File()
//                        .CSV()
                        .build();
                textureApiClient.createFileEntity(fileEntity);
                // Zde by se případně volalo API pro upload samotného souboru
                upload.clearFileList();
                inputStream = null;
                header.clear();
                Notification.show("Textura úspěšně nahrána.", 3000, Notification.Position.MIDDLE);
                UI.getCurrent().navigate("textures");
            } catch (Exception ex) {
                Notification.show("Chyba při nahrávání textury: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        });
        VerticalLayout layoutColumn1 = new VerticalLayout();
        layoutColumn1.setWidth("80vw");
        layoutColumn1.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        layoutColumn1.add(header, upload, uploadButton);
        VerticalLayout centerWrapper = new VerticalLayout();
        centerWrapper.setWidthFull();
        centerWrapper.setHeightFull();
        centerWrapper.setAlignItems(FlexComponent.Alignment.CENTER);
        centerWrapper.add(layoutColumn1);
        Scroller scroller = new Scroller(centerWrapper);
        scroller.setSizeFull();
        getContent().setSizeFull();
        getContent().removeAll();
        getContent().add(scroller);
    }
    private Upload getUpload() {
        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setMaxFileSize(50 * 1024 * 1024);
        upload.setAcceptedFileTypes(".jpg", ".png");
        upload.addSucceededListener(event -> {
            this.fileName = event.getFileName();
            this.inputStream = buffer.getInputStream(fileName);
        });
        upload.addFileRejectedListener(event -> {
            String errorMessage = event.getErrorMessage();
            Notification notification = Notification.show(errorMessage, 5000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });
        return upload;
    }
}
