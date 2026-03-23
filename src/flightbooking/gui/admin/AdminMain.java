package flightbooking.gui.admin;

import javax.swing.*;


public class AdminMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new FrmDangNhapNhanVien().setVisible(true);
        });
    }
}
