package cz.uhk.zlesak.threejslearningapp.views.scaffolds;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;
import cz.uhk.zlesak.threejslearningapp.data.ViewTypeEnum;
import cz.uhk.zlesak.threejslearningapp.threejsdraw.Three;
import cz.uhk.zlesak.threejslearningapp.views.interfaces.IView;
import org.springframework.context.annotation.Scope;

@Scope("prototype")
public abstract class ModelScaffold extends Composite<VerticalLayout> implements IView {
    protected final Three renderer = new Three();
    protected final ProgressBar progressBar = new ProgressBar();
    protected final Div modelDiv = new Div(progressBar, renderer);

    protected final TextField modelName = new TextField("Název modelu");

    public ModelScaffold(ViewTypeEnum viewTypeEnum) {
        HorizontalLayout modelPageLayout = new HorizontalLayout();
        VerticalLayout modelProperties = new VerticalLayout();
        VerticalLayout model = new VerticalLayout();

        modelPageLayout.setWidthFull();
        modelPageLayout.setClassName("modelPageLayout");
        modelPageLayout.addClassName(LumoUtility.Gap.MEDIUM);
        modelPageLayout.setFlexGrow(1, modelProperties);
        modelPageLayout.setFlexGrow(1, model);
        modelPageLayout.add(modelProperties, model);

        modelDiv.setId("modelDiv");
        modelDiv.setMaxHeight("85vh");
        modelDiv.setWidthFull();
        modelDiv.setHeight("85vh"); //TODO rethink logic about the height size

        renderer.getStyle().set("width", "100%");

        modelProperties.add(modelName);

        model.add(modelDiv);

        switch (viewTypeEnum){
            case CREATE -> {
                modelName.setLabel("Název modelu");
                modelName.setPlaceholder("Zadejte název modelu");
                showRendererAndProgressBar(false);
            }
            case EDIT -> {

            }
            case VIEW -> modelName.setVisible(false);
        }

        getContent().add(modelPageLayout);
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
    }

    public void showRendererAndProgressBar(Boolean show) {
        renderer.setVisible(show);
        progressBar.setVisible(show);
    }
}
