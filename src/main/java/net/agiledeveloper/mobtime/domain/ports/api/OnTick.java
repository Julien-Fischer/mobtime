package net.agiledeveloper.mobtime.domain.ports.api;

import net.agiledeveloper.mobtime.domain.session.Session;

import java.time.Duration;

@FunctionalInterface
public interface OnTick {

    void accept(Session session, Duration remainingSeconds);

}
