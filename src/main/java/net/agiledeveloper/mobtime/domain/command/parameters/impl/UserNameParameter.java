package net.agiledeveloper.mobtime.domain.command.parameters.impl;

import net.agiledeveloper.mobtime.domain.command.parameters.ValueParameter;
import net.agiledeveloper.mobtime.domain.session.Username;

public class UserNameParameter extends ValueParameter<Username> {

    public UserNameParameter(Username value) {
        super("user-name", value);
    }

}
