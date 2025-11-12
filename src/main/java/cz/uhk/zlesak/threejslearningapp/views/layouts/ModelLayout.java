package cz.uhk.zlesak.threejslearningapp.views.layouts;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import cz.uhk.zlesak.threejslearningapp.components.containers.ModelContainer;
import cz.uhk.zlesak.threejslearningapp.components.forms.ModelUploadForm;
import cz.uhk.zlesak.threejslearningapp.domain.common.QuickFileEntity;
import cz.uhk.zlesak.threejslearningapp.domain.model.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.domain.texture.QuickTextureEntity;
import cz.uhk.zlesak.threejslearningapp.views.IView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.Map;

@Slf4j
@Scope("prototype")
public abstract class ModelLayout extends Composite<VerticalLayout> implements IView {
    protected final ModelContainer modelDiv = new ModelContainer();
    protected final ModelUploadForm modelUploadForm;
    private Map<String, QuickModelEntity> quickModelEntity;

    public ModelLayout() {
        HorizontalLayout modelPageLayout = new HorizontalLayout();

        modelUploadForm = new ModelUploadForm();

        modelUploadForm.addModelLoadEventListener(
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
            modelDiv.modelTextureAreaSelectContainer.initializeData(quickModelEntity);
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
        modelPageLayout.add(modelUploadForm, chapterModel);
        modelPageLayout.addClassName(LumoUtility.Gap.MEDIUM);
        modelPageLayout.setFlexGrow(1, modelUploadForm);
        modelPageLayout.setFlexGrow(1, chapterModel);
        modelUploadForm.setWidthFull();
        modelUploadForm.getStyle().set("min-width", "0");
        modelPageLayout.setSizeFull();
        modelPageLayout.getStyle().set("min-width", "0");

        getContent().add(modelPageLayout);
        getContent().setWidth("100%");
        getContent().setHeightFull();
        getContent().setMinHeight("0");
    }
}
