package cz.uhk.zlesak.threejslearningapp.views.chapter;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.Route;
import cz.uhk.zlesak.threejslearningapp.common.SpringContextUtils;
import cz.uhk.zlesak.threejslearningapp.components.common.Pagination;
import cz.uhk.zlesak.threejslearningapp.components.lists.ChapterListItem;
import cz.uhk.zlesak.threejslearningapp.domain.chapter.ChapterEntity;
import cz.uhk.zlesak.threejslearningapp.domain.chapter.ChapterFilter;
import cz.uhk.zlesak.threejslearningapp.domain.common.FilterParameters;
import cz.uhk.zlesak.threejslearningapp.domain.common.PageResult;
import cz.uhk.zlesak.threejslearningapp.domain.model.ModelFilter;
import cz.uhk.zlesak.threejslearningapp.domain.quiz.QuizFilter;
import cz.uhk.zlesak.threejslearningapp.events.threejs.SearchEvent;
import cz.uhk.zlesak.threejslearningapp.services.ChapterService;
import cz.uhk.zlesak.threejslearningapp.views.layouts.ListingLayout;
import jakarta.annotation.security.PermitAll;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.function.Consumer;

/**
 * ChapterListView Class - Shows the list of available chapters to the user.
 * It fetches chapter data from the backend and displays it using ChapterListItemComponent.
 */
@Slf4j
@Route("chapters")
@Scope("prototype")
@Tag("chapters-listing")
@PermitAll
public class ChapterListView extends ListingLayout<ChapterFilter> {
    private final ChapterService chapterService;
    @Setter
    private Consumer<ChapterEntity> chapterSelectedListener;

    private final boolean quizMode;

    /**
     * Constructor for ChapterListView.
     * It initializes the view with the necessary controllers and internationalization provider.
     *
     * @param chapterService controller for handling chapter-related operations
     */
    @Autowired
    public ChapterListView(ChapterService chapterService) {
        filterParameters = new FilterParameters<>(PageRequest.of(0, 6, Sort.Direction.ASC, "Name"), new ChapterFilter(""));
        this.chapterService = chapterService;
        this.quizMode = false;
    }

    /**
     * No-args constructor for a dialog window for selecting a chapter in quiz create mode
     *
     * @see cz.uhk.zlesak.threejslearningapp.views.quizes.QuizCreateView
     */
    public ChapterListView() {
        this.chapterService = SpringContextUtils.getBean(ChapterService.class);
        this.quizMode = true;
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
     * It fetches the list of chapters from the ChapterService and adds them to the vertical layout.
     * If an error occurs during fetching, it logs the error and shows an error notification
     *
     * @param event after navigation event with event details
     */
    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        filterParameters = new FilterParameters<>(PageRequest.of(0, 6, Sort.Direction.ASC, "Name"), new ChapterFilter(""));
        filter.setSearchFieldValue(filterParameters.getFilter().getSearchText());
        listChapters();
    }

    /**
     * Fetches the list of chapters from the ChapterService and populates the vertical layout with ChapterListItemComponents.
     * It also adds a PaginationComponent at the end of the list for navigating through pages of chapters.
     */
    public void listChapters() {
        itemListLayout.removeAll();
        paginationLayout.removeAll();

        PageResult<ChapterEntity> chapterEntityPageResult = chapterService.getChapters(filterParameters);
        List<ChapterEntity> chapterEntities = chapterEntityPageResult.elements().stream().toList();
        for (ChapterEntity chapter : chapterEntities) {
            ChapterListItem itemComponent = new ChapterListItem(chapter, this.quizMode);
            if (chapterSelectedListener != null) {
                itemComponent.setSelectButtonClickListener(e -> chapterSelectedListener.accept(chapter));
            }
            itemListLayout.add(itemComponent);
        }
        Pagination pagination = new Pagination(filterParameters.getPageRequest().getPageNumber(), filterParameters.getPageRequest().getPageSize(), chapterEntityPageResult.total(),
                p -> {
                    filterParameters.setPageNumber(p);
                    listChapters();
                }
        );
        paginationLayout.add(pagination);
    }

    /**
     * Displays chapters filtered based on the search event parameters.
     *
     * @param event the SearchEvent containing filter parameters
     */
    private void showFilteredChapters(SearchEvent event) {
        filterParameters.setFilteredParameters(event, new ChapterFilter(event.getValue()));
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

        registrations.add(ComponentUtil.addListener(attachEvent.getUI(), SearchEvent.class, this::showFilteredChapters));
    }
}
