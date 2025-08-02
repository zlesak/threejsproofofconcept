package cz.uhk.zlesak.threejslearningapp.components;

import com.vaadin.flow.component.html.Div;

public class UploadLabelDiv extends Div {
    public UploadLabelDiv(UploadComponent uploadComponent, String label) {
        super(label);
        add(uploadComponent);
    }
}
