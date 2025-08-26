package cz.uhk.zlesak.threejslearningapp.components.compositions;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import cz.uhk.zlesak.threejslearningapp.components.selects.TextureAreaSelect;
import cz.uhk.zlesak.threejslearningapp.components.selects.TextureListingSelect;
import cz.uhk.zlesak.threejslearningapp.events.toTextureSelectComposition.SetTextureAreaByIdEvent;
import cz.uhk.zlesak.threejslearningapp.events.toTextureSelectComposition.SetTextureByIdEvent;
import cz.uhk.zlesak.threejslearningapp.events.toTextureSelectComposition.TextureSelectsInEvent;
import cz.uhk.zlesak.threejslearningapp.events.toThreeJs.ApplyMaskToMainTextureEvent;
import cz.uhk.zlesak.threejslearningapp.events.toThreeJs.ReturnToLastSelectedTextureEvent;
import cz.uhk.zlesak.threejslearningapp.events.toThreeJs.SwitchOtherTextureEvent;
import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickTextureEntity;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@Scope("prototype")
@Getter
public class TextureSelectsComponent extends HorizontalLayout implements ApplicationListener<TextureSelectsInEvent> {
    TextureListingSelect textureListingSelect = new TextureListingSelect();
    TextureAreaSelect textureAreaSelect = new TextureAreaSelect();

    public TextureSelectsComponent(ApplicationEventPublisher applicationEventPublisher) {
        textureListingSelect.addTextureListingChangeListener(
                event -> {
                    var newValue = event.getNewValue();
                    if (newValue != null) {
                        String textureId = newValue.id();
                        if (textureId != null && !Objects.equals(textureId, "")) {
                            textureAreaSelect.showSelectedTextureAreas(textureId);
                            applicationEventPublisher.publishEvent(new SwitchOtherTextureEvent(this, textureId));
                        }
                    }
                }
        );
        textureAreaSelect.addTextureAreaChangeListener(event -> {
            if (event.getNewValue() != null && !Objects.equals(event.getNewValue().textureId(), "")) {
                applicationEventPublisher.publishEvent(new ApplyMaskToMainTextureEvent(this, event.getNewValue().textureId(), event.getNewValue().hexColor()));
            } else {
                applicationEventPublisher.publishEvent(new ReturnToLastSelectedTextureEvent(this));
            }
        });
        add(textureListingSelect, textureAreaSelect);
        setWidthFull();
    }

    public void initializeData(List<QuickTextureEntity> textures) {
        textureAreaSelect.initializeTextureAreaSelect(textures);
        textureListingSelect.initializeTextureListingSelect(textures);

    }

    @Override
    public void onApplicationEvent(@NotNull TextureSelectsInEvent event) {
        switch (event) {
            case SetTextureByIdEvent setTextureByIdEvent -> textureListingSelect.setSelectedTextureById(setTextureByIdEvent.getTextureId());
            case SetTextureAreaByIdEvent setTextureByIdEvent -> textureAreaSelect.setSelectedAreaByHexColor(setTextureByIdEvent.getHexColor(), setTextureByIdEvent.getTextureId());
            default -> throw new IllegalStateException("Unexpected value: " + event);
        }
    }
}
