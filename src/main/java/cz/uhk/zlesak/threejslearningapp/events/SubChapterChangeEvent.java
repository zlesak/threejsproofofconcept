package cz.uhk.zlesak.threejslearningapp.events;

import com.vaadin.flow.component.ComponentEvent;
import cz.uhk.zlesak.threejslearningapp.components.ComboBoxes.ChapterSelectionComboBox;
import cz.uhk.zlesak.threejslearningapp.models.records.SubChapterForComboBoxRecord;
import lombok.Getter;

/**
 * Event fired when the selected sub-chapter in the ChapterSelectionComboBox changes.
 * This event carries the old and new values of the selected sub-chapter to allow listeners to react accordingly.
 */
@Getter
public class SubChapterChangeEvent extends ComponentEvent<ChapterSelectionComboBox> {
    private final SubChapterForComboBoxRecord oldValue;
    private final SubChapterForComboBoxRecord newValue;

    public SubChapterChangeEvent(ChapterSelectionComboBox source, boolean fromClient, SubChapterForComboBoxRecord oldValue, SubChapterForComboBoxRecord newValue) {
        super(source, fromClient);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

}


