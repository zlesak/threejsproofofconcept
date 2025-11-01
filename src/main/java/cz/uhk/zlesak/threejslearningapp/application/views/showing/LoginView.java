package cz.uhk.zlesak.threejslearningapp.application.views.showing;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import cz.uhk.zlesak.threejslearningapp.application.components.LoginFormComponent;
import cz.uhk.zlesak.threejslearningapp.application.views.IView;

@Route("login")
@AnonymousAllowed
public class LoginView extends Composite<VerticalLayout> implements IView {
    private final LoginForm loginForm;

    public LoginView() {
        loginForm = new LoginFormComponent();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setAlignItems(FlexComponent.Alignment.CENTER);
        getContent().add(loginForm);
    }

    @Override
    public String getPageTitle() {
        try {
            return text("page.title.loginView");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            loginForm.setError(true);
        }
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {

    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

    }
}
