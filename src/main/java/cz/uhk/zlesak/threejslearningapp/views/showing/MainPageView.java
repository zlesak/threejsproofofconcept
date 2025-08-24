package cz.uhk.zlesak.threejslearningapp.views.showing;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.Route;
import cz.uhk.zlesak.threejslearningapp.i18n.CustomI18NProvider;
import cz.uhk.zlesak.threejslearningapp.views.IView;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Main page view of the application.
 * This view is accessible at the root route ("/").
 * It displays a welcome message and some introductory text.
 */
@Route("")
@Tag("main-page-view")
public class MainPageView extends Composite<VerticalLayout> implements IView {
    private final CustomI18NProvider i18NProvider;

    @Autowired
    public MainPageView(CustomI18NProvider i18NProvider) {
        this.i18NProvider = i18NProvider;
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

    @Override
    public String getPageTitle() {
        try {
            return this.i18NProvider.getTranslation("page.title.mainPageView", UI.getCurrent().getLocale());
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
