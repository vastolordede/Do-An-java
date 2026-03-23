package flightbooking.gui.admin;

import flightbooking.dao.NhomQuyenDAO;
import flightbooking.gui.admin.common.AppNavigator;
import flightbooking.gui.admin.pnl.PnlDatVeAdmin;
import flightbooking.gui.admin.pnl.PnlNhomQuyen;
import flightbooking.gui.admin.pnl.PnlQuanLyChuyenBay;
import flightbooking.gui.admin.pnl.PnlQuanLyHHK;
import flightbooking.gui.admin.pnl.PnlQuanLyMayBay;
import flightbooking.gui.admin.pnl.PnlQuanLyNhanVien;
import flightbooking.gui.admin.pnl.PnlQuanLySanBay;
import flightbooking.gui.admin.pnl.PnlQuanLyTuyenBay;
import flightbooking.util.ActionConstants;
import flightbooking.util.AuthUtil;
import flightbooking.util.Permission;
import flightbooking.util.SessionContext;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FrmQuanTri extends JFrame {

    private final AppNavigator nav = new AppNavigator();

    public FrmQuanTri() {
        setTitle("Quản trị - FlightBooking");
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        int nhomId = SessionContext.getAdminNhomQuyenId();
        boolean isAdmin = (nhomId == 1);

        // Không phải admin và không có quyền nào thì chặn
        if (!isAdmin && (nhomId == 0 || !SessionContext.hasAnyPermission())) {
            showEmptyScreen();
            return;
        }

        // Lấy danh sách action của nhóm quyền hiện tại
        // Admin thì có tất cả quyền
        List<Integer> actions;
        if (isAdmin) {
            actions = List.of(
                ActionConstants.THEM,
                ActionConstants.XOA,
                ActionConstants.SUA,
                ActionConstants.TAO_VE,
                ActionConstants.GIA_HANG_GHE,
                ActionConstants.SO_DO_GHE,
                ActionConstants.TAO_GHE,
                ActionConstants.LAM_MOI,
                ActionConstants.PHAN_QUYEN
            );
        } else {
            actions = new NhomQuyenDAO().getActionByNhom(nhomId);
        }

        // Tạo các panel và apply quyền
        PnlQuanLyMayBay pnlMayBay = new PnlQuanLyMayBay();
        PnlQuanLySanBay pnlSanBay = new PnlQuanLySanBay();
        PnlQuanLyTuyenBay pnlTuyenBay = new PnlQuanLyTuyenBay();
        PnlQuanLyChuyenBay pnlChuyenBay = new PnlQuanLyChuyenBay();
        PnlQuanLyHHK pnlHHK = new PnlQuanLyHHK();
        PnlDatVeAdmin pnlDatVe = new PnlDatVeAdmin();
        PnlQuanLyNhanVien pnlNhanVien = new PnlQuanLyNhanVien();
        PnlNhomQuyen pnlNhomQuyen = new PnlNhomQuyen();

// ✅ MỚI - có log để biết lỗi ở panel nào
try {
    pnlMayBay.applyPermissions(actions);
    System.out.println("✅ MayBay OK");
    pnlSanBay.applyPermissions(actions);
    System.out.println("✅ SanBay OK");
    pnlTuyenBay.applyPermissions(actions);
    System.out.println("✅ TuyenBay OK");
    pnlChuyenBay.applyPermissions(actions);
    System.out.println("✅ ChuyenBay OK");
    pnlHHK.applyPermissions(actions);
    System.out.println("✅ HHK OK");
    pnlDatVe.applyPermissions(actions);
    System.out.println("✅ DatVe OK");
    pnlNhanVien.applyPermissions(actions);
    System.out.println("✅ NhanVien OK");
    pnlNhomQuyen.applyPermissions(actions);
    System.out.println("✅ NhomQuyen OK");
} catch (Exception ex) {
    ex.printStackTrace();
    JOptionPane.showMessageDialog(null, "Lỗi tại panel:\n" + ex.toString());
}

        // Register panel
        nav.register("MAY_BAY", pnlMayBay);
        nav.register("SAN_BAY", pnlSanBay);
        nav.register("TUYEN_BAY", pnlTuyenBay);
        nav.register("CHUYEN_BAY", pnlChuyenBay);
        nav.register("HANG_HANG_KHONG", pnlHHK);
        nav.register("DAT_VE_ADMIN", pnlDatVe);
        nav.register("NHAN_VIEN", pnlNhanVien);
        nav.register("NHOM_QUYEN", pnlNhomQuyen);

        // Màn hình mặc định
        if (isAdmin) {
            nav.show("NHAN_VIEN");
        } else if (AuthUtil.hasPermission(Permission.SAN_BAY)) {
            nav.show("SAN_BAY");
        } else if (AuthUtil.hasPermission(Permission.TUYEN_BAY)) {
            nav.show("TUYEN_BAY");
        } else if (AuthUtil.hasPermission(Permission.CHUYEN_BAY)) {
            nav.show("CHUYEN_BAY");
        } else if (AuthUtil.hasPermission(Permission.DAT_VE_ADMIN)) {
            nav.show("DAT_VE_ADMIN");
        } else if (AuthUtil.hasPermission(Permission.HANG_HANG_KHONG)) {
            nav.show("HANG_HANG_KHONG");
        } else if (AuthUtil.hasPermission(Permission.MAY_BAY)) {
            nav.show("MAY_BAY");
        } else if (AuthUtil.hasPermission(Permission.NHAN_VIEN)) {
            nav.show("NHAN_VIEN");
        } else if (AuthUtil.hasPermission(Permission.NHOM_QUYEN)) {
            nav.show("NHOM_QUYEN");
        } else {
            showEmptyScreen();
            return;
        }

        JPanel sidebar = buildSidebar();
        JPanel content = nav.getRoot();

        
        

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, content);
split.setDividerLocation(240);
split.setOneTouchExpandable(true);

// 🔥 WRAP lại để thêm header
JPanel root = new JPanel(new BorderLayout());
root.add(buildHeader(), BorderLayout.NORTH); // 👈 thêm dòng này
root.add(split, BorderLayout.CENTER);

setContentPane(root);
    }

    private JPanel buildSidebar() {
        int nhomId = SessionContext.getAdminNhomQuyenId();
        boolean isAdmin = (nhomId == 1);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("ADMIN MENU");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnMayBay    = new JButton("Quản lý Máy bay");
        JButton btnSanBay    = new JButton("Quản lý Sân bay");
        JButton btnTuyenBay  = new JButton("Quản lý Tuyến bay");
        JButton btnChuyenBay = new JButton("Quản lý Chuyến bay");
        JButton btnHHK       = new JButton("Quản lý Hãng hàng không");
        JButton btnDatVe     = new JButton("Đặt vé (quầy)");
        JButton btnNhanVien  = new JButton("Quản lý nhân viên");
        JButton btnNhomQuyen = new JButton("Quản lý nhóm quyền");

        JButton[] buttons = {
            btnSanBay, btnTuyenBay, btnChuyenBay,
            btnDatVe, btnHHK, btnMayBay, btnNhanVien, btnNhomQuyen
        };

        for (JButton b : buttons) {
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        p.add(title);
        p.add(Box.createVerticalStrut(12));

        if (isAdmin || AuthUtil.hasPermission(Permission.SAN_BAY)) {
            p.add(btnSanBay);
            p.add(Box.createVerticalStrut(8));
        }
        if (isAdmin || AuthUtil.hasPermission(Permission.TUYEN_BAY)) {
            p.add(btnTuyenBay);
            p.add(Box.createVerticalStrut(8));
        }
        if (isAdmin || AuthUtil.hasPermission(Permission.CHUYEN_BAY)) {
            p.add(btnChuyenBay);
            p.add(Box.createVerticalStrut(8));
        }
        if (isAdmin || AuthUtil.hasPermission(Permission.DAT_VE_ADMIN)) {
            p.add(btnDatVe);
            p.add(Box.createVerticalStrut(8));
        }
        if (isAdmin || AuthUtil.hasPermission(Permission.HANG_HANG_KHONG)) {
            p.add(btnHHK);
            p.add(Box.createVerticalStrut(8));
        }
        if (isAdmin || AuthUtil.hasPermission(Permission.MAY_BAY)) {
            p.add(btnMayBay);
            p.add(Box.createVerticalStrut(8));
        }
        if (isAdmin || AuthUtil.hasPermission(Permission.NHAN_VIEN)) {
            p.add(btnNhanVien);
            p.add(Box.createVerticalStrut(8));
        }
        if (isAdmin || AuthUtil.hasPermission(Permission.NHOM_QUYEN)) {
            p.add(btnNhomQuyen);
            p.add(Box.createVerticalStrut(8));
        }

        btnMayBay.addActionListener(e    -> nav.show("MAY_BAY"));
        btnSanBay.addActionListener(e    -> nav.show("SAN_BAY"));
        btnTuyenBay.addActionListener(e  -> nav.show("TUYEN_BAY"));
        btnChuyenBay.addActionListener(e -> nav.show("CHUYEN_BAY"));
        btnHHK.addActionListener(e       -> nav.show("HANG_HANG_KHONG"));
        btnDatVe.addActionListener(e     -> nav.show("DAT_VE_ADMIN"));
        btnNhanVien.addActionListener(e  -> nav.show("NHAN_VIEN"));
        btnNhomQuyen.addActionListener(e -> nav.show("NHOM_QUYEN"));

        return p;
    }

    private void showEmptyScreen() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel lbl = new JLabel("Tài khoản chưa được cấp quyền", JLabel.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 18));
        lbl.setForeground(Color.RED);

        panel.add(lbl, BorderLayout.CENTER);

        setContentPane(panel);
        revalidate();
        repaint();
    }

    private JPanel buildHeader() {

    JPanel header = new JPanel(new BorderLayout());
    header.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
    header.setBackground(new Color(245, 245, 245));

    // 👤 Xin chào
    String username = SessionContext.getAdminUsername();
    JLabel lblHello = new JLabel("Xin chào, " + username);
    lblHello.setFont(new Font("Arial", Font.BOLD, 14));

    // 🔴 Nút logout
    JButton btnLogout = new JButton("Đăng xuất");

    btnLogout.addActionListener(e -> {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn đăng xuất?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            SessionContext.clearAll(); // 🔥 clear session

            dispose(); // đóng admin

            new FrmDangNhapNhanVien().setVisible(true); // quay lại login
        }
    });

    JButton btnChangePass = new JButton("Đổi mật khẩu");

btnChangePass.addActionListener(e -> {
    new FrmDoiMatKhau().setVisible(true);
});

// 👉 panel chứa button bên phải
JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
right.setOpaque(false);

right.add(btnChangePass);
right.add(btnLogout);

header.add(lblHello, BorderLayout.WEST);
header.add(right, BorderLayout.EAST);

    return header;
}
}