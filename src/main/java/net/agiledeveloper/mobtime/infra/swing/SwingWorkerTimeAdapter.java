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

public class SwingWorkerTimeAdapter implements TimerPort {

    private static final int DEFAULT_FREQUENCY_MS = 1000;

    private final int tickFrequencyMilliseconds;

    private OnTick refreshCallback;
    private OnDone expiredCallback;


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
                    .plus(session.duration());

            while (running.get()) {
                remainingTime = Duration.between(Instant.now(), deadline);
                publish(remainingTime.toString());
                synchronized (this) {
                    wait(tickFrequencyMilliseconds);
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
