package cz.uhk.zlesak.threejslearningapp.views.odhlášení;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Odhlášení")
@Route("logout")
@Menu(order = 5, icon = LineAwesomeIconUrl.SIGN_OUT_ALT_SOLID)
public class OdhlášeníView extends Composite<VerticalLayout> {

    public OdhlášeníView() {
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
    }
}
