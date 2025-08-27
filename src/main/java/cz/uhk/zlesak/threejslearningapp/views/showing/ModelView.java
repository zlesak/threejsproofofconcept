package cz.uhk.zlesak.threejslearningapp.views.showing;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.function.SerializableRunnable;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import cz.uhk.zlesak.threejslearningapp.components.notifications.ErrorNotification;
import cz.uhk.zlesak.threejslearningapp.controllers.ModelController;
import cz.uhk.zlesak.threejslearningapp.controllers.TextureController;
import cz.uhk.zlesak.threejslearningapp.i18n.CustomI18NProvider;
import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickTextureEntity;
import cz.uhk.zlesak.threejslearningapp.utils.TextureMapHelper;
import cz.uhk.zlesak.threejslearningapp.views.listing.ModelListView;
import cz.uhk.zlesak.threejslearningapp.views.scaffolds.ModelScaffold;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Scope;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * ModelView for displaying a 3D model without the need for the sub-chapter to be present.
 * It is accessible at the route "/model/:modelId?".
 * The model is loaded from the backend using the ModelController.
 */
@Slf4j
@Route("model/:modelId?")
@Tag("view-model")
@Scope("prototype")
public class ModelView extends ModelScaffold {
    private final ModelController modelController;
    private final CustomI18NProvider i18nProvider;
    private final TextureController textureController;
    private QuickModelEntity quickModelEntity;
    private String modelId;

    /**
     * Constructor for ModelView.
     * Initializes the view with necessary controllers and providers.
     *
     * @param modelController   controller for handling model-related operations
     * @param i18nProvider      provider for internationalization and localization
     * @param textureController controller for handling texture-related operations
     */
    @Autowired
    public ModelView(ModelController modelController, CustomI18NProvider i18nProvider, TextureController textureController) {
        super();
        this.modelController = modelController;
        this.i18nProvider = i18nProvider;
        this.textureController = textureController;
    }

    /**
     * Handles actions before entering the view.
     * It checks for the presence of a modelId parameter and attempts to load the corresponding model.
     * If the modelId is missing or invalid, it forwards the user to the ModelListView.
     * When coming from a creation view, the model is expected to be in the session.
     *
     * @param event before navigation event with event details
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        RouteParameters parameters = event.getRouteParameters();
        if (parameters.getParameterNames().isEmpty()) {
            event.forwardTo(ModelListView.class);
        }
        this.modelId = parameters.get("modelId").orElse(null);
        if (this.modelId == null) {
            event.forwardTo(ModelListView.class);
        }

        this.quickModelEntity = (QuickModelEntity) VaadinSession.getCurrent().getAttribute("quickModelEntity");
    }

    /**
     * Handles actions before leaving the view.
     * It disposes of the renderer resources to free up memory.
     * The navigation is postponed until the disposal is complete.
     *
     * @param event before leave event with event details
     */
    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        BeforeLeaveEvent.ContinueNavigationAction postponed = event.postpone();
        renderer.dispose((SerializableRunnable) () -> UI.getCurrent().access(postponed::proceed));
    }

    /**
     * Provides the title of the page.
     * The title is localized using the CustomI18NProvider.
     *
     * @return the localized page title
     */
    @Override
    public String getPageTitle() {
        return i18nProvider.getTranslation("page.title.modelView", UI.getCurrent().getLocale());
    }

    /**
     * Handles actions after navigation to the view.
     * It sets the form to listing mode and populates the model name and texture selectors if a model is loaded.
     *
     * @param event after navigation event with event details
     */
    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        if (quickModelEntity != null && quickModelEntity.getModel() != null && Objects.equals(modelId, quickModelEntity.getModel().getId())) {
            try {
                String base64Model = modelController.getModelBase64(quickModelEntity.getModel().getId());
                String base64Texture = null;
                if (quickModelEntity.getMainTexture() != null) {
                    base64Texture = textureController.getTextureBase64(quickModelEntity.getMainTexture().getTextureFileId());
                }
                renderer.loadModel(base64Model, base64Texture);
            } catch (Exception e) {
                log.error(e.getMessage());
                new ErrorNotification("Nepovedlo se načíst model: " + e.getMessage());
                UI.getCurrent().navigate(ModelListView.class);
                return;
            } finally {
                VaadinSession.getCurrent().setAttribute("quickModelEntity", null);
            }
        }

        if (quickModelEntity != null) {
            if (quickModelEntity.getModel() != null && quickModelEntity.getModel().getName() != null) {
                modelUploadFormScrollerComposition.getModelName().setValue(quickModelEntity.getModel().getName());
            } else {
                modelUploadFormScrollerComposition.getModelName().clear();
            }
            if (quickModelEntity.getMainTexture() != null) {
                modelUploadFormScrollerComposition.getIsAdvanced().setValue(true);
                try {
                    Map<String, String> otherTexturesMap = TextureMapHelper.otherTexturesMap(quickModelEntity.getOtherTextures(), textureController);
                    renderer.addOtherTextures(otherTexturesMap);
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                    throw new ApplicationContextException(e.getMessage());
                }
            }
            List<QuickTextureEntity> allTextures = quickModelEntity.getOtherTextures();
            allTextures.addFirst(quickModelEntity.getMainTexture());
            textureSelectsComponent.initializeData(allTextures);
        }

        modelUploadFormScrollerComposition.listingMode();
    }
}
