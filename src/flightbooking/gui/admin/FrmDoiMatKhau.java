package flightbooking.gui.admin;

import flightbooking.bus.AccountBUS;
import flightbooking.util.SessionContext;

import javax.swing.*;
import java.awt.*;

public class FrmDoiMatKhau extends JFrame {

    private JPasswordField txtOld = new JPasswordField();
    private JPasswordField txtNew = new JPasswordField();
    private JPasswordField txtConfirm = new JPasswordField();

    private final AccountBUS bus = new AccountBUS();

    public FrmDoiMatKhau() {

        setTitle("Đổi mật khẩu");
        setSize(350, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel p = new JPanel(new GridLayout(4,2,10,10));
        p.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        p.add(new JLabel("Mật khẩu cũ"));
        p.add(txtOld);

        p.add(new JLabel("Mật khẩu mới"));
        p.add(txtNew);

        p.add(new JLabel("Xác nhận"));
        p.add(txtConfirm);

        JButton btn = new JButton("Đổi mật khẩu");
        btn.addActionListener(e -> changePass());

        add(p, BorderLayout.CENTER);
        add(btn, BorderLayout.SOUTH);
    }

    private void changePass() {

        int id = SessionContext.getAdminTaiKhoanId();

        String oldPass = new String(txtOld.getPassword());
        String newPass = new String(txtNew.getPassword());
        String confirm = new String(txtConfirm.getPassword());

        if (!newPass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp");
            return;
        }

        String msg = bus.changePassword(id, oldPass, newPass);

        if (msg == null) {
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, msg);
        }
    }
}