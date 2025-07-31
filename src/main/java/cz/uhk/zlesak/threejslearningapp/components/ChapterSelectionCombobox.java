package cz.uhk.zlesak.threejslearningapp.components;

import com.vaadin.flow.component.combobox.ComboBox;
import cz.uhk.zlesak.threejslearningapp.events.SubChapterChangeEvent;
import cz.uhk.zlesak.threejslearningapp.models.SubChapterForComboBox;
import java.util.List;
import com.vaadin.flow.component.ComponentEventListener;
import org.springframework.context.annotation.Scope;

@Scope("prototype")
public class ChapterSelectionCombobox extends ComboBox<SubChapterForComboBox> {
    public ChapterSelectionCombobox(){
        super();
        setWidthFull();
        setAllowCustomValue(false);
        setClearButtonVisible(true);
        setItemLabelGenerator(SubChapterForComboBox::text);
        addValueChangeListener(this::fireSubChapterChangeEvent);
    }

    private void fireSubChapterChangeEvent(ValueChangeEvent<SubChapterForComboBox> event) {
        if (event.getOldValue() == null || (event.getValue() != null && !event.getValue().equals(event.getOldValue()))) {
            fireEvent(new SubChapterChangeEvent(this, event.isFromClient(), event.getOldValue(), event.getValue()));
        }
    }

    public void addSubChapterChangeListener(ComponentEventListener<SubChapterChangeEvent> listener) {
        addListener(SubChapterChangeEvent.class, listener);
    }

    public void initializeChapterSelectionComboBox(List<SubChapterForComboBox> subChapters) {
        setItems(subChapters);
        setItemLabelGenerator(SubChapterForComboBox::text);

        if (!subChapters.isEmpty()) {
            setValue(subChapters.getFirst());
        }
    }
}
