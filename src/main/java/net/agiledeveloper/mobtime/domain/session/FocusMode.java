package net.agiledeveloper.mobtime.domain.session;

import static net.agiledeveloper.mobtime.utils.EnumUtils.normalize;

public enum FocusMode {

    NORMAL,
    CHILL,
    ZEN;


    public static FocusMode of(String name) {
        try {
            return FocusMode.valueOf(normalize(name));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid focus mode: " + name, exception);
        }
    }

}
