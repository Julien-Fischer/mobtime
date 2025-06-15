package net.agiledeveloper.mobtime.domain.ports.api;

import net.agiledeveloper.mobtime.domain.session.Session;

public interface SessionServicePort {

    void open(Session session);

    void close(Session session);

}
