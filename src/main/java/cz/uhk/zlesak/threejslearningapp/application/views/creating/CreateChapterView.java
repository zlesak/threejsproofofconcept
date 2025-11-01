package cz.uhk.zlesak.threejslearningapp.application.views.creating;

import com.vaadin.flow.component.*;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import cz.uhk.zlesak.threejslearningapp.application.components.dialogs.BeforeLeaveActionDialog;
import cz.uhk.zlesak.threejslearningapp.application.components.compositions.CreateChapterToolBarComposition;
import cz.uhk.zlesak.threejslearningapp.application.components.notifications.ErrorNotification;
import cz.uhk.zlesak.threejslearningapp.application.controllers.ChapterController;
import cz.uhk.zlesak.threejslearningapp.application.controllers.ModelController;
import cz.uhk.zlesak.threejslearningapp.application.controllers.TextureController;
import cz.uhk.zlesak.threejslearningapp.application.events.CreateChapterEvent;
import cz.uhk.zlesak.threejslearningapp.application.models.entities.quickEntities.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.application.models.entities.quickEntities.QuickTextureEntity;
import cz.uhk.zlesak.threejslearningapp.application.utils.TextureMapHelper;
import cz.uhk.zlesak.threejslearningapp.application.views.scaffolds.ChapterScaffold;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Scope;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * CreateChapterView for creating a new chapter.
 * It is accessible at the route "/createChapter".
 * The view allows users to input chapter details and create a new chapter in the system.
 */
@Slf4j
@Route("createChapter")
@Tag("create-chapter")
@Scope("prototype")
@RolesAllowed(value = "ADMIN")
public class CreateChapterView extends ChapterScaffold {
    private final TextureController textureController;
    private final ModelController modelController;
    private final ChapterController chapterController;
    private boolean skipBeforeLeaveDialog = false;
    private Registration createChapterEventRegistration;

    /**
     * Constructor for CreateChapterView.
     * Initializes the view with necessary controllers and providers.
     *
     * @param chapterController controller for handling chapter-related operations
     * @param modelController   controller for handling model-related operations
     * @param textureController controller for handling texture-related operations
     */
    @Autowired
    public CreateChapterView(ChapterController chapterController, ModelController modelController, TextureController textureController) {
        super(true);

        this.modelController = modelController;
        this.textureController = textureController;
        this.chapterController = chapterController;

        chapterSelect.setVisible(false);
        searchTextField.setVisible(false);
        chapterNavigation.setVisible(false);
        editorjs.toggleReadOnlyMode(false);

        CreateChapterToolBarComposition chapterContentButtons = new CreateChapterToolBarComposition(editorjs, mdEditor);

        secondaryNavigation.init(editorjs);

        secondaryNavigation.getModelsScroller().setModelSelectedConsumer(quickModelEntity -> {
            try {
                rendererSelectsAndEditorPreparation(quickModelEntity);
            } catch (IOException e) {
                log.error("Chyba při načítání modelu pro renderer: {}", e.getMessage(), e);
                new ErrorNotification("Chyba při načítání modelu: " + e.getMessage(), 5000);
            }
        });

        chapterContent.add(chapterContentButtons);
    }

    /**
     * Handles actions before entering the view.
     * Currently, no specific actions are defined.
     *
     * @param event before navigation event with event details
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {

    }

    /**
     * Handles actions before leaving the view.
     * If skipBeforeLeaveDialog is false, it shows a confirmation dialog to the user.
     * If the user confirms, they can leave the view; otherwise, they stay on the current view.
     * Handy for preventing accidental navigation away from unsaved changes.
     *
     * @param event before navigation event with event details
     */
    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        if (skipBeforeLeaveDialog) {
            BeforeLeaveEvent.ContinueNavigationAction postponed = event.postpone();
            var ui = event.getUI();
            if (modelDiv.renderer != null) {
                modelDiv.renderer.dispose(() -> ui.access(postponed::proceed));
            } else {
                postponed.proceed();
            }
            return;
        }

        BeforeLeaveActionDialog.leave(event, postponed -> {
            var ui = event.getUI();
            if (modelDiv.renderer != null) {
                modelDiv.renderer.dispose(() -> ui.access(postponed::proceed));
            } else {
                postponed.proceed();
            }
        });
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
            return text("page.title.createChapterView");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles actions after navigation to the view.
     * Currently, no specific actions are defined.
     *
     * @param event after navigation event with event details
     */
    @Override
    public void afterNavigation(AfterNavigationEvent event) {
    }

    /**
     * Load models into the renderer and prepare editor texture area selects.
     * @param quickModelEntityMap Map of QuickModelEntity objects to be loaded into the renderer and prepared for editor texture area selects.
     * @throws IOException if there is an error loading model or texture streams.
     */
    private void rendererSelectsAndEditorPreparation(Map<String, QuickModelEntity> quickModelEntityMap) throws IOException {
        for (QuickModelEntity quickModelEntity : quickModelEntityMap.values()) {
            String modelUrl = modelController.getModelStreamEndpoint(quickModelEntity.getModel().getId(), quickModelEntity.getMainTexture() != null);
            String textureUrl = null;
            if (quickModelEntity.getMainTexture() != null) {
                textureUrl = textureController.getTextureStreamEndpointUrl(quickModelEntity.getMainTexture().getTextureFileId());
            }
            modelDiv.renderer.loadModel(modelUrl, textureUrl, quickModelEntity.getModel().getId());

            if (textureUrl != null) {
                List<QuickTextureEntity> allTextures = new ArrayList<>(quickModelEntity.getOtherTextures());
                modelDiv.renderer.addOtherTextures(TextureMapHelper.otherTexturesMap(allTextures, textureController), quickModelEntity.getModel().getId());

            }
        }

        setupModelDiv(quickModelEntityMap);
    }

    /**
     * Create chapter consumer that handles the CreateChapterEvent.
     * It retrieves data from the editor and creates a new chapter using the ChapterController.
     * Upon successful creation, it navigates to the newly created chapter view.
     *
     */
    private void createChapterConsumer(CreateChapterEvent event) {

        try {
            secondaryNavigation.setMainContentTabSelected();

            Map<String, QuickModelEntity> allModels = secondaryNavigation.getModelsScroller().getAllModelsMappedToChapterHeaderBlockId();

            editorjs.getData().whenComplete((bodyData, error) -> {
                if (error != null) {
                    log.error("Chyba při získávání dat z editoru: {}", error.getMessage(), error);
                    throw new ApplicationContextException("Chyba při získávání dat z editoru: " + error.getMessage());
                }

                try {
                    String chapterId = chapterController.createChapter(nameTextField.getValue().trim(), bodyData, allModels);
                    skipBeforeLeaveDialog = true;
                    UI.getCurrent().navigate("chapter/" + chapterId);
                } catch (Exception e) {
                    throw new ApplicationContextException("Chyba při vytváření kapitoly: " + e.getMessage(), e);
                }
            }).exceptionally(error -> {
                if (error.getCause() instanceof ApplicationContextException) {
                    new ErrorNotification(error.getCause().getMessage(), 5000);
                    return null;
                } else {
                    throw new RuntimeException(error);
                }
            });
        } catch (ApplicationContextException ex) {
            new ErrorNotification("Chyba při ukládání kapitoly: " + ex.getMessage(), 5000);
        } catch (Exception ex) {
            log.error("Neočekávaná chyba při ukládání kapitoly: {}", ex.getMessage(), ex);
            new ErrorNotification("Neočekávaná chyba při vytváření kapitoly: " + ex.getMessage(), 5000);
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        createChapterEventRegistration = ComponentUtil.addListener(attachEvent.getUI(), CreateChapterEvent.class, this::createChapterConsumer);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        if (createChapterEventRegistration != null) {
            createChapterEventRegistration.remove();
            createChapterEventRegistration = null;
        }
    }
}
