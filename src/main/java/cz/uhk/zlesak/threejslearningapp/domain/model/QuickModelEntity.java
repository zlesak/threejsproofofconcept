package cz.uhk.zlesak.threejslearningapp.domain.model;

import cz.uhk.zlesak.threejslearningapp.domain.common.QuickFile;
import cz.uhk.zlesak.threejslearningapp.domain.common.QuickFileEntity;
import cz.uhk.zlesak.threejslearningapp.domain.texture.QuickTextureEntity;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * QuickModelEntity data class - holds data about a model, its main texture, and other textures.
 * This is used for quick access to model-related data in the application.
 * It includes the model file, the main texture file, and a list of other texture files.
 * @see QuickFileEntity
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Getter
@Setter
@Builder
public class QuickModelEntity extends QuickFile {
    String metadataId;
    QuickFileEntity model;
    QuickTextureEntity mainTexture;
    List<QuickTextureEntity> otherTextures;

    /**
     * Provides list of all textures assigned to the model including main texture.
     * @return list of model textures
     */
    public List<QuickTextureEntity> getAllTextures(){
        List<QuickTextureEntity> allTextures = new ArrayList<>();
        if(otherTextures != null && !otherTextures.isEmpty()){
            allTextures.addAll(otherTextures);
        }
        if(mainTexture != null){
            allTextures.addFirst(mainTexture);
        }
        return allTextures;
    }
}
