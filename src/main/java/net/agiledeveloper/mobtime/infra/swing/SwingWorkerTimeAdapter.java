package net.agiledeveloper.mobtime.infra.swing;

import net.agiledeveloper.mobtime.domain.ports.api.OnDone;
import net.agiledeveloper.mobtime.domain.ports.api.OnTick;
import net.agiledeveloper.mobtime.domain.ports.spi.TimerPort;
import net.agiledeveloper.mobtime.domain.session.Session;

import javax.swing.*;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.time.Duration.ofMillis;

public class SwingWorkerTimeAdapter implements TimerPort {

    private static final Duration DEFAULT_FREQUENCY = ofMillis(1000);

    private final Duration tickFrequency;

    private OnTick refreshCallback;
    private OnDone expiredCallback;


    public SwingWorkerTimeAdapter() {
        this(DEFAULT_FREQUENCY);
    }

    public SwingWorkerTimeAdapter(Duration tickFrequency) {
        this.tickFrequency = tickFrequency;
    }


    @Override
    public void runFor(Session session, OnTick during, OnDone then) {
        refreshCallback = during;
        expiredCallback = then;
        runInBackground(session);
    }


    private void runInBackground(Session session) {
        var worker = new SwingClock(session);
        worker.execute();
    }


    private class SwingClock extends SwingWorker<Void, String> {

        private final Session session;
        private final AtomicBoolean running = new AtomicBoolean(true);
        private Duration remainingTime;


        public SwingClock(Session session) {
            this.session = session;
        }


        @Override
        protected Void doInBackground() throws Exception {
            Instant startedAt = Instant.now();
            Instant deadline = startedAt
                    .plus(session.graceDuration())
                    .plus(session.initialDuration());

            while (running.get()) {
                remainingTime = Duration.between(Instant.now(), deadline);
                publish(remainingTime.toString());
                synchronized (this) {
                    wait(tickFrequency.toMillis());
                }
                if (remainingTime.toSeconds() <= Duration.ZERO.toSeconds()) {
                    stop();
                }
            }

            return null;
        }

        private void stop() {
            running.set(false);
            synchronized (this) {
                notifyAll();
            }
            cancel(true);
        }

        @Override
        protected void process(List<String> chunks) {
            refreshCallback.accept(session, remainingTime);
        }

        @Override
        protected void done() {
            expiredCallback.accept(session);
        }
    }

}
