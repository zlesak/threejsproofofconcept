package cz.uhk.zlesak.threejslearningapp.components.selects;

import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.data.renderer.TextRenderer;
import java.util.List;
import java.util.function.BiFunction;
import org.springframework.context.annotation.Scope;

/**
 * GenericSelectionComboBox nyní rozšiřuje Select místo ComboBox, zachovává původní chování.
 * @param <T>
 * @param <E>
 */
@Scope("prototype")
public abstract class GenericSelect<T, E extends ComponentEvent<?>> extends Select<T> {
    private final BiFunction<GenericSelect<T, E>, ValueChangeEvent<T>, E> eventFactory;
    private final Class<E> eventType;

    /**
     * Konstruktor s parametry label, itemLabelGenerator, eventType a eventFactory.
     * @param label popisek Selectu
     * @param itemLabelGenerator generátor textu pro položky (Vaadin ItemLabelGenerator)
     * @param eventType typ události
     * @param eventFactory továrna na události
     */
    public GenericSelect(String label, com.vaadin.flow.component.ItemLabelGenerator<T> itemLabelGenerator,
                         Class<E> eventType,
                         BiFunction<GenericSelect<T, E>, ValueChangeEvent<T>, E> eventFactory) {
        super();
        setLabel(label);
        setWidthFull();
        setRenderer(new TextRenderer<>(itemLabelGenerator));
        this.eventFactory = eventFactory;
        this.eventType = eventType;
        addValueChangeListener(this::fireGenericChangeEvent);
    }

    /**
     * Vyvolá generickou událost při změně hodnoty Selectu.
     * @param event událost změny hodnoty
     */
    private void fireGenericChangeEvent(ValueChangeEvent<T> event) {
        if (event.getOldValue() == null || (event.getValue() != null && !event.getValue().equals(event.getOldValue()))) {
            fireEvent(eventFactory.apply(this, event));
        }
    }

    /**
     * Přidá posluchač generické události.
     * @param listener posluchač
     */
    public void addGenericChangeListener(ComponentEventListener<E> listener) {
        addListener(eventType, listener);
    }

    /**
     * Nastaví položky Selectu a inicializuje hodnotu, pokud je seznam neprázdný.
     * @param items seznam položek
     */
    protected void initialize(List<T> items) {
        setItems(items);
        if (!items.isEmpty()) {
            setValue(items.getFirst());
        }
    }
}
