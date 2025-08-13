package cz.uhk.zlesak.threejslearningapp.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Custom upload component that allows multiple file uploads with a memory buffer.
 * It provides listeners for file upload success, removal, and rejection.
 * It also supports setting accepted file types and a maximum file size.
 */
@Getter
@Scope("prototype")
public class UploadComponent extends Upload {
    @Getter
    private Map<String, InputStream> inputStreams = new LinkedHashMap<>();
    @Setter
    private BiConsumer<String, InputStream> uploadListener;

    /**
     * Constructor for UploadComponent.
     *
     * @param buffer              The memory buffer to store uploaded files.
     * @param acceptedFileTypes   List of accepted file types for upload.
     * @param maxOneFile          If true, restricts the upload to a maximum of one file.
     */
    public UploadComponent(MultiFileMemoryBuffer buffer, List<String> acceptedFileTypes, boolean maxOneFile) {
        super(buffer);
        if (maxOneFile) {
            setMaxFiles(1);
        }
        setAcceptedFileTypes(acceptedFileTypes);
        setMaxFileSize(50 * 1024 * 1024);
        setDropAllowed(true);

        addSucceededListener(event -> {
            String fileName = event.getFileName();
            InputStream stream = buffer.getInputStream(fileName);
            inputStreams.put(fileName, stream);
            if (uploadListener != null) {
                uploadListener.accept(fileName, stream);
            }
        });

        addFileRemovedListener(event -> {
            String fileName = event.getFileName();
            inputStreams.remove(fileName);
        });

        addFileRejectedListener(event -> {
            String errorMessage = event.getErrorMessage();
            Notification notification = Notification.show(errorMessage, 5000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });

        String toolTipText = "Nahrajte soubory ve formátu: " + String.join(", ", acceptedFileTypes);

        if(maxOneFile) {
            toolTipText = "Nahrajte jeden soubor ve formátu: " + String.join(", ", acceptedFileTypes);
        }

        Tooltip.forComponent(this)
                .withText(toolTipText)
                .withPosition(Tooltip.TooltipPosition.TOP_START);
        setUploadButton(new Button("Nahrát soubor (" + String.join(", ", acceptedFileTypes) + ")"));
        setDropLabel(new Span("Přetáhněte soubor sem nebo klikněte na Nahrát soubor"));
    }

    /**
     * Clears the list of uploaded files and input streams that it has saved in operation.
     */
    public void clear() {
        clearFileList();
        inputStreams.clear();
    }

    /**
     * Sets the accepted file types for the upload component.
     *
     * @param acceptedFileTypes Array of accepted file types.
     */
    public void setAcceptedFileTypes(List<String> acceptedFileTypes) {
        setAcceptedFileTypes(acceptedFileTypes.toArray(new String[0]));
        Button uploadButton = (Button) getUploadButton();
        uploadButton.setText("Nahrát soubor (" + String.join(", ", acceptedFileTypes) + ")");    }
}
