package cz.uhk.zlesak.threejslearningapp.views.přihlášení;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Přihlášení")
@Route("login")
@Menu(order = 3, icon = LineAwesomeIconUrl.SIGN_IN_ALT_SOLID)
public class PřihlášeníView extends Composite<VerticalLayout> {

    public PřihlášeníView() {
        LoginForm loginForm = new LoginForm();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setAlignSelf(FlexComponent.Alignment.CENTER, loginForm);
        loginForm.setWidth("min-content");
        getContent().add(loginForm);
    }
}
