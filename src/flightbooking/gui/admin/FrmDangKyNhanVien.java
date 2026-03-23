package flightbooking.gui.admin;

import flightbooking.bus.AccountBUS;
import flightbooking.dto.AccountDTO;

import javax.swing.*;
import java.awt.*;

public class FrmDangKyNhanVien extends JFrame {

    private JTextField txtNhanVienId = new JTextField();
    private JTextField txtUsername = new JTextField();
    private JPasswordField txtPassword = new JPasswordField();
    private JPasswordField txtConfirm = new JPasswordField();

    private final AccountBUS accountBus = new AccountBUS();

    public FrmDangKyNhanVien() {

        setTitle("Đăng ký tài khoản nhân viên");
        setSize(400, 280);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        JPanel form = new JPanel(new GridLayout(4,2,10,10));

        form.add(new JLabel("ID Nhân viên"));
        form.add(txtNhanVienId);

        form.add(new JLabel("Tên đăng nhập"));
        form.add(txtUsername);

        form.add(new JLabel("Mật khẩu"));
        form.add(txtPassword);

        form.add(new JLabel("Nhập lại mật khẩu"));
        form.add(txtConfirm);

        JButton btnRegister = new JButton("Tạo tài khoản");
        btnRegister.addActionListener(e -> register());

        root.add(new JLabel("TẠO TÀI KHOẢN NHÂN VIÊN", JLabel.CENTER), BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        root.add(btnRegister, BorderLayout.SOUTH);
        

        setContentPane(root);
    }

    private void register() {

        try {

            int nhanVienId = Integer.parseInt(txtNhanVienId.getText().trim());
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword());
            String confirm = new String(txtConfirm.getPassword());

            AccountDTO acc = new AccountDTO();
            acc.setNhanVienId(nhanVienId);
            acc.setTenDangNhap(username);
            acc.setMatKhauMaHoa(password);
            acc.setDangHoatDong(true);

            // 🔥 KHÔNG GÁN QUYỀN
            acc.setNhomQuyenId(0);

            String msg = accountBus.registerAccount(acc, confirm);

            if (msg == null) {
                JOptionPane.showMessageDialog(this, "Tạo tài khoản thành công!\nChờ admin cấp quyền.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, msg);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID nhân viên không hợp lệ!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}