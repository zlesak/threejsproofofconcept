package cz.uhk.zlesak.threejslearningapp.components.inputs.textFields;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

/**
 * A custom TextField component for search functionality.
 */
public class SearchTextField extends TextField {
    public SearchTextField(String placeholder) {
        super();
        setWidthFull();
        setValueChangeMode(ValueChangeMode.EAGER);
        setPlaceholder(placeholder);
    }
}
