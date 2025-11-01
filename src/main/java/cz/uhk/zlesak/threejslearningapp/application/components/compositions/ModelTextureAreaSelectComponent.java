package cz.uhk.zlesak.threejslearningapp.application.components.compositions;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import cz.uhk.zlesak.threejslearningapp.application.components.ThreeJsComponent;
import cz.uhk.zlesak.threejslearningapp.application.components.selects.ModelListingSelect;
import cz.uhk.zlesak.threejslearningapp.application.components.selects.TextureAreaSelect;
import cz.uhk.zlesak.threejslearningapp.application.components.selects.TextureListingSelect;
import cz.uhk.zlesak.threejslearningapp.application.models.entities.quickEntities.QuickModelEntity;
import lombok.Getter;
import org.springframework.context.annotation.Scope;

import java.util.Map;
import java.util.Objects;

/**
 * A component that combines texture listing and texture area selection for 3D rendering.
 * It allows users to select textures and apply them to specific areas of a 3D model.
 * The component interacts with a ThreeJsComponent to update the displayed texture based on user selections.
 *
 */
@Scope("prototype")
@Getter
public class ModelTextureAreaSelectComponent extends HorizontalLayout {
    ModelListingSelect modelListingSelect = new ModelListingSelect();
    TextureListingSelect textureListingSelect = new TextureListingSelect();
    TextureAreaSelect textureAreaSelect = new TextureAreaSelect();

    public ModelTextureAreaSelectComponent(ThreeJsComponent renderer) {
        modelListingSelect.addModelChangeListener(
                event -> {
                    var newValue = event.getNewValue();
                    if (newValue != null) {
                        String modelId = newValue.id();
                        if (modelId != null && !Objects.equals(modelId, "")) {
                            renderer.showModel(modelId);
                            textureListingSelect.showTexturesForSelectedModel(modelId);
                        }
                    }
                }
        );
        textureListingSelect.addTextureListingChangeListener(
                event -> {
                    var newValue = event.getNewValue();
                    if (newValue != null) {
                        String textureId = newValue.id();
                        if (textureId != null && !Objects.equals(textureId, "")) {
                            textureAreaSelect.showSelectedTextureAreas(textureId);
                            renderer.switchOtherTexture(event.getNewValue().modelId(), event.getNewValue().id());
                        }
                    }
                }
        );
        textureAreaSelect.addTextureAreaChangeListener(event -> {
            if (event.getNewValue() != null && !Objects.equals(event.getNewValue().textureId(), "")) {
                renderer.applyMaskToMainTexture(event.getNewValue().modelId(), event.getNewValue().textureId(), event.getNewValue().hexColor());
            }
        });
        add(modelListingSelect, textureListingSelect, textureAreaSelect);
        setVisible(false);
        setWidthFull();
    }

    public void initializeData(Map<String, QuickModelEntity> models) {
        textureAreaSelect.initializeTextureAreaSelect(models);
        textureListingSelect.initializeTextureListingSelect(models);
        modelListingSelect.initializeModelSelect(models);
        setVisible(true);
    }
}
