package flightbooking.gui.admin.common;

import javax.swing.*;
import java.awt.*;

public class AppNavigator {
    private final JPanel root = new JPanel(new CardLayout());

    public JPanel getRoot() { return root; }

    public void register(String name, JComponent comp) {
        root.add(comp, name);
    }

    public void show(String name) {
        ((CardLayout) root.getLayout()).show(root, name);
    }
}
