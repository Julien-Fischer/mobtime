package net.agiledeveloper.mobtime.domain.command.parameters.impl;

import net.agiledeveloper.mobtime.domain.command.parameters.ValueParameter;
import net.agiledeveloper.mobtime.infra.swing.gui.Location;

public class LocationParameter extends ValueParameter<Location> {

    public LocationParameter(Location location) {
        super("location", location);
    }

}
