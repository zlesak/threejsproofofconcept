package cz.uhk.zlesak.threejslearningapp.views.chapter;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.Route;
import cz.uhk.zlesak.threejslearningapp.components.common.Pagination;
import cz.uhk.zlesak.threejslearningapp.components.lists.ChapterListItem;
import cz.uhk.zlesak.threejslearningapp.domain.chapter.ChapterEntity;
import cz.uhk.zlesak.threejslearningapp.domain.common.PageResult;
import cz.uhk.zlesak.threejslearningapp.events.threejs.SearchEvent;
import cz.uhk.zlesak.threejslearningapp.services.ChapterService;
import cz.uhk.zlesak.threejslearningapp.views.layouts.ListingLayout;
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
public class ChapterListView extends ListingLayout {
    private final ChapterService chapterService;


    /**
     * Constructor for ChapterListView.
     * It initializes the view with the necessary controllers and internationalization provider.
     *
     * @param chapterService controller for handling chapter-related operations
     */
    @Autowired
    public ChapterListView(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    /**
     * Provides the title for the page.
     * The title is fetched using the i18NProvider to support localization.
     *
     * @return the localized title of the page
     */
    @Override
    public String getPageTitle() {
        return text("page.title.chapterListView");
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
        afterNavigationAction(event);
        filter.setSearchFieldValue(filterParameters.getSearchText());
        listChapters();
    }

    /**
     * Fetches the list of chapters from the ChapterController and populates the vertical layout with ChapterListItemComponents.
     * It also adds a PaginationComponent at the end of the list for navigating through pages of chapters.
     */
    public void listChapters() {
        itemListLayout.removeAll();
        paginationLayout.removeAll();

        UI.getCurrent().getPage().getHistory().replaceState(null, filterParameters.getLocationQueryParams("chapters"));

        if (filterParameters.getSearchText() != null && !filterParameters.getSearchText().isBlank()) {
            queryFilteredChapters();
        } else {

            PageResult<ChapterEntity> chapterEntityPageResult = chapterService.getChapters(filterParameters);

            List<ChapterEntity> chapterEntities = chapterEntityPageResult.elements().stream().toList();

            for (ChapterEntity chapter : chapterEntities) {
                ChapterListItem itemComponent = new ChapterListItem(chapter);
                itemListLayout.add(itemComponent);
            }
            Pagination pagination = new Pagination(filterParameters.getPageNumber(), filterParameters.getPageSize(), chapterEntityPageResult.total(),
                    p -> {
                        filterParameters.setPageNumber(p);
                        UI.getCurrent().navigate(filterParameters.getLocationQueryParams("chapters"));
                    }
            );
            paginationLayout.add(pagination);
        }
    }

    /**
     * Fetches and displays chapters based on the current filter parameters.
     * It retrieves the filtered list of chapters from the ChapterController and populates the item list layout with ChapterListItem components.
     * A Pagination component is added to the pagination layout, although paging of filtered results is not yet supported. TODO
     */
    private void queryFilteredChapters() {
        List<ChapterEntity> filteredChapters = chapterService.getChapters(filterParameters.getSearchText());

        for (ChapterEntity chapter : filteredChapters) {
            ChapterListItem itemComponent = new ChapterListItem(chapter);
            itemListLayout.add(itemComponent);
        }
        Pagination pagination = new Pagination(1, filteredChapters.size(), filteredChapters.size(), null); //TODO BE needs to support paging of filtered results, otherwise pagination is pointless here
        paginationLayout.add(pagination);
    }

    /**
     * Displays chapters filtered based on the search event parameters.
     *
     * @param event the SearchEvent containing filter parameters
     */
    private void showFilteredChapters(SearchEvent event) {
        filterParameters.setOrderBy(event.getOrderBy());
        filterParameters.setSortDirection(event.getSortDirection());
        filterParameters.setSearchText(event.getValue());
        listChapters();
    }

    /**
     * Handles actions to be performed when the view is attached to the UI.
     * It registers a listener for SearchEvent to update the displayed chapters based on search criteria.
     *
     * @param attachEvent the AttachEvent containing attachment details
     */
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        listingEventRegistrations = ComponentUtil.addListener(attachEvent.getUI(), SearchEvent.class, this::showFilteredChapters);
    }
}
