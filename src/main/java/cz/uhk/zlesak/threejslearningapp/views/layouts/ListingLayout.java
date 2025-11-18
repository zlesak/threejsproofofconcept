package cz.uhk.zlesak.threejslearningapp.views.layouts;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import cz.uhk.zlesak.threejslearningapp.components.common.Filter;
import cz.uhk.zlesak.threejslearningapp.domain.common.FilterParameters;
import org.springframework.context.annotation.Scope;

/**
 * ListingScaffold is an abstract base class for views that display listings of entities.
 * It provides a common layout with a vertical layout inside a scroller for displaying the listing content.
 * The class is designed to be extended by specific listing views.
 */
@Scope("prototype")
@Tag("listing-scaffold")
public abstract class ListingLayout<R> extends BaseLayout {
    protected final VerticalLayout listingLayout, itemListLayout, paginationLayout, secondaryFilterLayout;
    protected final Filter filter = new Filter();

    protected FilterParameters<R> filterParameters;

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
}
