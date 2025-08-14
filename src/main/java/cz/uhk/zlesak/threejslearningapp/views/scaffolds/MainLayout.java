package cz.uhk.zlesak.threejslearningapp.views.scaffolds;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import cz.uhk.zlesak.threejslearningapp.components.AvatarItemComponent;
import cz.uhk.zlesak.threejslearningapp.views.creating.CreateChapterView;
import cz.uhk.zlesak.threejslearningapp.views.creating.CreateModelView;
import cz.uhk.zlesak.threejslearningapp.views.showing.MainPageView;
import org.springframework.context.annotation.Scope;
import org.vaadin.lineawesome.LineAwesomeIcon;

@Layout
@AnonymousAllowed
@Scope("prototype")
public class MainLayout extends AppLayout {
    public static class MenuItemInfo extends ListItem {

        private final Class<? extends Component> view;

        public MenuItemInfo(String menuTitle, Component icon, Class<? extends Component> view) {
            this.view = view;
            RouterLink link = new RouterLink();
            link.addClassNames(Display.FLEX, Gap.XSMALL, Height.MEDIUM, AlignItems.CENTER, Padding.Horizontal.SMALL, TextColor.BODY);
            link.setRoute(view);
            Span text = new Span(menuTitle);
            text.addClassNames(FontWeight.MEDIUM, FontSize.MEDIUM, Whitespace.NOWRAP);
            if (icon != null) {
                link.add(icon);
            }
            link.add(text);
            add(link);
        }

        public Class<?> getView() {
            return view;
        }
    }

    public MainLayout() {
        addToNavbar(createHeaderContent());
    }

    private Component createHeaderContent() {
        /// Header item component wrapper
        Header header = new Header();
        header.addClassNames(BoxSizing.BORDER, Display.FLEX, FlexDirection.COLUMN, Width.FULL);

        /// Layout item in form of div component
        Div layout = new Div();
        layout.addClassNames(Display.FLEX, AlignItems.CENTER, Padding.Horizontal.LARGE);

        /// App name heading
        H1 appName = new H1("MISH APP");
        appName.addClassNames(Margin.Vertical.MEDIUM, FontSize.LARGE);
        layout.add(appName);

        /// Navigation item component
        Nav nav = new Nav();
        nav.addClassNames(Display.FLEX, Overflow.AUTO, Padding.Horizontal.MEDIUM, Padding.Vertical.XSMALL, Margin.End.AUTO, AlignItems.START);
        /// UL for items of the navigation
        UnorderedList list = new UnorderedList();
        list.addClassNames(Display.FLEX, Gap.SMALL, ListStyleType.NONE, Margin.NONE, Padding.NONE);
        nav.add(list);
        layout.add(nav);
        /// For loop for inserting the menu items into the UL wrapper
        for (MenuItemInfo menuItem : createMenuItems()) {
            list.add(menuItem);
        }

        /// Light or dark mode toggle switch with default of light mode
        Button buttonPrimary = new Button();
        buttonPrimary.addClassNames(Display.FLEX, Gap.MEDIUM, Margin.MEDIUM, Padding.MEDIUM);
        buttonPrimary.setWidth("20px");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        layout.add(buttonPrimary);

        UI.getCurrent().getPage().executeJs(
                "const match = document.cookie.match('(^|;) ?themeMode=([^;]*)(;|$)'); return match ? match[2] : null;"
        ).then(String.class, value -> {
            if ("dark".equals(value)) {
                getElement().getThemeList().add(Lumo.DARK);
                buttonPrimary.setText("☀️");
            } else {
                buttonPrimary.setText("🌑");
            }
        });

        buttonPrimary.addClickListener(e -> changeMode(buttonPrimary));

        UI.getCurrent().getPage().executeJs(
                "const match = document.cookie.match('(^|;) ?cookieConsent=([^;]*)(;|$)'); return match ? match[2] : null;"
        ).then(String.class, value -> {
            if (!"accepted".equals(value)) {
                showCookieNotification();
            }
        });

        /// Login user basic information avatar item TODO in phase where ogin is implemented via BE API react to these changes and change accordingly to use the appropriate APIs
        AvatarItemComponent avatarItem = new AvatarItemComponent("Aria Bailey", "Endocrinologist", new Avatar("Aria Bailey"));
        layout.add(avatarItem);

        header.add(layout);
        return header;
    }

    /// Function for creating menu items to appear at the top navigation
    private MenuItemInfo[] createMenuItems() {
        return new MenuItemInfo[]{
                new MenuItemInfo("Domovská stránka", LineAwesomeIcon.HOME_SOLID.create(), MainPageView.class),
                new MenuItemInfo("Vytvořit kapitolu", LineAwesomeIcon.PEN_ALT_SOLID.create(), CreateChapterView.class),
                new MenuItemInfo("Nahrát model", LineAwesomeIcon.OBJECT_GROUP.create(), CreateModelView.class)
        };
    }

    /// Dark theme or light theme change function
    private void changeMode(Button button) {
        ThemeList themeList = getElement().getThemeList();
        String mode;
        if (themeList.contains(Lumo.DARK)) {
            themeList.remove(Lumo.DARK);
            button.setText("🌑");
            mode = "light";
        } else {
            themeList.add(Lumo.DARK);
            button.setText("☀️");
            mode = "dark";
        }

        UI.getCurrent().getPage().executeJs(
                "document.cookie = 'themeMode=' + $0 + '; path=/; max-age=31536000';", mode);
    }

    private void showCookieNotification() {
        Span message = new Span("Tato stránka používá cookies pro uložení zvoleného režimu zobrazení.");

        Notification notification = new Notification();
        notification.setPosition(Notification.Position.BOTTOM_CENTER);
        notification.setDuration(0);

        Button acceptButton = new Button("Rozumím", e -> {
            UI.getCurrent().getPage().executeJs(
                    "document.cookie = 'cookieConsent=accepted; path=/; max-age=31536000';"
            );
            notification.close();
        });

        HorizontalLayout layout = new HorizontalLayout(message, acceptButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setSpacing(true);

        notification.add(layout);
        notification.open();
    }

}
