package cz.uhk.zlesak.threejslearningapp.components;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import lombok.Getter;

import java.io.InputStream;
import java.util.List;

@Getter
public class UploadComponent extends Upload {
    @Getter
    String fileName;
    InputStream inputStream;

    public UploadComponent(MultiFileMemoryBuffer buffer, List<String> acceptedFileTypes) {
        super(buffer);
        setAcceptedFileTypes(acceptedFileTypes.toArray(new String[0]));
        this.setMaxFileSize(50 * 1024 * 1024);
        setDropAllowed(true);

        this.addSucceededListener(event -> {
            this.fileName = event.getFileName();
            this.inputStream = buffer.getInputStream(fileName);
        });

        this.addFileRejectedListener(event -> {
            String errorMessage = event.getErrorMessage();
            Notification notification = Notification.show(errorMessage, 5000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });
    }

}
