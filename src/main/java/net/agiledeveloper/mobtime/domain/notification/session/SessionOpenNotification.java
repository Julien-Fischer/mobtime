package net.agiledeveloper.mobtime.domain.notification.session;

import net.agiledeveloper.mobtime.domain.notification.Notification;

public record SessionOpenNotification(String message, String value) implements Notification {

}
