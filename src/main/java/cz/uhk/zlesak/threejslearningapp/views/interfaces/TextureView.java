package cz.uhk.zlesak.threejslearningapp.views.interfaces;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.*;
import cz.uhk.zlesak.threejslearningapp.controllers.TextureController;
import cz.uhk.zlesak.threejslearningapp.data.ViewTypeEnum;
import cz.uhk.zlesak.threejslearningapp.views.MainPageView;
import cz.uhk.zlesak.threejslearningapp.views.listing.ModelListView;
import cz.uhk.zlesak.threejslearningapp.views.scaffolds.TextureScaffold;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;


@Slf4j
@PageTitle("Zobrazit texturu")
@Route("texture/:textureId?")
@Tag("view-texture")
@Scope("prototype")
public class TextureView extends TextureScaffold {
    protected final TextureController textureController;

    public TextureView(TextureController textureController) {
        super(ViewTypeEnum.VIEW);
        this.textureController = textureController;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        RouteParameters parameters = event.getRouteParameters();
        if (parameters.getParameterNames().isEmpty()) {
            event.forwardTo(MainPageView.class);
        }
        String textureId = parameters.get("textureId").orElse(null);
        if (textureId == null) {
            event.forwardTo(MainPageView.class);
        }
        try {
            textureImage.setSrc(textureController.getTextureImage(textureId));
            textureName.setValue(textureController.getTextureName(textureId));
        } catch (Exception e) {
            log.error(e.getMessage());
            Notification.show("Nepovedlo se načíst texturu: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
            event.forwardTo(ModelListView.class);
        }
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {

    }
}
