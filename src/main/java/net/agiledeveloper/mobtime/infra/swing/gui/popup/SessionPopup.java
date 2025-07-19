package net.agiledeveloper.mobtime.infra.swing.gui.popup;

import net.agiledeveloper.mobtime.infra.swing.gui.GUIEvent;
import net.agiledeveloper.mobtime.infra.swing.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

import static net.agiledeveloper.mobtime.infra.swing.theme.Theme.BUTTON_BG;
import static net.agiledeveloper.mobtime.infra.swing.theme.Theme.BUTTON_FG;

public abstract class SessionPopup extends JFrame {

    private Consumer<GUIEvent> onClickCallback;


    protected SessionPopup(String title) throws HeadlessException {
        super(title);
    }


    public void onClick(Consumer<GUIEvent> onClickCallback) {
        this.onClickCallback = onClickCallback;
    }

    protected void onButtonClick(GUIEvent event) {
        if (onClickCallback != null) {
            onClickCallback.accept(event);
        }
        dispose();
    }

    protected static void blink(JLabel label) {
        var blinkFrequency = 500;
        Color defaultColor = label.getForeground();
        Color blinkColor = Theme.MESSAGE_WARN;
        var timer = new Timer(blinkFrequency, e -> {
            if (label.getForeground().equals(defaultColor)) {
                label.setForeground(blinkColor);
            } else {
                label.setForeground(defaultColor);
            }
        });
        timer.start();
    }

    public static class GlassPanel extends JPanel {

        public GlassPanel() {
            this(new FlowLayout(FlowLayout.LEFT));
        }

        public GlassPanel(LayoutManager layout) {
            super(layout);
            this.setOpaque(false);
        }


        @Override
        public GlassPanel add(Component component) {
            super.add(component);
            return this;
        }

    }

    public static class GlassButton extends JButton {

        public GlassButton(String label) {
            super(label);
            setBackground(BUTTON_BG);
            setForeground(BUTTON_FG);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }

        public GlassButton onClick(ActionListener listener) {
            addActionListener(listener);
            return this;
        }

    }

    public static class MobButton extends GlassButton {

        private Consumer<GUIEvent> onClickCallback;


        public MobButton(String label, GUIEvent event) {
            super(label);
            addActionListener(e -> dispatch(event));
            setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        }


        public MobButton onClick(Consumer<GUIEvent> callback) {
            this.onClickCallback = callback;
            return this;
        }

        private void dispatch(GUIEvent event) {
            if (onClickCallback != null) {
                onClickCallback.accept(event);
            }
        }

    }

}
