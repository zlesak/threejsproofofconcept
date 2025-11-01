package cz.uhk.zlesak.threejslearningapp.application.views.showing;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.router.*;
import cz.uhk.zlesak.threejslearningapp.application.components.notifications.ErrorNotification;
import cz.uhk.zlesak.threejslearningapp.application.controllers.ChapterController;
import cz.uhk.zlesak.threejslearningapp.application.controllers.ModelController;
import cz.uhk.zlesak.threejslearningapp.application.controllers.TextureController;
import cz.uhk.zlesak.threejslearningapp.application.models.entities.quickEntities.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.application.utils.TextureMapHelper;
import cz.uhk.zlesak.threejslearningapp.application.views.listing.ChapterListView;
import cz.uhk.zlesak.threejslearningapp.application.views.scaffolds.ChapterScaffold;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import java.util.Map;

/**
 * ChapterView Class - Shows the requested chapter from URL parameter. Initializes all the necessary elements
 * to provide the user with the chapter content in intuitive way.
 */
@Slf4j
@Route("chapter/:chapterId?")
@JavaScript("./js/scroll-to-element-data-id.js")
@Tag("chapter-view")
@Scope("prototype")
@PermitAll
public class ChapterView extends ChapterScaffold {
    private final ModelController modelController;
    private final TextureController textureController;
    private final ChapterController chapterController;

    private String chapterId;

    /**
     * ChapterView constructor - creates instance of chapter view instance that then accomplishes the goal of getting
     * and serving the user the requested chapter from proper backend API endpoint via chapterApiClient.
     */
    @Autowired
    public ChapterView(ChapterController chapterController, ModelController modelController, TextureController textureController) {
        super(false);
        this.chapterController = chapterController;
        this.modelController = modelController;
        this.textureController = textureController;
    }

    /**
     * Overridden beforeLeave function to proper disposal of the model renderer to free the used RAM memory immediately.
     *
     * @param event BeforeLeave
     */
    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        BeforeLeaveEvent.ContinueNavigationAction postponed = event.postpone();
        UI ui = UI.getCurrent();
        if (modelDiv.renderer != null) {
            modelDiv.renderer.dispose(() -> ui.access(postponed::proceed));
        } else {
            postponed.proceed();
        }
    }

    /**
     * Overridden beforeEnter function to check if the chapterId parameter is present in the URL.
     * If not, it redirects the user to the ChapterListView. If the chapterId is present, it stores it for later use.
     *
     * @param event before navigation event with event details
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        RouteParameters parameters = event.getRouteParameters();
        if (parameters.getParameterNames().isEmpty()) {
            event.forwardTo(ChapterListView.class);
        }
        this.chapterId = event.getRouteParameters().get("chapterId").orElse(null);
        if (chapterId == null) {
            log.error("Nelze načíst kapitolu bez ID");
            new ErrorNotification("Nelze načíst kapitolu bez ID", 5000);
            UI.getCurrent().navigate(ChapterListView.class);
        }
    }

    /**
     * Overridden getPageTitle function to provide dynamic page title based on the chapter name.
     *
     * @return String - page title
     */
    @Override
    public String getPageTitle() {
        try {
            return text("page.title.chapterView") + " - " + chapterController.getChapterName(chapterId).trim();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Overridden afterNavigation function to initialize and load all the necessary data for the chapter view.
     *
     * @param event after navigation event with event details
     */
    @Override
    public void afterNavigation(AfterNavigationEvent event) {

        nameTextField.setReadOnly(true);
        editorjs.toggleReadOnlyMode(true);

        try {
            nameTextField.setValue(chapterController.getChapterName(chapterId));
            editorjs.setChapterContentData(chapterController.getChapterContent(chapterId));
            chapterSelect.initializeChapterSelectionSelect(chapterController.getSubChaptersNames(chapterId));
            navigationContentLayout.initializeSubChapterData(chapterController.getSubChaptersContent(chapterId));
            chapterSelect.addSubChapterChangeListener(event2 -> {
                try {
                    if (event2.getNewValue() == null) {
                        editorjs.showWholeChapterData();
                        return;
                    }
                    editorjs.setSelectedSubchapterData(chapterController.getSelectedSubChapterContent(event2.getNewValue().id()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            try {
                Map<String, QuickModelEntity> quickModelEntityMap = chapterController.getChaptersModels(chapterId);

                for (QuickModelEntity quickModelEntity : quickModelEntityMap.values()) {

                    String model = modelController.getModelFileBeEndpointUrl(quickModelEntity.getModel().getId());
                    String texture = null;
                    if (quickModelEntity.getMainTexture() != null) {
                        texture = textureController.getTextureFileBeEndpointUrl(quickModelEntity.getMainTexture().getTextureFileId());
                    }
                    modelDiv.renderer.loadModel(model, texture, quickModelEntity.getModel().getId());

                    if (quickModelEntity.getOtherTextures() != null && !quickModelEntity.getOtherTextures().isEmpty()) {
                        modelDiv.renderer.addOtherTextures(TextureMapHelper.otherTexturesMap(quickModelEntity.getOtherTextures(), textureController), quickModelEntity.getModel().getId());
                    }
                }
                setupModelDiv(quickModelEntityMap);
            } catch (Exception e) {
                log.error(e.getMessage());
                new ErrorNotification("Nepovedlo se načíst model: " + e.getMessage(), 5000);
                throw e;
            }
        } catch (Exception e) {
            log.error("Chyba při načítání kapitoly: {}", e.getMessage(), e);
            new ErrorNotification("Chyba při načítání kapitoly: " + e.getMessage(), 5000);
            UI.getCurrent().navigate(ChapterListView.class);
        }
    }
}
