package cz.uhk.zlesak.threejslearningapp.components.buttons;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.spring.security.AuthenticationContext;
import cz.uhk.zlesak.threejslearningapp.i18n.I18nAware;

/**
 * A button that logs out the current user.
 * Requires AuthenticationContext to be provided in the constructor.
 */
public class LogoutButton extends Button implements I18nAware {

    public LogoutButton(AuthenticationContext authenticationContext) {
        super(VaadinIcon.SIGN_OUT.create());
        setText(text("logoutButton.label"));
        addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        addClickListener(e -> authenticationContext.logout());
    }
}

