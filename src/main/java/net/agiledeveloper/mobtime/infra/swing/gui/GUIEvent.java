package net.agiledeveloper.mobtime.infra.swing.gui;

public enum GUIEvent {

    NEXT     ("mobnext"),
    DONE     ("mobdone"),
    DEROGATE ("derogate");

    private final String commandName;


    GUIEvent(String commandName) {
        this.commandName = commandName;
    }


    public String commandName() {
        return commandName;
    }

}
