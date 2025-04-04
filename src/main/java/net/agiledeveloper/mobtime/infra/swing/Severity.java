package net.agiledeveloper.mobtime.infra.swing;

import java.awt.*;

import static net.agiledeveloper.mobtime.infra.swing.theme.Theme.*;

public enum Severity {

    SUCCESS  (MESSAGE_OK),
    INFO     (MESSAGE_INFO),
    CRITICAL (MESSAGE_WARN);

    private final Color color;

    Severity(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

}
