package cz.uhk.zlesak.threejslearningapp.components;

import com.vaadin.flow.component.html.Div;

/**
 * A simple component that combines an UploadComponent with a label.
 */
public class UploadLabelDiv extends Div {
    /**
     * Constructor that initializes the UploadLabelDiv with an UploadComponent and a label.
     *
     * @param uploadComponent The UploadComponent to be included in the div.
     * @param label The label text to be displayed in the div.
     */
    public UploadLabelDiv(UploadComponent uploadComponent, String label) {
        super(label);
        add(uploadComponent);
    }
}
