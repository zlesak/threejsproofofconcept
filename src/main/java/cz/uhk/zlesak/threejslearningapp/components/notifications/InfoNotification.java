package cz.uhk.zlesak.threejslearningapp.components.notifications;

import com.vaadin.flow.component.notification.Notification;

public class InfoNotification extends Notification {

    public InfoNotification(String message) {
        super(message, 3000, Position.BOTTOM_END);
        open();
    }

    public InfoNotification(String message, int duration) {
        super(message, duration, Position.BOTTOM_END);
        open();
    }
}
