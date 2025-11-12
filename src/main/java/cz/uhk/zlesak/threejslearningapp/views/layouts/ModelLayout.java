package cz.uhk.zlesak.threejslearningapp.views.layouts;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.LumoUtility;
import cz.uhk.zlesak.threejslearningapp.components.containers.ModelContainer;
import cz.uhk.zlesak.threejslearningapp.components.forms.ModelUploadForm;
import cz.uhk.zlesak.threejslearningapp.domain.common.QuickFileEntity;
import cz.uhk.zlesak.threejslearningapp.domain.model.QuickModelEntity;
import cz.uhk.zlesak.threejslearningapp.domain.texture.QuickTextureEntity;
import cz.uhk.zlesak.threejslearningapp.events.model.ModelClearEvent;
import cz.uhk.zlesak.threejslearningapp.events.model.ModelTextureChangeEvent;
import cz.uhk.zlesak.threejslearningapp.events.model.ModelUploadEvent;
import cz.uhk.zlesak.threejslearningapp.events.texture.OtherTextureLoadedEvent;
import cz.uhk.zlesak.threejslearningapp.events.texture.OtherTextureRemovedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.Map;

@Slf4j
@Scope("prototype")
public abstract class ModelLayout extends BaseLayout {
    protected final ModelContainer modelDiv = new ModelContainer();
    protected final ModelUploadForm modelUploadForm;
    private Map<String, QuickModelEntity> quickModelEntity;

    public ModelLayout() {
        HorizontalLayout modelPageLayout = new HorizontalLayout();

        modelUploadForm = new ModelUploadForm();

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
    }

    /**
     * Called when the component is attached to the UI.
     * Registers event listeners for various model and texture events.
     *
     * @param attachEvent the attach event
     */
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        // ModelUploadEvent listener
        registrations.add(ComponentUtil.addListener(
                attachEvent.getUI(),
                ModelUploadEvent.class,
                event -> {
                    quickModelEntity = Map.of(event.getModelId(), QuickModelEntity.builder()
                            .model(QuickFileEntity.builder().id(event.getModelId()).name(event.getModelName()).build())
                            .mainTexture(QuickTextureEntity.builder().textureFileId(event.getTextureName()).name(event.getTextureName()).build())
                            .build());
                    modelDiv.renderer.loadModel(event.getModel(), event.getTexture(), event.getModelId());
                }
        ));

        // ModelClearEvent listener
        registrations.add(ComponentUtil.addListener(
                attachEvent.getUI(),
                ModelClearEvent.class,
                event -> modelDiv.renderer.clear()
        ));

        // OtherTextureLoadedEvent listener
        registrations.add(ComponentUtil.addListener(
                attachEvent.getUI(),
                OtherTextureLoadedEvent.class,
                event -> modelDiv.renderer.addOtherTextures(event.getBase64Textures(), "modelId")
        ));

        // OtherTextureRemovedEvent listener
        registrations.add(ComponentUtil.addListener(
                attachEvent.getUI(),
                OtherTextureRemovedEvent.class,
                event -> modelDiv.renderer.removeOtherTexture("modelId", event.getName())
        ));

        // ModelTextureChangeEvent listener
        registrations.add(ComponentUtil.addListener(
                attachEvent.getUI(),
                ModelTextureChangeEvent.class,
                event -> {
                    QuickModelEntity model = quickModelEntity.get("modelId");
                    model.setMainTexture(event.getQuickTextureEntity().get("main"));

                    List<QuickTextureEntity> textures = event.getQuickTextureEntity().entrySet().stream()
                            .filter(e -> !e.getKey().equals("main")).map(Map.Entry::getValue)
                            .toList();

                    model.setOtherTextures(textures);
                    modelDiv.modelTextureAreaSelectContainer.initializeData(quickModelEntity);
                }
        ));
    }

    /**
     * Called when the component is detached from the UI.
     * Removes all registered event listeners to prevent memory leaks.
     *
     * @param detachEvent the detach event
     */
    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        registrations.forEach(Registration::remove);
        registrations.clear();
    }
}
