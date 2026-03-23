package flightbooking.gui.user;

import flightbooking.bus.KhachHangBUS;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FrmDangKy extends JFrame {

    private final JTextField txtHoTen = new RoundedTextField(20);
    private final JTextField txtSDT = new RoundedTextField(20);
    private final JTextField txtUser = new RoundedTextField(20);
    private final JPasswordField txtPass = new RoundedPasswordField(20);
    private final JPasswordField txtConfirm = new RoundedPasswordField(20);
    private final KhachHangBUS khachHangBus = new KhachHangBUS();

    public FrmDangKy() {
        setTitle("Đăng ký tài khoản mới");
        setSize(1000, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

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
        whiteBox.setPreferredSize(new Dimension(400, 620));
        whiteBox.setBackground(Color.WHITE);
        whiteBox.setLayout(new BoxLayout(whiteBox, BoxLayout.Y_AXIS));
        whiteBox.setBorder(new EmptyBorder(40,40,40,40));

        JLabel lblTitle = new JLabel("Đăng ký tài khoản mới");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Bắt đầu hành trình cá nhân hoá của bạn");
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSub.setForeground(Color.GRAY);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnReg = new RoundedButton("TẠO TÀI KHOẢN");
        btnReg.setMaximumSize(new Dimension(320,50));
        btnReg.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnReg.addActionListener(e -> handleRegister());

        whiteBox.add(lblTitle);
        whiteBox.add(Box.createVerticalStrut(5));
        whiteBox.add(lblSub);
        whiteBox.add(Box.createVerticalStrut(30));

        whiteBox.add(createInput("HỌ VÀ TÊN", txtHoTen));
        whiteBox.add(Box.createVerticalStrut(15));
        whiteBox.add(createInput("SỐ ĐIỆN THOẠI", txtSDT));
        whiteBox.add(Box.createVerticalStrut(15));
        whiteBox.add(createInput("TÊN ĐĂNG NHẬP", txtUser));
        whiteBox.add(Box.createVerticalStrut(15));
        whiteBox.add(createInput("MẬT KHẨU", txtPass));
        whiteBox.add(Box.createVerticalStrut(15));
        whiteBox.add(createInput("NHẬP LẠI MẬT KHẨU", txtConfirm));

        whiteBox.add(Box.createVerticalStrut(30));
        whiteBox.add(btnReg);
        

        JButton btnBack = new RoundedButton("QUAY LẠI ĐĂNG NHẬP");
        btnBack.setBackground(new Color(220,220,220));
        btnBack.setForeground(Color.WHITE);
        btnBack.setMaximumSize(new Dimension(320,50));
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnBack.addActionListener(e -> {
            new FrmDangNhapUser().setVisible(true);
            this.dispose();
        });

        whiteBox.add(Box.createVerticalStrut(30));
        
        whiteBox.add(Box.createVerticalStrut(15)); // khoảng cách
        whiteBox.add(btnBack);

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

    private void handleRegister() {
        String hoTen = txtHoTen.getText().trim();
        String sdt = txtSDT.getText().trim();
        String u = txtUser.getText().trim();
        String p = new String(txtPass.getPassword());
        String cp = new String(txtConfirm.getPassword());
if (hoTen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Họ tên không được để trống");
            return;
        }

        if (!hoTen.matches("^[a-zA-ZÀ-ỹ\\s]+$")) {
            JOptionPane.showMessageDialog(this, "Họ tên không hợp lệ");
            return;
        }

        if (sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không được để trống");
            return;
        }

        if (!sdt.matches("^0\\d{9}$")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải gồm 10 số và bắt đầu bằng 0");
            return;
        }

        if (u.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập không được để trống");
            return;
        }

        if (u.length() < 3) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập phải >= 3 ký tự");
            return;
        }

        if (p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mật khẩu không được để trống");
            return;
        }

        if (p.length() < 3) {
            JOptionPane.showMessageDialog(this, "Mật khẩu phải >= 3 ký tự");
            return;
        }

        if (!p.equals(cp)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu nhập lại không khớp");
            return;
        }

        if (khachHangBus.dangKy(this, hoTen, sdt, u, p, cp)) {
            this.dispose();
            new FrmDangNhapUser().setVisible(true);
        }
        if (khachHangBus.dangKy(this, hoTen, sdt, u, p, cp)) {
            this.dispose();
            new FrmDangNhapUser().setVisible(true);
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