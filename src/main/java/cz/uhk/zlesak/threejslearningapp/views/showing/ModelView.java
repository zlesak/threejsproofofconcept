package cz.uhk.zlesak.threejslearningapp.views.showing;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.function.SerializableRunnable;
import com.vaadin.flow.router.*;
import com.vaadin.flow.i18n.I18NProvider;
import cz.uhk.zlesak.threejslearningapp.controllers.ModelController;
import cz.uhk.zlesak.threejslearningapp.data.enums.ViewTypeEnum;
import cz.uhk.zlesak.threejslearningapp.views.listing.ModelListView;
import cz.uhk.zlesak.threejslearningapp.views.scaffolds.ModelScaffold;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import java.io.IOException;

@Slf4j
@Route("model/:modelId?")
@Tag("view-model")
@Scope("prototype")
public class ModelView extends ModelScaffold {
    private final ModelController modelController;

    @Autowired
    public ModelView(ModelController modelController, I18NProvider i18nProvider) {
        super(ViewTypeEnum.VIEW);
        this.modelController = modelController;
        this.i18nProvider = i18nProvider;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        RouteParameters parameters = event.getRouteParameters();
        if (parameters.getParameterNames().isEmpty()) {
            event.forwardTo(ModelListView.class);
        }
        String modelId = parameters.get("modelId").orElse(null);
        if (modelId == null) {
            event.forwardTo(ModelListView.class);
        }

        try {
            String base64Model = modelController.getModelBase64(modelId); //TODO
            renderer.loadModel(base64Model);
        } catch (IOException e) {
            log.error(e.getMessage());
            Notification.show("Nepovedlo se načíst model: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
            event.forwardTo(ModelListView.class);
        } catch (Exception e){
            log.error("Neočekávaná chyba při načítání modelu: {}", e.getMessage(), e);
            Notification.show("Neočekávaná chyba při načítání modelu: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
            event.forwardTo(ModelListView.class);
        }
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        BeforeLeaveEvent.ContinueNavigationAction postponed = event.postpone();
        renderer.dispose((SerializableRunnable) () -> UI.getCurrent().access(postponed::proceed));
    }

    @Override
    public String getPageTitle() {
        return i18nProvider.getTranslation("page.title.modelView", UI.getCurrent().getLocale());
    }
}
