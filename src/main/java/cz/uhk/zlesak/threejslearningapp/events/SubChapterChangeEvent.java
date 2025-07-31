package cz.uhk.zlesak.threejslearningapp.events;

import com.vaadin.flow.component.ComponentEvent;
import cz.uhk.zlesak.threejslearningapp.components.ChapterSelectionCombobox;
import cz.uhk.zlesak.threejslearningapp.models.SubChapterForComboBox;
import lombok.Getter;

@Getter
public class SubChapterChangeEvent extends ComponentEvent<ChapterSelectionCombobox> {
    private final SubChapterForComboBox oldValue;
    private final SubChapterForComboBox newValue;

    public SubChapterChangeEvent(ChapterSelectionCombobox source, boolean fromClient, SubChapterForComboBox oldValue, SubChapterForComboBox newValue) {
        super(source, fromClient);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

}


