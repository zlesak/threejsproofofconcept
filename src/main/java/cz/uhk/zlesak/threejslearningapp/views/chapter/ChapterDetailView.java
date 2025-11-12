package cz.uhk.zlesak.threejslearningapp.views.chapter;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.router.*;
import com.vaadin.flow.shared.Registration;
import cz.uhk.zlesak.threejslearningapp.common.TextureMapHelper;
import cz.uhk.zlesak.threejslearningapp.components.notifications.ErrorNotification;
import cz.uhk.zlesak.threejslearningapp.domain.chapter.SubChapterForSelect;
import cz.uhk.zlesak.threejslearningapp.domain.model.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.events.chapter.SubChapterChangeEvent;
import cz.uhk.zlesak.threejslearningapp.services.ChapterService;
import cz.uhk.zlesak.threejslearningapp.services.ModelService;
import cz.uhk.zlesak.threejslearningapp.services.TextureService;
import cz.uhk.zlesak.threejslearningapp.views.layouts.ChapterLayout;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;
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
public class ChapterDetailView extends ChapterLayout {
    private final ModelService modelService;
    private final TextureService textureService;
    private final ChapterService chapterService;
    private final List<Registration> registrations = new ArrayList<>();

    private String chapterId;
    private Map<String, QuickModelEntity> modelsMap;

    /**
     * ChapterView constructor - creates instance of chapter view instance that then accomplishes the goal of getting
     * and serving the user the requested chapter from proper backend API endpoint via chapterApiClient.
     */
    @Autowired
    public ChapterDetailView(ChapterService chapterService, ModelService modelService, TextureService textureService) {
        super(false);
        this.chapterService = chapterService;
        this.modelService = modelService;
        this.textureService = textureService;
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
            return text("page.title.chapterDetailView") + " - " + chapterService.getChapterName(chapterId).trim();
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
        configureReadOnlyMode();

        try {
            loadChapterData();
            setupSubChapterModelMap();
            loadAndDisplay3DModels();
        } catch (Exception e) {
            handleChapterLoadError(e);
        }
    }

    /**
     * Configures the view to read-only mode.
     * Disables editing of the chapter name and content.
     */
    private void configureReadOnlyMode() {
        nameTextField.setReadOnly(true);
        editorjs.toggleReadOnlyMode(true);
    }

    /**
     * Loads the main chapter data including name, content, and sub-chapters.
     *
     * @throws Exception if chapter data cannot be loaded
     */
    private void loadChapterData() throws Exception {
        nameTextField.setValue(chapterService.getChapterName(chapterId));
        editorjs.setChapterContentData(chapterService.getChapterContent(chapterId));
        chapterSelect.initializeChapterSelectionSelect(chapterService.getSubChaptersNames(chapterId));
        navigationContentLayout.initializeSubChapterData(chapterService.getSubChaptersContent(chapterId));
    }

    /**
     * Sets up sub-chapter model map for quick access during sub-chapter changes.
     *
     * @throws Exception if models cannot be retrieved
     */
    private void setupSubChapterModelMap() throws Exception {
        modelsMap = chapterService.getChaptersModels(chapterId);
    }

    /**
     * Handles sub-chapter selection change.
     * Updates the displayed content and 3D model based on the selected sub-chapter.
     *
     * @param event     the sub-chapter change event
     */
    private void handleSubChapterChange(SubChapterChangeEvent event) {
        try {
            SubChapterForSelect newValue = event.getNewValue();

            if (newValue == null) {
                showWholeChapter();
                return;
            }

            String subChapterId = newValue.id();
            editorjs.setSelectedSubchapterData(chapterService.getSelectedSubChapterContent(subChapterId));

            QuickModelEntity modelToShow = modelsMap.getOrDefault(subChapterId, modelsMap.get("main"));
            if (modelToShow != null) {
                modelDiv.modelTextureAreaSelectContainer.getModelListingSelect()
                        .setSelectedModelById(modelToShow.getModel().getId());
            }
        } catch (Exception e) {
            log.error("Error changing sub-chapter: {}", e.getMessage(), e);
            new ErrorNotification(text("error.subChapterLoadFailed") + ": " + e.getMessage(), 5000);
        }
    }

    /**
     * Shows the whole chapter content and selects the main model.
     */
    private void showWholeChapter() {
        editorjs.showWholeChapterData();
        if (modelsMap.containsKey("main")) {
            modelDiv.modelTextureAreaSelectContainer.getModelListingSelect()
                    .setSelectedModelById(modelsMap.get("main").getModel().getId());
        }
    }

    /**
     * Loads and displays all 3D models associated with the chapter.
     * For each model, loads the model file, textures, and sets up the renderer.
     *
     * @throws Exception if models cannot be loaded or displayed
     */
    private void loadAndDisplay3DModels() throws Exception {
        try {
            Map<String, QuickModelEntity> quickModelEntityMap = chapterService.getChaptersModels(chapterId);

            for (QuickModelEntity quickModelEntity : quickModelEntityMap.values()) {
                loadModelWithTextures(quickModelEntity);
            }

            setupModelDiv(quickModelEntityMap);
        } catch (Exception e) {
            log.error("Failed to load 3D models: {}", e.getMessage(), e);
            new ErrorNotification(text("error.modelLoadFailed") + ": " + e.getMessage(), 5000);
            throw e;
        }
    }

    /**
     * Loads a single model with its textures and adds it to the renderer.
     *
     * @param quickModelEntity the model entity to load
     * @throws Exception if model or textures cannot be loaded
     */
    private void loadModelWithTextures(QuickModelEntity quickModelEntity) throws Exception {
        String modelUrl = modelService.getModelFileBeEndpointUrl(quickModelEntity.getModel().getId());
        String textureUrl = null;

        if (quickModelEntity.getMainTexture() != null) {
            textureUrl = textureService.getTextureFileBeEndpointUrl(
                    quickModelEntity.getMainTexture().getTextureFileId()
            );
        }

        modelDiv.renderer.loadModel(modelUrl, textureUrl, quickModelEntity.getModel().getId());

        if (quickModelEntity.getOtherTextures() != null && !quickModelEntity.getOtherTextures().isEmpty()) {
            Map<String, String> otherTexturesMap = TextureMapHelper.otherTexturesMap(
                    quickModelEntity.getOtherTextures(),
                    textureService
            );
            modelDiv.renderer.addOtherTextures(otherTexturesMap, quickModelEntity.getModel().getId());
        }
    }

    /**
     * Handles errors that occur during chapter loading.
     * Logs the error, shows a notification, and redirects to the chapter list.
     *
     * @param e the exception that occurred
     */
    private void handleChapterLoadError(Exception e) {
        log.error("Error loading chapter: {}", e.getMessage(), e);
        new ErrorNotification(text("error.chapterLoadFailed") + ": " + e.getMessage(), 5000);
        UI.getCurrent().navigate(ChapterListView.class);
    }

    /**
     * On attach function to register event listeners when the view is attached.
     * Registers a listener for SubChapterChangeEvent to handle sub-chapter changes.
     * @param attachEvent the attach event
     */
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        registrations.add(ComponentUtil.addListener(
                attachEvent.getUI(),
                SubChapterChangeEvent.class,
                event -> {
                    try {
                        handleSubChapterChange(event);
                    } catch (Exception e) {
                        log.error("Error changing sub-chapter: {}", e.getMessage(), e);
                        new ErrorNotification(text("error.subChapterLoadFailed") + ": " + e.getMessage(), 5000);
                    }
                }
        ));
    }

    /**
     * Overridden onDetach function to clean up event registrations when the view is detached.
     * @param detachEvent the detach event
     */
    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        registrations.forEach(Registration::remove);
        registrations.clear();
    }
}
