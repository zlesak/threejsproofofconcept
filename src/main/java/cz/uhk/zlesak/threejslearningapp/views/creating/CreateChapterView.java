package cz.uhk.zlesak.threejslearningapp.views.creating;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.Route;
import cz.uhk.zlesak.threejslearningapp.components.BeforeLeaveActionDialog;
import cz.uhk.zlesak.threejslearningapp.components.notifications.ErrorNotification;
import cz.uhk.zlesak.threejslearningapp.controllers.ChapterController;
import cz.uhk.zlesak.threejslearningapp.controllers.ModelController;
import cz.uhk.zlesak.threejslearningapp.controllers.TextureController;
import cz.uhk.zlesak.threejslearningapp.data.enums.ViewTypeEnum;
import cz.uhk.zlesak.threejslearningapp.models.entities.ChapterEntity;
import cz.uhk.zlesak.threejslearningapp.views.scaffolds.ChapterScaffold;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Scope;

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
    private boolean skipBeforeLeaveDialog = false;

    /**
     * Constructor for CreateChapterView.
     * Initializes the view with necessary controllers and providers.
     *
     * @param chapterController controller for handling chapter-related operations
     * @param modelController controller for handling model-related operations
     * @param textureController controller for handling texture-related operations
     */
    @Autowired
    public CreateChapterView(ChapterController chapterController, ModelController modelController, TextureController textureController) {
        super(ViewTypeEnum.CREATE);

        Button createChapterButton = new Button("Vytvořit kapitolu");
        createChapterButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createChapterButton.setWidthFull();

        chapterContent.add(createChapterButton);

        Button createModelButton = getButton(chapterController, modelController, textureController);

        secondaryNavigationBar.add(createModelButton);

        createChapterButton.addClickListener(e -> editorjs.getData().whenComplete((body, error) -> {
            try {
                if (error != null) {
                    throw new ApplicationContextException("Chyba při získávání obsahu: ", error);
                }
                ChapterEntity created = chapterController.createChapter(chapterNameTextField.getValue().trim(), body);
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

    }

    /**
     * Creates and returns a button for creating a model.
     * When clicked, it opens a CreateModelDialog for model creation.
     * Once a model is created, it updates the button text and icon, disables the button,
     * and loads the model into the renderer.
     *
     * @param chapterController controller for handling chapter-related operations
     * @param modelController controller for handling model-related operations
     * @param textureController controller for handling texture-related operations
     * @return the button for creating a model
     */
    @NotNull
    private Button getButton(ChapterController chapterController, ModelController modelController, TextureController textureController) {
        CreateModelDialog dialog = new CreateModelDialog(modelController);
        Button createModelButton = new Button("Vytvořit model");

        createModelButton.addClickListener(e -> dialog.open());
        dialog.setModelCreatedListener(entity -> {
            chapterController.setUploadedModel(entity);
            createModelButton.setText("Model přidán: " + entity.getModel().getName());
            createModelButton.setIcon(new Icon(VaadinIcon.CHECK));
            createModelButton.setEnabled(false);

            String base64ModelFile = modelController.getModelBase64(entity.getModel().getId());
            String textureFileEntity = null;
            if (dialog.isAdvanced()) {
                textureFileEntity = textureController.getTextureBase64(entity.getMainTexture().getTextureFileId());
            }
            renderer.loadModel(base64ModelFile, textureFileEntity);

            renderer.setVisible(true);
        });
        return createModelButton;
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
        if (!skipBeforeLeaveDialog) {
            BeforeLeaveActionDialog.leave(event);
        }
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
}
