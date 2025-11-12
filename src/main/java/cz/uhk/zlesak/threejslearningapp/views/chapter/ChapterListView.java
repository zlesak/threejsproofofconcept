package cz.uhk.zlesak.threejslearningapp.views.chapter;

import com.vaadin.flow.component.*;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import cz.uhk.zlesak.threejslearningapp.components.lists.ChapterListItem;
import cz.uhk.zlesak.threejslearningapp.components.common.Pagination;
import cz.uhk.zlesak.threejslearningapp.services.ChapterService;
import cz.uhk.zlesak.threejslearningapp.events.threejs.ThreeJsSearchEvent;
import cz.uhk.zlesak.threejslearningapp.domain.chapter.ChapterEntity;
import cz.uhk.zlesak.threejslearningapp.domain.common.PageResult;
import cz.uhk.zlesak.threejslearningapp.domain.common.SortDirectionEnum;
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
    private final ChapterService chapterController;

    private Registration chapterListingViewEventRegistration;


    /**
     * Constructor for ChapterListView.
     * It initializes the view with the necessary controllers and internationalization provider.
     *
     * @param chapterController controller for handling chapter-related operations
     */
    @Autowired
    public ChapterListView(ChapterService chapterController) {
        this.chapterController = chapterController;
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
            return text("page.title.chapterListView");
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
        afterNavigationAction(event);
        if(this.searchText != null && !this.searchText.isBlank()) {
            filter.setSearchFieldValue(this.searchText);
            queryFilteredChapters(this.searchText);
            this.searchText = null;
        }else {
            listChapters(this.page, this.pageSize, this.orderBy, this.sortDirection);
        }
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
     *
     * @param page  current page number
     * @param limit number of chapters to display per page
     */
    public void listChapters(int page, int limit, String orderBy, SortDirectionEnum sortDirection) {
        clearList();

        UI.getCurrent().getPage().getHistory().replaceState(null, "chapters?page=" + page + "&limit=" + limit + "&orderBy=" + orderBy + "&sortDirection=" + sortDirection);

        PageResult<ChapterEntity> chapterEntityPageResult = chapterController.getChapters(page - 1, limit, orderBy, sortDirection);

        List<ChapterEntity> chapterEntities = chapterEntityPageResult.elements().stream().toList();

        for (ChapterEntity chapter : chapterEntities) {
            ChapterListItem itemComponent = new ChapterListItem(chapter);
            itemListLayout.add(itemComponent);
        }
        Pagination pagination = new Pagination(page, limit, chapterEntityPageResult.total(), p -> UI.getCurrent().navigate("chapters?page=" + p + "&limit=" + limit));
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

    private void showFilteredChapters(ThreeJsSearchEvent event) {
        if (event.getValue() == null || event.getValue().isBlank()) {
            listChapters(this.page, this.pageSize, event.getOrderBy(), event.getSortDirection());
            return;
        }
        queryFilteredChapters(event.getValue());
    }

    private void queryFilteredChapters(String keyword) {

        UI.getCurrent().getPage().getHistory().replaceState(null, "chapters?searchedText=" + keyword);

        List<ChapterEntity> filteredChapters = chapterController.getChapters(keyword);

        clearList();

        for (ChapterEntity chapter : filteredChapters) {
            ChapterListItem itemComponent = new ChapterListItem(chapter);
            itemListLayout.add(itemComponent);
        }
        Pagination pagination = new Pagination(1, filteredChapters.size(), filteredChapters.size(), null);
        paginationLayout.add(pagination);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        chapterListingViewEventRegistration = ComponentUtil.addListener(attachEvent.getUI(), ThreeJsSearchEvent.class, this::showFilteredChapters);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        if (chapterListingViewEventRegistration != null) {
            chapterListingViewEventRegistration.remove();
            chapterListingViewEventRegistration = null;
        }
    }
}
