package cz.uhk.zlesak.threejslearningapp.views.domovskástránka;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Domovská stránka")
@Route("")
@Menu(order = 0, icon = LineAwesomeIconUrl.HOME_SOLID)
public class DomovskástránkaView extends Composite<VerticalLayout> {

    public DomovskástránkaView() {
        H1 h1 = new H1();
        Hr hr = new Hr();
        Paragraph textSmall = new Paragraph();
        HorizontalLayout layoutRow = new HorizontalLayout();
        Icon icon = new Icon();
        Icon icon2 = new Icon();
        Icon icon3 = new Icon();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);
        h1.setText("Moderní metody učení nejen pro budoucí lékaře");
        h1.setWidth("max-content");
        textSmall.setText(
                "Pro moderní lékařství se musí využívat i moderních přístupů učení pro efektivní a smysluplné učební procesy našich nastávajících lékařů.");
        getContent().setAlignSelf(FlexComponent.Alignment.CENTER, textSmall);
        textSmall.setWidth("max-content");
        textSmall.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        layoutRow.setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow);
        layoutRow.addClassName(Gap.LARGE);
        layoutRow.addClassName(Padding.LARGE);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        layoutRow.setAlignItems(Alignment.CENTER);
        layoutRow.setJustifyContentMode(JustifyContentMode.CENTER);
        icon.setIcon("lumo:user");
        icon.setWidth("100%");
        icon.setHeight("100%");
        icon2.setIcon("lumo:user");
        icon2.setWidth("100%");
        icon2.setHeight("100%");
        icon3.setIcon("lumo:user");
        icon3.setWidth("100%");
        icon3.setHeight("100%");
        getContent().add(h1);
        getContent().add(hr);
        getContent().add(textSmall);
        getContent().add(layoutRow);
        layoutRow.add(icon);
        layoutRow.add(icon2);
        layoutRow.add(icon3);
    }
}
