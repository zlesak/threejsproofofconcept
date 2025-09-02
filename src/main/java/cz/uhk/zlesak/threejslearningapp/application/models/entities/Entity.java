package cz.uhk.zlesak.threejslearningapp.application.models.entities;

import cz.uhk.zlesak.threejslearningapp.application.data.files.InputStreamMultipartFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * Entity data class - holds common data for all entities in the application.
 * This includes ID, name, creator, creation date, last update date, and an optional file.
 * It provides a method to get the base64 representation of the file if it exists.
 *
 * @see IEntity
 * @see ModelEntity
 * @see TextureEntity
 */
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Entity implements IEntity {
    String Id;
    String Name;
    String Creator;
    String CreationDate;
    String LastUpdateDate;
    InputStreamMultipartFile File;
}
