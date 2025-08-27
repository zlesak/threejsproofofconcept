package cz.uhk.zlesak.threejslearningapp.views.scaffolds;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import cz.uhk.zlesak.threejslearningapp.components.ModelDiv;
import cz.uhk.zlesak.threejslearningapp.components.ThreeJsComponent;
import cz.uhk.zlesak.threejslearningapp.components.compositions.ModelUploadFormScrollerComposition;
import cz.uhk.zlesak.threejslearningapp.components.compositions.TextureSelectsComponent;
import cz.uhk.zlesak.threejslearningapp.views.IView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;

@Slf4j
@Scope("prototype")
public abstract class ModelScaffold extends Composite<VerticalLayout> implements IView {
    protected final ThreeJsComponent renderer = new ThreeJsComponent();
    protected final ModelDiv modelDiv = new ModelDiv(renderer);
    protected final TextureSelectsComponent textureSelectsComponent = new TextureSelectsComponent(renderer);
    protected final ModelUploadFormScrollerComposition modelUploadFormScrollerComposition;

    public ModelScaffold() {
        HorizontalLayout modelPageLayout = new HorizontalLayout();

        modelUploadFormScrollerComposition = new ModelUploadFormScrollerComposition();

        modelUploadFormScrollerComposition.addModelLoadEventListener(
                event -> renderer.loadModel(event.getBase64Model(), event.getBase64Texture())
        );
        modelUploadFormScrollerComposition.addModelClearEventListener(
                event -> renderer.clear()
        );

        renderer.getStyle().set("width", "100%");
        modelDiv.setId("modelDiv");
        modelDiv.setSizeFull();
        modelDiv.getStyle().set("min-width", "0");

        //Model layout
        HorizontalLayout selectsLayout = new HorizontalLayout(textureSelectsComponent);
        selectsLayout.setWidthFull();

        VerticalLayout chapterModel = new VerticalLayout();
        chapterModel.add(selectsLayout, modelDiv);
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
}
