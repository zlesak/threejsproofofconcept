package cz.uhk.zlesak.threejslearningapp.application.components.compositions;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import cz.uhk.zlesak.threejslearningapp.application.components.ThreeJsComponent;
import cz.uhk.zlesak.threejslearningapp.application.components.selects.TextureAreaSelect;
import cz.uhk.zlesak.threejslearningapp.application.components.selects.TextureListingSelect;
import cz.uhk.zlesak.threejslearningapp.application.models.entities.quickEntities.QuickTextureEntity;
import lombok.Getter;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.Objects;

@Scope("prototype")
@Getter
public class TextureSelectsComponent extends HorizontalLayout {
    TextureListingSelect textureListingSelect = new TextureListingSelect();
    TextureAreaSelect textureAreaSelect = new TextureAreaSelect();

    public TextureSelectsComponent(ThreeJsComponent renderer) {
        textureListingSelect.addTextureListingChangeListener(
                event -> {
                    var newValue = event.getNewValue();
                    if (newValue != null) {
                        String textureId = newValue.id();
                        if (textureId != null && !Objects.equals(textureId, "")) {
                            textureAreaSelect.showSelectedTextureAreas(textureId);
                            renderer.switchOtherTexture(textureId);
                        }
                    }
                }
        );
        textureAreaSelect.addTextureAreaChangeListener(event -> {
            if (event.getNewValue() != null && !Objects.equals(event.getNewValue().textureId(), "")) {
                renderer.applyMaskToMainTexture(event.getNewValue().textureId(), event.getNewValue().hexColor());
            } else {
                renderer.returnToLastSelectedTexture();
            }
        });
        add(textureListingSelect, textureAreaSelect);
        setVisible(false);
        setWidthFull();
    }

    public void initializeData(List<QuickTextureEntity> textures) {
        textureAreaSelect.initializeTextureAreaSelect(textures);
        textureListingSelect.initializeTextureListingSelect(textures);
        setVisible(true);

    }
}
