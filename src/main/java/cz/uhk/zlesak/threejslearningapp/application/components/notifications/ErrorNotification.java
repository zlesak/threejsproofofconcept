package cz.uhk.zlesak.threejslearningapp.application.components.notifications;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class ErrorNotification extends Notification {

    public ErrorNotification(String message) {
        super(message, 5000, Position.BOTTOM_END);
        this.addThemeVariants(NotificationVariant.LUMO_ERROR);
        open();
    }

    public ErrorNotification(String message, int duration) {
        super(message, duration, Position.BOTTOM_END);
        this.addThemeVariants(NotificationVariant.LUMO_ERROR);
        open();
    }
}
