package net.agiledeveloper.mobtime.infra.swing;

import net.agiledeveloper.mobtime.domain.notification.Notification;
import net.agiledeveloper.mobtime.domain.notification.session.*;
import net.agiledeveloper.mobtime.domain.ports.api.SessionPort;
import net.agiledeveloper.mobtime.domain.ports.spi.NotificationPort;
import net.agiledeveloper.mobtime.infra.InfraException;
import net.agiledeveloper.mobtime.infra.swing.gui.GUIEvent;
import net.agiledeveloper.mobtime.infra.swing.gui.Location;
import net.agiledeveloper.mobtime.infra.swing.gui.SwingPopup;
import net.agiledeveloper.mobtime.infra.swing.theme.Theme;
import net.agiledeveloper.mobtime.utils.AppLogger;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutionException;

import static net.agiledeveloper.mobtime.infra.swing.theme.Theme.MESSAGE_INFO;
import static net.agiledeveloper.mobtime.infra.swing.theme.Theme.MESSAGE_OK;

public class SwingNotificationAdapter implements NotificationPort {

    private final SessionPort mobPort;

    private SwingPopup currentFrame;
    private Color currentColor;
    private final boolean shouldMinimize;
    private final Location location;

    private boolean awaitingKillSignal = false;


    public SwingNotificationAdapter(SessionPort mobPort, boolean shouldMinimize, Location location) {
        this.mobPort = mobPort;
        this.shouldMinimize = shouldMinimize;
        this.location = location;
    }


    @Override
    public void send(Notification notification) {
        if (notification instanceof SessionOpenNotification) {
            handleOpenNotification(notification);
        } else if (notification instanceof SessionStartNotification) {
            handleStartNotification(notification);
        } else if (notification instanceof SessionRefreshNotification refreshNotification) {
            handleRefreshNotification(refreshNotification);
        } else if (notification instanceof SessionCloseNotification) {
            handleCloseNotification(notification);
        } else if (notification instanceof SessionShutdownNotification) {
            handleShutdownNotification(notification);
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
            AppLogger.log("App will shutdown soon...");
            return;
        }
        var color = notification.hasLittleTimeLeft() ? MESSAGE_INFO : MESSAGE_OK;
        currentFrame.updateProgress(notification, color);
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
            currentFrame = new SwingPopup(notification, shouldMinimize);
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
        currentFrame = new SwingPopup(notification, shouldMinimize);
        currentFrame.onClick(this::onGuiEvent);
        currentFrame.setVisible(true);
        currentFrame.setPosition(location);
        currentFrame.setFocusableWindowState(false);
    }

    private void onGuiEvent(GUIEvent event) {
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

}
