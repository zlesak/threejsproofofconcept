package cz.uhk.zlesak.threejslearningapp.views.kapitolaxyz;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Kapitola - XYZ")
@Route("chapter-id")
@Menu(order = 2, icon = LineAwesomeIconUrl.BOOK_OPEN_SOLID)
public class KapitolaXYZView extends Composite<VerticalLayout> {

    public KapitolaXYZView() {
        HorizontalLayout layoutRow = new HorizontalLayout();
        VerticalLayout layoutColumn2 = new VerticalLayout();
        Button buttonSecondary = new Button();
        Button buttonSecondary2 = new Button();
        Button buttonSecondary3 = new Button();
        Button buttonSecondary4 = new Button();
        Button buttonSecondary5 = new Button();
        Button buttonSecondary6 = new Button();
        Button buttonSecondary7 = new Button();
        VerticalLayout layoutColumn3 = new VerticalLayout();
        Icon icon = new Icon();
        VerticalLayout layoutColumn4 = new VerticalLayout();
        H2 h2 = new H2();
        H3 h3 = new H3();
        Paragraph textMedium = new Paragraph();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        layoutColumn2.setHeightFull();
        layoutRow.setFlexGrow(1.0, layoutColumn2);
        layoutColumn2.addClassName(Gap.SMALL);
        layoutColumn2.setWidth("min-content");
        layoutColumn2.getStyle().set("flex-grow", "1");
        buttonSecondary.setText("Ov");
        buttonSecondary.setWidth("min-content");
        buttonSecondary2.setText("Lá");
        buttonSecondary2.setWidth("min-content");
        buttonSecondary3.setText("Da");
        buttonSecondary3.setWidth("min-content");
        buttonSecondary4.setText("Cí");
        buttonSecondary4.setWidth("min-content");
        buttonSecondary5.setText("Pr");
        buttonSecondary5.setWidth("min-content");
        buttonSecondary6.setText("V");
        buttonSecondary6.setWidth("min-content");
        buttonSecondary7.setText("Ky");
        buttonSecondary7.setWidth("min-content");
        layoutColumn3.setHeightFull();
        layoutRow.setFlexGrow(1.0, layoutColumn3);
        layoutColumn3.setWidth("100%");
        layoutColumn3.getStyle().set("flex-grow", "1");
        icon.setIcon("lumo:user");
        icon.setWidth("100%");
        icon.getStyle().set("flex-grow", "1");
        layoutColumn4.addClassName(Gap.SMALL);
        layoutColumn4.setWidth("450px");
        layoutColumn4.setHeight("100%");
        layoutColumn4.setJustifyContentMode(JustifyContentMode.CENTER);
        layoutColumn4.setAlignItems(Alignment.START);
        h2.setText("Kapitola - XYZ");
        h2.setWidth("100%");
        h3.setText("Podnadpis");
        h3.setWidth("max-content");
        textMedium.setText(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
        textMedium.setWidth("100%");
        textMedium.getStyle().set("font-size", "var(--lumo-font-size-m)");
        getContent().add(layoutRow);
        layoutRow.add(layoutColumn2);
        layoutColumn2.add(buttonSecondary);
        layoutColumn2.add(buttonSecondary2);
        layoutColumn2.add(buttonSecondary3);
        layoutColumn2.add(buttonSecondary4);
        layoutColumn2.add(buttonSecondary5);
        layoutColumn2.add(buttonSecondary6);
        layoutColumn2.add(buttonSecondary7);
        layoutRow.add(layoutColumn3);
        layoutColumn3.add(icon);
        layoutRow.add(layoutColumn4);
        layoutColumn4.add(h2);
        layoutColumn4.add(h3);
        layoutColumn4.add(textMedium);
    }
}
