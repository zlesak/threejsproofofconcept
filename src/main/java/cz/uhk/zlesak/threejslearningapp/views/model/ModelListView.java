package cz.uhk.zlesak.threejslearningapp.views.model;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.Route;
import cz.uhk.zlesak.threejslearningapp.common.SpringContextUtils;
import cz.uhk.zlesak.threejslearningapp.components.common.Pagination;
import cz.uhk.zlesak.threejslearningapp.components.lists.ModelListItem;
import cz.uhk.zlesak.threejslearningapp.domain.common.FilterParameters;
import cz.uhk.zlesak.threejslearningapp.domain.common.PageResult;
import cz.uhk.zlesak.threejslearningapp.domain.common.QuickFile;
import cz.uhk.zlesak.threejslearningapp.domain.common.SortDirectionEnum;
import cz.uhk.zlesak.threejslearningapp.domain.model.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.events.threejs.SearchEvent;
import cz.uhk.zlesak.threejslearningapp.services.ModelService;
import cz.uhk.zlesak.threejslearningapp.views.layouts.ListingLayout;
import jakarta.annotation.security.PermitAll;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
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
    private final ModelService modelService;
    @Setter
    private Consumer<QuickModelEntity> modelSelectedListener;

    /**
     * Constructor for ModelListView.
     * Initializes the view with the necessary services using dependency injection.
     * filterParameters is initialized here for ModelListDialog usage.
     *
     * @see cz.uhk.zlesak.threejslearningapp.components.dialogs.ModelListDialog
     */
    public ModelListView() {
        filterParameters = new FilterParameters(1, 6, "Name", SortDirectionEnum.ASC, "");
        this.modelService = SpringContextUtils.getBean(ModelService.class);
        filter.getSearchField().setEnabled(false);
    }

    /**
     * Provides the title for the page.
     * The title is fetched using the i18NProvider to support localization.
     *
     * @return the localized title of the page
     */
    @Override
    public String getPageTitle() {
        return text("page.title.modelListView");
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
        listModels(true);
    }

    /**
     * Lists the model components in the view.
     * It retrieves the list of models from the ModelService and creates a ModelListItemComponent for each model.
     * Based on listView parameter, it displays the item based on the specified view format.
     *
     * @param listView boolean indicating whether to display the models in a list view format
     */
    public void listModels(boolean listView) {
        itemListLayout.removeAll();
        paginationLayout.removeAll();
        PageResult<QuickFile> quickFilePageResult = modelService.getModels(filterParameters);

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
        Pagination pagination = getPagination(listView, quickFilePageResult);
        paginationLayout.add(pagination);
    }

    /**
     * Generates a Pagination component based on the current view format and page result.
     * @param listView boolean indicating whether the view is in list format
     * @param quickFilePageResult the PageResult containing QuickFile elements
     * @return a Pagination component configured for the current view format
     */
    @NotNull
    private Pagination getPagination(boolean listView, PageResult<QuickFile> quickFilePageResult) {
        Pagination pagination;
        if (listView) {
            pagination = new Pagination(filterParameters.getPageNumber(), filterParameters.getPageSize(), quickFilePageResult.total(),
                    p -> {
                        filterParameters.setPageNumber(p);
                        UI.getCurrent().navigate(filterParameters.getLocationQueryParams("models"));
                    });
        } else {
            pagination = new Pagination(filterParameters.getPageNumber(), filterParameters.getPageSize(), quickFilePageResult.total(),
                    p -> {
                        filterParameters.setPageNumber(p);
                        listModels(false);
                    });
        }
        return pagination;
    }

    /**
     * Displays models filtered based on the search event parameters.
     * @param event the SearchEvent containing filter parameters
     */
    private void showFilteredModels(SearchEvent event) {
        filterParameters.setOrderBy(event.getOrderBy());
        filterParameters.setSortDirection(event.getSortDirection());
        filterParameters.setSearchText(event.getValue());
        listModels(true);
    }

    /**
     * Handles actions to be performed when the view is attached to the UI.
     * It registers a listener for SearchEvent to update the displayed models based on search criteria.
     * @param attachEvent the AttachEvent containing attachment details
     */
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        registrations.add(ComponentUtil.addListener(attachEvent.getUI(), SearchEvent.class, this::showFilteredModels));
    }
}
