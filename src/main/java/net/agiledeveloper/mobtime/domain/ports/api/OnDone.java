package net.agiledeveloper.mobtime.domain.ports.api;

import net.agiledeveloper.mobtime.domain.session.Session;

@FunctionalInterface
public interface OnDone {

    void accept(Session session);

}
