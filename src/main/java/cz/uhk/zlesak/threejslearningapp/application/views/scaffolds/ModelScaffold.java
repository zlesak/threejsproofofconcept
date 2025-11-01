package cz.uhk.zlesak.threejslearningapp.application.views.scaffolds;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import cz.uhk.zlesak.threejslearningapp.application.components.divs.ModelDiv;
import cz.uhk.zlesak.threejslearningapp.application.components.compositions.ModelUploadFormScrollerComposition;
import cz.uhk.zlesak.threejslearningapp.application.controllers.ModelController;
import cz.uhk.zlesak.threejslearningapp.application.models.entities.quickEntities.QuickFileEntity;
import cz.uhk.zlesak.threejslearningapp.application.models.entities.quickEntities.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.application.components.notifications.ErrorNotification;
import cz.uhk.zlesak.threejslearningapp.application.components.notifications.InfoNotification;
import cz.uhk.zlesak.threejslearningapp.application.models.entities.quickEntities.QuickTextureEntity;
import cz.uhk.zlesak.threejslearningapp.application.views.IView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Scope("prototype")
public abstract class ModelScaffold extends Composite<VerticalLayout> implements IView {
    protected final ModelDiv modelDiv = new ModelDiv();
    protected final ModelUploadFormScrollerComposition modelUploadFormScrollerComposition;
    private Map<String, QuickModelEntity> quickModelEntity;

    public ModelScaffold() {
        HorizontalLayout modelPageLayout = new HorizontalLayout();

        modelUploadFormScrollerComposition = new ModelUploadFormScrollerComposition();

        modelUploadFormScrollerComposition.addModelLoadEventListener(
                event -> {
                    quickModelEntity = Map.of(event.getModelId(), QuickModelEntity.builder()
                            .model(QuickFileEntity.builder().id(event.getModelId()).name(event.getModelName()).build())
                            .mainTexture(QuickTextureEntity.builder().textureFileId(event.getTextureName()).name(event.getTextureName()).build())
                            .build());
                    modelDiv.renderer.loadModel(event.getModel(), event.getTexture(), event.getModelId());
                }
        ).addModelClearEventListener(
                event -> modelDiv.renderer.clear()
        ).addOtherTextureLoadedEventListener(
                event -> modelDiv.renderer.addOtherTextures(event.getBase64Textures(), "modelId")
        ).addOtherTextureRemovedEventListener(
                event -> modelDiv.renderer.removeOtherTexture("modelId", event.getName())
        ).addTextureChangeEventListener(
        event -> {
            QuickModelEntity model = quickModelEntity.get("modelId");
            model.setMainTexture(event.getQuickTextureEntity().get("main"));

            List<QuickTextureEntity> textures = event.getQuickTextureEntity().entrySet().stream()
                    .filter(e -> !e.getKey().equals("main")).map(Map.Entry::getValue)
                    .toList();

            model.setOtherTextures(textures);
            modelDiv.modelTextureAreaSelectComponent.initializeData(quickModelEntity);
        }
        );

        modelDiv.setId("modelDiv");
        modelDiv.setSizeFull();
        modelDiv.getStyle().set("min-width", "0");

        //Model layout
        VerticalLayout chapterModel = new VerticalLayout();
        chapterModel.add(modelDiv);
        chapterModel.addClassName(LumoUtility.Gap.MEDIUM);
        chapterModel.setSizeFull();
        chapterModel.setPadding(false);
        chapterModel.getStyle().set("min-width", "0");
        chapterModel.getStyle().set("flex-grow", "1");

        // Layout
        modelPageLayout.setClassName("modelPageLayout");
        modelPageLayout.add(modelUploadFormScrollerComposition, chapterModel);
        modelPageLayout.addClassName(LumoUtility.Gap.MEDIUM);
        modelPageLayout.setFlexGrow(1, modelUploadFormScrollerComposition);
        modelPageLayout.setFlexGrow(1, chapterModel);
        modelUploadFormScrollerComposition.setWidthFull();
        modelUploadFormScrollerComposition.getStyle().set("min-width", "0");
        modelPageLayout.setSizeFull();
        modelPageLayout.getStyle().set("min-width", "0");

        getContent().add(modelPageLayout);
        getContent().setWidth("100%");
        getContent().setHeightFull();
        getContent().setMinHeight("0");
    }

    /**
     * Creates a button that, when clicked, uploads a model using the provided ModelController.
     * It retrieves the necessary data from the ModelUploadFormScrollerComposition and handles success and error
     * notifications. If the model is successfully created, it invokes the onModelCreated consumer with the created
     * QuickModelEntity.
     * @param modelController the controller used to upload the model
     * @param onModelCreated a consumer that accepts the created QuickModelEntity upon successful upload
     * @return the created Button
     */
    protected Button createModelButton(ModelController modelController, Consumer<QuickModelEntity> onModelCreated) {
        Button createButton = new Button("Vytvořit model");
        createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createButton.addClickListener(event -> {
            QuickModelEntity quickModelEntity;
            try {
                if (modelUploadFormScrollerComposition.getIsAdvanced().getValue()) {
                    quickModelEntity = modelController.uploadModel(
                            modelUploadFormScrollerComposition.getModelName().getValue().trim(),
                            modelUploadFormScrollerComposition.getObjUploadComponent().getUploadedFiles().getFirst(),
                            modelUploadFormScrollerComposition.getMainTextureUploadComponent().getUploadedFiles().getFirst(),
                            modelUploadFormScrollerComposition.getOtherTexturesUploadComponent().getUploadedFiles(),
                            modelUploadFormScrollerComposition.getCsvUploadComponent().getUploadedFiles()
                    );
                } else {
                    quickModelEntity = modelController.uploadModel(
                            modelUploadFormScrollerComposition.getModelName().getValue().trim(),
                            modelUploadFormScrollerComposition.getObjUploadComponent().getUploadedFiles().getFirst()
                    );
                }
                new InfoNotification("Úspěšně nahráno");
                if (onModelCreated != null) {
                    onModelCreated.accept(quickModelEntity);
                }
            } catch (ApplicationContextException e) {
                new ErrorNotification("Chyba při nahrávání modelu: " + e.getMessage());
            } catch (Exception e) {
                log.error("Error uploading model", e);
                new ErrorNotification("Chyba při nahrávání modelu: " + e.getMessage());
            }
        });
        return createButton;
    }
}
