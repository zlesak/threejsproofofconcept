package cz.uhk.zlesak.threejslearningapp.views.creating;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.*;
import cz.uhk.zlesak.threejslearningapp.components.UploadComponent;
import cz.uhk.zlesak.threejslearningapp.controllers.ChapterController;
import cz.uhk.zlesak.threejslearningapp.controllers.ModelController;
import cz.uhk.zlesak.threejslearningapp.data.ViewTypeEnum;
import cz.uhk.zlesak.threejslearningapp.models.InputStreamMultipartFile;
import cz.uhk.zlesak.threejslearningapp.models.entities.ChapterEntity;
import cz.uhk.zlesak.threejslearningapp.models.entities.ModelEntity;
import cz.uhk.zlesak.threejslearningapp.models.entities.quickEntities.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.views.scaffolds.ChapterScaffold;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Scope;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@PageTitle("Vytvořit kapitolu")
@Route("createChapter")
@Menu(order = 1, icon = LineAwesomeIconUrl.BOOK_OPEN_SOLID)
@Tag("create-chapter")
@Scope("prototype")
public class CreateChapterView extends ChapterScaffold {

    @Autowired
    public CreateChapterView(ChapterController chapterController, ModelController modelController) {
        super(ViewTypeEnum.CREATE);

        Button createChapterButton = new Button("Vytvořit kapitolu");
        createChapterButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createChapterButton.setWidthFull();

        final List<QuickModelEntity> modelIds = new ArrayList<>();

        TextField modelName = new TextField("Název modelu");

        UploadComponent uploadComponent = new UploadComponent(new MultiFileMemoryBuffer(), List.of(".glb"));
        uploadComponent.setUploadListener((fileName, inputStream) -> {
            try {
                String name = modelName.getValue().trim();
                InputStreamMultipartFile multipartFile = new InputStreamMultipartFile(inputStream, fileName);
                QuickModelEntity quickModelEntity = modelController.uploadModel(name, multipartFile);
                modelIds.add(quickModelEntity);
                uploadComponent.clearFileList();

                Notification.show("Model úspěšně nahrán.", 3000, Notification.Position.MIDDLE);
                modelDiv.remove(modelName, uploadComponent);

            } catch (ApplicationContextException ex) {
                Notification.show("Chyba: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
                log.warn(ex.getMessage(), ex);
            } catch (Exception ex) {
                Notification.show("Chyba při nahrávání modelu: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
                log.error(ex.getMessage(), ex);
            }

            ModelEntity modelFile = modelController.getModel(modelIds.getFirst().getModel().getId());
            try {
                if (modelFile == null) {
                    throw new ApplicationContextException("Model nebyl nalezen.");
                }
                String base64Model = modelFile.getBase64File();
                showProgressBar(true);
                showRenderer(true);
                renderer.loadModel(base64Model);
            } catch (IOException e) {
                log.error(e.getMessage());
                Notification.show("Nepovedlo se načíst model: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
            } catch (Exception e) {
                log.error("Neočekávaná chyba při načítání modelu: {}", e.getMessage(), e);
                Notification.show("Neočekávaná chyba při načítání modelu: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        });


        createChapterButton.addClickListener(e -> editorjs.getData().whenComplete((body, error) -> {
            try {
                if (error != null) {
                    throw new ApplicationContextException("Chyba při získávání obsahu: ", error);
                }
                ChapterEntity created = chapterController.createChapter(chapterNameTextField.getValue().trim(), body, modelIds);
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


        modelDiv.add(modelName, uploadComponent);
        chapterContent.add(createChapterButton);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {

    }
}
