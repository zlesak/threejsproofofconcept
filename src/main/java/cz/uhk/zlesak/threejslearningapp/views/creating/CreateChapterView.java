package cz.uhk.zlesak.threejslearningapp.views.creating;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.Route;
import cz.uhk.zlesak.threejslearningapp.components.BeforeLeaveActionDialog;
import cz.uhk.zlesak.threejslearningapp.components.ModelListDialog;
import cz.uhk.zlesak.threejslearningapp.components.notifications.ErrorNotification;
import cz.uhk.zlesak.threejslearningapp.controllers.ChapterController;
import cz.uhk.zlesak.threejslearningapp.controllers.ModelController;
import cz.uhk.zlesak.threejslearningapp.controllers.TextureController;
import cz.uhk.zlesak.threejslearningapp.models.entities.ChapterEntity;
import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickTextureEntity;
import cz.uhk.zlesak.threejslearningapp.utils.TextureMapHelper;
import cz.uhk.zlesak.threejslearningapp.views.listing.ModelListView;
import cz.uhk.zlesak.threejslearningapp.views.scaffolds.ChapterScaffold;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Scope;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * CreateChapterView for creating a new chapter.
 * It is accessible at the route "/createChapter".
 * The view allows users to input chapter details and create a new chapter in the system.
 */
@Slf4j
@Route("createChapter")
@Tag("create-chapter")
@Scope("prototype")
public class CreateChapterView extends ChapterScaffold {
    private final TextureController textureController;
    private final ModelController modelController;
    private final ChapterController chapterController;
    private boolean skipBeforeLeaveDialog = false;

    private Button chooseAlreadyCreatedModelButton;

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
        super();
        this.modelController = modelController;
        this.textureController = textureController;
        this.chapterController = chapterController;

        chapterSelect.setVisible(false);
        searchTextField.setVisible(false);
        chapterNavigation.setVisible(false);
        editorjs.toggleReadOnlyMode(false);

        HorizontalLayout chapterContentButtons = getHorizontalLayout();
        chapterContent.add(chapterContentButtons);
    }

    /**
     * Creates and returns a HorizontalLayout containing buttons for creating a chapter, creating a model, and choosing an existing model.
     * The layout is configured to stretch and space the buttons appropriately.
     * @return the HorizontalLayout with chapter content buttons
     */
    @NotNull
    private HorizontalLayout getHorizontalLayout() {
        Button createChapterButton = getCreateChapterButton();
        Button createModelButton = getCreateModelButton();
        chooseAlreadyCreatedModelButton = getChooseAlreadyCreatedModelButton(createModelButton);
        HorizontalLayout chapterContentButtons = new HorizontalLayout(createModelButton, chooseAlreadyCreatedModelButton, createChapterButton);
        chapterContentButtons.setWidthFull();
        chapterContentButtons.setSpacing(true);
        chapterContentButtons.setPadding(false);
        chapterContentButtons.setAlignItems(FlexComponent.Alignment.STRETCH);
        chapterContentButtons.setFlexGrow(0, createModelButton);
        chapterContentButtons.setFlexGrow(0, chooseAlreadyCreatedModelButton);
        chapterContentButtons.setFlexGrow(1, createChapterButton);
        return chapterContentButtons;
    }

    /**
     * Creates and returns a button for creating a model.
     * When clicked, it opens a CreateModelDialog for model creation.
     * Once a model is created, it updates the button text and icon, disables the button, and loads the model into the renderer.
     *
     * @return the button for creating a model
     */
    @NotNull
    private Button getCreateModelButton() {
        CreateModelDialog dialog = new CreateModelDialog(this.modelController);
        Button createModelButton = new Button("Vytvořit model");

        createModelButton.addClickListener(e -> dialog.open());
        dialog.setModelCreatedListener(entity -> {
            this.chapterController.setUploadedModel(entity);
            createModelButton.setText("Model přidán: " + entity.getModel().getName());
            createModelButton.setIcon(new Icon(VaadinIcon.CHECK));
            createModelButton.setEnabled(false);
            chooseAlreadyCreatedModelButton.setVisible(false);
            rendererSelectsAndEditorPreparation(entity);
        });
        return createModelButton;
    }

    /**
     * Creates and returns a button for choosing an already created model.
     * When clicked, it opens a ModelListDialog for selecting an existing model.
     * Once a model is selected, it updates the createModelButton text and icon, disables both buttons, and loads the model into the renderer.
     *
     * @param createModelButton the button for creating a model, which will be updated upon model selection
     * @return the button for choosing an already created model
     */
    @NotNull
    private Button getChooseAlreadyCreatedModelButton(Button createModelButton) {
        Button chooseAlreadyCreatedModelButton = new Button("Přidat existující model");

        ModelListDialog modelListDialog = new ModelListDialog(new ModelListView(modelController));
        modelListDialog.setModelSelectedListener(entity -> {
            chapterController.setUploadedModel(entity);
            createModelButton.setText("Model přidán: " + entity.getModel().getName());
            createModelButton.setIcon(new Icon(VaadinIcon.CHECK));
            createModelButton.setEnabled(false);
            chooseAlreadyCreatedModelButton.setVisible(false);
            try {
                rendererSelectsAndEditorPreparation(entity);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        chooseAlreadyCreatedModelButton.addClickListener(e -> modelListDialog.open());
        return chooseAlreadyCreatedModelButton;
    }

    /**
     * Creates and returns a button for creating a chapter.
     * When clicked, it retrieves the content from the editor and attempts to create a new chapter using the ChapterController.
     * If successful, it navigates to the newly created chapter's view.
     * If there are errors during the process, it logs the error and shows an error notification.
     *
     * @return the button for creating a chapter
     */
    @NotNull Button getCreateChapterButton() {
        Button createChapterButton = new Button("Vytvořit kapitolu");
        createChapterButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createChapterButton.addClickListener(e -> editorjs.getData().whenComplete((body, error) -> {
            try {
                if (error != null) {
                    throw new ApplicationContextException("Chyba při získávání obsahu: ", error);
                }
                ChapterEntity created = chapterController.createChapter(nameTextField.getValue().trim(), body);
                if (created != null) {
                    skipBeforeLeaveDialog = true;
                    UI.getCurrent().navigate("chapter/" + created.getId());
                } else {
                    throw new ApplicationContextException("Kapitola nebyla uložena, zkuste to znovu později.");
                }
            } catch (ApplicationContextException ex) {
                log.error("Chyba při ukládání kapitoly: {}", ex.getMessage(), ex);
                new ErrorNotification("Chyba při ukládání kapitoly: " + ex.getMessage(), 5000);
            } catch (Exception ex) {
                log.error("Neočekávaná chyba při ukládání kapitoly: {}", ex.getMessage(), ex);
                new ErrorNotification("Neočekávaná chyba při vytváření kapitoly: " + ex.getMessage(), 5000);
            }
        }));
        return createChapterButton;
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
            if (renderer != null) {
                renderer.dispose(() -> ui.access(() -> {
                    modelDiv.remove(renderer);
                    postponed.proceed();
                }));
            } else {
                postponed.proceed();
            }
            return;
        }

        BeforeLeaveActionDialog.leave(event, postponed -> {
            var ui = event.getUI();
            if (renderer != null) {
                renderer.dispose(() -> ui.access(() -> {
                    modelDiv.remove(renderer);
                    postponed.proceed();
                }));
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
            return this.i18NProvider.getTranslation("page.title.createChapterView", UI.getCurrent().getLocale());
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
     * Prepares the renderer and editor with the provided QuickModelEntity.
     * Loads the model and main texture into the renderer and initializes the editor with other textures.
     *
     * @param quickModelEntity the QuickModelEntity containing model and texture information
     * @throws IOException if there is an error loading the model or textures
     */
    private void rendererSelectsAndEditorPreparation(QuickModelEntity quickModelEntity) throws IOException {
        String base64ModelFile = modelController.getModelBase64(quickModelEntity.getModel().getId());
        String textureFileEntity = null;
        if (quickModelEntity.getMainTexture() != null) {
            textureFileEntity = textureController.getTextureBase64(quickModelEntity.getMainTexture().getTextureFileId());
        }
        renderer.loadModel(base64ModelFile, textureFileEntity);

        if(textureFileEntity != null) {
            List<QuickTextureEntity> allTextures = new ArrayList<>(quickModelEntity.getOtherTextures());
            renderer.addOtherTextures(TextureMapHelper.otherTexturesMap(allTextures, textureController));

            editorjs.initializeTextureSelects(quickModelEntity.getOtherTextures());
            editorjs.addTextureColorAreaClickListener((textureId, hexColor, text) -> {
                textureSelectsComponent.getTextureListingSelect().setSelectedTextureById(textureId);
                textureSelectsComponent.getTextureAreaSelect().setSelectedAreaByHexColor(hexColor, textureId);

            });
            allTextures.addFirst(quickModelEntity.getMainTexture());
            textureSelectsComponent.initializeData(allTextures);
        }
    }
}
