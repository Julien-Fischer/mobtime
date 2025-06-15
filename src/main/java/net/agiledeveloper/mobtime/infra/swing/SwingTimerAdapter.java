package net.agiledeveloper.mobtime.infra.swing;

import net.agiledeveloper.App;
import net.agiledeveloper.mobtime.domain.ports.api.OnDone;
import net.agiledeveloper.mobtime.domain.ports.api.OnTick;
import net.agiledeveloper.mobtime.domain.ports.spi.TimerPort;
import net.agiledeveloper.mobtime.domain.session.Session;

import javax.swing.*;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.time.Duration.ofMillis;

public class SwingTimerAdapter implements TimerPort {

    private static final Duration DEFAULT_FREQUENCY = ofMillis(1000);

    private final Duration tickFrequency;

    private OnTick refreshCallback;
    private OnDone expiredCallback;


    public SwingTimerAdapter() {
        this(DEFAULT_FREQUENCY);
    }

    public SwingTimerAdapter(Duration tickFrequency) {
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


        public SwingClock(Session session) {
            this.session = session;
        }


        @Override
        protected Void doInBackground() throws TimerException {
            session.start();

            while (shouldRun()) {
                try {
                    publish();
                    synchronized (this) {
                        wait(tickFrequency.toMillis());
                    }
                    if (session.isOver()) {
                        stop();
                    }
                } catch (Exception exception) {
                    App.logger.err(exception);
                    Thread.currentThread().interrupt();
                    throw new TimerException("Timer interrupted by runtime exception: " + exception);
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

        private boolean shouldRun() {
            return running.get();
        }

        @Override
        protected void process(List<String> chunks) {
            refreshCallback.accept(session, session.remainingTime());
        }

        @Override
        protected void done() {
            expiredCallback.accept(session);
        }
    }

    private static class TimerException extends RuntimeException {
        public TimerException(String message) {
            super(message);
        }
    }

}
