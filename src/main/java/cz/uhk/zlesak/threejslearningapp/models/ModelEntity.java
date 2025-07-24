package cz.uhk.zlesak.threejslearningapp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

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
}