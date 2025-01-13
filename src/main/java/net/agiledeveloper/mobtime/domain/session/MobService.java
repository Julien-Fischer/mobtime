package net.agiledeveloper.mobtime.domain.session;

import net.agiledeveloper.mobtime.domain.ports.api.SessionPort;
import net.agiledeveloper.mobtime.domain.ports.spi.MobPort;

public class MobService implements SessionPort {

    private final MobPort mobPort;


    public MobService(MobPort mobPort) {
        this.mobPort = mobPort;
    }


    @Override
    public void next() {
        mobPort.next();
    }

    @Override
    public void done() {
        mobPort.done();
    }

}
