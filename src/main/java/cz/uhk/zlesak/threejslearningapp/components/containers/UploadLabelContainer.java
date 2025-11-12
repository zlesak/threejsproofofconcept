package cz.uhk.zlesak.threejslearningapp.components.containers;

import com.vaadin.flow.component.html.Div;
import cz.uhk.zlesak.threejslearningapp.components.inputs.files.FileUpload;

/**
 * A simple component that combines an UploadComponent with a label.
 */
public class UploadLabelContainer extends Div {
    /**
     * Constructor that initializes the UploadLabelDiv with an UploadComponent and a label.
     *
     * @param fileUpload The UploadComponent to be included in the div.
     * @param label The label text to be displayed in the div.
     */
    public UploadLabelContainer(FileUpload fileUpload, String label) {
        super(label);
        setWidthFull();
        add(fileUpload);
    }
}
