package net.agiledeveloper.mobtime.domain.command.parameters.impl;

import net.agiledeveloper.mobtime.domain.command.parameters.ValueParameter;

public class UserNameParameter extends ValueParameter<String> {

    public UserNameParameter(String value) {
        super("user-name", value);
    }

}
