package net.agiledeveloper.mobtime.domain.notification.session;

import net.agiledeveloper.mobtime.domain.notification.Notification;

public record SessionRefreshNotification(String message, String value, boolean hasLittleTimeLeft) implements Notification {

}
