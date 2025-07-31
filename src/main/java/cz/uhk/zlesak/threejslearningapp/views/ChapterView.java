package cz.uhk.zlesak.threejslearningapp.views;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.*;
import cz.uhk.zlesak.threejslearningapp.controllers.ChapterController;
import cz.uhk.zlesak.threejslearningapp.controllers.ModelController;
import cz.uhk.zlesak.threejslearningapp.data.ViewTypeEnum;
import cz.uhk.zlesak.threejslearningapp.models.entities.ModelEntity;
import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.views.listing.ChapterListView;
import cz.uhk.zlesak.threejslearningapp.views.scaffolds.ChapterScaffold;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

/**
 * ChapterView Class - Shows the requested chapter from URL parameter. Initializes all the necessary elements
 * to provide the user with the chapter content in intuitive way.
 */
@Slf4j
@PageTitle("Kapitola")
@Route("chapter/:chapterId?")
@JavaScript("./js/scroll-to-element-data-id.js")
@Tag("chapter-view")
@Scope("prototype")
public class ChapterView extends ChapterScaffold {
    private final ChapterController chapterController;
    private final ModelController modelController;

    /**
     * ChapterView constructor - creates instance of chapter view instance that then accomplishes the goal of getting
     * and serving the user the requested chapter from proper backend API endpoint via chapterApiClient.
     */
    @Autowired
    public ChapterView(ChapterController chapterController, ModelController modelController) {
        super(ViewTypeEnum.VIEW);
        this.chapterController = chapterController;
        this.modelController = modelController;
    }

    /**
     * Overridden beforeLeave function to proper disposal of the model renderer to free the used RAM memory immediately.
     *
     * @param event BeforeLeave
     */
    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        BeforeLeaveEvent.ContinueNavigationAction postponed = event.postpone();
        renderer.dispose(() -> UI.getCurrent().access(postponed::proceed));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        RouteParameters parameters = event.getRouteParameters();
        if (parameters.getParameterNames().isEmpty()) {
            event.forwardTo(ChapterListView.class);
        }
        String chapterId = event.getRouteParameters().get("chapterId").orElse(null);
        if (chapterId == null) {
            Notification.show("Nelze načíst kapitolu bez ID", 3000, Notification.Position.MIDDLE);
            log.error("Nelze načíst kapitolu bez ID");
            UI.getCurrent().navigate(ChapterListView.class);
        }

        try {
            chapterController.getChapter(chapterId);

            chapterNameTextField.setValue(chapterController.getChapterName());
            chapterNameTextField.setReadOnly(true);

            QuickModelEntity quickModelEntity = chapterController.getChapterFirstQuickModelEntity();
            try {
                ModelEntity modelFile = modelController.getModel(quickModelEntity.getModel().getId());
                String base64Model = modelFile.getBase64File();
                renderer.loadModel(base64Model);
            } catch (Exception e) {
                log.error(e.getMessage());
                Notification.show("Nepovedlo se načíst model: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
                throw e;
            }
            editorjs.setChapterContentData(chapterController.getChapterEntityContent());
            chapterSelectionComboBox.initializeChapterSelectionComboBox(chapterController.getSubChaptersNames());
            navigationContentLayout.initializeSubChapterData(chapterController.getSubChaptersContent());

            chapterSelectionComboBox.addSubChapterChangeListener(event2 -> {
                try {
                    editorjs.setSelectedSubchapterData(chapterController.getSelectedSubChapterContent(event2.getNewValue().id()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (Exception e) {
            Notification.show("Chyba při načítání kapitoly: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
            log.error("Chyba při načítání kapitoly", e);
            UI.getCurrent().navigate(ChapterListView.class);
        }
    }
}
