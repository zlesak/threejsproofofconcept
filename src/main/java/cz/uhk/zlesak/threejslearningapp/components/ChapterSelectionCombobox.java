package cz.uhk.zlesak.threejslearningapp.components;

import com.vaadin.flow.component.combobox.ComboBox;
import cz.uhk.zlesak.threejslearningapp.events.SubChapterChangeEvent;
import cz.uhk.zlesak.threejslearningapp.models.records.SubChapterForComboBoxRecord;
import java.util.List;
import com.vaadin.flow.component.ComponentEventListener;
import org.springframework.context.annotation.Scope;

@Scope("prototype")
public class ChapterSelectionCombobox extends ComboBox<SubChapterForComboBoxRecord> {
    public ChapterSelectionCombobox(){
        super();
        setWidthFull();
        setAllowCustomValue(false);
        setClearButtonVisible(true);
        setItemLabelGenerator(SubChapterForComboBoxRecord::text);
        addValueChangeListener(this::fireSubChapterChangeEvent);
    }

    private void fireSubChapterChangeEvent(ValueChangeEvent<SubChapterForComboBoxRecord> event) {
        if (event.getOldValue() == null || (event.getValue() != null && !event.getValue().equals(event.getOldValue()))) {
            fireEvent(new SubChapterChangeEvent(this, event.isFromClient(), event.getOldValue(), event.getValue()));
        }
    }

    public void addSubChapterChangeListener(ComponentEventListener<SubChapterChangeEvent> listener) {
        addListener(SubChapterChangeEvent.class, listener);
    }

    public void initializeChapterSelectionComboBox(List<SubChapterForComboBoxRecord> subChapters) {
        setItems(subChapters);
        setItemLabelGenerator(SubChapterForComboBoxRecord::text);

        if (!subChapters.isEmpty()) {
            setValue(subChapters.getFirst());
        }
    }
}
