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
import flightbooking.gui.admin.pnl.PnlQuanLyVe;
import flightbooking.gui.admin.pnl.PnlThongKe;
import flightbooking.gui.admin.theme.AdminTheme;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FrmQuanTri extends JFrame {

    private final AppNavigator nav = new AppNavigator();

    public FrmQuanTri() {
         AdminTheme.apply();
        setTitle("Quản trị - FlightBooking");
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // ✅ THÊM DÒNG NÀY: Mở rộng toàn màn hình (vẫn thấy Taskbar)
        setExtendedState(JFrame.MAXIMIZED_BOTH);

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



        // Tạo các panel và apply quyền
        PnlQuanLyMayBay pnlMayBay = new PnlQuanLyMayBay();
        PnlQuanLySanBay pnlSanBay = new PnlQuanLySanBay();
        PnlQuanLyTuyenBay pnlTuyenBay = new PnlQuanLyTuyenBay();
        PnlQuanLyChuyenBay pnlChuyenBay = new PnlQuanLyChuyenBay();
        PnlQuanLyHHK pnlHHK = new PnlQuanLyHHK();
        PnlDatVeAdmin pnlDatVe = new PnlDatVeAdmin();
        PnlQuanLyNhanVien pnlNhanVien = new PnlQuanLyNhanVien();
        PnlNhomQuyen pnlNhomQuyen = new PnlNhomQuyen();
        PnlQuanLyVe pnlQuanLyVe = new PnlQuanLyVe();
        PnlThongKe pnlThongKe = new PnlThongKe();

        // 🔥 CONNECT 2 PANEL
pnlQuanLyVe.setPnlDatVeAdmin(pnlDatVe);

// ✅ MỚI - có log để biết lỗi ở panel nào
try {
   pnlMayBay.applyPermissions(getActionsByPermission(nhomId, isAdmin, Permission.MAY_BAY));
System.out.println("✅ MayBay OK");

pnlSanBay.applyPermissions(getActionsByPermission(nhomId, isAdmin, Permission.SAN_BAY));
System.out.println("✅ SanBay OK");

pnlTuyenBay.applyPermissions(getActionsByPermission(nhomId, isAdmin, Permission.TUYEN_BAY));
System.out.println("✅ TuyenBay OK");

pnlChuyenBay.applyPermissions(getActionsByPermission(nhomId, isAdmin, Permission.CHUYEN_BAY));
System.out.println("✅ ChuyenBay OK");

pnlHHK.applyPermissions(getActionsByPermission(nhomId, isAdmin, Permission.HANG_HANG_KHONG));
System.out.println("✅ HHK OK");

pnlDatVe.applyPermissions(getActionsByPermission(nhomId, isAdmin, Permission.DAT_VE_ADMIN));
System.out.println("✅ DatVe OK");

pnlNhanVien.applyPermissions(getActionsByPermission(nhomId, isAdmin, Permission.NHAN_VIEN));
System.out.println("✅ NhanVien OK");

pnlNhomQuyen.applyPermissions(getActionsByPermission(nhomId, isAdmin, Permission.NHOM_QUYEN));
System.out.println("✅ NhomQuyen OK");

pnlQuanLyVe.applyPermissions(getActionsByPermission(nhomId, isAdmin, Permission.QUAN_LY_VE));
System.out.println("✅ QuanLyVe OK");
pnlThongKe.applyPermissions(getActionsByPermission(nhomId, isAdmin, Permission.THONG_KE));
System.out.println("✅ ThongKe OK");
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
        nav.register("QUAN_LY_VE", pnlQuanLyVe);
        nav.register("NHAN_VIEN", pnlNhanVien);
        nav.register("NHOM_QUYEN", pnlNhomQuyen);
        nav.register("THONG_KE", pnlThongKe);

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
        }else if (AuthUtil.hasPermission(Permission.QUAN_LY_VE)) {
    nav.show("QUAN_LY_VE");} else if (AuthUtil.hasPermission(Permission.HANG_HANG_KHONG)) {
            nav.show("HANG_HANG_KHONG");
        } else if (AuthUtil.hasPermission(Permission.MAY_BAY)) {
            nav.show("MAY_BAY");
        } else if (AuthUtil.hasPermission(Permission.NHAN_VIEN)) {
            nav.show("NHAN_VIEN");
        } else if (AuthUtil.hasPermission(Permission.NHOM_QUYEN)) {
            nav.show("NHOM_QUYEN");
        }else if (AuthUtil.hasPermission(Permission.THONG_KE)) {
    nav.show("THONG_KE");} else {
            showEmptyScreen();
            return;
        }

        JPanel sidebar = buildSidebar();
        JPanel content = nav.getRoot();
        content.setBackground(AdminTheme.CONTENT_BG);
        

        
        

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, content);
split.setDividerLocation(240);
split.setOneTouchExpandable(true);
split.setBorder(null);
split.getLeftComponent().setBackground(AdminTheme.SIDEBAR_BG);
split.getRightComponent().setBackground(AdminTheme.CONTENT_BG);

// 🔥 WRAP lại để thêm header
JPanel root = new JPanel(new BorderLayout());
root.add(buildHeader(), BorderLayout.NORTH); // 👈 thêm dòng này
root.add(split, BorderLayout.CENTER);

setContentPane(root);
AdminTheme.styleActionButtonsRecursively(root);
SwingUtilities.updateComponentTreeUI(this);
    }

    private JPanel buildSidebar() {
    int nhomId = SessionContext.getAdminNhomQuyenId();
    boolean isAdmin = (nhomId == 1);

    JPanel p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    p.setBorder(BorderFactory.createEmptyBorder(14, 12, 14, 12));
    p.setBackground(AdminTheme.SIDEBAR_BG);
    p.setPreferredSize(new Dimension(240, 0));

    // --- Khởi tạo các Button ---
    JButton btnMayBay      = AdminTheme.createSidebarButton("Quản lý Máy bay", "/icons/plane-solid.png");
    JButton btnSanBay      = AdminTheme.createSidebarButton("Quản lý Sân bay", "/icons/sanbay.png");
    JButton btnTuyenBay    = AdminTheme.createSidebarButton("Quản lý Tuyến bay", "/icons/route-solid.png");
    JButton btnChuyenBay   = AdminTheme.createSidebarButton("Quản lý Chuyến bay", "/icons/chuyenbay.png");
    JButton btnHHK         = AdminTheme.createSidebarButton("Quản lý Hãng hàng không", "/icons/hhk.png");
    JButton btnDatVe       = AdminTheme.createSidebarButton("Đặt vé (quầy)", "/icons/datve.png");
    JButton btnQuanLyVe    = AdminTheme.createSidebarButton("Quản lý vé", "/icons/quanlyve.png");
    JButton btnThongKe     = AdminTheme.createSidebarButton("Thống kê vé", "/icons/thongke.png");
    JButton btnNhanVien    = AdminTheme.createSidebarButton("Quản lý nhân viên", "/icons/nvien.png");
    JButton btnNhomQuyen   = AdminTheme.createSidebarButton("Quản lý nhóm quyền", "/icons/nhomquyen.png");

    // ==========================================
    // PHẦN 1: QUẢN LÝ CHUNG
    // ==========================================
    JLabel lblChung = new JLabel("QUẢN LÝ CHUNG");
    lblChung.setFont(new Font("Segoe UI", Font.BOLD, 13));
    lblChung.setForeground(Color.WHITE); // Màu trắng theo yêu cầu
    p.add(lblChung);
    p.add(Box.createVerticalStrut(12));

    // 1. Hãng hàng không
    if (isAdmin || AuthUtil.hasPermission(Permission.HANG_HANG_KHONG)) {
        p.add(btnHHK);
        p.add(Box.createVerticalStrut(8));
    }
    // 2. Máy bay
    if (isAdmin || AuthUtil.hasPermission(Permission.MAY_BAY)) {
        p.add(btnMayBay);
        p.add(Box.createVerticalStrut(8));
    }
    // 3. Sân bay
    if (isAdmin || AuthUtil.hasPermission(Permission.SAN_BAY)) {
        p.add(btnSanBay);
        p.add(Box.createVerticalStrut(8));
    }
    // 4. Tuyến bay
    if (isAdmin || AuthUtil.hasPermission(Permission.TUYEN_BAY)) {
        p.add(btnTuyenBay);
        p.add(Box.createVerticalStrut(8));
    }
    // 5. Chuyến bay
    if (isAdmin || AuthUtil.hasPermission(Permission.CHUYEN_BAY)) {
        p.add(btnChuyenBay);
        p.add(Box.createVerticalStrut(8));
    }
    // 6. Quản lý vé
    if (isAdmin || AuthUtil.hasPermission(Permission.QUAN_LY_VE)) {
        p.add(btnQuanLyVe);
        p.add(Box.createVerticalStrut(8));
    }
    // 7. Thống kê vé
    if (isAdmin || AuthUtil.hasPermission(Permission.THONG_KE)) {
        p.add(btnThongKe);
        p.add(Box.createVerticalStrut(8));
    }
    // 8. Đặt vé
    if (isAdmin || AuthUtil.hasPermission(Permission.DAT_VE_ADMIN)) {
        p.add(btnDatVe);
        p.add(Box.createVerticalStrut(8));
    }

    // Khoảng cách giữa 2 phân đoạn
    p.add(Box.createVerticalStrut(25));

    // ==========================================
    // PHẦN 2: HỆ THỐNG
    // ==========================================
    JLabel lblHeThong = new JLabel("HỆ THỐNG");
    lblHeThong.setFont(new Font("Segoe UI", Font.BOLD, 13));
    lblHeThong.setForeground(Color.WHITE); // Màu trắng theo yêu cầu
    p.add(lblHeThong);
    p.add(Box.createVerticalStrut(12));

    if (isAdmin || AuthUtil.hasPermission(Permission.NHAN_VIEN)) {
        p.add(btnNhanVien);
        p.add(Box.createVerticalStrut(8));
    }
    if (isAdmin || AuthUtil.hasPermission(Permission.NHOM_QUYEN)) {
        p.add(btnNhomQuyen);
        p.add(Box.createVerticalStrut(8));
    }

    p.add(Box.createVerticalGlue());

    // --- Action Listeners ---
    btnMayBay.addActionListener(e    -> nav.show("MAY_BAY"));
    btnSanBay.addActionListener(e    -> nav.show("SAN_BAY"));
    btnTuyenBay.addActionListener(e  -> nav.show("TUYEN_BAY"));
    btnChuyenBay.addActionListener(e -> nav.show("CHUYEN_BAY"));
    btnHHK.addActionListener(e       -> nav.show("HANG_HANG_KHONG"));
    btnDatVe.addActionListener(e     -> nav.show("DAT_VE_ADMIN"));
    btnQuanLyVe.addActionListener(e  -> nav.show("QUAN_LY_VE"));
    btnNhanVien.addActionListener(e  -> nav.show("NHAN_VIEN"));
    btnNhomQuyen.addActionListener(e -> nav.show("NHOM_QUYEN"));
    btnThongKe.addActionListener(e   -> nav.show("THONG_KE"));

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
    header.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
    header.setBackground(AdminTheme.HEADER_BG);

    String username = SessionContext.getAdminUsername();
    JLabel lblHello = new JLabel("Xin chào, " + username);
    lblHello.setFont(new Font("Segoe UI", Font.BOLD, 15));
    lblHello.setForeground(Color.WHITE);

    JButton btnLogout = AdminTheme.createHeaderButton("Đăng xuất");
    btnLogout.addActionListener(e -> {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn đăng xuất?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            SessionContext.clearAll();
            dispose();
            new FrmDangNhapNhanVien().setVisible(true);
        }
    });

    JButton btnChangePass = AdminTheme.createHeaderButton("Đổi mật khẩu");
    btnChangePass.addActionListener(e -> new FrmDoiMatKhau().setVisible(true));

    JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    right.setOpaque(false);
    right.add(btnChangePass);
    right.add(btnLogout);

    header.add(lblHello, BorderLayout.WEST);
    header.add(right, BorderLayout.EAST);

    return header;
}
private List<Integer> getActionsByPermission(int nhomId, boolean isAdmin, int quyenId) {
    if (isAdmin) {
        switch (quyenId) {
            case Permission.SAN_BAY:
                return java.util.Arrays.asList(
                        ActionConstants.THEM,
                        ActionConstants.SUA,
                        ActionConstants.XOA,
                        ActionConstants.XUAT_EXCEL,
                        ActionConstants.NHAP_EXCEL
                );
            case Permission.TUYEN_BAY:
                return java.util.Arrays.asList(
                        ActionConstants.THEM,
                        ActionConstants.SUA,
                        ActionConstants.XOA,
                        ActionConstants.XUAT_EXCEL,
                        ActionConstants.NHAP_EXCEL
                );
            case Permission.CHUYEN_BAY:
                return java.util.Arrays.asList(
                        ActionConstants.THEM,
                        ActionConstants.SUA,
                        ActionConstants.XOA,
                        ActionConstants.GIA_HANG_GHE,
                        ActionConstants.SO_DO_GHE,
                        ActionConstants.XUAT_EXCEL,
                        ActionConstants.NHAP_EXCEL
                );
            case Permission.DAT_VE_ADMIN:
                return java.util.Arrays.asList(
                        ActionConstants.TAO_VE,
                        ActionConstants.XUAT_EXCEL,
                        ActionConstants.NHAP_EXCEL
                );
            case Permission.MAY_BAY:
                return java.util.Arrays.asList(
                        ActionConstants.THEM,
                        ActionConstants.SUA,
                        ActionConstants.XOA,
                        ActionConstants.TAO_GHE,
                        ActionConstants.XUAT_EXCEL,
                        ActionConstants.NHAP_EXCEL
                );
            case Permission.HANG_HANG_KHONG:
                return java.util.Arrays.asList(
                        ActionConstants.THEM,
                        ActionConstants.SUA,
                        ActionConstants.XOA,
                        ActionConstants.XUAT_EXCEL,
                        ActionConstants.NHAP_EXCEL
                );
            case Permission.NHAN_VIEN:
                return java.util.Arrays.asList(
                        ActionConstants.THEM,
                        ActionConstants.SUA,
                        ActionConstants.XOA,
                        ActionConstants.LAM_MOI,
                        ActionConstants.PHAN_QUYEN,
                        ActionConstants.XUAT_EXCEL,
                        ActionConstants.NHAP_EXCEL
                );
            case Permission.NHOM_QUYEN:
                return java.util.Arrays.asList(
                        ActionConstants.THEM,
                        ActionConstants.SUA,
                        ActionConstants.XOA
                );
            case Permission.QUAN_LY_VE:
                return java.util.Arrays.asList(
                        ActionConstants.HUY_VE,
                        ActionConstants.XUAT_EXCEL,
                        ActionConstants.NHAP_EXCEL
                );
                case Permission.THONG_KE:
    return java.util.Arrays.asList(
            ActionConstants.XUAT_EXCEL
    );
            default:
                return new ArrayList<>();
        }
    }

    return new NhomQuyenDAO().getActionByNhomAndQuyen(nhomId, quyenId);
}
}