package cz.uhk.zlesak.threejslearningapp.application.models.records;

/**
 * SubChapterForComboBoxRecord - a record class representing a sub-chapter for a select component.
 * This class is used to encapsulate the ID and text of a sub-chapter, typically for UI components.
 *
 * @param id   the unique identifier of the sub-chapter
 * @param text the display text of the sub-chapter
 * @param modelId the id of the model the subchapter belongs to
 */
public record SubChapterForSelectRecord(String id, String text, String modelId) {}

