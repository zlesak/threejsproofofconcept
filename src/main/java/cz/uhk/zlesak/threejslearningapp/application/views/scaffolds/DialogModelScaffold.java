package cz.uhk.zlesak.threejslearningapp.application.views.scaffolds;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.router.AfterNavigationEvent;
import cz.uhk.zlesak.threejslearningapp.application.controllers.ModelController;
import cz.uhk.zlesak.threejslearningapp.application.views.creating.CreateModelDialog;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@Getter
public class DialogModelScaffold extends ModelScaffold {
    protected boolean isAdvanced;
    @Setter
    private CreateModelDialog.ModelCreatedListener modelCreatedListener;

    public DialogModelScaffold(ModelController modelController, Dialog dialog) {
        super();

        modelDiv.setHeight("100vh");
        modelDiv.renderer.getStyle().set("height", "100%");

        Button createButton = createModelButton(modelController, quickModelEntity -> {
            if (modelCreatedListener != null) {
                try {
                    modelCreatedListener.modelCreated(quickModelEntity);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                dialog.close();
            }
        });
        modelUploadFormScrollerComposition.getVl().add(createButton);
    }

    @Override
    public void beforeEnter(com.vaadin.flow.router.BeforeEnterEvent event) {
    }

    @Override
    public void beforeLeave(com.vaadin.flow.router.BeforeLeaveEvent event) {
    }

    @Override
    public String getPageTitle() {
        return "";
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

    }
}
