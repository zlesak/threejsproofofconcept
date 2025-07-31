package cz.uhk.zlesak.threejslearningapp.models.entities.listingEntities;

import lombok.Data;
import lombok.AllArgsConstructor;

/**
 * Chapter listing entity data class - holds listing data for a chapter on the frontend side for display purposes.
 */
@Data
@AllArgsConstructor
public class ChapterListingEntity {
    private String chapterId;
    private String chapterName;
    private String chapterCreator;
    private String chapterCreationDate;
    private String chapterLastUpdateDate;
    private String chapterMetadata;
}
