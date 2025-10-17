package cz.uhk.zlesak.threejslearningapp.application.components;

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
        getStyle().set("flex", "1 1 auto");
        getStyle().set("min-width", "0");
    }
}
