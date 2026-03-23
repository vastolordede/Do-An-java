package flightbooking.gui.admin;

import flightbooking.bus.AccountBUS;
import flightbooking.dao.NhomQuyenDAO;
import flightbooking.dto.AccountDTO;
import flightbooking.util.SessionContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FrmDangNhapNhanVien extends JFrame {

    private final JTextField txtTenDangNhap = new RoundedTextField(20);
    private final JPasswordField txtMatKhau = new RoundedPasswordField(20);

    private final AccountBUS accountBus = new AccountBUS();

    public FrmDangNhapNhanVien() {

        setTitle("Đăng nhập hệ thống quản trị");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel pnlBackground = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ImageIcon img = new ImageIcon(getClass().getResource("/resources/images/bien.jpg"));
                    g.drawImage(img.getImage(), 0, 0, getWidth(), getHeight(), null);
                } catch (Exception e) {
                    g.setColor(new Color(245,245,245));
                    g.fillRect(0,0,getWidth(),getHeight());
                }
            }
        };

        JPanel whiteBox = new JPanel();
        whiteBox.setPreferredSize(new Dimension(400, 400));
        whiteBox.setBackground(Color.WHITE);
        whiteBox.setLayout(new BoxLayout(whiteBox, BoxLayout.Y_AXIS));
        whiteBox.setBorder(new EmptyBorder(40,40,40,40));

        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN TRỊ");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("FlightBooking Admin");
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSub.setForeground(Color.GRAY);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnLogin = new RoundedButton("ĐĂNG NHẬP");
        btnLogin.setMaximumSize(new Dimension(320,50));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(e -> doLogin());
        

        whiteBox.add(lblTitle);
        whiteBox.add(Box.createVerticalStrut(5));
        whiteBox.add(lblSub);
        whiteBox.add(Box.createVerticalStrut(30));

        whiteBox.add(createInput("TÊN ĐĂNG NHẬP", txtTenDangNhap));
        whiteBox.add(Box.createVerticalStrut(15));
        whiteBox.add(createInput("MẬT KHẨU", txtMatKhau));

        whiteBox.add(Box.createVerticalStrut(30));
        whiteBox.add(btnLogin);

        pnlBackground.add(whiteBox);
        setContentPane(pnlBackground);
    }

    private JPanel createInput(String label, JComponent field) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setOpaque(false);
        wrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        wrapper.setMaximumSize(new Dimension(320, 65));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(new Color(120,120,120));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        field.setMaximumSize(new Dimension(320,45));

        wrapper.add(lbl);
        wrapper.add(Box.createVerticalStrut(5));
        wrapper.add(field);

        return wrapper;
    }

    private void doLogin() {

        String u = txtTenDangNhap.getText().trim();
        String p = new String(txtMatKhau.getPassword());

        String validateMsg = accountBus.validateLogin(u, p);
        if (validateMsg != null) {
            JOptionPane.showMessageDialog(this, validateMsg);
            return;
        }

        try {

            AccountDTO account = accountBus.checkLogin(u, p);

            if (account != null) {

                int nhomId = account.getNhomQuyenId();

                SessionContext.setAdminSession(
                        account.getTenDangNhap(),
                        nhomId,
                        account.getTaiKhoanId()
                );

                Set<Integer> perms = new HashSet<>();

                if (nhomId != 0) {
                    List<Integer> list = new NhomQuyenDAO().getPermissionsByNhom(nhomId);
                    perms.addAll(list);
                }

                SessionContext.setPermissions(perms);

                JOptionPane.showMessageDialog(this, "✓ Đăng nhập thành công!");
                dispose();
                new FrmQuanTri().setVisible(true);

            } else {
                JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!");
            }

        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // ===== UI CUSTOM =====

    class RoundedTextField extends JTextField {
        public RoundedTextField(int col) {
            super(col);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(10,15,10,15));
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(240,240,240));
            g2.fillRoundRect(0,0,getWidth(),getHeight(),20,20);
            super.paintComponent(g);
        }
    }

    class RoundedPasswordField extends JPasswordField {
        public RoundedPasswordField(int col) {
            super(col);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(10,15,10,15));
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(240,240,240));
            g2.fillRoundRect(0,0,getWidth(),getHeight(),20,20);
            super.paintComponent(g);
        }
    }

    class RoundedButton extends JButton {
        public RoundedButton(String text) {
            super(text);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setForeground(Color.WHITE);
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(210,32,38));
            g2.fillRoundRect(0,0,getWidth(),getHeight(),30,30);
            super.paintComponent(g);
        }
    }
    
}