package net.agiledeveloper.mobtime.infra.swing;

import net.agiledeveloper.mobtime.domain.notification.Notification;
import net.agiledeveloper.mobtime.domain.notification.session.*;
import net.agiledeveloper.mobtime.domain.ports.api.SessionPort;
import net.agiledeveloper.mobtime.domain.ports.spi.NotificationPort;
import net.agiledeveloper.mobtime.infra.InfraException;
import net.agiledeveloper.mobtime.infra.roaming.Roaming;
import net.agiledeveloper.mobtime.infra.swing.gui.Coordinate;
import net.agiledeveloper.mobtime.infra.swing.gui.GUIEvent;
import net.agiledeveloper.mobtime.infra.swing.gui.Location;
import net.agiledeveloper.mobtime.infra.swing.gui.SwingPopup;
import net.agiledeveloper.mobtime.infra.swing.theme.Theme;
import net.agiledeveloper.mobtime.utils.App;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static net.agiledeveloper.mobtime.infra.swing.theme.Theme.MESSAGE_INFO;
import static net.agiledeveloper.mobtime.infra.swing.theme.Theme.MESSAGE_OK;

public class SwingNotificationAdapter implements NotificationPort {

    private final SessionPort mobPort;
    private final Roaming roaming;

    private SwingPopup currentFrame;
    private Color currentColor;
    private final boolean shouldMinimize;
    private final boolean autosave;
    private final Location location;

    private boolean awaitingKillSignal = false;
    private Duration remainingTime;


    public SwingNotificationAdapter(
            SessionPort mobPort,
            Roaming roaming,
            boolean shouldMinimize,
            boolean autosave,
            Location location
    ) {
        this.mobPort = mobPort;
        this.roaming = roaming;
        this.shouldMinimize = shouldMinimize;
        this.autosave = autosave;
        this.location = location;
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
        currentColor = MESSAGE_INFO;
        showPopup(notification);
        notifySessionStart(notification);
    }

    private void handleStartNotification(Notification notification) {
        currentColor = MESSAGE_OK;
        displayMessage(notification);
    }

    private void handleRefreshNotification(SessionRefreshNotification notification) {
        if (awaitingKillSignal) {
            App.logger.log("App will shutdown soon...");
            return;
        }
        var color = notification.hasLittleTimeLeft() ? MESSAGE_INFO : MESSAGE_OK;
        currentFrame.updateProgress(notification, color);
        remainingTime = notification.remainingTime();
    }

    private void handleCloseNotification(Notification notification) {
        currentColor = Theme.MESSAGE_WARN;
        notifySessionEnd(notification);
    }

    private void handleShutdownNotification(Notification notification) {
        currentColor = Theme.MESSAGE_WARN;
        shutdown(notification);
    }

    private void showPopup(Notification notification) {
        createPopupFor(notification);
    }

    private void notifySessionEnd(Notification notification) {
        SwingUtilities.invokeLater(() -> {
            currentFrame.dispose();
            currentFrame = createPopup(notification);
            currentFrame.setLabelForeground(currentColor);
            currentFrame.setVisible(true);
            currentFrame.pack();
            currentFrame.onClick(this::onGuiEvent);
            currentFrame.setPosition(location);
        });
    }

    private void notifySessionStart(Notification notification) {
        SwingUtilities.invokeLater(() -> {
            if (currentFrame == null) {
                createPopupFor(notification);
            } else {
                currentFrame.setMessage(notification, currentColor);
            }
        });
    }

    private void displayMessage(Notification notification) {
        displayMessage(notification, currentColor);
    }

    private void displayMessage(Notification notification, Color color) {
        SwingUtilities.invokeLater(() ->
                currentFrame.setMessage(notification, color)
        );
    }

    private void shutdown(Notification notification) {
        displayMessage(notification);
        currentFrame.setButtonsVisible(false);
    }

    private void createPopupFor(Notification notification) {
        currentFrame = createPopup(notification);
        currentFrame.onClick(this::onGuiEvent);
        currentFrame.setVisible(true);
        currentFrame.setPosition(location);
        currentFrame.setFocusableWindowState(false);
    }

    private SwingPopup createPopup(Notification notification) {
        Optional<Coordinate> offset = roaming.getCoordinate();
        return autosave && offset.isPresent() ?
                new SwingPopup(notification, shouldMinimize, offset.get()) :
                new SwingPopup(notification, shouldMinimize);
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
        SwingUtilities.invokeLater(() ->
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
        if (autosave && location != null) {
            roaming.setCoordinate(location);
        }
        if (roaming.isDetached()) {
            roaming.setActivityRemaining(remainingTime);
        }
    }

}
