package cz.uhk.zlesak.threejslearningapp.application.models.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * Texture entity data class - holds data about texture from a model on FE side or when communicating with backend API endpoints.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@SuperBuilder
public class TextureEntity extends Entity {
    String CsvContent;
}