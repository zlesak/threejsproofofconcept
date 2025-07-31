package cz.uhk.zlesak.threejslearningapp.components;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;

import java.io.InputStream;
import java.util.List;
import java.util.function.BiConsumer;

@Getter
@Scope("prototype")
public class UploadComponent extends Composite<Div> {
    private final Upload upload;
    private final Button uploadButton;
    private String fileName;
    private InputStream inputStream;
    @Setter
    private BiConsumer<String, InputStream> uploadListener;

    public UploadComponent(MultiFileMemoryBuffer buffer, List<String> acceptedFileTypes) {
        upload = new Upload(buffer);
        upload.setAcceptedFileTypes(acceptedFileTypes.toArray(new String[0]));
        upload.setMaxFileSize(50 * 1024 * 1024);
        upload.setDropAllowed(true);

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

        uploadButton = new Button("Nahrát soubor", e -> {
            if (uploadListener != null && inputStream != null && fileName != null) {
                uploadListener.accept(fileName, inputStream);
            } else {
                Notification.show("Nejprve vyberte soubor.", 3000, Notification.Position.MIDDLE);
            }
        });
        uploadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        uploadButton.setWidthFull();

        getContent().add(upload, uploadButton);
    }

    public void clearFileList() {
        upload.clearFileList();
        fileName = null;
        inputStream = null;
    }
}
