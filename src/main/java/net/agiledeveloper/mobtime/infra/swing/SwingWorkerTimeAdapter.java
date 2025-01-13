package net.agiledeveloper.mobtime.infra.swing;

import net.agiledeveloper.mobtime.domain.ports.api.OnDone;
import net.agiledeveloper.mobtime.domain.ports.api.OnTick;
import net.agiledeveloper.mobtime.domain.ports.spi.TimerPort;
import net.agiledeveloper.mobtime.domain.session.Session;

import javax.swing.*;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class SwingWorkerTimeAdapter implements TimerPort {

    private static final int DEFAULT_FREQUENCY_MS = 1000;

    private final int tickFrequencyMilliseconds;

    private OnTick refreshCallback;
    private OnDone expiredCallback;
    private Instant startedAt;
    private Instant deadline;
    private Duration remainingTime;


    public SwingWorkerTimeAdapter() {
        this(DEFAULT_FREQUENCY_MS);
    }

    public SwingWorkerTimeAdapter(int tickFrequencyMilliseconds) {
        this.tickFrequencyMilliseconds = tickFrequencyMilliseconds;
    }


    @Override
    public void runFor(Session session, OnTick during, OnDone then) {
        refreshCallback = during;
        expiredCallback = then;
        runInBackground(session);
    }


    private void runInBackground(Session session) {
        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                startedAt = Instant.now();
                deadline = startedAt
                        .plus(session.graceDuration())
                        .plus(session.duration());

                do {
                    remainingTime = Duration.between(Instant.now(), deadline);
                    publish(remainingTime.toString());
                    Thread.sleep(tickFrequencyMilliseconds);
                } while (remainingTime.toSeconds() > 0);

                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                refreshCallback.accept(session, remainingTime);
            }

            @Override
            protected void done() {
                expiredCallback.accept(session);
            }
        };
        worker.execute();
    }

}
