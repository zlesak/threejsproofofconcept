package cz.uhk.zlesak.threejslearningapp.views.showing;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Domovská stránka")
@Route("")
@Menu(order = 0, icon = LineAwesomeIconUrl.HOME_SOLID)
@Scope("prototype")
public class MainPageView extends Composite<VerticalLayout> {

    public MainPageView() {
/// content
        Paragraph textSmall = new Paragraph();
        Icon icon = new Icon();
        H1 h1 = new H1();
        icon.setIcon("lumo:user");
        h1.setText("Moderní metody učení nejen pro budoucí lékaře");
        h1.setWidth("max-content");
        textSmall.setText(
                "MISH APP - moderní systém ");
        textSmall.setWidth("max-content");
        textSmall.getStyle().set("font-size", "var(--lumo-font-size-xs)");

/// layout setup
        HorizontalLayout layoutRow = new HorizontalLayout();
        VerticalLayout layoutColumn = new VerticalLayout();
        Hr hr = new Hr();

        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        getContent().setAlignItems(FlexComponent.Alignment.CENTER);
        getContent().setFlexGrow(1.0, layoutRow);

        layoutColumn.setWidth("100%");
        layoutRow.add(layoutColumn);
        layoutColumn.add(h1, hr, textSmall);
        getContent().add(layoutRow);
    }
}
