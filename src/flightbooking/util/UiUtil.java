package flightbooking.util;

import javax.swing.*;
import java.awt.*;

public final class UiUtil {
    private UiUtil() {}

    public static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
    }

    public static void showInfo(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Thong bao", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm(Component parent, String msg) {
        int r = JOptionPane.showConfirmDialog(parent, msg, "Xac nhan", JOptionPane.YES_NO_OPTION);
        return r == JOptionPane.YES_OPTION;
    }
}
