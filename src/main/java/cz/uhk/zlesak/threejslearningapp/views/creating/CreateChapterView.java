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
import cz.uhk.zlesak.threejslearningapp.components.Notifications.ErrorNotification;
import cz.uhk.zlesak.threejslearningapp.controllers.ChapterController;
import cz.uhk.zlesak.threejslearningapp.controllers.ModelController;
import cz.uhk.zlesak.threejslearningapp.controllers.TextureController;
import cz.uhk.zlesak.threejslearningapp.data.enums.ViewTypeEnum;
import cz.uhk.zlesak.threejslearningapp.i18n.CustomI18NProvider;
import cz.uhk.zlesak.threejslearningapp.models.entities.ChapterEntity;
import cz.uhk.zlesak.threejslearningapp.views.scaffolds.ChapterScaffold;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Scope;

@Slf4j
@Route("createChapter")
@Tag("create-chapter")
@Scope("prototype")
public class CreateChapterView extends ChapterScaffold {

    private boolean skipBeforeLeaveDialog = false;

    @Autowired
    public CreateChapterView(ChapterController chapterController, ModelController modelController, TextureController textureController, CustomI18NProvider i18nProvider) {
        super(ViewTypeEnum.CREATE);

        Button createChapterButton = new Button("Vytvořit kapitolu");
        createChapterButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createChapterButton.setWidthFull();

        chapterContent.add(createChapterButton);

        CreateModelDialog dialog = new CreateModelDialog(modelController, i18nProvider);
        Button createModelButton = new Button("Vytvořit model");

        createModelButton.addClickListener(e -> dialog.open());
        dialog.setModelCreatedListener(entity -> {
            chapterController.setUploadedModel(entity);
            createModelButton.setText("Model přidán: " + entity.getModel().getName());
            createModelButton.setIcon(new Icon(VaadinIcon.CHECK));
            createModelButton.setEnabled(false);

            String base64ModelFile = modelController.getModelBase64(entity.getModel().getId());
            if (dialog.isAdvanced()) {
                String textureFileEntity = textureController.getTextureBase64(entity.getMainTexture().getTextureFileId());
                renderer.loadAdvancedModel(base64ModelFile, textureFileEntity);
            } else {
                renderer.loadModel(base64ModelFile);
            }
            renderer.setVisible(true);
        });

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

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        if (!skipBeforeLeaveDialog) {
            BeforeLeaveActionDialog.leave(event);
        }
    }

    @Override
    public String getPageTitle() {
        try {
            return this.i18NProvider.getTranslation("page.title.createChapterView", UI.getCurrent().getLocale());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

    }
}
