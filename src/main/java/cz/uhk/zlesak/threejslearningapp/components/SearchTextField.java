package cz.uhk.zlesak.threejslearningapp.components;

import com.vaadin.flow.component.textfield.TextField;

/**
 * A custom TextField component for search functionality.
 */
public class SearchTextField extends TextField {
    public SearchTextField(String placeholder) {
        super();
        setWidthFull();
        setPlaceholder(placeholder);
    }
}
