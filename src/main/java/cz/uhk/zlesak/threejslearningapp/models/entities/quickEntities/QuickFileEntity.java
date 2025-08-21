package cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * QuickFileEntity data class - holds data about a file (model or texture) on the frontend side.
 * The reason behind this QickFileEntity is to provide a lightweight representation of a file without the need for it
 * having it loaded into the application.
 * Can be then used to fetch the certain file form the BE side when needed.
 */
@Data
@AllArgsConstructor
@Builder
public class QuickFileEntity implements IQuickFile{
    String id;
    String name;
}
