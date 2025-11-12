package cz.uhk.zlesak.threejslearningapp.views.auth;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import cz.uhk.zlesak.threejslearningapp.components.forms.LoginForm;
import cz.uhk.zlesak.threejslearningapp.views.IView;

@Route("login")
@AnonymousAllowed
public class LoginView extends Composite<VerticalLayout> implements IView {
    private final com.vaadin.flow.component.login.LoginForm loginForm;

    public LoginView() {
        loginForm = new LoginForm();
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

}
