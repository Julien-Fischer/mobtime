package net.agiledeveloper.mobtime.infra.swing.gui.popup;

import net.agiledeveloper.mobtime.infra.swing.gui.GUIEvent;
import net.agiledeveloper.mobtime.infra.swing.gui.Location;
import net.agiledeveloper.mobtime.infra.swing.theme.Theme;

import javax.swing.*;
import java.awt.*;

import static net.agiledeveloper.mobtime.infra.swing.theme.Theme.WINDOW_BG;

public class SessionEndingPopup extends SessionPopup {

    private static final String DEFAULT_TITLE = "Time's up";


    public SessionEndingPopup() {
        super(DEFAULT_TITLE);
        init();
        pack();
        setLocation(Location.CENTER);
    }


    public void setLocation(Location location) {
        var sizeOfCurrentScreen = getSizeOfCurrentScreen(this);
        Point point = location.relativeTo(getSize(), sizeOfCurrentScreen);
        setLocation(point);
    }


    private static Dimension getSizeOfCurrentScreen(JFrame frame) {
        GraphicsConfiguration gc = frame.getGraphicsConfiguration();
        Rectangle screenBounds = gc.getBounds();
        return screenBounds.getSize();
    }

    private void init() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        setLayout(new BorderLayout());
        setResizable(false);
        add(createMainContainer(), BorderLayout.CENTER);
    }

    private Container createMainContainer() {
        var textContainer = createTextContainer();
        var buttonsContainer = createButtonsContainer();
        return createContentPane(textContainer, buttonsContainer);
    }

    private static JPanel createContentPane(JPanel textContainer, JPanel buttonsContainer) {
        var contentPane = new JPanel(new BorderLayout());
        contentPane.setLayout(new BorderLayout());
        contentPane.add(textContainer, BorderLayout.CENTER);
        contentPane.add(buttonsContainer, BorderLayout.SOUTH);
        contentPane.setBackground(WINDOW_BG);
        return contentPane;
    }

    private JPanel createTextContainer() {
        var textContainer = new GlassPanel(new BorderLayout());
        textContainer.add(createTimesUpLabel(), BorderLayout.NORTH);
        textContainer.add(createSessionInfoLabel(), BorderLayout.CENTER);
        return textContainer;
    }

    private static JLabel createTimesUpLabel() {
        var timesUpLabel = new JLabel("Time's up!");
        timesUpLabel.setForeground(Theme.MESSAGE_OK);
        timesUpLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        timesUpLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
        return timesUpLabel;
    }

    private JLabel createSessionInfoLabel() {
        var sessionInfoLabel = new JLabel("Time to pass the keyboard");
        sessionInfoLabel.setForeground(Theme.MESSAGE_NEUTRAL_EMPHASIS);
        sessionInfoLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        sessionInfoLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 0));
        blink(sessionInfoLabel);
        return sessionInfoLabel;
    }

    private JPanel createButtonsContainer() {
        var keepDriving = new MobButton("Keep Driving", GUIEvent.DEROGATE)
                .onClick(this::onButtonClick);
        var endSession = new MobButton("End Session", GUIEvent.DONE)
                .onClick(this::onButtonClick);
        var passKeyboard = new MobButton("Pass Keyboard", GUIEvent.NEXT)
                .onClick(this::onButtonClick);

        var left = new GlassPanel()
                .add(keepDriving);
        var right = new GlassPanel()
                .add(endSession)
                .add(passKeyboard);
        return new GlassPanel()
                .add(left)
                .add(right);
    }

}
