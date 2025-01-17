package net.agiledeveloper.mobtime.domain.command.parameters.impl;

import net.agiledeveloper.mobtime.domain.command.parameters.ValueParameter;
import net.agiledeveloper.mobtime.domain.session.FocusMode;

public class FocusModeParameter extends ValueParameter<FocusMode> {

    public FocusModeParameter(FocusMode focusMode) {
        super("focus", focusMode);
    }

}
