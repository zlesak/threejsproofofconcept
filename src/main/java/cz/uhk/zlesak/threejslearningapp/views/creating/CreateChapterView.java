package cz.uhk.zlesak.threejslearningapp.views.creating;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.*;
import cz.uhk.zlesak.threejslearningapp.controllers.ChapterController;
import cz.uhk.zlesak.threejslearningapp.controllers.ModelController;
import cz.uhk.zlesak.threejslearningapp.controllers.TextureController;
import cz.uhk.zlesak.threejslearningapp.data.ViewTypeEnum;
import cz.uhk.zlesak.threejslearningapp.models.entities.ChapterEntity;
import cz.uhk.zlesak.threejslearningapp.models.entities.ModelEntity;
import cz.uhk.zlesak.threejslearningapp.models.entities.TextureEntity;
import cz.uhk.zlesak.threejslearningapp.views.scaffolds.ChapterScaffold;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Scope;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@Slf4j
@PageTitle("Vytvořit kapitolu")
@Route("createChapter")
@Menu(order = 1, icon = LineAwesomeIconUrl.BOOK_OPEN_SOLID)
@Tag("create-chapter")
@Scope("prototype")
public class CreateChapterView extends ChapterScaffold {

    @Autowired
    public CreateChapterView(ChapterController chapterController, ModelController modelController, TextureController textureController) {
        super(ViewTypeEnum.CREATE);

        Button createChapterButton = new Button("Vytvořit kapitolu");
        createChapterButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createChapterButton.setWidthFull();

        CreateModelDialog dialog = new CreateModelDialog(modelController);
        Button createModelButton = new Button("Vytvořit model");
        createModelButton.addClickListener(e -> {
            dialog.open();
        });

        dialog.setModelCreatedListener(entity -> {
            chapterController.setUploadedModel(entity);
            createModelButton.setText("Model přidán: " + entity.getModel().getName());
            createModelButton.setIcon(new Icon(VaadinIcon.CHECK));
            createModelButton.setEnabled(false);

            String base64ModelFile = modelController.getModelBase64(entity.getModel().getId());
            if(dialog.isAdvanced()) {
                String textureFileEntity = textureController.getTextureBase64(entity.getMainTexture().getId());
                renderer.loadAdvancedModel(base64ModelFile, textureFileEntity);
                log.info("Pokročilý model s texturou byl načten.");
            }else{
                renderer.loadModel(base64ModelFile);
                log.info("Jednoduchý model byl načten.");
            }
            showRenderer(true);
        });

        secondaryNavigationBar.add(createModelButton);

        createChapterButton.addClickListener(e -> editorjs.getData().whenComplete((body, error) -> {
            try {
                if (error != null) {
                    throw new ApplicationContextException("Chyba při získávání obsahu: ", error);
                }
                ChapterEntity created = chapterController.createChapter(chapterNameTextField.getValue().trim(), body);
                if (created != null) {
                    UI.getCurrent().navigate("chapter/" + created.getId());
                } else {
                    throw new ApplicationContextException("Kapitola nebyla uložena, zkuste to znovu později.");
                }
            } catch (ApplicationContextException ex) {
                log.error("Chyba při ukládání kapitoly: {}", ex.getMessage(), ex);
                Notification.show("Chyba při ukládání kapitoly: " + ex.getMessage());
            } catch (Exception ex) {
                log.error("Neočekávaná chyba při ukládání kapitoly: {}", ex.getMessage(), ex);
                Notification.show("Neočekávaná chyba při vytváření kapitoly: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        }));

        chapterContent.add(createChapterButton);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {

    }
}
