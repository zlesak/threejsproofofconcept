package cz.uhk.zlesak.threejslearningapp.views.model;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.Route;
import cz.uhk.zlesak.threejslearningapp.common.SpringContextUtils;
import cz.uhk.zlesak.threejslearningapp.components.common.Pagination;
import cz.uhk.zlesak.threejslearningapp.components.lists.ModelListItem;
import cz.uhk.zlesak.threejslearningapp.domain.common.PageResult;
import cz.uhk.zlesak.threejslearningapp.domain.common.QuickFile;
import cz.uhk.zlesak.threejslearningapp.domain.model.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.services.ModelService;
import cz.uhk.zlesak.threejslearningapp.views.layouts.ListingLayout;
import jakarta.annotation.security.PermitAll;
import lombok.Setter;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.function.Consumer;

/**
 * ModelListView displays a list of 3D models available in the application.
 * It extends ListingLayout to provide a structured layout for listing models.
 * The view retrieves model data from the ModelService and displays each model using ModelListItem component.
 */
@Route("models")
@Scope("prototype")
@Tag("models-listing")
@PermitAll
public class ModelListView extends ListingLayout {
    private final ModelService modelController;
    @Setter
    private Consumer<QuickModelEntity> modelSelectedListener;

    /**
     * Constructor for ModelListView.
     * Initializes the view with the necessary services using dependency injection.
     */
    public ModelListView() {
        this.modelController = SpringContextUtils.getBean(ModelService.class);
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
        listModels(this.page, this.pageSize, true);
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
    public void listModels(int page, int limit, boolean listView) {
        clearList();
        PageResult<QuickFile> quickFilePageResult = modelController.getModels(page - 1, limit);

        List<QuickModelEntity> quickModelEntities = quickFilePageResult.elements().stream()
                .filter(f -> f instanceof QuickModelEntity)
                .map(f -> (QuickModelEntity) f)
                .toList();
        for (QuickModelEntity model : quickModelEntities) {
            ModelListItem itemComponent = new ModelListItem(model, listView);
            itemComponent.setSelectButtonClickListener(e -> {
                if (modelSelectedListener != null) {
                    modelSelectedListener.accept(model);
                }
            });
            itemListLayout.add(itemComponent);
        }
        Pagination pagination;
        if (listView) {
            pagination = new Pagination(page, limit, quickFilePageResult.total(), p -> UI.getCurrent().navigate("models?page=" + p + "&limit=" + limit));
        } else {
            pagination = new Pagination(page, limit, quickFilePageResult.total(), p -> listModels(p, limit, false));
        }
        paginationLayout.add(pagination);
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
