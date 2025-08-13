package cz.uhk.zlesak.threejslearningapp.components.ComboBoxes;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentEvent;
import java.util.List;
import java.util.function.BiFunction;
import org.springframework.context.annotation.Scope;

/**
 * A generic ComboBox component that is further specialized for selection purposes in classes extending it.
 * It allows for custom event handling and item labeling.
 * @param <T>
 * @param <E>
 */
@Scope("prototype")
public abstract class GenericSelectionComboBox<T, E extends ComponentEvent<?>> extends ComboBox<T> {
    private final BiFunction<GenericSelectionComboBox<T, E>, ValueChangeEvent<T>, E> eventFactory;
    private final Class<E> eventType;

    /**
     * Constructor with the specified label, item label generator, event type, and event factory.
     *
     * @param label the label for the ComboBox
     * @param itemLabelGenerator a function to generate labels for items
     * @param eventType the class of the event type to be fired
     * @param eventFactory a function to create events based on value changes
     */
    public GenericSelectionComboBox(String label, java.util.function.Function<T, String> itemLabelGenerator,
                                    Class<E> eventType,
                                    BiFunction<GenericSelectionComboBox<T, E>, ValueChangeEvent<T>, E> eventFactory) {
        super(label);
        setWidthFull();
        setAllowCustomValue(false);
        setClearButtonVisible(true);
        setItemLabelGenerator(itemLabelGenerator::apply);
        this.eventFactory = eventFactory;
        this.eventType = eventType;
        addValueChangeListener(this::fireGenericChangeEvent);
    }

    /**
     * Fires a change event when the value of the ComboBox changes.
     *
     * @param event the value change event containing old and new values
     */
    private void fireGenericChangeEvent(ValueChangeEvent<T> event) {
        if (event.getOldValue() == null || (event.getValue() != null && !event.getValue().equals(event.getOldValue()))) {
            fireEvent(eventFactory.apply(this, event));
        }
    }

    /**
     * Adds a change listener to the ComboBox.
     *
     * @param listener the listener to be added
     */
    public void addGenericChangeListener(ComponentEventListener<E> listener) {
        addListener(eventType, listener);
    }

    /**
     * Sets the items for the ComboBox and initializes the value if the list is not empty.
     *
     * @param items the list of items to be set in the ComboBox
     */
    protected void initialize(List<T> items) {
        setItems(items);
        if (!items.isEmpty()) {
            setValue(items.getFirst());
        }
    }
}
