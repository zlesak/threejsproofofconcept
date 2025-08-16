package cz.uhk.zlesak.threejslearningapp.events;

import com.vaadin.flow.component.ComponentEvent;
import cz.uhk.zlesak.threejslearningapp.components.Selects.ChapterSelect;
import cz.uhk.zlesak.threejslearningapp.models.records.SubChapterForSelectRecord;
import lombok.Getter;

/**
 * Event fired when the selected sub-chapter in the ChapterSelect changes.
 * This event carries the old and new values of the selected sub-chapter to allow listeners to react accordingly.
 */
@Getter
public class SubChapterChangeEvent extends ComponentEvent<ChapterSelect> {
    private final SubChapterForSelectRecord oldValue;
    private final SubChapterForSelectRecord newValue;

    public SubChapterChangeEvent(ChapterSelect source, boolean fromClient, SubChapterForSelectRecord oldValue, SubChapterForSelectRecord newValue) {
        super(source, fromClient);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

}


