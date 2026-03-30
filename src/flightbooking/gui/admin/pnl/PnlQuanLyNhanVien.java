package flightbooking.gui.admin.pnl;

import flightbooking.bus.ChucVuBUS;
import flightbooking.bus.NhanVienBUS;
import flightbooking.bus.PhongBanBUS;
import flightbooking.dao.QuyenDAO;
import flightbooking.dto.ChucVuDTO;
import flightbooking.dto.NhanVienDTO;
import flightbooking.dto.PhongBanDTO;
import flightbooking.dto.QuyenDTO;
import flightbooking.gui.admin.theme.AdminTheme;
import flightbooking.util.ActionConstants;
import flightbooking.util.ExcelExporter;
import flightbooking.util.ExcelImporter;
import flightbooking.util.ValidationUtil;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PnlQuanLyNhanVien extends JPanel {

    private final NhanVienBUS nhanVienBUS = new NhanVienBUS();
    private final PhongBanBUS phongBanBUS = new PhongBanBUS();
    private final ChucVuBUS chucVuBUS = new ChucVuBUS();
    // private final QuyenDAO quyenDAO = new QuyenDAO();

    private final DefaultTableModel model = new DefaultTableModel(
        new Object[]{
                "ID",
                "Họ tên",
                "SĐT",
                "Email",
                "Phòng ban",
                "Chức vụ",
                "Ngày vào làm",
                "Trạng thái",
                "Ngày nghỉ",
                "Lương cơ bản"
        }, 0
    ) {
        @Override
        public boolean isCellEditable(int r, int c) { return false; }
    };

    private final JTable table = new JTable(model);

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnExport;
    private JButton btnImport;

    private final JTextField txtHoTen = new JTextField();
    private final JTextField txtEmail = new JTextField();
    private final JTextField txtDienThoai = new JTextField();
    private final JTextField txtLuong = new JTextField();
    private final JSpinner spNgayVaoLam = new JSpinner(new SpinnerDateModel());
private final JSpinner spNgayNghi = new JSpinner(new SpinnerDateModel());

    private final JComboBox<Item> cbPhongBan = new JComboBox<>();
    private final JComboBox<Item> cbChucVu = new JComboBox<>();
    private final JComboBox<String> cbTrangThai =
            new JComboBox<>(new String[]{"1 - Đang làm", "0 - Nghỉ việc"});

    private final JTextField txtQuyen = new JTextField();
    private final JButton btnPhanQuyen = new JButton("Phân quyền");

    public PnlQuanLyNhanVien() {

        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        add(buildForm(), BorderLayout.NORTH);
        AdminTheme.styleTable(table, false);
add(AdminTheme.wrapTable(table), BorderLayout.CENTER);

        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cbTrangThai.setEnabled(false);
        txtQuyen.setEditable(false);
        spNgayVaoLam.setEditor(new JSpinner.DateEditor(spNgayVaoLam, "yyyy-MM-dd"));
spNgayNghi.setEditor(new JSpinner.DateEditor(spNgayNghi, "yyyy-MM-dd"));

        btnPhanQuyen.addActionListener(e -> openPermissionDialog());

        try {
            loadPhongBan();
            loadChucVu();
            reload();
        } catch (Exception e) {
            e.printStackTrace();
        }

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillForm();
            }
        });
    }

    private JPanel buildForm() {
    JPanel form = new JPanel(new GridBagLayout());
    form.setOpaque(false);

AdminTheme.styleSoftTextField(txtHoTen);
AdminTheme.styleSoftTextField(txtEmail);
AdminTheme.styleSoftTextField(txtDienThoai);
AdminTheme.styleSoftTextField(txtLuong);
AdminTheme.styleSoftSpinner(spNgayVaoLam);
AdminTheme.styleSoftSpinner(spNgayNghi);
AdminTheme.styleSoftComboBox(cbPhongBan);
AdminTheme.styleSoftComboBox(cbChucVu);
AdminTheme.styleSoftComboBox(cbTrangThai);
AdminTheme.styleSoftTextField(txtQuyen);
    form.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

    GridBagConstraints lc = new GridBagConstraints();
    lc.anchor = GridBagConstraints.WEST;
    lc.insets = new Insets(6, 0, 6, 8);

    GridBagConstraints fc = new GridBagConstraints();
    fc.fill = GridBagConstraints.HORIZONTAL;
    fc.weightx = 1.0;
    fc.insets = new Insets(6, 0, 6, 16);
btnExport = new JButton("Xuất Excel");
btnExport.addActionListener(e -> {
    ExcelExporter.export(table, this);
});

btnImport = AdminTheme.createActionButton("Nhập Excel", AdminTheme.ButtonRole.NEUTRAL);
btnImport.addActionListener(e -> {
    ExcelImporter.importToTable(table, this);
});
    // Hàng 0: Họ tên | Email | Số điện thoại
    addFormRow(form, lc, fc, 0, 0, "Họ tên", txtHoTen);
    addFormRow(form, lc, fc, 0, 2, "Email", txtEmail);
    addFormRow(form, lc, fc, 0, 4, "Số điện thoại", txtDienThoai);

    // Hàng 1: Lương | Ngày vào làm | Ngày nghỉ
    addFormRow(form, lc, fc, 1, 0, "Lương cơ bản", txtLuong);
    addFormRow(form, lc, fc, 1, 2, "Ngày vào làm", spNgayVaoLam);
addFormRow(form, lc, fc, 1, 4, "Ngày nghỉ", spNgayNghi);

    // Hàng 2: Phòng ban | Chức vụ | Trạng thái
    addFormRow(form, lc, fc, 2, 0, "Phòng ban", cbPhongBan);
    addFormRow(form, lc, fc, 2, 2, "Chức vụ", cbChucVu);
    addFormRow(form, lc, fc, 2, 4, "Trạng thái", cbTrangThai);

    // Hàng 3: Quyền
    lc.gridx = 0; lc.gridy = 3;
    form.add(makeLabel("Quyền"), lc);
    fc.gridx = 1; fc.gridy = 3;
    fc.gridwidth = 5;
    form.add(buildPermissionBox(), fc);
    fc.gridwidth = 1;

    // Style các field
   

    btnAdd = AdminTheme.createActionButton("Thêm", AdminTheme.ButtonRole.NEUTRAL);
    btnUpdate = AdminTheme.createActionButton("Sửa", AdminTheme.ButtonRole.NEUTRAL);
    btnDelete = AdminTheme.createActionButton("Xóa", AdminTheme.ButtonRole.NEUTRAL);
    btnClear  = AdminTheme.createActionButton("Làm mới", AdminTheme.ButtonRole.NEUTRAL);

    btnAdd.addActionListener(e -> add());
    btnUpdate.addActionListener(e -> update());
    btnDelete.addActionListener(e -> delete());
    btnClear.addActionListener(e -> { table.clearSelection(); clearForm(); });

    JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    actions.add(btnClear);
    actions.add(btnAdd);
    actions.add(btnUpdate);
    actions.add(btnDelete);
    btnExport = AdminTheme.createActionButton("Xuất Excel", AdminTheme.ButtonRole.NEUTRAL);
btnExport.addActionListener(e -> {
    ExcelExporter.export(table, this);
});

actions.add(btnExport);
actions.add(btnImport);

    return AdminTheme.wrapFormCard(form, btnClear, btnAdd, btnUpdate, btnDelete, btnExport, btnImport);
    
}

// Helper: thêm 1 cặp label + field vào đúng ô
private void addFormRow(JPanel form, GridBagConstraints lc,
                        GridBagConstraints fc, int row, int col,
                        String labelText, JComponent field) {
    lc.gridx = col;     lc.gridy = row;
    form.add(makeLabel(labelText), lc);
    fc.gridx = col + 1; fc.gridy = row;
    form.add(field, fc);
}

private JLabel makeLabel(String text) {
    JLabel lb = new JLabel(text);
    lb.setFont(lb.getFont().deriveFont(Font.PLAIN, 13f));
    return lb;
}

private void styleField(JTextField field) {
    field.setPreferredSize(new Dimension(160, 30));
    field.setFont(field.getFont().deriveFont(13f));
    field.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200)),
        BorderFactory.createEmptyBorder(3, 8, 3, 8)
    ));
}

    private JPanel buildPermissionBox() {
        JPanel p = new JPanel(new BorderLayout(5, 0));
        p.setOpaque(false);
        p.add(txtQuyen, BorderLayout.CENTER);
        p.add(btnPhanQuyen, BorderLayout.EAST);
        return p;
    }

    // ================= LOAD DATA =================

    private void loadPhongBan() {
        cbPhongBan.removeAllItems();
        List<PhongBanDTO> list = phongBanBUS.layDanhSach();
        for (PhongBanDTO p : list) {
            cbPhongBan.addItem(new Item(p.getPhongBanId(), p.getTenPhongBan()));
        }
    }

    private void loadChucVu() {
        cbChucVu.removeAllItems();
        List<ChucVuDTO> list = chucVuBUS.layDanhSach();
        for (ChucVuDTO c : list) {
            cbChucVu.addItem(new Item(c.getChucVuId(), c.getTenChucVu()));
        }
    }

    private void reload() {
        model.setRowCount(0);
        for (NhanVienDTO nv : nhanVienBUS.layDanhSach()) {
            model.addRow(new Object[]{
                nv.getNhanVienId(),
                nv.getHoTen(),
                nv.getDienThoai(),
                nv.getEmail(),
                nv.getTenPhongBan(),
                nv.getTenChucVu(),
                nv.getNgayVaoLam(),
                nv.getTrangThai() == 1 ? "Đang làm" : "Nghỉ việc",
                nv.getNgayNghi(),
                nv.getLuongCoBan()
            });
        }
    }

    private void clearForm() {
    txtHoTen.setText("");
    txtEmail.setText("");
    txtDienThoai.setText("");
    txtLuong.setText("");

    spNgayVaoLam.setValue(new java.util.Date());
    spNgayNghi.setValue(new java.util.Date());

    txtQuyen.setText("");

    if (cbPhongBan.getItemCount() > 0) cbPhongBan.setSelectedIndex(0);
    if (cbChucVu.getItemCount() > 0) cbChucVu.setSelectedIndex(0);

    cbTrangThai.setSelectedIndex(0);
    cbTrangThai.setEnabled(false);
}

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
        NhanVienDTO nv = nhanVienBUS.findById(id);

        txtHoTen.setText(nv.getHoTen());
        txtEmail.setText(nv.getEmail());
        txtDienThoai.setText(nv.getDienThoai());
        txtLuong.setText(String.valueOf(nv.getLuongCoBan()));
        if (nv.getNgayVaoLam() != null) {
    spNgayVaoLam.setValue(
        java.util.Date.from(
            nv.getNgayVaoLam()
            .atStartOfDay(java.time.ZoneId.systemDefault())
            .toInstant()
        )
    );
}

if (nv.getNgayNghi() != null) {
    spNgayNghi.setValue(
        java.util.Date.from(
            nv.getNgayNghi()
            .atStartOfDay(java.time.ZoneId.systemDefault())
            .toInstant()
        )
    );
}

        selectComboById(cbPhongBan, nv.getPhongBanId());
        selectComboById(cbChucVu, nv.getChucVuId());

        cbTrangThai.setSelectedIndex(nv.getTrangThai() == 1 ? 0 : 1);
        cbTrangThai.setEnabled(true);
        
        loadPermissionSummary(id);
    }


    // ================= CRUD =================

    private void add() {
        try {
            NhanVienDTO nv = getFormData(null);

            String username = txtEmail.getText().trim();
            String password = "123456";

            int nhomQuyenId = 0; // không dùng nhóm quyền ở đây nữa

            nhanVienBUS.themNhanVienVaTaiKhoan(
                    nv,
                    username,
                    password,
                    nhomQuyenId
            );

            reload();
            clearForm();

            JOptionPane.showMessageDialog(
                this,
                "Thêm nhân viên + tạo tài khoản thành công!\nChọn nhân viên trong bảng rồi bấm 'Phân quyền' để cấp quyền."
            );

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void update() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần sửa.");
            return;
        }

        try {
            int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
            NhanVienDTO nv = getFormData(id);
            nhanVienBUS.suaNhanVien(nv);
            reload();
            clearForm();
            table.clearSelection();
            JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void delete() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xoá nhân viên này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
        nhanVienBUS.xoaNhanVien(id);
        reload();
        clearForm();
        table.clearSelection();
    }

    // ================= PHÂN QUYỀN =================

    private void openPermissionDialog() {
    int row = table.getSelectedRow();
    if (row < 0) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên trước.");
        return;
    }

    int nhanVienId = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));

    List<flightbooking.dto.NhomQuyenDTO> dsNhomQuyen =
            new flightbooking.dao.NhomQuyenDAO().findAll();

    if (dsNhomQuyen == null || dsNhomQuyen.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Chưa có nhóm quyền nào.");
        return;
    }

    Integer currentNhomQuyenId = nhanVienBUS.getNhomQuyenIdByNhanVien(nhanVienId);
    boolean firstTime = (currentNhomQuyenId == null || currentNhomQuyenId <= 0);

    JDialog dialog = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "Phân quyền cho nhân viên ID " + nhanVienId,
            true
    );
    dialog.setSize(420, 260);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout(10, 10));

    DefaultComboBoxModel<NhomQuyenItem> cbModel = new DefaultComboBoxModel<>();
    JComboBox<NhomQuyenItem> cbNhomQuyen = new JComboBox<>(cbModel);

    for (flightbooking.dto.NhomQuyenDTO nq : dsNhomQuyen) {
        cbModel.addElement(new NhomQuyenItem(
                nq.getNhomQuyenId(),
                nq.getTenNhomQuyen()
        ));
    }

    if (currentNhomQuyenId != null) {
        for (int i = 0; i < cbModel.getSize(); i++) {
            NhomQuyenItem item = cbModel.getElementAt(i);
            if (item.id == currentNhomQuyenId) {
                cbNhomQuyen.setSelectedIndex(i);
                break;
            }
        }
    }

    JPanel center = new JPanel(new GridLayout(2, 1, 10, 10));
    center.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
    center.add(new JLabel("Chọn nhóm quyền"));
    center.add(cbNhomQuyen);

    JButton btnSave = new JButton("Lưu");
    JButton btnUpdate = new JButton("Cập nhật");
    JButton btnClose = new JButton("Đóng");

    btnSave.setEnabled(firstTime);
    btnUpdate.setEnabled(!firstTime);

    btnSave.addActionListener(e -> {
        try {
            NhomQuyenItem selected = (NhomQuyenItem) cbNhomQuyen.getSelectedItem();
            if (selected == null) {
                throw new RuntimeException("Vui lòng chọn nhóm quyền.");
            }

            nhanVienBUS.saveNhomQuyenForNhanVien(nhanVienId, selected.id);
            txtQuyen.setText(selected.text);

            JOptionPane.showMessageDialog(dialog, "Lưu nhóm quyền thành công!");
            dialog.dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(dialog, ex.getMessage());
        }
    });

    btnUpdate.addActionListener(e -> {
        try {
            NhomQuyenItem selected = (NhomQuyenItem) cbNhomQuyen.getSelectedItem();
            if (selected == null) {
                throw new RuntimeException("Vui lòng chọn nhóm quyền.");
            }

            nhanVienBUS.updateNhomQuyenForNhanVien(nhanVienId, selected.id);
            txtQuyen.setText(selected.text);

            JOptionPane.showMessageDialog(dialog, "Cập nhật nhóm quyền thành công!");
            dialog.dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(dialog, ex.getMessage());
        }
    });

    btnClose.addActionListener(e -> dialog.dispose());

    JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    south.add(btnSave);
    south.add(btnUpdate);
    south.add(btnClose);

    JLabel lblHint = new JLabel(
            firstTime
                    ? "Nhân viên này chưa có nhóm quyền. Dùng nút Lưu."
                    : "Nhân viên này đã có nhóm quyền. Dùng nút Cập nhật."
    );
    lblHint.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));

    dialog.add(lblHint, BorderLayout.NORTH);
    dialog.add(center, BorderLayout.CENTER);
    dialog.add(south, BorderLayout.SOUTH);

    dialog.setVisible(true);
}


    // ================= HELPER =================

    private NhanVienDTO getFormData(Integer id) {
    NhanVienDTO nv = new NhanVienDTO();

    if (id != null) nv.setNhanVienId(id);

    nv.setHoTen(txtHoTen.getText().trim());
    ValidationUtil.validateName(nv.getHoTen(), "Họ tên");

    nv.setEmail(txtEmail.getText().trim());
    ValidationUtil.validateEmail(nv.getEmail());

    nv.setDienThoai(txtDienThoai.getText().trim());
    ValidationUtil.validatePhone10Digits(nv.getDienThoai());

    try {
        nv.setLuongCoBan(new BigDecimal(txtLuong.getText().trim()));
        ValidationUtil.validateNonNegative(nv.getLuongCoBan(), "Lương");
    } catch (Exception e) {
        throw new RuntimeException("Lương phải là số hợp lệ.");
    }

    try {
        java.util.Date d1 = (java.util.Date) spNgayVaoLam.getValue();
        nv.setNgayVaoLam(
                d1.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
        );
    } catch (Exception e) {
        throw new RuntimeException("Ngày vào sai định dạng yyyy-MM-dd.");
    }

    try {
        java.util.Date d2 = (java.util.Date) spNgayNghi.getValue();
        nv.setNgayNghi(
                d2.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
        );
    } catch (Exception e) {
        throw new RuntimeException("Ngày nghỉ sai định dạng yyyy-MM-dd.");
    }

    if (nv.getNgayVaoLam() != null && nv.getNgayNghi() != null) {
        ValidationUtil.validateDateOrder(
                nv.getNgayVaoLam(),
                nv.getNgayNghi(),
                "Ngày vào làm",
                "Ngày nghỉ"
        );
    }

    Item pb = (Item) cbPhongBan.getSelectedItem();
    Item cv = (Item) cbChucVu.getSelectedItem();

    if (pb != null) nv.setPhongBanId(pb.id);
    if (cv != null) nv.setChucVuId(cv.id);

    if (id == null) {
        nv.setTrangThai(1);
    } else {
        nv.setTrangThai(cbTrangThai.getSelectedIndex() == 0 ? 1 : 0);
    }

    return nv;
}

    private void selectComboById(JComboBox<Item> cb, Integer id) {
        if (id == null) return;
        for (int i = 0; i < cb.getItemCount(); i++) {
            Item it = cb.getItemAt(i);
            if (it != null && it.id == id) {
                cb.setSelectedIndex(i);
                return;
            }
        }
    }

    private static class Item {
        final int id;
        final String text;

        Item(int id, String text) {
            this.id = id;
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private static class NhomQuyenItem {
    final int id;
    final String text;

    NhomQuyenItem(int id, String text) {
        this.id = id;
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}

private void loadPermissionSummary(int nhanVienId) {
    try {
        Integer nhomQuyenId = nhanVienBUS.getNhomQuyenIdByNhanVien(nhanVienId);

        if (nhomQuyenId == null || nhomQuyenId <= 0) {
            txtQuyen.setText("Chưa phân quyền");
            return;
        }

        List<flightbooking.dto.NhomQuyenDTO> dsNhomQuyen =
                new flightbooking.dao.NhomQuyenDAO().findAll();

        for (flightbooking.dto.NhomQuyenDTO nq : dsNhomQuyen) {
            if (nq.getNhomQuyenId() == nhomQuyenId) {
                txtQuyen.setText(nq.getTenNhomQuyen());
                return;
            }
        }

        txtQuyen.setText("Nhóm quyền #" + nhomQuyenId);

    } catch (Exception e) {
        txtQuyen.setText("Không tải được nhóm quyền");
    }
}

public void applyPermissions(List<Integer> actionIds) {
    btnAdd.setVisible(actionIds.contains(ActionConstants.THEM));
    btnUpdate.setVisible(actionIds.contains(ActionConstants.SUA));
    btnDelete.setVisible(actionIds.contains(ActionConstants.XOA));
    btnClear.setVisible(actionIds.contains(ActionConstants.LAM_MOI));
    btnPhanQuyen.setVisible(actionIds.contains(ActionConstants.PHAN_QUYEN));
    btnExport.setVisible(actionIds.contains(ActionConstants.XUAT_EXCEL));
    btnImport.setVisible(actionIds.contains(ActionConstants.NHAP_EXCEL));
    revalidate();
    repaint();
}
}