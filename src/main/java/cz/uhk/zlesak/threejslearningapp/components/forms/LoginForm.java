package cz.uhk.zlesak.threejslearningapp.components.forms;

import com.vaadin.flow.component.login.LoginI18n;
import cz.uhk.zlesak.threejslearningapp.i18n.CustomI18NProvider;
import cz.uhk.zlesak.threejslearningapp.i18n.I18nAware;
import cz.uhk.zlesak.threejslearningapp.common.SpringContextUtils;

public class LoginForm extends com.vaadin.flow.component.login.LoginForm implements I18nAware {
    CustomI18NProvider i18NProvider;

    /**
     * Constructor that initializes the LoginFormComponent with multiple language support.
     */
    public LoginForm() {
        super();
        this.i18NProvider = SpringContextUtils.getBean(CustomI18NProvider.class);
        setI18n(getLoginI18n());
        setAction("login");
        setForgotPasswordButtonVisible(false);
        getStyle().setPaddingTop("5vh");
    }

    private LoginI18n getLoginI18n() {
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle(text("login.title"));
        i18n.getHeader().setDescription(text("login.description"));
        i18n.getForm().setUsername(text("login.username"));
        i18n.getForm().setPassword(text("login.password"));
        i18n.getForm().setSubmit(text("login.submit"));
        i18n.getForm().setTitle(text("login.form.title"));
        i18n.getErrorMessage().setTitle(text("login.error.title"));
        i18n.getErrorMessage().setMessage(text("login.error.message"));
        return i18n;
    }
}
