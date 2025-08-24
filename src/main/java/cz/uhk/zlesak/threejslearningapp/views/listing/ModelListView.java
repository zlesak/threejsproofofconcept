package cz.uhk.zlesak.threejslearningapp.views.listing;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.Route;
import cz.uhk.zlesak.threejslearningapp.components.ModelListItemComponent;
import cz.uhk.zlesak.threejslearningapp.controllers.ModelController;
import cz.uhk.zlesak.threejslearningapp.i18n.CustomI18NProvider;
import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.views.scaffolds.ListingScaffold;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import java.util.List;

/**
 * ModelListView is a view that displays a list of 3D models available in the application.
 * It extends ListingScaffold to provide a structured layout for listing models.
 * The view retrieves model data from the ModelController and displays each model using ModelListItemComponent.
 */
@Route("models")
@Scope("prototype")
public class ModelListView extends ListingScaffold {
    private final ModelController modelController;
    private final CustomI18NProvider customI18NProvider;

    /**
     * Constructor for ModelListView.
     * It initializes the view with the necessary controllers and providers.
     * @param modelController controller for handling model-related operations
     * @param customI18NProvider provider for internationalization and localization
     */
    @Autowired
    public ModelListView(ModelController modelController, CustomI18NProvider customI18NProvider) {
        this.modelController = modelController;
        this.customI18NProvider = customI18NProvider;
    }

    /**
     * Provides the title for the page.
     * The title is fetched using the i18NProvider to support localization.
     *
     * @return the localized title of the page
     */
    @Override
    public String getPageTitle() {
        try {
            return customI18NProvider.getTranslation("page.title.modelListView", UI.getCurrent().getLocale());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Called after the navigation to this view is complete.
     * It retrieves the list of models from the ModelController and populates the vertical layout with ModelListItemComponent instances for each model.
     * @param event after navigation event with event details
     */
    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        List<QuickModelEntity> quickModels = modelController.getModels();

        for (QuickModelEntity model : quickModels) {
            ModelListItemComponent itemComponent = new ModelListItemComponent(model);
            verticalLayout.add(itemComponent);
        }
    }

    /**
     * Called before entering the view.
     * Currently, this method does not perform any actions but can be used for pre-navigation logic if needed.
     * @param event before navigation event with event details
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {

    }

    /**
     * Called before leaving the view.
     * Currently, this method does not perform any actions but can be used for pre-navigation logic if needed.
     * @param event before navigation event with event details
     */
    @Override
    public void beforeLeave(BeforeLeaveEvent event) {

    }
}
