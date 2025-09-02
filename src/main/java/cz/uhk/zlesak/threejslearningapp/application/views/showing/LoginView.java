package cz.uhk.zlesak.threejslearningapp.application.views.showing;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import cz.uhk.zlesak.threejslearningapp.application.i18n.CustomI18NProvider;
import cz.uhk.zlesak.threejslearningapp.application.views.IView;
import org.springframework.beans.factory.annotation.Autowired;

@Route("login")
@AnonymousAllowed
public class LoginView extends Composite<VerticalLayout> implements IView {
    private final LoginForm loginForm;
    CustomI18NProvider i18NProvider;


    @Autowired
    public LoginView(CustomI18NProvider i18NProvider) {
        this.i18NProvider = i18NProvider;
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        getContent().setAlignItems(FlexComponent.Alignment.CENTER);

        loginForm = new LoginForm();
        loginForm.setAction("login");

        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setAlignSelf(FlexComponent.Alignment.CENTER, loginForm);
        getContent().add(loginForm);
    }

    @Override
    public String getPageTitle() {
        try {
            return this.i18NProvider.getTranslation("page.title.loginView", UI.getCurrent().getLocale());
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
