package cz.uhk.zlesak.threejslearningapp.application.views.listing;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.Route;
import cz.uhk.zlesak.threejslearningapp.application.components.ModelListItemComponent;
import cz.uhk.zlesak.threejslearningapp.application.components.PaginationComponent;
import cz.uhk.zlesak.threejslearningapp.application.controllers.ModelController;
import cz.uhk.zlesak.threejslearningapp.application.models.entities.quickEntities.QuickFile;
import cz.uhk.zlesak.threejslearningapp.application.models.entities.quickEntities.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.application.models.records.PageResult;
import cz.uhk.zlesak.threejslearningapp.application.utils.SpringContextUtils;
import cz.uhk.zlesak.threejslearningapp.application.views.scaffolds.ListingScaffold;
import jakarta.annotation.security.PermitAll;
import lombok.Setter;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.function.Consumer;

/**
 * ModelListView is a view that displays a list of 3D models available in the application.
 * It extends ListingScaffold to provide a structured layout for listing models.
 * The view retrieves model data from the ModelController and displays each model using ModelListItemComponent.
 */
@Route("models")
@Scope("prototype")
@Tag("models-listing")
@PermitAll
public class ModelListView extends ListingScaffold {
    private final ModelController modelController;
    @Setter
    private Consumer<QuickModelEntity> modelSelectedListener;

    /**
     * Constructor for ModelListView.
     * It initializes the view with the necessary controllers and providers.
     *
     */
    public ModelListView() {
        this.modelController = SpringContextUtils.getBean(ModelController.class);
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
            return text("page.title.modelListView");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Called after the navigation to this view is complete.
     * This method populates the list of models by calling listComponents.
     *
     * @param event after navigation event with event details
     */
    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        afterNavigationAction(event);
        listModels(this.page, this.pageSize, true, null);
    }

    /**
     * Called before entering the view.
     * Currently, this method does not perform any actions but can be used for pre-navigation logic if needed.
     *
     * @param event before navigation event with event details
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {

    }

    /**
     * Called before leaving the view.
     * Currently, this method does not perform any actions but can be used for pre-navigation logic if needed.
     *
     * @param event before navigation event with event details
     */
    @Override
    public void beforeLeave(BeforeLeaveEvent event) {

    }

    /**
     * Lists the model components in the view.
     * It retrieves the list of models from the ModelController and creates a ModelListItemComponent for each model.
     * Based on listView parameter, it displays the item based on the specified view format.
     *
     * @param page     the page number to retrieve
     * @param limit    the number of models to retrieve per page
     * @param listView boolean indicating whether to display the models in a list view format
     */
    public void listModels(int page, int limit, boolean listView, List<String> alreadySelectedIds) {
        clearList();
        PageResult<QuickFile> quickFilePageResult = modelController.getModels(page - 1, limit);

        List<QuickModelEntity> quickModelEntities = quickFilePageResult.elements().stream()
                .filter(f -> f instanceof QuickModelEntity)
                .map(f -> (QuickModelEntity) f)
                .toList();
        for (QuickModelEntity model : quickModelEntities) {
            if(alreadySelectedIds != null && alreadySelectedIds.contains(model.getModel().getId())) {
                continue;
            }
            ModelListItemComponent itemComponent = new ModelListItemComponent(model, listView);
            itemComponent.setSelectButtonClickListener(e -> {
                if (modelSelectedListener != null) {
                    modelSelectedListener.accept(model);
                }
            });
            itemListLayout.add(itemComponent);
        }
        PaginationComponent paginationComponent;
        if (listView) {
            paginationComponent = new PaginationComponent(page, limit, quickFilePageResult.total(), p -> UI.getCurrent().navigate("models?page=" + p + "&limit=" + limit));
        } else {
            paginationComponent = new PaginationComponent(page, limit, quickFilePageResult.total(), p -> listModels(p, limit, false, null));
        }
        paginationLayout.add(paginationComponent);
    }

    /**
     * Clears all components from the vertical layout.
     * This method is used to reset the list before populating it with new components.
     */
    private void clearList() {
        itemListLayout.removeAll();
        paginationLayout.removeAll();
    }
}
