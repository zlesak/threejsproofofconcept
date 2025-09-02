package cz.uhk.zlesak.threejslearningapp.application.views.showing;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import cz.uhk.zlesak.threejslearningapp.application.i18n.CustomI18NProvider;
import cz.uhk.zlesak.threejslearningapp.application.views.IView;
import jakarta.annotation.security.PermitAll;
import org.springframework.context.annotation.Scope;

/**
 * View for displaying the user's profile.
 * WIP, will be implemented after the proper AAA implementation on BE side.
 * This view is accessible at the route "profile".
 * It uses a prototype scope, meaning a new instance is created for each request.
 */
//TODO implement the profile view after the proper AAA implementation on BE side
@Route("profile")
@Uses(Icon.class)
@Scope("prototype")
@PermitAll
public class ProfileView extends Composite<VerticalLayout> implements IView {
    CustomI18NProvider i18NProvider;

    public ProfileView(CustomI18NProvider i18NProvider) {
        this.i18NProvider = i18NProvider;

    }
    @Override
    public String getPageTitle() {
        try {
            return this.i18NProvider.getTranslation("page.title.profileView", UI.getCurrent().getLocale());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {

    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

    }
}
