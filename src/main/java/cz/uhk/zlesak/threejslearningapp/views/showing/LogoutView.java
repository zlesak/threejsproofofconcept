package cz.uhk.zlesak.threejslearningapp.views.showing;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.Route;
import cz.uhk.zlesak.threejslearningapp.i18n.CustomI18NProvider;
import cz.uhk.zlesak.threejslearningapp.views.IView;
import org.springframework.context.annotation.Scope;

/**
 * Logout view of the application.
 * This view is accessible at the route "/logout".
 * It handles user logout functionality.
 * WIP - currently does not implement any specific logout logic as no AAA implementation is present on the BE.
 */
@Route("logout")
@Scope("prototype")
public class LogoutView extends Composite<VerticalLayout> implements IView {
    CustomI18NProvider i18NProvider;

    public LogoutView(CustomI18NProvider i18NProvider) {
        this.i18NProvider = i18NProvider;
        getContent().setSizeFull();
    }

    @Override
    public String getPageTitle() {
        try {
            return this.i18NProvider.getTranslation("page.title.logoutView", UI.getCurrent().getLocale());
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
