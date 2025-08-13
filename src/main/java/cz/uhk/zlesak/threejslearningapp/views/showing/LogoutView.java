package cz.uhk.zlesak.threejslearningapp.views.showing;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.Route;
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

    @Override
    public String getPageTitle() {
        return "Odhlášení"; //TODO: use i18n for the page title
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {

    }
}
