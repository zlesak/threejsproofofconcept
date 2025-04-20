package cz.uhk.zlesak.threejslearningapp.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Můj účet")
@Route("profile")
//@Menu(order = 4, icon = LineAwesomeIconUrl.USER)
@Uses(Icon.class)
public class ProfileView extends Composite<VerticalLayout> {

}
