package cz.uhk.zlesak.threejslearningapp.application.views.creating;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import cz.uhk.zlesak.threejslearningapp.application.components.BeforeLeaveActionDialog;
import cz.uhk.zlesak.threejslearningapp.application.controllers.ModelController;
import cz.uhk.zlesak.threejslearningapp.application.views.scaffolds.ModelScaffold;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

@Slf4j
@Route("createModel")
@Tag("create-model")
@Scope("prototype")
@RolesAllowed(value = "ADMIN")
public class CreateModelView extends ModelScaffold {
    private boolean skipBeforeLeaveDialog = false;

    @Autowired
    public CreateModelView(ModelController modelController) {
        super();

        Button createButton = createModelButton(modelController, quickModelEntity -> {
            skipBeforeLeaveDialog = true;
            VaadinSession.getCurrent().setAttribute("quickModelEntity", quickModelEntity);
            UI.getCurrent().navigate("model/" + quickModelEntity.getModel().getId());
        });
        modelUploadFormScrollerComposition.getVl().add(createButton);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        if (!skipBeforeLeaveDialog) {
            BeforeLeaveActionDialog.leave(event);
        }
    }

    @Override
    public String getPageTitle() {
        try {
            return text("page.title.createChapterView");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

    }
}
