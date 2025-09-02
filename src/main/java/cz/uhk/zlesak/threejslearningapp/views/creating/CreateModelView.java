package cz.uhk.zlesak.threejslearningapp.views.creating;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import cz.uhk.zlesak.threejslearningapp.components.BeforeLeaveActionDialog;
import cz.uhk.zlesak.threejslearningapp.controllers.ModelController;
import cz.uhk.zlesak.threejslearningapp.i18n.CustomI18NProvider;
import cz.uhk.zlesak.threejslearningapp.utils.SpringContextUtils;
import cz.uhk.zlesak.threejslearningapp.views.scaffolds.ModelScaffold;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

@Slf4j
@Route("createModel")
@Tag("create-model")
@Scope("prototype")
public class CreateModelView extends ModelScaffold {
    private final CustomI18NProvider i18nProvider;
    private boolean skipBeforeLeaveDialog = false;

    @Autowired
    public CreateModelView(ModelController modelController) {
        super();
        this.i18nProvider = SpringContextUtils.getBean(CustomI18NProvider.class);

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
            return this.i18nProvider.getTranslation("page.title.createChapterView", UI.getCurrent().getLocale());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

    }
}
