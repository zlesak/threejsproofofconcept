package cz.uhk.zlesak.threejslearningapp.views.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import cz.uhk.zlesak.threejslearningapp.components.buttons.LoginButton;
import cz.uhk.zlesak.threejslearningapp.components.buttons.LogoutButton;
import cz.uhk.zlesak.threejslearningapp.components.buttons.ThemeModeToggleButton;
import cz.uhk.zlesak.threejslearningapp.components.lists.AvatarItem;
import cz.uhk.zlesak.threejslearningapp.views.MainPageView;
import cz.uhk.zlesak.threejslearningapp.views.chapter.ChapterListView;
import cz.uhk.zlesak.threejslearningapp.views.chapter.CreateChapterView;
import cz.uhk.zlesak.threejslearningapp.views.model.CreateModelView;
import cz.uhk.zlesak.threejslearningapp.views.model.ModelListView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@AnonymousAllowed
@Scope("prototype")
@Layout
@Slf4j
public class MainLayout extends AppLayout {
    public MainLayout() {
        addToNavbar(createHeaderContent());
    }

    private Component createHeaderContent() {
        AuthenticationContext authenticationContext = VaadinService.getCurrent().getInstantiator().getOrCreate(AuthenticationContext.class);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
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
        for (MenuItemInfo menuItem : createMenuItems(authentication)) {
            list.add(menuItem);
        }

        /// Light or dark mode toggle switch with default of light mode
        ThemeModeToggleButton buttonPrimary = new ThemeModeToggleButton();
        layout.add(buttonPrimary);


        UI.getCurrent().getPage().executeJs(
                "const match = document.cookie.match('(^|;) ?cookieConsent=([^;]*)(;|$)'); return match ? match[2] : null;"
        ).then(String.class, value -> {
            if (!"accepted".equals(value)) {
                showCookieNotification();
            }
        });

        /// Login user basic information avatar item TODO in phase where ogin is implemented via BE API react to these changes and change accordingly to use the appropriate APIs
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            String username = authentication.getName();
            AvatarItem avatarItem = new AvatarItem(username, getUserRoleName(authentication), new Avatar(username));
            layout.add(avatarItem);
            LogoutButton logoutButton = new LogoutButton(authenticationContext);
            layout.add(logoutButton);
        } else {
            LoginButton loginButton = new LoginButton();
            layout.add(loginButton);
        }
        header.add(layout);
        return header;
    }

    /// Function for creating menu items to appear at the top navigation
    private MenuItemInfo[] createMenuItems(Authentication authentication) {
        if (authentication != null && authentication.getAuthorities() != null) {
            if (authentication.getAuthorities().stream().anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()))) {
                return new MenuItemInfo[]{
                        new MenuItemInfo("Domovská stránka", VaadinIcon.HOME.create(), MainPageView.class),
                        new MenuItemInfo("Vytvořit kapitolu", VaadinIcon.PENCIL.create(), CreateChapterView.class),
                        new MenuItemInfo("Nahrát model", VaadinIcon.FILE_ZIP.create(), CreateModelView.class),
                        new MenuItemInfo("Modely", VaadinIcon.FILE_TREE.create(), ModelListView.class),
                        new MenuItemInfo("Kapitoly", VaadinIcon.MODAL_LIST.create(), ChapterListView.class)
                };
            } else if (authentication.getAuthorities().stream().anyMatch(auth -> "ROLE_USER".equals(auth.getAuthority()))) {
                return new MenuItemInfo[]{
                        new MenuItemInfo("Domovská stránka", VaadinIcon.HOME.create(), MainPageView.class),
                        new MenuItemInfo("Modely", VaadinIcon.FILE_TREE.create(), ModelListView.class),
                        new MenuItemInfo("Kapitoly", VaadinIcon.MODAL_LIST.create(), ChapterListView.class)
                };
            }
        }
        return new MenuItemInfo[]{new MenuItemInfo("Domovská stránka", VaadinIcon.HOME.create(), MainPageView.class)};
    }

    private String getUserRoleName(Authentication authentication) {
        if (authentication != null && authentication.getAuthorities() != null) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                    return "Administrátor";
                } else if ("ROLE_USER".equals(authority.getAuthority())) {
                    return "Uživatel";
                }
            }
            throw new ApplicationContextException("Role uživatele nejsou mezi známými rolemi systému" + authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        }
        throw new ApplicationContextException("Neplatné role uživatele, authentication není nastavena!");
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
}
