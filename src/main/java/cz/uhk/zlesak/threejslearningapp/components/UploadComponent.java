package cz.uhk.zlesak.threejslearningapp.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import cz.uhk.zlesak.threejslearningapp.components.notifications.ErrorNotification;
import cz.uhk.zlesak.threejslearningapp.data.files.InputStreamMultipartFile;
import cz.uhk.zlesak.threejslearningapp.i18n.CustomI18NProvider;
import cz.uhk.zlesak.threejslearningapp.utils.SpringContextUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;

import java.io.InputStream;
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
public class UploadComponent extends Upload {
    protected final CustomI18NProvider i18nProvider = SpringContextUtils.getBean(CustomI18NProvider.class);
    private final VerticalLayout fileListLayout = new VerticalLayout();
    @Getter
    private List<InputStreamMultipartFile> uploadedFiles = new ArrayList<>();
    @Setter
    private BiConsumer<String, InputStream> uploadListener;

    /**
     * Constructor for UploadComponent.
     *
     * @param buffer            The memory buffer to store uploaded files.
     * @param acceptedFileTypes List of accepted file types for upload.
     * @param maxOneFile        If true, restricts the upload to a maximum of one file.
     * @param canNameFiles      If true, display names can be entered by the user for individual files.
     */
    public UploadComponent(MultiFileMemoryBuffer buffer, List<String> acceptedFileTypes, boolean maxOneFile, boolean canNameFiles) {
        super(buffer);
        if (maxOneFile) {
            setMaxFiles(1);
        }
        setAcceptedFileTypes(acceptedFileTypes);
        setMaxFileSize(50 * 1024 * 1024);
        setDropAllowed(true);

        fileListLayout.setPadding(false);
        fileListLayout.setSpacing(true);
        fileListLayout.setWidthFull();

        getElement().appendChild(fileListLayout.getElement());

        addSucceededListener(event -> {
            String fileName = event.getFileName();
            InputStream stream = buffer.getInputStream(fileName);
            InputStreamMultipartFile file = new InputStreamMultipartFile(stream, fileName, fileName);
            uploadedFiles.add(file);
            if (uploadListener != null) {
                uploadListener.accept(fileName, stream);
            }
            if(canNameFiles){
                HorizontalLayout fileRow = getHorizontalLayout(fileName, file);
                fileListLayout.add(fileRow);
            }
        });

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

        String toolTipText = i18nProvider.getTranslation("upload.file.format.info.multiple", UI.getCurrent().getLocale()) + ": " + String.join(", ", acceptedFileTypes);

        if (maxOneFile) {
            toolTipText = i18nProvider.getTranslation("upload.file.format.info.single", UI.getCurrent().getLocale()) + ": " + String.join(", ", acceptedFileTypes);
        }

        Tooltip.forComponent(this)
                .withText(toolTipText)
                .withPosition(Tooltip.TooltipPosition.TOP_START);
        setUploadButton(new Button(i18nProvider.getTranslation("upload.file", UI.getCurrent().getLocale()) + " (" + String.join(", ", acceptedFileTypes) + ")"));
        setDropLabel(new Span(i18nProvider.getTranslation("upload.dropLabel.information", UI.getCurrent().getLocale())));
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
        TextField displayNameField = new TextField(i18nProvider.getTranslation("upload.horizontalLayout.file.displayName", UI.getCurrent().getLocale()));
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
        uploadButton.setText(i18nProvider.getTranslation("upload.file", UI.getCurrent().getLocale()) + " (" + String.join(", ", acceptedFileTypes) + ")");
    }
}
