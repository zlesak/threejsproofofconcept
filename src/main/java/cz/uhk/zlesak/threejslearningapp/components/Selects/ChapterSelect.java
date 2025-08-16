package cz.uhk.zlesak.threejslearningapp.components.Selects;

import com.vaadin.flow.component.ComponentEventListener;
import cz.uhk.zlesak.threejslearningapp.events.SubChapterChangeEvent;
import cz.uhk.zlesak.threejslearningapp.models.records.SubChapterForSelectRecord;
import org.springframework.context.annotation.Scope;

import java.util.List;

/**
 * ChapterSelect is a select box for selecting sub-chapters.
 * It extends GenericSelect to provide functionality for handling sub-chapter selection changes.
 */
@Scope("prototype")
public class ChapterSelect extends GenericSelect<SubChapterForSelectRecord, SubChapterChangeEvent> {
    /**
     * Constructor for ChapterSelect.
     * It initializes the select with an empty label, a text generator for items, and sets up the event handling for sub-chapter changes.
     * Calls the parent class constructor with the appropriate parameters.
     */
    public ChapterSelect() {
        super("", SubChapterForSelectRecord::text,
                SubChapterChangeEvent.class,
                (select, event) -> new SubChapterChangeEvent((ChapterSelect) select, event.isFromClient(), event.getOldValue(), event.getValue()));
    }

    /**
     * Adds a listener for sub-chapter change events.
     * Calls the addGenericChangeListener method from the parent class to register the listener.
     *
     * @param listener the listener to be added
     */
    public void addSubChapterChangeListener(ComponentEventListener<SubChapterChangeEvent> listener) {
        addGenericChangeListener(listener);
    }

    /**
     * This method is used to populate the select with sub-chapter records.
     * Calls the initialize method from the parent class to set the items.
     *
     * @param subChapters the list of sub-chapter records to be displayed in the select
     */
    public void initializeChapterSelectionSelect(List<SubChapterForSelectRecord> subChapters) {
        initialize(subChapters);
    }
}
