package cz.uhk.zlesak.threejslearningapp.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
