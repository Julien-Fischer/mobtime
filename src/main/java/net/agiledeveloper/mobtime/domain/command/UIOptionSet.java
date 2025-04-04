package net.agiledeveloper.mobtime.domain.command;

import net.agiledeveloper.mobtime.infra.cli.BashParameter;
import net.agiledeveloper.mobtime.infra.swing.gui.Location;
import net.agiledeveloper.mobtime.infra.swing.gui.SwingPopup;

import java.util.List;

public record UIOptionSet(List<BashParameter> parameters) {

    public boolean isDebugModeEnabled() {
        return hasFlag("debug");
    }

    public boolean shouldMinimize() {
        return hasFlag("mini");
    }

    public boolean shouldRelocate() {
        return hasFlag("relocate");
    }

    public Location getLocation() {
        for (BashParameter bashParameter : parameters) {
            if (bashParameter.hasName("location") && bashParameter.hasValue()) {
                return Location.of(bashParameter.value());
            }
        }
        return SwingPopup.DEFAULT_LOCATION;
    }


    private boolean hasFlag(String flag) {
        return parameters.stream()
                .anyMatch(param -> param.hasName(flag));
    }

}
