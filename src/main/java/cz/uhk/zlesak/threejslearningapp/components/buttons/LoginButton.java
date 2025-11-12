package cz.uhk.zlesak.threejslearningapp.components.buttons;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import cz.uhk.zlesak.threejslearningapp.i18n.I18nAware;
import cz.uhk.zlesak.threejslearningapp.views.auth.LoginView;

/**
 * A button that navigates to the login view.
 */
public class LoginButton extends Button implements I18nAware {

    public LoginButton() {
        super(VaadinIcon.SIGN_IN.create());
        setText(text("loginButton.label"));
        addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addClickListener(e -> UI.getCurrent().navigate(LoginView.class));
    }
}

