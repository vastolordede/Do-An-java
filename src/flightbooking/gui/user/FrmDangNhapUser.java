package flightbooking.gui.user;

import flightbooking.bus.KhachHangBUS;
import flightbooking.util.SessionContext;
import flightbooking.dto.KhachHangDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FrmDangNhapUser extends JFrame {

    private final JTextField txtUsername = new RoundedTextField(20);
    private final JPasswordField txtPassword = new RoundedPasswordField(20);
    private final KhachHangBUS khachHangBus = new KhachHangBUS();

    public FrmDangNhapUser() {
        setTitle("FlightBooking - Đăng nhập");
        setSize(1000, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel pnlBackground = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ImageIcon img = new ImageIcon(getClass().getResource("/resources/images/bien.jpg"));
                    g.drawImage(img.getImage(), 0, 0, getWidth(), getHeight(), null);
                } catch (Exception e) {
                    g.setColor(new Color(245, 245, 245));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        JPanel whiteBox = new JPanel();
        whiteBox.setPreferredSize(new Dimension(400, 580));
        whiteBox.setBackground(Color.WHITE);
        whiteBox.setLayout(new BoxLayout(whiteBox, BoxLayout.Y_AXIS));
        whiteBox.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel lblTop = new JLabel("Vui lòng đăng nhập để tiếp tục");
        lblTop.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblTop.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnLogin = new RoundedButton("ĐĂNG NHẬP");
        btnLogin.setMaximumSize(new Dimension(320, 50));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(e -> handleLogin());

        JLabel lblOr = new JLabel("HOẶC");
        lblOr.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnReg = new RoundedButton("ĐĂNG KÝ TÀI KHOẢN MỚI");
        btnReg.setBackground(new Color(220,220,220));
        btnReg.setForeground(Color.BLACK);
        btnReg.setMaximumSize(new Dimension(320, 50));
        btnReg.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnReg.addActionListener(e -> {
            new FrmDangKy().setVisible(true);
            this.dispose();
        });

        whiteBox.add(lblTop);
        whiteBox.add(Box.createVerticalStrut(30));

        whiteBox.add(createInput("USERNAME", txtUsername));
        whiteBox.add(Box.createVerticalStrut(20));
        whiteBox.add(createInput("PASSWORD", txtPassword));

        whiteBox.add(Box.createVerticalStrut(40));
        whiteBox.add(btnLogin);
        whiteBox.add(Box.createVerticalStrut(20));
        whiteBox.add(lblOr);
        whiteBox.add(Box.createVerticalStrut(20));
        whiteBox.add(btnReg);

        pnlBackground.add(whiteBox);
        setContentPane(pnlBackground);
    }

    private JPanel createInput(String label, JComponent field) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setOpaque(false);
        wrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        wrapper.setMaximumSize(new Dimension(320, 70)); // FIX width

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(new Color(120,120,120));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        field.setMaximumSize(new Dimension(320, 45)); // FIX field size

        wrapper.add(lbl);
        wrapper.add(Box.createVerticalStrut(5));
        wrapper.add(field); // ❗ bỏ panel trung gian

        return wrapper;
    }

    private void handleLogin() {
        String u = txtUsername.getText().trim();
        String p = new String(txtPassword.getPassword());

        if (khachHangBus.dangNhap(this, u, p)) {
            KhachHangDTO user = khachHangBus.getByUsername(u);
            if (user != null) {
                SessionContext.setCurrentUser(user);
            }
            this.dispose();
            new FrmNguoiDung(user).setVisible(true);
        }
    }

    // ================= CUSTOM UI =================

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
            g2.setColor(getBackground().equals(new Color(220,220,220)) 
                ? new Color(220,220,220) 
                : new Color(210,32,38));
            g2.fillRoundRect(0,0,getWidth(),getHeight(),30,30);
            super.paintComponent(g);
        }
    }
}