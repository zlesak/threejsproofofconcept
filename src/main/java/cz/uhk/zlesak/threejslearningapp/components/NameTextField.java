package cz.uhk.zlesak.threejslearningapp.components;

import com.vaadin.flow.component.textfield.TextField;

/**
 * A custom TextField component for chapter names.
 */
public class NameTextField extends TextField {
    public NameTextField(String placeholder) {
        super();
        setPlaceholder(placeholder);
        setMaxLength(255);
        setRequired(true);
        setRequiredIndicatorVisible(true);
        setWidthFull();
    }
}
