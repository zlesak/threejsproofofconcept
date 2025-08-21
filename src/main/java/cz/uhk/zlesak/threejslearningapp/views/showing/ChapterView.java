package cz.uhk.zlesak.threejslearningapp.views.showing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.router.*;
import cz.uhk.zlesak.threejslearningapp.components.Notifications.ErrorNotification;
import cz.uhk.zlesak.threejslearningapp.controllers.ChapterController;
import cz.uhk.zlesak.threejslearningapp.controllers.ModelController;
import cz.uhk.zlesak.threejslearningapp.controllers.TextureController;
import cz.uhk.zlesak.threejslearningapp.data.enums.ViewTypeEnum;
import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickTextureEntity;
import cz.uhk.zlesak.threejslearningapp.models.records.parsers.TextureListingDataParser;
import cz.uhk.zlesak.threejslearningapp.views.listing.ChapterListView;
import cz.uhk.zlesak.threejslearningapp.views.scaffolds.ChapterScaffold;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import java.util.List;

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
        super(ViewTypeEnum.VIEW);
        this.chapterController = chapterController;
        this.modelController = modelController;
        this.textureController = textureController;


        //TODO remove after proper logic and layout implemented after DOD
        Button applyMaskToMainTextureButton = new Button("Aplikovat maskování hlavní textury #0000fe", addClickListener -> renderer.applyMaskToMainTexture("#0000fe"));
        modelDiv.add(applyMaskToMainTextureButton);


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
        this.chapterId = event.getRouteParameters().get("chapterId").orElse(null);
        if (chapterId == null) {
            log.error("Nelze načíst kapitolu bez ID");
            new ErrorNotification("Nelze načíst kapitolu bez ID", 5000);
            UI.getCurrent().navigate(ChapterListView.class);
        }


    }

    @Override
    public String getPageTitle() {
        try {
            return this.i18NProvider.getTranslation("page.title.chapterView", UI.getCurrent().getLocale()) + " - " + chapterController.getChapterName(chapterId).trim();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        try {
            chapterNameTextField.setValue(chapterController.getChapterName(chapterId));
            chapterNameTextField.setReadOnly(true);

            QuickModelEntity quickModelEntity = chapterController.getChapterFirstQuickModelEntity(chapterId);

            String json = new ObjectMapper().writeValueAsString(quickModelEntity);
            log.info(json);

            try {
                String base64Model = modelController.getModelBase64(quickModelEntity.getModel().getId());
                if (quickModelEntity.getMainTexture() != null) {
                    String base64Texture = textureController.getTextureBase64(quickModelEntity.getMainTexture().getTextureFileId());
                    renderer.loadAdvancedModel(base64Model, base64Texture);
                } else {
                    renderer.loadModel(base64Model);
                }
                log.info("Počet vedlejších textur: {}", (long) quickModelEntity.getOtherTextures().size());
                if (!quickModelEntity.getOtherTextures().isEmpty()) {
                    for (var texture : quickModelEntity.getOtherTextures()) {
                        String otherTextureBase64 = textureController.getTextureBase64(texture.getTextureFileId());
                        renderer.addOtherTexture(otherTextureBase64);
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                new ErrorNotification("Nepovedlo se načíst model: " + e.getMessage(), 5000);
                throw e;
            }

            //TODO move to controller?
            List<QuickTextureEntity> allModelTextures = quickModelEntity.getOtherTextures();
            QuickTextureEntity mainTexture = quickModelEntity.getMainTexture();
            if (mainTexture != null) {
                allModelTextures.addFirst(mainTexture);
            }
            modelDiv.initializeTextureListingSelectData(TextureListingDataParser.textureListingForSelectDataParser(allModelTextures));


            editorjs.setChapterContentData(chapterController.getChapterContent(chapterId));
            chapterSelect.initializeChapterSelectionSelect(chapterController.getSubChaptersNames(chapterId));
            navigationContentLayout.initializeSubChapterData(chapterController.getSubChaptersContent(chapterId));

            chapterSelect.addSubChapterChangeListener(event2 -> {
                try {
                    editorjs.setSelectedSubchapterData(chapterController.getSelectedSubChapterContent(event2.getNewValue().id()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });


        } catch (Exception e) {
            log.error("Chyba při načítání kapitoly: {}", e.getMessage(), e);
            new ErrorNotification("Chyba při načítání kapitoly: " + e.getMessage(), 5000);
            UI.getCurrent().navigate(ChapterListView.class);
        }
    }
}
