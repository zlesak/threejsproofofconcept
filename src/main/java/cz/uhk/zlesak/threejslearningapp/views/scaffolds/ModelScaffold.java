package cz.uhk.zlesak.threejslearningapp.views.scaffolds;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import cz.uhk.zlesak.threejslearningapp.components.ModelDiv;
import cz.uhk.zlesak.threejslearningapp.components.ThreeJsComponent;
import cz.uhk.zlesak.threejslearningapp.components.compositions.ModelUploadFormScrollerComposition;
import cz.uhk.zlesak.threejslearningapp.utils.SpringContextUtils;
import cz.uhk.zlesak.threejslearningapp.views.IView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;

@Slf4j
@Scope("prototype")
public abstract class ModelScaffold extends Composite<VerticalLayout> implements IView {
    protected final ModelDiv modelDiv = new ModelDiv();;
    protected final ThreeJsComponent renderer;
    protected final ModelUploadFormScrollerComposition modelUploadFormScrollerComposition;

    public ModelScaffold() {
        HorizontalLayout modelPageLayout = new HorizontalLayout();
        this.renderer = new ThreeJsComponent();
        modelDiv.setRenderer(renderer);
        modelUploadFormScrollerComposition = new ModelUploadFormScrollerComposition();

        modelUploadFormScrollerComposition.addModelLoadEventListener(
                event -> this.renderer.loadModel(event.getBase64Model(), event.getBase64Texture())
        );
        modelUploadFormScrollerComposition.addModelClearEventListener(
                event -> this.renderer.clear()
        );

        this.renderer.getStyle().set("width", "100%");
        modelDiv.setId("modelDiv");
        modelDiv.setSizeFull();

        modelPageLayout.setClassName("modelPageLayout");
        modelPageLayout.addClassName(LumoUtility.Gap.MEDIUM);
        modelPageLayout.setFlexGrow(1, modelUploadFormScrollerComposition);
        modelPageLayout.setFlexGrow(1, modelDiv);
        modelPageLayout.add(modelUploadFormScrollerComposition, modelDiv);
        modelPageLayout.setSizeFull();

        getContent().add(modelPageLayout);
        getContent().setWidth("100%");
        getContent().setHeightFull();
        getContent().setMinHeight("0");
    }
}
