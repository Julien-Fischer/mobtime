package net.agiledeveloper.mobtime.infra.swing;

import net.agiledeveloper.App;
import net.agiledeveloper.mobtime.domain.ports.api.OnDone;
import net.agiledeveloper.mobtime.domain.ports.api.OnTick;
import net.agiledeveloper.mobtime.domain.ports.spi.TimerPort;
import net.agiledeveloper.mobtime.domain.session.Session;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.agiledeveloper.mobtime.infra.swing.SwingTimerAdapter.TickFrequency.ofMilliseconds;

public class SwingTimerAdapter implements TimerPort {

    private static final TickFrequency DEFAULT_FREQUENCY = ofMilliseconds(1000);

    private final TickFrequency frequency;

    private OnTick onTick;
    private OnDone onDone;


    public SwingTimerAdapter() {
        this(DEFAULT_FREQUENCY);
    }

    public SwingTimerAdapter(TickFrequency frequency) {
        this.frequency = frequency;
    }


    @Override
    public void runFor(Session session, OnTick during, OnDone then) {
        onTick = during;
        onDone = then;
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
                        wait(frequency.milliseconds());
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
            onTick.accept(session, session.remainingTime());
        }

        @Override
        protected void done() {
            onDone.accept(session);
        }
    }

    private static class TimerException extends RuntimeException {
        public TimerException(String message) {
            super(message);
        }
    }


    public record TickFrequency(long milliseconds) {

        public static TickFrequency ofMilliseconds(long milliseconds) {
            return new TickFrequency(milliseconds);
        }

    }

}
