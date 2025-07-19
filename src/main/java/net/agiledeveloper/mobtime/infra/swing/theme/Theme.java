package net.agiledeveloper.mobtime.infra.swing.theme;

import net.agiledeveloper.mobtime.domain.notification.Severity;

import java.awt.*;

import static net.agiledeveloper.mobtime.infra.swing.theme.Palette.*;

public class Theme {

    public static final Color WINDOW_BG       = DARKER_GRAY;
    public static final Color BUTTON_BG       = DARK_GRAY;
    public static final Color BUTTON_FG       = LIGHTGRAY;

    public static final Color MESSAGE_OK        = GREEN;
    public static final Color MESSAGE_INFO      = YELLOW;
    public static final Color MESSAGE_WARN      = MAGENTA;
    public static final Color MESSAGE_NEUTRAL   = GRAY;
    public static final Color MESSAGE_NEUTRAL_EMPHASIS = WHITE;

    public static Color of(Severity severity) {
        return switch (severity) {
            case SUCCESS  -> MESSAGE_OK;
            case INFO     -> MESSAGE_INFO;
            case CRITICAL -> MESSAGE_WARN;
        };
    }

    private Theme() {}

}
