package cz.uhk.zlesak.threejslearningapp.views.showing;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.uhk.zlesak.threejslearningapp.views.IView;
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
public class ProfileView extends Composite<VerticalLayout> implements IView {

    @Override
    public String getPageTitle() {
        return "Můj účet";//TODO use i18n for the page title
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {

    }
}
