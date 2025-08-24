package cz.uhk.zlesak.threejslearningapp.views.listing;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.Route;
import cz.uhk.zlesak.threejslearningapp.components.ChapterListItemComponent;
import cz.uhk.zlesak.threejslearningapp.components.notifications.ErrorNotification;
import cz.uhk.zlesak.threejslearningapp.controllers.ChapterController;
import cz.uhk.zlesak.threejslearningapp.i18n.CustomI18NProvider;
import cz.uhk.zlesak.threejslearningapp.models.entities.ChapterEntity;
import cz.uhk.zlesak.threejslearningapp.views.scaffolds.ListingScaffold;
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
        try {
            List<ChapterEntity> chapters = chapterController.getChapters();
            for (ChapterEntity chapter : chapters) {
                ChapterListItemComponent itemComponent = new ChapterListItemComponent(chapter);
                verticalLayout.add(itemComponent);
            }
        } catch (Exception e) {
            log.error("Error loading chapters", e);
            new ErrorNotification("Chyba při načítání kapitol" + e.getMessage());
            throw new RuntimeException(e);
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
}
