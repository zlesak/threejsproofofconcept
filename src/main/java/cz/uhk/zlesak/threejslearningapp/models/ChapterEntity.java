package cz.uhk.zlesak.threejslearningapp.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Chapter entity data class - holds data about chapter on frontend side or when communicating with backend API endpoints.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChapterEntity{
        String id;
        String header;
        String content;
        String modelPath;
        List<ChapterEntity> childChapter;
}
