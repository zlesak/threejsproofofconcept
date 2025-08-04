package cz.uhk.zlesak.threejslearningapp.views.showing;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import cz.uhk.zlesak.threejslearningapp.controllers.ChapterController;
import cz.uhk.zlesak.threejslearningapp.controllers.ModelController;
import cz.uhk.zlesak.threejslearningapp.controllers.TextureController;
import cz.uhk.zlesak.threejslearningapp.data.enums.ViewTypeEnum;
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
@Route("chapter/:chapterId?")
@JavaScript("./js/scroll-to-element-data-id.js")
@Tag("chapter-view")
@Scope("prototype")
public class ChapterView extends ChapterScaffold {
    private final ChapterController chapterController;
    private final ModelController modelController;
    private final TextureController textureController;

    /**
     * ChapterView constructor - creates instance of chapter view instance that then accomplishes the goal of getting
     * and serving the user the requested chapter from proper backend API endpoint via chapterApiClient.
     */
    @Autowired
    public ChapterView(ChapterController chapterController, ModelController modelController, TextureController textureController) {
        super(ViewTypeEnum.VIEW);
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
        //TODO remove after proper logic and layout implemented after DOD
        Button applyMaskToMainTextureButton = new Button("Aplikovat maskování hlavní textury #0000fe", addClickListener -> {
            renderer.applyMaskToMainTexture("#0000fe");
        });


        Button switchToMainTexture = new Button("Přepnout na hlavní texturu", addClickListener -> renderer.switchMainTexture());

        Button switchTextureButton = new Button("Přepnout vedlejší texturu texturu", addClickListener -> {
            switchToMainTexture.setEnabled(true);
            switchToMainTexture.setVisible(true);
            renderer.switchOtherTexture();
        });
        switchTextureButton.setVisible(false);

        switchToMainTexture.setVisible(false);
        switchToMainTexture.setEnabled(false);
        switchTextureButton.setVisible(false);

        try {
            chapterNameTextField.setValue(chapterController.getChapterName(chapterId));
            chapterNameTextField.setReadOnly(true);

            QuickModelEntity quickModelEntity = chapterController.getChapterFirstQuickModelEntity(chapterId);

            String json = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(quickModelEntity);
            log.info(json);

            try {
                String base64Model = modelController.getModelBase64(quickModelEntity.getModel().getId());
                if (quickModelEntity.getMainTexture() != null) {
                    String base64Texture = textureController.getTextureBase64(quickModelEntity.getMainTexture().getId());
                    renderer.loadAdvancedModel(base64Model, base64Texture);
                    switchToMainTexture.setVisible(true);
                    switchTextureButton.setVisible(true);
                } else {
                    renderer.loadModel(base64Model);
                }
                log.info("Počet vedlejších textur: {}", (long) quickModelEntity.getOtherTextures().size());
                if (!quickModelEntity.getOtherTextures().isEmpty()) {
                    for (var texture : quickModelEntity.getOtherTextures()) {
                        String otherTextureBase64 = textureController.getTextureBase64(texture.getId());
                        renderer.addOtherTexture(otherTextureBase64);
                    }
                }
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

            modelDiv.add(switchTextureButton, switchToMainTexture, applyMaskToMainTextureButton); //TODO change beeter layout for all the needed action buttons

        } catch (Exception e) {
            Notification.show("Chyba při načítání kapitoly: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
            log.error("Chyba při načítání kapitoly", e);
            UI.getCurrent().navigate(ChapterListView.class);
        }
    }

    @Override
    public String getPageTitle() {
        return "";
    }
}
