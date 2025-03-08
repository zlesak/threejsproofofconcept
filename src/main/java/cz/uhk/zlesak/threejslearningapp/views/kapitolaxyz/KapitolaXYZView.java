package cz.uhk.zlesak.threejslearningapp.views.kapitolaxyz;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import cz.uhk.zlesak.threejslearningapp.threejsdraw.Three;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Kapitola - XYZ")
@Route("chapter-id")
@Menu(order = 2, icon = LineAwesomeIconUrl.BOOK_OPEN_SOLID)
@Tag("chapter-view")
public class KapitolaXYZView extends Composite<VerticalLayout> {
    ProgressBar progressBar = new ProgressBar();
    Three renderer = new Three();

    public KapitolaXYZView() {
//set the id
        getContent().setId("chapter-container");
//definition
        HorizontalLayout layoutRow = new HorizontalLayout();
        VerticalLayout layoutColumn3 = new VerticalLayout();
        Icon icon = new Icon();
        VerticalLayout layoutColumn4 = new VerticalLayout();
        H2 h2 = new H2();
        H3 h3 = new H3();
        Div div = new Div();
        Paragraph textMedium = new Paragraph();

//styling
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        layoutColumn3.setHeightFull();
        layoutRow.setFlexGrow(1.0, layoutColumn3);
        layoutColumn3.setWidth("100%");
        layoutColumn3.getStyle().set("flex-grow", "1");
        renderer.getStyle().set("width", "100%");
        renderer.getStyle().set("height", "100%");
        icon.setIcon("lumo:user");
        icon.getStyle().set("flex-grow", "1");
        layoutColumn4.addClassName(Gap.SMALL);
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
//placing elements
        getContent().add(layoutRow);
    //renderer
        layoutRow.add(layoutColumn3);
        div.add(progressBar);
        div.add(renderer);
        div.setSizeFull();
        layoutColumn3.add(div);
        //TODO set load bar title and add percentage as info for better UX
        progressBar.getStyle().set("position", "absolute")
                .set("width", "40%")
                .set("top", "50%")
                .set("left", "50%")
                .set("transform", "translate(-50%, -50%)")
                .set("z-index", "10");
        div.getStyle().set("position", "relative");
    //study text
        layoutRow.add(layoutColumn4);
        layoutColumn4.add(h2);
        layoutColumn4.add(h3);
        layoutColumn4.add(textMedium);
    }
    @ClientCallable
    public void updateProgress(double progress) {
        progressBar.setValue(progress);
    }
    @ClientCallable
    public void hideProgressBar() {
        progressBar.setVisible(false);
    }
}

