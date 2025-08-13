package cz.uhk.zlesak.threejslearningapp.components.ComboBoxes;

import com.vaadin.flow.component.ComponentEventListener;
import cz.uhk.zlesak.threejslearningapp.events.SubChapterChangeEvent;
import cz.uhk.zlesak.threejslearningapp.models.records.SubChapterForComboBoxRecord;
import org.springframework.context.annotation.Scope;

import java.util.List;

/**
 * ChapterSelectionComboBox is a custom combo box for selecting sub-chapters.
 * It extends GenericSelectionComboBox to provide functionality for handling sub-chapter selection changes.
 */
@Scope("prototype")
public class ChapterSelectionComboBox extends GenericSelectionComboBox<SubChapterForComboBoxRecord, SubChapterChangeEvent> {
    /**
     * Constructor for ChapterSelectionComboBox.
     * It initializes the combo box with an empty label, a text generator for items, and sets up the event handling for sub-chapter changes.
     * Calls the parent class constructor with the appropriate parameters.
     */
    public ChapterSelectionComboBox() {
        super("", SubChapterForComboBoxRecord::text,
                SubChapterChangeEvent.class,
                (combo, event) -> new SubChapterChangeEvent((ChapterSelectionComboBox) combo, event.isFromClient(), event.getOldValue(), event.getValue()));
    }

    /**
     * Adds a listener for sub-chapter change events.
     * Calls the addGenericChangeListener method from the parent class to register the listener.
     * @param listener the listener to be added
     */
    public void addSubChapterChangeListener(ComponentEventListener<SubChapterChangeEvent> listener) {
        addGenericChangeListener(listener);
    }

    /**
     * This method is used to populate the combo box with sub-chapter records.
     * Calls the initialize method from the parent class to set the items.
     * @param subChapters the list of sub-chapter records to be displayed in the combo box
     */
    public void initializeChapterSelectionComboBox(List<SubChapterForComboBoxRecord> subChapters) {
        initialize(subChapters);
    }
}
