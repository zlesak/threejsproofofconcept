package cz.uhk.zlesak.threejslearningapp.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.function.SerializableRunnable;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;
import cz.uhk.zlesak.threejslearningapp.controllers.ModelController;
import cz.uhk.zlesak.threejslearningapp.models.ModelEntity;
import cz.uhk.zlesak.threejslearningapp.threejsdraw.Three;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Nahrát 3D model")
@Route("model/:modelId?")
@Tag("view-model")
public class ModelView extends Composite<VerticalLayout> implements IView {
    private final ModelController modelController;
    Three renderer = new Three();
    private String modelId;

    @Autowired
    public ModelView(ModelController modelController){
        VerticalLayout chapterModel = new VerticalLayout();

        chapterModel.setHeightFull();
        chapterModel.setWidthFull();
        chapterModel.addClassName(LumoUtility.Gap.MEDIUM);
        chapterModel.setPadding(false);

        ProgressBar progressBar = new ProgressBar();


        Div modelDiv = new Div(progressBar, renderer);
        modelDiv.setId("modelDiv");
        modelDiv.setWidth("100%");
        modelDiv.getStyle().set("flex-grow", "1");

        renderer.getStyle().set("width", "100%");
        chapterModel.add(modelDiv);

        getContent().add(modelDiv);
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        this.modelController = modelController;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        RouteParameters parameters = event.getRouteParameters();
        if (parameters.getParameterNames().isEmpty()){
            event.forwardTo(ModelListView.class);
        }
        modelId = parameters.get("modelId").orElse(null);
        if (modelId == null){
            event.forwardTo(ModelListView.class);
        }

        ModelEntity modelFile = modelController.getModel(modelId);
        try{
            String base64Model =  modelFile.getBase64File();
            renderer.loadModel(base64Model);
        }catch (Exception e){
            logger.error(e.getMessage());
            Notification.show("Nepovedlo se načíst model: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        BeforeLeaveEvent.ContinueNavigationAction postponed = event.postpone();
        renderer.dispose((SerializableRunnable) () -> UI.getCurrent().access(postponed::proceed));
    }
}
