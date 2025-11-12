package cz.uhk.zlesak.threejslearningapp.events.chapter;

import com.vaadin.flow.component.ComponentEvent;
import cz.uhk.zlesak.threejslearningapp.components.selects.ChapterSelect;
import cz.uhk.zlesak.threejslearningapp.domain.chapter.SubChapterForSelect;
import lombok.Getter;

/**
 * Event fired when the selected sub-chapter in the ChapterSelect changes.
 * This event carries the old and new values of the selected sub-chapter to allow listeners to react accordingly.
 */
@Getter
public class SubChapterChangeEvent extends ComponentEvent<ChapterSelect> {
    private final SubChapterForSelect oldValue;
    private final SubChapterForSelect newValue;

    public SubChapterChangeEvent(ChapterSelect source, boolean fromClient, SubChapterForSelect oldValue, SubChapterForSelect newValue) {
        super(source, fromClient);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

}


