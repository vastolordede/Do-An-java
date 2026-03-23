package flightbooking.gui.user;

import flightbooking.gui.user.theme.UserTheme;

import javax.swing.*;

public class UserMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserTheme.apply();
            new FrmDangNhapUser().setVisible(true);
        });
    }
}
