package cz.uhk.zlesak.threejslearningapp.models.entities;

import cz.uhk.zlesak.threejslearningapp.data.files.InputStreamMultipartFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

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

    public String getBase64File() throws IOException {
        if (File != null) {
            InputStream inputStream = File.getInputStream();
            byte[] bytes = inputStream.readAllBytes();
            inputStream.close();

            return Base64.getEncoder().encodeToString(bytes);
        }
        return null;
    }
}
