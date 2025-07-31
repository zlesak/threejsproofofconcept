package cz.uhk.zlesak.threejslearningapp.models.entities;

import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickModelEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Chapter entity data class - holds data about chapter on frontend side or when communicating with backend API endpoints.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
public class ChapterEntity  extends Entity {
    String Content;
    List<QuickModelEntity> Models;
    String Metadata;
}