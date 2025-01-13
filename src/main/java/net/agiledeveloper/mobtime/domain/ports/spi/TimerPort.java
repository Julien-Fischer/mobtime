package net.agiledeveloper.mobtime.domain.ports.spi;

import net.agiledeveloper.mobtime.domain.ports.api.OnDone;
import net.agiledeveloper.mobtime.domain.ports.api.OnTick;
import net.agiledeveloper.mobtime.domain.session.Session;

public interface TimerPort {

    void runFor(Session session, OnTick onTick, OnDone onDone);

}
