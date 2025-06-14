package net.agiledeveloper.mobtime.domain.notification;

import net.agiledeveloper.mobtime.domain.session.Session;

public interface Notification {

    Session session();

    String message();

    String value();

    Severity severity();

}
