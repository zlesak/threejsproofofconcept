package cz.uhk.zlesak.threejslearningapp.views;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;

@PageTitle("Odhlášení")
@Route("logout")
@Scope("prototype")
public class LogoutView extends Composite<VerticalLayout> {

}
