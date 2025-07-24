package cz.uhk.zlesak.threejslearningapp.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Chapter entity data class - holds data about chapter on frontend side or when communicating with backend API endpoints.
 */
@Data
@AllArgsConstructor
@Builder
public class ChapterEntity {
    String ChapterEntityId;
    String ChapterEntityName;
    String ChapterEntityCreator;
    String ChapterEntityCreationDate;
    String ChapterEntityLastUpdateDate;
    String ChapterEntityContent; //when using file as a way to save the chapter content, remove
    List<String> ChapterEntityModelEntities;
    String ChapterEntityMetadata;
//    InputStreamMultipartFile ModelFile; will may not be needed if the chapter will not be saved as a JSON file
}