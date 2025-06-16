package net.agiledeveloper.mobtime.infra.notification;

import net.agiledeveloper.mobtime.domain.notification.Notification;
import net.agiledeveloper.mobtime.domain.notification.session.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static net.agiledeveloper.mobtime.test.builders.SessionBuilder.aSession;
import static net.agiledeveloper.mobtime.test.lib.MockAssertion.expectThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class CompositeNotificationAdapterTest {

    private NotificationMock a;
    private NotificationMock b;

    private CompositeNotificationAdapter adapter;


    @BeforeEach
    void setUp() {
        a = new NotificationMock();
        b = new NotificationMock();
        adapter = new CompositeNotificationAdapter(a, b);
    }


    static Stream<Arguments> notifications() {
        return Stream.of(
                arguments(aSessionStartNotification(),    SessionStartNotification.class),
                arguments(aSessionShutdownNotification(), SessionShutdownNotification.class),
                arguments(aSessionCloseNotification(),    SessionCloseNotification.class),
                arguments(aSessionRefreshNotification(),  SessionRefreshNotification.class),
                arguments(aSessionOpenNotification(),     SessionOpenNotification.class)
        );
    }


    @ParameterizedTest
    @MethodSource("notifications")
    void all_notification_ports_are_notified(Notification notification, Class<? extends Notification> type) {
        adapter.send(notification);

        expectThat(a).wasCalledOnce();
        expectThat(b).wasCalledOnce();
        assertThat(a.getCallCount(type)).isEqualTo(1);
        assertThat(b.getCallCount(type)).isEqualTo(1);
    }


    private static SessionRefreshNotification aSessionRefreshNotification() {
        return new SessionRefreshNotification(aSession().started().build());
    }

    private static SessionOpenNotification aSessionOpenNotification() {
        return new SessionOpenNotification(aSession().build(), "a message", "a value");
    }

    private static SessionCloseNotification aSessionCloseNotification() {
        return new SessionCloseNotification(aSession().started().build(), "a message", "a value");
    }

    private static SessionShutdownNotification aSessionShutdownNotification() {
        return new SessionShutdownNotification(aSession().started().build(), "a message", "a value");
    }

    private static SessionStartNotification aSessionStartNotification() {
        return new SessionStartNotification(aSession().started().build(), "a message", "a value");
    }

}
