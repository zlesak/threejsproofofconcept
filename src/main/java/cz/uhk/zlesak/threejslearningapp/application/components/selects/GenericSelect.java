package cz.uhk.zlesak.threejslearningapp.application.components.selects;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import cz.uhk.zlesak.threejslearningapp.application.models.records.TextureAreaForSelectRecord;
import lombok.Getter;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.function.BiFunction;

/**
 * GenericSelect abstract class extending Vaadin Select component.
 * It provides a generic implementation for select components with custom event handling.
 *
 * @param <T>
 * @param <E>
 */
@Scope("prototype")
public abstract class GenericSelect<T, E extends ComponentEvent<?>> extends Select<T> {
    protected final ItemLabelGenerator<T> itemLabelGenerator;
    private final BiFunction<GenericSelect<T, E>, ValueChangeEvent<T>, E> eventFactory;
    private final Class<E> eventType;
    @Getter
    private List<T> items;

    /**
     * Constructor for GenericSelect.
     *
     * @param label              Select label
     * @param itemLabelGenerator label generator for items
     * @param eventType          event class type
     * @param eventFactory       event factory function to create events
     */
    public GenericSelect(String label, com.vaadin.flow.component.ItemLabelGenerator<T> itemLabelGenerator,
                         Class<E> eventType,
                         BiFunction<GenericSelect<T, E>, ValueChangeEvent<T>, E> eventFactory) {
        super();
        setLabel(label);
        setWidthFull();
        setRenderer(new TextRenderer<>(itemLabelGenerator));
        this.itemLabelGenerator = itemLabelGenerator;
        this.eventFactory = eventFactory;
        this.eventType = eventType;
        addValueChangeListener(this::fireGenericChangeEvent);
    }

    /**
     * Fires a generic change event if the value has changed.
     *
     * @param event value change event
     */
    private void fireGenericChangeEvent(ValueChangeEvent<T> event) {
        if ((event.getOldValue() == null && event.getValue() != null) ||
                (event.getOldValue() != null && !event.getOldValue().equals(event.getValue()))) {
            fireEvent(eventFactory.apply(this, event));
        }
    }

    /**
     * Registers a generic change listener for the select component.
     *
     * @param listener the listener to be added
     */
    public void addGenericChangeListener(ComponentEventListener<E> listener) {
        addListener(eventType, listener);
    }

    /**
     * Sets the items for the select component and optionally sets the first item as the selected value.
     *
     * @param items           list of items to set
     * @param setFirstAsValue if true, sets the first item as the selected value
     */
    protected void initialize(List<T> items, boolean setFirstAsValue) {
        this.items = items;

        if (!items.isEmpty() && items.getFirst() instanceof TextureAreaForSelectRecord) {
            setRenderer(new ComponentRenderer<>(item -> {
                Span span = new Span(itemLabelGenerator.apply(item));
                String color = ((TextureAreaForSelectRecord) item).hexColor();
                if (color != null) {
                    span.getStyle().set("color", color);
                }
                return span;
            }));
        } else {
            setRenderer(new TextRenderer<>(itemLabelGenerator));
        }
        setItems(this.items);
        if (!items.isEmpty() && setFirstAsValue) {
            setValue(items.getFirst());
        }
    }
}
