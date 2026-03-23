package flightbooking.gui.admin.pnl;

import flightbooking.bus.GheGeneratorBUS;
import flightbooking.bus.MayBayBUS;
import flightbooking.dto.MayBayDTO;
import flightbooking.util.ActionConstants;
import flightbooking.util.ExcelExporter;
import flightbooking.dao.GheDAO;
import flightbooking.util.ExcelImporter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.util.ArrayList;
import java.util.List;

public class PnlQuanLyMayBay extends JPanel {

    private final MayBayBUS mayBayBUS = new MayBayBUS();
    private final GheGeneratorBUS gheGenBUS = new GheGeneratorBUS();
    private final GheDAO gheDAO = new GheDAO();

    // Đã bỏ cột "Số tầng", chỉ còn 4 cột
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Tên máy bay", "Kiểu", "Tổng ghế"}, 0
    ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

    private final JTable table = new JTable(model);

    private final JTextField txtTen = new JTextField();
    private final JTextField txtKieu = new JTextField();


    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnGen;
    private JButton btnExport;
    private JButton btnImport;

    public PnlQuanLyMayBay() {
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        add(buildTop(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> fillForm());
        reload();
    }

    private JComponent buildTop() {
    JPanel form = new JPanel(new GridBagLayout());
    GridBagConstraints lc = makeLc(); GridBagConstraints fc = makeFc();

    lc.gridx=0; lc.gridy=0; form.add(makeLabel("Tên máy bay"), lc);
    fc.gridx=1; fc.gridy=0; styleField(txtTen); form.add(txtTen, fc);

    lc.gridx=2; lc.gridy=0; form.add(makeLabel("Kiểu máy bay"), lc);
    fc.gridx=3; fc.gridy=0; styleField(txtKieu); form.add(txtKieu, fc);

    btnAdd = new JButton("Thêm"); btnUpdate = new JButton("Sửa"); btnDelete = new JButton("Xóa");
    btnGen = new JButton("Tạo ghế cho máy bay đang chọn");
    btnAdd.addActionListener(e -> add());
    btnUpdate.addActionListener(e -> update());
    btnDelete.addActionListener(e -> delete());
    btnGen.addActionListener(e -> openGenSeatDialog());
    btnExport = new JButton("Xuất Excel");
btnExport.addActionListener(e -> {
    ExcelExporter.export(table, this);
});

btnImport = new JButton("Nhập Excel");
btnImport.addActionListener(e -> {
    ExcelImporter.importToTable(table, this);
});

    JPanel wrap = wrapWithActions(form, btnAdd, btnUpdate, btnDelete, btnExport, btnImport, btnGen);
    
    return wrap;
}

    private void reload() {
        model.setRowCount(0);
        for (MayBayDTO m : mayBayBUS.dsMayBay()) {
            // Hiển thị chữ "null" nếu tổng số ghế chưa có hoặc = 0
            Object tongGhe = (m.getTongSoGhe() == null || m.getTongSoGhe() == 0) ? "null" : m.getTongSoGhe();
            model.addRow(new Object[]{
                    m.getMayBayId(),
                    m.getTenMayBay(),
                    m.getKieuMayBay(),
                    tongGhe
            });
        }
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        txtTen.setText(String.valueOf(model.getValueAt(row, 1)));
        txtKieu.setText(String.valueOf(model.getValueAt(row, 2)));
    }

    private void add() {
        MayBayDTO m = new MayBayDTO();
        m.setTenMayBay(txtTen.getText().trim());
        m.setKieuMayBay(txtKieu.getText().trim());
        
        // Mặc định set null cho ghế khi mới thêm máy bay
        m.setTongSoGhe(0);
        // m.setSoTang(1); // Mở comment nếu Database của bạn vẫn bắt buộc có thuộc tính so_tang

        mayBayBUS.themMayBay(m);
        reload();
    }

    private void update() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));

        MayBayDTO m = new MayBayDTO();
        m.setMayBayId(id);
        m.setTenMayBay(txtTen.getText().trim());
        m.setKieuMayBay(txtKieu.getText().trim());

        // Lấy lại giá trị tổng số ghế từ bảng để không bị đè mất khi Update tên/kiểu
        Object tsg = model.getValueAt(row, 3);
        if (tsg != null && !tsg.toString().equals("null")) {
            m.setTongSoGhe(Integer.parseInt(tsg.toString()));
        } else {
            m.setTongSoGhe(null);
        }

        mayBayBUS.capNhatMayBay(m);
        reload();
    }

    private void delete() {
    int row = table.getSelectedRow();
    if (row < 0) return;

    int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));

    // Confirm with user before deleting
    int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this airplane?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
    if (confirm != JOptionPane.YES_OPTION) return;

    try {
        mayBayBUS.xoaMayBay(id);
        reload();
        JOptionPane.showMessageDialog(this, "Airplane deleted successfully!");
    } catch (RuntimeException e) {
        // Look inside the RuntimeException for the database error
        Throwable cause = e.getCause();
        if (cause instanceof org.postgresql.util.PSQLException) {
            org.postgresql.util.PSQLException sqlEx = (org.postgresql.util.PSQLException) cause;
            
            // "23503" is the standard SQL state for Foreign Key Violation
            if ("23503".equals(sqlEx.getSQLState())) {
                JOptionPane.showMessageDialog(this, 
                    "Cannot delete: This airplane is currently linked to existing flights.\n" +
                    "Please delete the flights associated with this airplane first.", 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // Fallback for other unexpected errors
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        e.printStackTrace();
    }
}

    private void openGenSeatDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn 1 máy bay trước.");
            return;
        }

        int mayBayId = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Cấu hình ghế - Máy bay ID=" + mayBayId,
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        // Sắp xếp lại thứ tự xuất hiện trong danh sách chọn (không đổi ID, chỉ đổi thứ tự)
        HangGheItem[] danhSachHangGhe = new HangGheItem[]{
            new HangGheItem(3, "First Class"),     // Đưa lên đầu
            new HangGheItem(2, "Business"), 
            new HangGheItem(4, "Premium Economy"),
            new HangGheItem(1, "Economy")          // Đưa xuống cuối
        };

        JPanel pnlConfigList = new JPanel();
        pnlConfigList.setLayout(new BoxLayout(pnlConfigList, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(pnlConfigList);
        
        JButton btnAddConfig = new JButton("+ Thêm hạng ghế");
        List<ConfigRow> configRows = new ArrayList<>();

        Runnable addRowUI = () -> {
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            JComboBox<HangGheItem> cbHangGhe = new JComboBox<>(danhSachHangGhe);
            JSpinner spSoGhe = new JSpinner(new SpinnerNumberModel(50, 1, 500, 1));
            JSpinner spSoHang = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
            JButton btnRemove = new JButton("Xóa");
            
            rowPanel.add(new JLabel("Hạng:"));
            rowPanel.add(cbHangGhe);
            rowPanel.add(new JLabel("Tổng số ghế:"));
            rowPanel.add(spSoGhe);
            rowPanel.add(new JLabel("Số hàng:"));
            rowPanel.add(spSoHang);
            rowPanel.add(btnRemove);

            pnlConfigList.add(rowPanel);
            pnlConfigList.revalidate();
            pnlConfigList.repaint();

            ConfigRow configData = new ConfigRow(cbHangGhe, spSoGhe, spSoHang, rowPanel);
            configRows.add(configData);

            btnRemove.addActionListener(e -> {
                pnlConfigList.remove(rowPanel);
                configRows.remove(configData);
                pnlConfigList.revalidate();
                pnlConfigList.repaint();
            });
        };

        addRowUI.run();
        btnAddConfig.addActionListener(e -> addRowUI.run());

        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTop.add(btnAddConfig);
        pnlTop.add(new JLabel("  (Cột sẽ được tự động tính = Tổng ghế / Số hàng)"));

        JButton btnOk = new JButton("Bắt đầu tạo");
        JButton btnCancel = new JButton("Hủy");

        btnOk.addActionListener(e -> {
    try {
        if (!gheDAO.findByMayBay(mayBayId).isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Máy bay đã có ghế trong Database.\nHãy xóa cũ trước khi tạo lại.");
            return;
        }

        if (configRows.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Vui lòng thêm ít nhất 1 cấu hình hạng ghế.");
            return;
        }

        int tongGheDaTao = 0;
        int rowOffset = 0;

        // 🔥 BƯỚC 1: VALIDATE ALL TRƯỚC
        for (ConfigRow rowData : configRows) {
            int soGhe = (int) rowData.spSoGhe.getValue();
            int soHang = (int) rowData.spSoHang.getValue();

            if (soGhe <= 0 || soHang <= 0 || soGhe < soHang) {
                JOptionPane.showMessageDialog(dialog, "Dữ liệu không hợp lệ (soGhe >= soHang > 0)");
                return; // ❌ CHƯA TẠO GÌ CẢ → AN TOÀN
            }
        }

        for (ConfigRow rowData : configRows) {
            HangGheItem selectedHangGhe = (HangGheItem) rowData.cbHangGhe.getSelectedItem();
            int hangGheId = selectedHangGhe.id;
            String tenHang = selectedHangGhe.ten;

            int soGhe = (int) rowData.spSoGhe.getValue();
            int soHang = (int) rowData.spSoHang.getValue();

            // ✅ TÍNH SỐ CỘT CHỈ ĐỂ CHIA GRID (KHÔNG NHÂN NGƯỢC)
            int soCot = (int) Math.ceil((double) soGhe / soHang);

            // ✅ GỌI BUS (TRUYỀN ĐỦ soCot)
            gheGenBUS.taoGheTheoHang(
                    mayBayId,
                    hangGheId,
                    tenHang,
                    soGhe,
                    soHang,
                    soCot,
                    rowOffset
            );

            rowOffset += soHang;
            tongGheDaTao += soGhe; // ✅ CHUẨN
        }

        MayBayDTO mbUpdate = new MayBayDTO();
        mbUpdate.setMayBayId(mayBayId);
        mbUpdate.setTenMayBay(String.valueOf(model.getValueAt(row, 1)));
        mbUpdate.setKieuMayBay(String.valueOf(model.getValueAt(row, 2)));
        mbUpdate.setTongSoGhe(tongGheDaTao);

        mayBayBUS.capNhatMayBay(mbUpdate);

        JOptionPane.showMessageDialog(dialog, "Thành công! Đã tạo " + tongGheDaTao + " ghế.");
        dialog.dispose();
        reload();

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage());
        ex.printStackTrace();
    }
});

        btnCancel.addActionListener(e -> dialog.dispose());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(btnCancel); actions.add(btnOk);

        dialog.add(pnlTop, BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(actions, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private static class ConfigRow {
        JComboBox<HangGheItem> cbHangGhe;
        JSpinner spSoGhe, spSoHang;
        JPanel panel;

        public ConfigRow(JComboBox<HangGheItem> cbHangGhe, JSpinner spSoGhe, JSpinner spSoHang, JPanel panel) {
            this.cbHangGhe = cbHangGhe;
            this.spSoGhe = spSoGhe;
            this.spSoHang = spSoHang;
            this.panel = panel;
        }
    }

    private static class HangGheItem {
        int id; String ten;
        public HangGheItem(int id, String ten) {
            this.id = id; this.ten = ten;
        }
        @Override
        public String toString() { return ten; }
    }

    public void applyPermissions(List<Integer> actionIds) {
    btnAdd.setVisible(actionIds.contains(ActionConstants.THEM));
    btnUpdate.setVisible(actionIds.contains(ActionConstants.SUA));
    btnDelete.setVisible(actionIds.contains(ActionConstants.XOA));
    btnGen.setVisible(actionIds.contains(ActionConstants.TAO_GHE));
    revalidate(); repaint();
}

private GridBagConstraints makeLc() {
    GridBagConstraints lc = new GridBagConstraints();
    lc.anchor = GridBagConstraints.WEST;
    lc.insets = new Insets(6, 4, 6, 6);
    return lc;
}

private GridBagConstraints makeFc() {
    GridBagConstraints fc = new GridBagConstraints();
    fc.fill = GridBagConstraints.HORIZONTAL;
    fc.weightx = 1.0;
    fc.insets = new Insets(6, 0, 6, 12);
    return fc;
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

private JPanel wrapWithActions(JPanel form, JButton... buttons) {
    JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
    for (JButton b : buttons) actions.add(b);
    JPanel wrap = new JPanel(new BorderLayout(0, 8));
    wrap.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
    wrap.add(form, BorderLayout.CENTER);
    wrap.add(actions, BorderLayout.SOUTH);
    return wrap;
}
}