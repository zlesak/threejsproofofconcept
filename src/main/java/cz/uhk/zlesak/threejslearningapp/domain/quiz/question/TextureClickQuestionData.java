package cz.uhk.zlesak.threejslearningapp.domain.quiz.question;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Texture click question - user clicks on specific area of 3D model texture.
 * Uses ThreeJs.onColorPicked method to capture user's click.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TextureClickQuestionData extends QuestionData {
    /**
     * ID of the model to display
     */
    String modelId;

    /**
     * ID of the texture to use
     */
    String textureId;

    /**
     * Instructions for where to click
     */
    String clickInstruction;
}

