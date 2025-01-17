package net.agiledeveloper.mobtime.domain.notification.session;

import net.agiledeveloper.mobtime.domain.notification.Notification;
import net.agiledeveloper.mobtime.domain.session.Session;

public record SessionCloseNotification(Session session, String message, String value) implements Notification {

}
