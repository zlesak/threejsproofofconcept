package cz.uhk.zlesak.threejslearningapp.components.selects;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import cz.uhk.zlesak.threejslearningapp.domain.chapter.SubChapterForSelect;
import cz.uhk.zlesak.threejslearningapp.events.chapter.SubChapterChangeEvent;
import org.springframework.context.annotation.Scope;

import java.util.List;

/**
 * ChapterSelect is a select box for selecting sub-chapters.
 * Fires UI-level SubChapterChangeEvent when selection changes.
 */
@Scope("prototype")
public class ChapterSelect extends GenericSelect<SubChapterForSelect, SubChapterChangeEvent> {
    /**
     * Constructor for ChapterSelect.
     * It initializes the select with an empty label, a text generator for items, and sets up the event handling for sub-chapter changes.
     * Calls the parent class constructor with the appropriate parameters.
     */
    public ChapterSelect() {
        super("", SubChapterForSelect::text,
                SubChapterChangeEvent.class,
                (select, event) -> new SubChapterChangeEvent(UI.getCurrent(), event.getOldValue(), event.getValue()));
        setEmptySelectionAllowed(true);
        setEmptySelectionCaption(text("chapterSelect.caption"));
        setWidthFull();

        addValueChangeListener(event ->
                ComponentUtil.fireEvent(
                        UI.getCurrent(),
                        new SubChapterChangeEvent(UI.getCurrent(), event.getOldValue(), event.getValue())
                )
        );
    }

    /**
     * This method is used to populate the select with sub-chapter records.
     * Calls the initialize method from the parent class to set the items.
     *
     * @param subChapters the list of sub-chapter records to be displayed in the select
     */
    public void initializeChapterSelectionSelect(List<SubChapterForSelect> subChapters) {
        initialize(subChapters, false);
    }
}
