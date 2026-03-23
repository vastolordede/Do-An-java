package flightbooking.gui.user;

import flightbooking.dto.KhachHangDTO;
import flightbooking.gui.user.common.AppNavigator;
import flightbooking.gui.user.pnl.*;
import flightbooking.gui.user.theme.UserTheme;

import javax.swing.*;
import java.awt.*;

public class FrmNguoiDung extends JFrame {

    private final AppNavigator nav = new AppNavigator();
    private final KhachHangDTO currentUser;

    public FrmNguoiDung(KhachHangDTO user) {
        this.currentUser = user;

        setTitle("FlightBooking - Đặt vé");
        setSize(1150, 760);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel top = buildTopBar();
        JPanel content = nav.getRoot();

        // ===== REGISTER PANEL =====
        nav.register("TIM_CHUYEN", new PnlTimChuyenBay(nav));
        nav.register("KQ_CHUYEN", new PnlKetQuaChuyenBay(nav));
        nav.register("CHON_GHE", new PnlChonGhe(nav));
        nav.register("HANH_KHACH", new PnlThongTinHanhKhach(nav));
        nav.register("XAC_NHAN", new PnlXacNhanDatVe(nav));

        nav.show("TIM_CHUYEN");

        JPanel root = new JPanel(new BorderLayout());

        root.add(top, BorderLayout.NORTH);
        root.add(content, BorderLayout.CENTER);

        // 🔥 ADD PANEL USER BÊN PHẢI
        root.add(new PnlUserInfo(currentUser), BorderLayout.EAST);

        setContentPane(root);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBorder(BorderFactory.createEmptyBorder(12,16,12,16));
        bar.setBackground(UserTheme.CARD);

        JLabel brand = new JLabel("FlightBooking");
        brand.setForeground(UserTheme.PRIMARY);
        brand.setFont(brand.getFont().deriveFont(Font.BOLD, 18f));

        JLabel sub = new JLabel("Tìm chuyến • Chọn ghế • Thanh toán");
        sub.setForeground(UserTheme.ACCENT);

        JPanel left = new JPanel(new GridLayout(2,1));
        left.setOpaque(false);
        left.add(brand);
        left.add(sub);

        bar.add(left, BorderLayout.WEST);
        return bar;
    }
}