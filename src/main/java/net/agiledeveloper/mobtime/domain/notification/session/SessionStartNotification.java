package net.agiledeveloper.mobtime.domain.notification.session;

import net.agiledeveloper.mobtime.domain.notification.Notification;

public record SessionStartNotification(String message, String value) implements Notification {

}
