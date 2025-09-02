package cz.uhk.zlesak.threejslearningapp.application.views.listing;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.Route;
import cz.uhk.zlesak.threejslearningapp.application.components.ChapterListItemComponent;
import cz.uhk.zlesak.threejslearningapp.application.components.PaginationComponent;
import cz.uhk.zlesak.threejslearningapp.application.controllers.ChapterController;
import cz.uhk.zlesak.threejslearningapp.application.i18n.CustomI18NProvider;
import cz.uhk.zlesak.threejslearningapp.application.models.entities.ChapterEntity;
import cz.uhk.zlesak.threejslearningapp.application.models.records.PageResult;
import cz.uhk.zlesak.threejslearningapp.application.views.scaffolds.ListingScaffold;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import java.util.List;

/**
 * ChapterListView Class - Shows the list of available chapters to the user.
 * It fetches chapter data from the backend and displays it using ChapterListItemComponent.
 */
@Slf4j
@Route("chapters")
@Scope("prototype")
@Tag("chapters-listing")
@PermitAll
public class ChapterListView extends ListingScaffold {
    private final ChapterController chapterController;
    private final CustomI18NProvider customI18NProvider;

    /**
     * Constructor for ChapterListView.
     * It initializes the view with the necessary controllers and internationalization provider.
     *
     * @param chapterController controller for handling chapter-related operations
     * @param customI18NProvider provider for internationalization and localization
     */
    @Autowired
    public ChapterListView(ChapterController chapterController, CustomI18NProvider customI18NProvider) {
        this.chapterController = chapterController;
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
            return customI18NProvider.getTranslation("page.title.chapterListView", UI.getCurrent().getLocale());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Called after navigation to this view.
     * It fetches the list of chapters from the ChapterController and adds them to the vertical layout.
     * If an error occurs during fetching, it logs the error and shows an error notification
     *
     * @param event after navigation event with event details
     */
    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        int page = 1;
        int limit = 10;
        if (event.getLocation().getQueryParameters().getParameters().containsKey("page")) {
            page = Integer.parseInt(event.getLocation().getQueryParameters().getParameters().get("page").getFirst());
        }
        if (event.getLocation().getQueryParameters().getParameters().containsKey("limit")) {
            limit = Integer.parseInt(event.getLocation().getQueryParameters().getParameters().get("limit").getFirst());
        }
        listChapters(page, limit);
    }

    /**
     * Called before leaving this view.
     * Currently, it does not perform any specific actions but can be extended in the future if needed.
     *
     * @param event before navigation event with event details
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {

    }

    /**
     * Called before leaving this view.
     * Currently, it does not perform any specific actions but can be extended in the future if needed.
     *
     * @param event before navigation event with event details
     */
    @Override
    public void beforeLeave(BeforeLeaveEvent event) {

    }

    /**
     * Fetches the list of chapters from the ChapterController and populates the vertical layout with ChapterListItemComponents.
     * It also adds a PaginationComponent at the end of the list for navigating through pages of chapters.
     * @param page current page number
     * @param limit number of chapters to display per page
     */
    public void listChapters(int page, int limit) {
        clearList();
        PageResult<ChapterEntity> chapterEntityPageResult = chapterController.getChapters(page-1, limit);

        List<ChapterEntity> chapterEntities = chapterEntityPageResult.elements().stream().toList();

        for (ChapterEntity chapter : chapterEntities) {
            ChapterListItemComponent itemComponent = new ChapterListItemComponent(chapter);
            itemListLayout.add(itemComponent);
        }
        PaginationComponent paginationComponent = new PaginationComponent(page, limit, chapterEntityPageResult.total(), p -> UI.getCurrent().navigate("chapters?page=" + p + "&limit=" + limit));
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
