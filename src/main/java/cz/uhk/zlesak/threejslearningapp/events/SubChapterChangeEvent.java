package cz.uhk.zlesak.threejslearningapp.events;

import com.vaadin.flow.component.ComponentEvent;
import cz.uhk.zlesak.threejslearningapp.components.ChapterSelectionCombobox;
import cz.uhk.zlesak.threejslearningapp.models.records.SubChapterForComboBoxRecord;
import lombok.Getter;

@Getter
public class SubChapterChangeEvent extends ComponentEvent<ChapterSelectionCombobox> {
    private final SubChapterForComboBoxRecord oldValue;
    private final SubChapterForComboBoxRecord newValue;

    public SubChapterChangeEvent(ChapterSelectionCombobox source, boolean fromClient, SubChapterForComboBoxRecord oldValue, SubChapterForComboBoxRecord newValue) {
        super(source, fromClient);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

}


