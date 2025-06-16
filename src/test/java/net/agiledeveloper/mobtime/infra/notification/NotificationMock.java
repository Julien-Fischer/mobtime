package net.agiledeveloper.mobtime.infra.notification;

import net.agiledeveloper.mobtime.domain.notification.Notification;
import net.agiledeveloper.mobtime.domain.notification.session.*;
import net.agiledeveloper.mobtime.domain.ports.spi.NotificationPort;
import net.agiledeveloper.mobtime.test.lib.Mock;

import java.util.HashMap;
import java.util.Map;

public class NotificationMock extends Mock implements NotificationPort {

    private Map<Class<? extends Notification>, Integer> calls = new HashMap<>();


    @Override
    public void handleOpenNotification(SessionOpenNotification notification) {
        increment(notification);
    }

    @Override
    public void handleStartNotification(SessionStartNotification notification) {
        increment(notification);
    }

    @Override
    public void handleRefreshNotification(SessionRefreshNotification notification) {
        increment(notification);
    }

    @Override
    public void handleCloseNotification(SessionCloseNotification notification) {
        increment(notification);
    }

    @Override
    public void handleShutdownNotification(SessionShutdownNotification notification) {
        increment(notification);
    }


    public int getCallCount(Class<? extends Notification> notification) {
        return calls.get(notification);
    }

    @Override
    public boolean wasNeverCalled() {
        return calls.isEmpty();
    }

    @Override
    public boolean wasCalledOnce() {
        return calls.size() == 1;
    }

    @Override
    public boolean wasCalledNTimes(int n) {
        return calls.size() == n;
    }


    @Override
    protected int getCallCount() {
        return calls.size();
    }


    private void increment(Notification notification) {
        var type = notification.getClass();
        if (calls.containsKey(type)) {
            calls.put(type, calls.get(type) + 1);
        } else {
            calls.put(type, 1);
        }
    }

}
