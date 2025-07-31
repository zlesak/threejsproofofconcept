package cz.uhk.zlesak.threejslearningapp.models.entities;

import cz.uhk.zlesak.threejslearningapp.models.IEntity;
import cz.uhk.zlesak.threejslearningapp.models.InputStreamMultipartFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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
