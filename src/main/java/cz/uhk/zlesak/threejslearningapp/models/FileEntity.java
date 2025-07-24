package cz.uhk.zlesak.threejslearningapp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class FileEntity implements IFileEntity {
    String Id;
    String Name;
    String Creator;
    String CreationDate;
    String LastUpdateDate;
    String Metadata;
    InputStreamMultipartFile File;
}
