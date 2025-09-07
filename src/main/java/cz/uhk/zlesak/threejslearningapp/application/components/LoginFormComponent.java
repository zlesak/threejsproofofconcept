package cz.uhk.zlesak.threejslearningapp.application.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import cz.uhk.zlesak.threejslearningapp.application.i18n.CustomI18NProvider;
import cz.uhk.zlesak.threejslearningapp.application.utils.SpringContextUtils;

public class LoginFormComponent extends LoginForm {
    CustomI18NProvider i18NProvider;

    public LoginFormComponent() {
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
        i18n.getHeader().setTitle(i18NProvider.getTranslation("login.title", UI.getCurrent().getLocale()));
        i18n.getHeader().setDescription(i18NProvider.getTranslation("login.description", UI.getCurrent().getLocale()));
        i18n.getForm().setUsername(i18NProvider.getTranslation("login.username", UI.getCurrent().getLocale()));
        i18n.getForm().setPassword(i18NProvider.getTranslation("login.password", UI.getCurrent().getLocale()));
        i18n.getForm().setSubmit(i18NProvider.getTranslation("login.submit", UI.getCurrent().getLocale()));
        i18n.getForm().setTitle(i18NProvider.getTranslation("login.form.title", UI.getCurrent().getLocale()));
        i18n.getErrorMessage().setTitle(i18NProvider.getTranslation("login.error.title", UI.getCurrent().getLocale()));
        i18n.getErrorMessage().setMessage(i18NProvider.getTranslation("login.error.message", UI.getCurrent().getLocale()));
        return i18n;
    }
}
