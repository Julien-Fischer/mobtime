package net.agiledeveloper.mobtime.infra.notification;

import net.agiledeveloper.App;
import net.agiledeveloper.mobtime.domain.command.UIOptionSet;
import net.agiledeveloper.mobtime.domain.notification.Notification;
import net.agiledeveloper.mobtime.domain.notification.session.*;
import net.agiledeveloper.mobtime.domain.ports.spi.MobPort;
import net.agiledeveloper.mobtime.domain.ports.spi.NotificationPort;
import net.agiledeveloper.mobtime.domain.ports.spi.SessionStorage;
import net.agiledeveloper.mobtime.domain.session.Session;
import net.agiledeveloper.mobtime.infra.InfraException;
import net.agiledeveloper.mobtime.infra.swing.gui.Coordinate;
import net.agiledeveloper.mobtime.infra.swing.gui.GUIEvent;
import net.agiledeveloper.mobtime.infra.swing.gui.popup.SessionEndingPopup;
import net.agiledeveloper.mobtime.infra.swing.gui.popup.SessionRunningPopup;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class SwingNotificationAdapter implements NotificationPort {

    private final MobPort mobPort;
    private final SessionStorage roaming;

    private SessionRunningPopup currentFrame;
    private final UIOptionSet options;

    private boolean awaitingKillSignal = false;
    private Duration remainingTime = Session.DEFAULT_DURATION;


    public SwingNotificationAdapter(
            MobPort mobPort, SessionStorage roaming,
            UIOptionSet options
    ) {
        this.mobPort = mobPort;
        this.roaming = roaming;
        this.options = options;
    }


    @Override
    public void handleOpenNotification(SessionOpenNotification notification) {
        showPopup(notification);
        notifySessionStart(notification);
    }

    @Override
    public void handleStartNotification(SessionStartNotification notification) {
        displayMessage(notification);
    }

    @Override
    public void handleRefreshNotification(SessionRefreshNotification notification) {
        if (awaitingKillSignal) {
            App.logger.log("App will shutdown soon...");
            return;
        }
        currentFrame.updateProgress(notification);
        remainingTime = notification.remainingTime();
    }

    @Override
    public void handleCloseNotification(SessionCloseNotification notification) {
        notifySessionEnd(notification);
    }

    @Override
    public void handleShutdownNotification(SessionShutdownNotification notification) {
        shutdown(notification);
    }


    private void showPopup(Notification notification) {
        createPopupFor(notification);
    }

    private void notifySessionEnd(Notification notification) {
        invokeLater(() -> {
            GraphicsConfiguration gc = currentFrame.getGraphicsConfiguration();
            Rectangle screenBounds = gc.getBounds();
            var sessionEndFrame = createSessionEndPopup(notification);
            centerPopup(sessionEndFrame, screenBounds);
            sessionEndFrame.setVisible(true);
        });
    }

    private SessionEndingPopup createSessionEndPopup(Notification ignored) {
        var sessionEndFrame = new SessionEndingPopup();
        sessionEndFrame.onClick(this::onGuiEvent);
        sessionEndFrame.pack();
        sessionEndFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        return sessionEndFrame;
    }

    private static void centerPopup(SessionEndingPopup sessionEndFrame, Rectangle screenBounds) {
        var comfortOffset = 100;
        var width = sessionEndFrame.getWidth();
        var height = sessionEndFrame.getHeight();
        sessionEndFrame.setBounds(
                screenBounds.x + (screenBounds.width - width) / 2,
                screenBounds.y + (screenBounds.height - height) / 2 - comfortOffset,
                width,
                height
        );
    }

    private void notifySessionStart(Notification notification) {
        invokeLater(() -> {
            if (currentFrame == null) {
                createPopupFor(notification);
            } else {
                currentFrame.setMessage(notification);
            }
        });
    }

    private void displayMessage(Notification notification) {
        invokeLater(() -> currentFrame.setMessage(notification));
    }

    private void shutdown(Notification notification) {
        displayMessage(notification);
        currentFrame.setButtonsVisible(false);
    }

    private void createPopupFor(Notification notification) {
        currentFrame = createPopup(notification);
        currentFrame.onClick(this::onGuiEvent);
        currentFrame.setVisible(true);
        currentFrame.setPosition(options.getLocation());
        currentFrame.setFocusableWindowState(false);
    }

    private SessionRunningPopup createPopup(Notification notification) {
        Optional<Coordinate> offset = roaming.getCoordinate();
        return options.shouldRelocate() && offset.isPresent() ?
                new SessionRunningPopup(notification, options.shouldMinimize(), offset.get()) :
                new SessionRunningPopup(notification, options.shouldMinimize());
    }

    private void onGuiEvent(GUIEvent event) {
        closeRoaming();
        switch (event) {
            case NEXT -> passKeyboard(event);
            case DONE -> endSession(event);
            case DEROGATE -> keepDriving();
            default -> throw new UnsupportedOperationException("Unsupported UI event: " + event);
        }
    }

    private void keepDriving() {
        currentFrame.derogate();
    }

    private void endSession(GUIEvent event) {
        executeInBackground(mobPort::done);
        awaitKillSignal(event);
    }

    private void passKeyboard(GUIEvent event) {
        executeInBackground(mobPort::next);
        awaitKillSignal(event);
    }

    private void awaitKillSignal(GUIEvent event) {
        awaitingKillSignal = true;
        var message = "Executing %s...".formatted(event.commandName());
        invokeLater(() ->
            handleShutdownNotification(new SessionShutdownNotification(null, message, ""))
        );
    }

    private void executeInBackground(Runnable task) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                task.run();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (ExecutionException cause) {
                    throw new InfraException(cause);
                } catch (InterruptedException cause) {
                    Thread.currentThread().interrupt();
                    throw new InfraException(cause);
                }
            }
        };
        worker.execute();
    }

    private void closeRoaming() {
        Coordinate location = currentFrame.getCurrentLocation();
        if (options.shouldRelocate() && location != null) {
            roaming.setCoordinate(location);
        }
        if (roaming.isPausable()) {
            roaming.setActivityRemaining(remainingTime);
        }
    }

}
