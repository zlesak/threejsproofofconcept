package cz.uhk.zlesak.threejslearningapp.views.layouts;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import cz.uhk.zlesak.threejslearningapp.components.common.Filter;
import cz.uhk.zlesak.threejslearningapp.domain.common.SortDirectionEnum;
import cz.uhk.zlesak.threejslearningapp.common.ViewUtils;
import cz.uhk.zlesak.threejslearningapp.views.IView;
import org.springframework.context.annotation.Scope;

import java.util.Objects;

/**
 * ListingScaffold is an abstract base class for views that display listings of entities.
 * It provides a common layout with a vertical layout inside a scroller for displaying the listing content.
 * The class is designed to be extended by specific listing views.
 */
@Scope("prototype")
@Tag("listing-scaffold")
public abstract class ListingLayout extends Composite<VerticalLayout> implements IView {
    protected final VerticalLayout listingLayout, itemListLayout, paginationLayout, secondaryFilterLayout;
    protected final Filter filter = new Filter();

    protected int page = 1;
    protected int pageSize = 10;
    protected SortDirectionEnum sortDirection = SortDirectionEnum.ASC;
    protected String orderBy = "Name";
    protected String searchText = "";

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
        Object[] requestParams = ViewUtils.extractFilterRequestParameters(event.getLocation());
        int newPage = ((Number) requestParams[0]).intValue();
        int newPageSize = ((Number) requestParams[1]).intValue();
        String orderBy = (String) requestParams[2];
        SortDirectionEnum newSortDirection = (SortDirectionEnum) requestParams[3];
        String searchText = (String) requestParams[4];

        if (newPage != this.page) {
            this.page = newPage;
        }
        if (newPageSize != this.pageSize) {
            this.pageSize = newPageSize;
        }
        if (newSortDirection != this.sortDirection) {
            this.sortDirection = newSortDirection;
        }
        if (!Objects.equals(orderBy, this.orderBy)) {
            this.orderBy = orderBy;
        }
        if (!Objects.equals(orderBy, this.searchText)) {
            this.searchText = searchText;
        }
    }
}
