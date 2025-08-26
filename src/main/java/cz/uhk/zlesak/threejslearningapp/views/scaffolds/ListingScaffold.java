package cz.uhk.zlesak.threejslearningapp.views.scaffolds;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import cz.uhk.zlesak.threejslearningapp.views.IView;
import org.springframework.context.annotation.Scope;

/**
 * ListingScaffold is an abstract base class for views that display listings of entities.
 * It provides a common layout with a vertical layout inside a scroller for displaying the listing content.
 * The class is designed to be extended by specific listing views.
 */
@Scope("prototype")
@Tag("listing-scaffold")
public abstract class ListingScaffold extends Composite<VerticalLayout> implements IView {
    protected final VerticalLayout listingLayout, itemListLayout, paginationLayout;

    /**
     * Constructor for ListingScaffold.
     * Initializes the layout with a vertical layout inside a scroller for displaying listing content.
     */
    public ListingScaffold() {
        this.listingLayout = new VerticalLayout();
        this.itemListLayout = new VerticalLayout();
        this.paginationLayout = new VerticalLayout();
        Scroller modelListScroller = new Scroller(itemListLayout, Scroller.ScrollDirection.VERTICAL);
        itemListLayout.setSpacing(false);
        itemListLayout.getThemeList().add("spacing-s");
        modelListScroller.setSizeFull();

        paginationLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        listingLayout.setFlexGrow(1, modelListScroller);
        listingLayout.setSizeFull();
        listingLayout.add(modelListScroller, paginationLayout);

        getContent().setPadding(false);
        getContent().add(listingLayout);
        getContent().setSizeFull();
    }
}
