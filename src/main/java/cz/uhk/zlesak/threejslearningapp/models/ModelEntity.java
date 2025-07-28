package cz.uhk.zlesak.threejslearningapp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

/**
 * Model entity data class - holds data about model on FE side or when communicating with backend API endpoints.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@SuperBuilder
public class ModelEntity extends FileEntity {
    String MainTextureEntity;
    List<String> TextureEntities;

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