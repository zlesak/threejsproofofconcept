package cz.uhk.zlesak.threejslearningapp.views.scaffolds;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
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
    protected final VerticalLayout verticalLayout;

    /**
     * Constructor for ListingScaffold.
     * Initializes the layout with a vertical layout inside a scroller for displaying listing content.
     */
    public ListingScaffold() {
        this.verticalLayout = new VerticalLayout();
        Scroller modelListScroller = new Scroller(verticalLayout, Scroller.ScrollDirection.VERTICAL);
        modelListScroller.setSizeFull();

        getContent().setPadding(false);
        getContent().add(modelListScroller);
        getContent().setSizeFull();
    }
}
