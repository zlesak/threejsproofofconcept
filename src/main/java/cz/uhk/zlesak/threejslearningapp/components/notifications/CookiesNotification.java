package cz.uhk.zlesak.threejslearningapp.components.notifications;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import cz.uhk.zlesak.threejslearningapp.i18n.I18nAware;

/**
 * CookiesNotification Class - Displays a notification to inform users about cookie usage
 */
public class CookiesNotification extends Notification implements I18nAware {

    /**
     * Constructor - Initializes the CookiesNotification with a message and an accept button
     */
    public CookiesNotification() {
        super();
        Span message = new Span(text("notification.cookieConsent"));

        setPosition(Notification.Position.BOTTOM_CENTER);
        setDuration(0);

        Button acceptButton = new Button("Rozumím", e -> {
            UI.getCurrent().getPage().executeJs(
                    "document.cookie = 'cookieConsent=accepted; path=/; max-age=31536000';"
            );
            close();
        });

        HorizontalLayout layout = new HorizontalLayout(message, acceptButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setSpacing(true);

        add(layout);
    }
}
