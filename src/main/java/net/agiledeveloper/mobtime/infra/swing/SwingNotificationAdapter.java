package net.agiledeveloper.mobtime.infra.swing;

import net.agiledeveloper.App;
import net.agiledeveloper.mobtime.domain.command.UIOptionSet;
import net.agiledeveloper.mobtime.domain.notification.Notification;
import net.agiledeveloper.mobtime.domain.notification.session.*;
import net.agiledeveloper.mobtime.domain.ports.api.SessionPort;
import net.agiledeveloper.mobtime.domain.ports.spi.NotificationPort;
import net.agiledeveloper.mobtime.domain.ports.spi.RoamingPort;
import net.agiledeveloper.mobtime.domain.session.Session;
import net.agiledeveloper.mobtime.infra.InfraException;
import net.agiledeveloper.mobtime.infra.swing.gui.Coordinate;
import net.agiledeveloper.mobtime.infra.swing.gui.GUIEvent;
import net.agiledeveloper.mobtime.infra.swing.gui.SwingPopup;
import net.agiledeveloper.mobtime.infra.swing.theme.Theme;

import javax.swing.*;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static javax.swing.SwingUtilities.invokeLater;

public class SwingNotificationAdapter implements NotificationPort {

    private final SessionPort mobPort;
    private final RoamingPort roaming;

    private SwingPopup currentFrame;
    private final UIOptionSet options;

    private boolean awaitingKillSignal = false;
    private Duration remainingTime = Session.DEFAULT_DURATION;


    public SwingNotificationAdapter(
            SessionPort mobPort,
            RoamingPort roaming,
            UIOptionSet options
    ) {
        this.mobPort = mobPort;
        this.roaming = roaming;
        this.options = options;
    }


    @Override
    public void send(Notification notification) {
        switch (notification) {
            case SessionOpenNotification ignored                -> handleOpenNotification(notification);
            case SessionStartNotification ignored               -> handleStartNotification(notification);
            case SessionRefreshNotification refreshNotification -> handleRefreshNotification(refreshNotification);
            case SessionCloseNotification ignored               -> handleCloseNotification(notification);
            case SessionShutdownNotification ignored            -> handleShutdownNotification(notification);
            case null, default -> throw new UnsupportedOperationException("Unknown notification type: " + notification);
        }
    }


    private void handleOpenNotification(Notification notification) {
        showPopup(notification);
        notifySessionStart(notification);
    }

    private void handleStartNotification(Notification notification) {
        displayMessage(notification);
    }

    private void handleRefreshNotification(SessionRefreshNotification notification) {
        if (awaitingKillSignal) {
            App.logger.log("App will shutdown soon...");
            return;
        }
        currentFrame.updateProgress(notification);
        remainingTime = notification.remainingTime();
    }

    private void handleCloseNotification(Notification notification) {
        notifySessionEnd(notification);
    }

    private void handleShutdownNotification(Notification notification) {
        shutdown(notification);
    }

    private void showPopup(Notification notification) {
        createPopupFor(notification);
    }

    private void notifySessionEnd(Notification notification) {
        invokeLater(() -> {
            currentFrame.dispose();
            currentFrame = createPopup(notification);
            currentFrame.setLabelForeground(Theme.of(notification.severity()));
            currentFrame.setVisible(true);
            currentFrame.pack();
            currentFrame.onClick(this::onGuiEvent);
            currentFrame.setPosition(options.getLocation());
        });
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

    private SwingPopup createPopup(Notification notification) {
        Optional<Coordinate> offset = roaming.getCoordinate();
        return options.shouldRelocate() && offset.isPresent() ?
                new SwingPopup(notification, options.shouldMinimize(), offset.get()) :
                new SwingPopup(notification, options.shouldMinimize());
    }

    private void onGuiEvent(GUIEvent event) {
        closeRoaming();
        switch (event) {
            case NEXT:
                executeInBackground(mobPort::next);
                break;
            case DONE:
                executeInBackground(mobPort::done);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported UI event: " + event);
        }
        awaitingKillSignal = true;
        var message = "Executing " + event.commandName() + "...";
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
