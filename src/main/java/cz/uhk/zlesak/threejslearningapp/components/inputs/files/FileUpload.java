package cz.uhk.zlesak.threejslearningapp.components.inputs.files;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.server.streams.InMemoryUploadHandler;
import com.vaadin.flow.server.streams.UploadHandler;
import cz.uhk.zlesak.threejslearningapp.components.notifications.ErrorNotification;
import cz.uhk.zlesak.threejslearningapp.common.InputStreamMultipartFile;
import cz.uhk.zlesak.threejslearningapp.i18n.I18nAware;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Custom upload component that allows multiple file uploads with a memory buffer.
 * It provides listeners for file upload success, removal, and rejection.
 * It also supports setting accepted file types and a maximum file size.
 */
@Getter
@Scope("prototype")
public class FileUpload extends Upload implements I18nAware {
    private final VerticalLayout fileListLayout = new VerticalLayout();
    @Getter
    private final List<InputStreamMultipartFile> uploadedFiles = new ArrayList<>();
    @Setter
    private BiConsumer<String, InputStreamMultipartFile> uploadListener;

    /**
     * Constructor for UploadComponent.
     *
     * @param acceptedFileTypes List of accepted file types for upload.
     * @param maxOneFile        If true, restricts the upload to a maximum of one file.
     * @param canNameFiles      If true, display names can be entered by the user for individual files.
     */
    public FileUpload(List<String> acceptedFileTypes, boolean maxOneFile, boolean canNameFiles) {
        this(acceptedFileTypes, maxOneFile, canNameFiles, true);
    }

    public FileUpload(List<String> acceptedFileTypes, boolean maxOneFile, boolean canNameFiles, boolean dragAndDropEnabled) {
        super();
        InMemoryUploadHandler temporaryFileUploadHandler = UploadHandler.inMemory(
                (metadata, data) -> {
                    String fileName = metadata.fileName();
                    InputStreamMultipartFile uploadedMultipartFile;
                    uploadedMultipartFile = new InputStreamMultipartFile(new ByteArrayInputStream(data), fileName, null);

                    uploadedFiles.add(uploadedMultipartFile);
                    if (uploadListener != null) {
                        uploadListener.accept(fileName, uploadedMultipartFile);
                    }
                    if (canNameFiles) {
                        HorizontalLayout fileRow = getHorizontalLayout(fileName, uploadedMultipartFile);
                        fileListLayout.add(fileRow);
                    }
                });
        setUploadHandler(temporaryFileUploadHandler);
        if (maxOneFile) {
            setMaxFiles(1);
        }
        setAcceptedFileTypes(acceptedFileTypes);
        setMaxFileSize(50 * 1024 * 1024);
        if (dragAndDropEnabled) {
            setDropLabel(new Span(text("upload.dropLabel.information")));
        } else {
            setDropAllowed(false);
            setDropLabel(null);
        }

        fileListLayout.setPadding(false);
        fileListLayout.setSpacing(true);
        fileListLayout.setWidthFull();

        getElement().appendChild(fileListLayout.getElement());

        addFileRemovedListener(event -> {
            String fileName = event.getFileName();
            uploadedFiles.removeIf(f -> f.getName().equals(fileName));
            fileListLayout.getChildren()
                    .filter(c -> c.getId().isPresent() && c.getId().get().equals("file-row-" + fileName.hashCode()))
                    .findFirst()
                    .ifPresent(fileListLayout::remove);
        });

        addFileRejectedListener(event -> {
            String errorMessage = event.getErrorMessage();
            new ErrorNotification(errorMessage);
        });

        String toolTipText = text("upload.file.format.info.multiple") + ": " + String.join(", ", acceptedFileTypes);

        if (maxOneFile) {
            toolTipText = text("upload.file.format.info.single") + ": " + String.join(", ", acceptedFileTypes);
        }

        Tooltip.forComponent(this)
                .withText(toolTipText)
                .withPosition(Tooltip.TooltipPosition.TOP_START);
        setUploadButton(new Button(text("upload.file") + " (" + String.join(", ", acceptedFileTypes) + ")"));
    }

    /**
     * Creates a horizontal layout for displaying the file name and a text field for the display name.
     * The text field allows the user to change the display name of the file.
     *
     * @param fileName the name of the file to be displayed
     * @param file     the InputStreamMultipartFile object containing the file data
     * @return a HorizontalLayout containing the file name and display name text field
     */
    private HorizontalLayout getHorizontalLayout(String fileName, InputStreamMultipartFile file) {
        TextField displayNameField = new TextField(text("upload.horizontalLayout.file.displayName"));
        displayNameField.setValue(fileName);
        displayNameField.addValueChangeListener(e -> file.setDisplayName(e.getValue()));
        displayNameField.setWidthFull();
        Span fileNameLabel = new Span(fileName);
        HorizontalLayout fileRow = new HorizontalLayout(fileNameLabel, displayNameField);
        fileRow.setAlignItems(HorizontalLayout.Alignment.CENTER);
        fileRow.setId("file-row-" + fileName.hashCode());
        fileRow.setWidthFull();
        return fileRow;
    }

    /**
     * Clears the list of uploaded files and input streams that it has saved in operation.
     */
    public void clear() {
        uploadedFiles.clear();
    }

    /**
     * Sets the accepted file types for the upload component.
     *
     * @param acceptedFileTypes Array of accepted file types.
     */
    public void setAcceptedFileTypes(List<String> acceptedFileTypes) {
        setAcceptedFileTypes(acceptedFileTypes.toArray(new String[0]));
        Button uploadButton = (Button) getUploadButton();
        uploadButton.setText(text("upload.file") + " (" + String.join(", ", acceptedFileTypes) + ")");
    }
}
