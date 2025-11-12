package cz.uhk.zlesak.threejslearningapp.views.layouts;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import cz.uhk.zlesak.threejslearningapp.components.common.Filter;
import cz.uhk.zlesak.threejslearningapp.domain.common.FilterParameters;
import cz.uhk.zlesak.threejslearningapp.domain.common.SortDirectionEnum;
import org.springframework.context.annotation.Scope;

/**
 * ListingScaffold is an abstract base class for views that display listings of entities.
 * It provides a common layout with a vertical layout inside a scroller for displaying the listing content.
 * The class is designed to be extended by specific listing views.
 */
@Scope("prototype")
@Tag("listing-scaffold")
public abstract class ListingLayout extends BaseLayout {
    protected final VerticalLayout listingLayout, itemListLayout, paginationLayout, secondaryFilterLayout;
    protected final Filter filter = new Filter();

    protected FilterParameters filterParameters;

    /**
     * Constructor for ListingScaffold.
     * Initializes the layout with a vertical layout inside a scroller for displaying listing content.
     * Includes secondary filter layout for search/filter per individual implementations on specific views.
     *
     */
    public ListingLayout() {
        this.listingLayout = new VerticalLayout();
        this.itemListLayout = new VerticalLayout();
        this.paginationLayout = new VerticalLayout();
        this.secondaryFilterLayout = new VerticalLayout(filter);

        Scroller modelListScroller = new Scroller(itemListLayout, Scroller.ScrollDirection.VERTICAL);
        itemListLayout.setSpacing(false);
        itemListLayout.getThemeList().add("spacing-s");
        modelListScroller.setSizeFull();

        paginationLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        paginationLayout.setPadding(false);

        listingLayout.setFlexGrow(1, modelListScroller);
        listingLayout.setSizeFull();
        listingLayout.setSpacing(false);
        listingLayout.add(secondaryFilterLayout, modelListScroller, paginationLayout);

        getContent().setPadding(false);
        getContent().add(listingLayout);
        getContent().setSizeFull();
    }

    /**
     * Handles actions to be performed after navigation events.
     *
     * @param event the AfterNavigationEvent containing navigation details
     */
    protected void afterNavigationAction(AfterNavigationEvent event) {
        filterParameters = new FilterParameters();

        var params = event.getLocation().getQueryParameters().getParameters();
        if (params.containsKey("page")) {
            filterParameters.setPageNumber(Integer.parseInt(params.get("page").getFirst()));
        }
        if (params.containsKey("limit")) {
            filterParameters.setPageSize(Integer.parseInt(params.get("limit").getFirst()));
        }
        if (params.containsKey("orderBy")) {
            filterParameters.setOrderBy(String.valueOf(params.get("orderBy").getFirst()));
        }
        if (params.containsKey("sortDirection")) {
            filterParameters.setSortDirection(SortDirectionEnum.valueOf(params.get("sortDirection").getFirst()));
        }
        if (params.containsKey("searchText")) {
            filterParameters.setSearchText(String.valueOf(params.get("searchText").getFirst()));
        }
    }
}
