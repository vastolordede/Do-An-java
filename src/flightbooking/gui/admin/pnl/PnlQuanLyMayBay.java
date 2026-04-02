package flightbooking.gui.admin.pnl;

import flightbooking.bus.ChuyenBayBUS;
import flightbooking.bus.GheGeneratorBUS;
import flightbooking.bus.MayBayBUS;
import flightbooking.dao.GheDAO;
import flightbooking.dto.CauHinhKhoangGheDTO;
import flightbooking.dto.GheDTO;
import flightbooking.dto.MayBayDTO;
import flightbooking.gui.admin.theme.AdminTheme;
import flightbooking.util.ActionConstants;
import flightbooking.util.ExcelExporter;
import flightbooking.util.ExcelImporter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PnlQuanLyMayBay extends JPanel {

    private final MayBayBUS mayBayBUS = new MayBayBUS();
    private final GheGeneratorBUS gheGenBUS = new GheGeneratorBUS();
    private final GheDAO gheDAO = new GheDAO();
    private final ChuyenBayBUS chuyenBayBUS = new ChuyenBayBUS();

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Tên máy bay", "Kiểu máy bay", "Tổng ghế"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    private final JTable table = new JTable(model);
    private final JTextField txtTen = new JTextField();
    private final JTextField txtKieu = new JTextField();

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnGen;
    private JButton btnEditSeat;
    private JButton btnExport;
    private JButton btnImport;

    public PnlQuanLyMayBay() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buildTop(), BorderLayout.NORTH);
        AdminTheme.styleTable(table, false);
        add(AdminTheme.wrapTable(table), BorderLayout.CENTER);
        table.getSelectionModel().addListSelectionListener(e -> fillForm());
        reload();
    }

    private JComponent buildTop() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        AdminTheme.styleSoftTextField(txtTen);
        AdminTheme.styleSoftTextField(txtKieu);
        GridBagConstraints lc = makeLc();
        GridBagConstraints fc = makeFc();

        lc.gridx = 0; lc.gridy = 0; form.add(makeLabel("Tên máy bay"), lc);
        fc.gridx = 1; fc.gridy = 0; form.add(txtTen, fc);
        lc.gridx = 2; lc.gridy = 0; form.add(makeLabel("Kiểu máy bay"), lc);
        fc.gridx = 3; fc.gridy = 0; form.add(txtKieu, fc);

        btnAdd      = AdminTheme.createActionButton("Thêm", AdminTheme.ButtonRole.ADD);
        btnUpdate   = AdminTheme.createActionButton("Sửa", AdminTheme.ButtonRole.EDIT);
        btnDelete   = AdminTheme.createActionButton("Xóa", AdminTheme.ButtonRole.DELETE);
        btnExport   = AdminTheme.createActionButton("Xuất Excel", AdminTheme.ButtonRole.NEUTRAL);
        btnImport   = AdminTheme.createActionButton("Nhập Excel", AdminTheme.ButtonRole.NEUTRAL);
        btnGen      = AdminTheme.createActionButton("Tạo ghế", AdminTheme.ButtonRole.NEUTRAL);
        btnEditSeat = AdminTheme.createActionButton("Sửa ghế", AdminTheme.ButtonRole.NEUTRAL);

        btnAdd.addActionListener(e -> addMayBay());
        btnUpdate.addActionListener(e -> updateMayBay());
        btnDelete.addActionListener(e -> deleteMayBay());
        btnGen.addActionListener(e -> openGenSeatDialog());
        btnEditSeat.addActionListener(e -> openEditSeatDialog());
        btnExport.addActionListener(e -> ExcelExporter.export(table, this));
        btnImport.addActionListener(e -> ExcelImporter.importToTable(table, this));

        return AdminTheme.wrapFormCard(form, btnAdd, btnUpdate, btnDelete, btnExport, btnImport, btnGen, btnEditSeat);
    }

    private void reload() {
        model.setRowCount(0);
        for (MayBayDTO m : mayBayBUS.dsMayBay()) {
            model.addRow(new Object[]{m.getMayBayId(), m.getTenMayBay(), m.getKieuMayBay(), m.getTongSoGhe()});
        }
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        txtTen.setText(String.valueOf(model.getValueAt(row, 1)));
        txtKieu.setText(String.valueOf(model.getValueAt(row, 2)));
    }

    private void addMayBay() {
        try {
            String ten = txtTen.getText().trim();
            String kieu = txtKieu.getText().trim();
            if (ten.isEmpty() || kieu.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
                return;
            }
            MayBayDTO dto = new MayBayDTO();
            dto.setTenMayBay(ten);
            dto.setKieuMayBay(kieu);
            if (dto.getSoTang() == null) dto.setSoTang(1);
            if (dto.getTongSoGhe() == null) dto.setTongSoGhe(0);
            mayBayBUS.themMayBay(dto);
            reload();
            clearForm();
            JOptionPane.showMessageDialog(this, "Thêm máy bay thành công!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateMayBay() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn máy bay cần sửa.");
            return;
        }
        try {
            String ten = txtTen.getText().trim();
            String kieu = txtKieu.getText().trim();
            if (ten.isEmpty() || kieu.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
                return;
            }
            MayBayDTO dto = new MayBayDTO();
            dto.setMayBayId(Integer.parseInt(String.valueOf(model.getValueAt(row, 0))));
            dto.setTenMayBay(ten);
            dto.setKieuMayBay(kieu);
            Object tongSoGheObj = model.getValueAt(row, 3);
            dto.setTongSoGhe(tongSoGheObj != null ? Integer.parseInt(String.valueOf(tongSoGheObj)) : 0);
            if (dto.getSoTang() == null) dto.setSoTang(1);
            mayBayBUS.capNhatMayBay(dto);
            reload();
            JOptionPane.showMessageDialog(this, "Cập nhật máy bay thành công!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteMayBay() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn máy bay cần xóa.");
            return;
        }
        int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa máy bay này không?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            mayBayBUS.xoaMayBay(id);
            reload();
            clearForm();
            JOptionPane.showMessageDialog(this, "Xóa máy bay thành công!");
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof org.postgresql.util.PSQLException) {
                org.postgresql.util.PSQLException sqlEx = (org.postgresql.util.PSQLException) cause;
                if ("23503".equals(sqlEx.getSQLState())) {
                    JOptionPane.showMessageDialog(this,
                            "Không thể xóa máy bay này vì đang được liên kết với dữ liệu khác.\nHãy xóa dữ liệu liên quan trước.",
                            "Lỗi ràng buộc dữ liệu",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openGenSeatDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn 1 máy bay trước.");
            return;
        }
        final int mayBayId = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));

        final JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Cấu hình ghế - Máy bay ID=" + mayBayId,
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setSize(920, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        final HangGheItem[] danhSachHangGhe = {
                new HangGheItem(3, "First Class"),
                new HangGheItem(2, "Business"),
                new HangGheItem(4, "Premium Economy"),
                new HangGheItem(1, "Economy")
        };

        final JPanel pnlConfigList = new JPanel();
        pnlConfigList.setLayout(new BoxLayout(pnlConfigList, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(pnlConfigList);

        JButton btnAddConfig = AdminTheme.createActionButton("+ Thêm hạng ghế", AdminTheme.ButtonRole.NEUTRAL);
        final List<ConfigRow> configRows = new ArrayList<>();

        JSpinner spTongHang = new JSpinner(new SpinnerNumberModel(0, 0, 200, 1));
        ((JSpinner.DefaultEditor) spTongHang.getEditor()).getTextField().setEditable(false);

        JSpinner spGheTrai = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));
        JSpinner spGhePhai = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));
        spGheTrai.setEnabled(false);
        spGhePhai.setEnabled(false);
        ((JSpinner.DefaultEditor) spGheTrai.getEditor()).getTextField().setEditable(false);
        ((JSpinner.DefaultEditor) spGhePhai.getEditor()).getTextField().setEditable(false);

        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTop.add(btnAddConfig);
        // pnlTop.add(new JLabel("Tổng hàng:"));
        // pnlTop.add(spTongHang);
        // pnlTop.add(new JLabel("Ghế trái:"));
        // pnlTop.add(spGheTrai);
        // pnlTop.add(new JLabel("Ghế phải:"));
        // pnlTop.add(spGhePhai);

        Runnable addRowUI = () -> {
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
            JComboBox<HangGheItem> cbHangGhe = new JComboBox<>(danhSachHangGhe);
            JSpinner spTongGhe = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));
            JButton btnRemove = AdminTheme.createActionButton("Xóa", AdminTheme.ButtonRole.NEUTRAL);

            rowPanel.add(new JLabel("Hạng:"));
            rowPanel.add(cbHangGhe);
            rowPanel.add(new JLabel("Tổng ghế:"));
            rowPanel.add(spTongGhe);
            rowPanel.add(btnRemove);

            pnlConfigList.add(rowPanel);
            pnlConfigList.revalidate();
            pnlConfigList.repaint();

            ConfigRow configData = new ConfigRow(cbHangGhe, spTongGhe, rowPanel);
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

        JButton btnOk = AdminTheme.createActionButton("Bắt đầu tạo", AdminTheme.ButtonRole.NEUTRAL);
        JButton btnCancel = AdminTheme.createActionButton("Hủy", AdminTheme.ButtonRole.NEUTRAL);

        btnOk.addActionListener(e -> {
            try {
                if (!gheDAO.findByMayBay(mayBayId).isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Máy bay đã có ghế. Hãy dùng nút 'Sửa ghế' để chỉnh.");
                    return;
                }

                Set<Integer> usedHangIds = new HashSet<>();
                List<CauHinhKhoangGheDTO> dsKhoang = new ArrayList<>();

                for (ConfigRow rowData : configRows) {
                    HangGheItem item = (HangGheItem) rowData.cbHangGhe.getSelectedItem();
                    if (item == null) continue;

                    if (usedHangIds.contains(item.id)) {
                        JOptionPane.showMessageDialog(dialog, "Hạng " + item.ten + " bị trùng.");
                        return;
                    }
                    usedHangIds.add(item.id);
                    dsKhoang.add(new CauHinhKhoangGheDTO(item.id, item.ten, (Integer) rowData.spTongGhe.getValue()));
                }

                int daTao = gheGenBUS.taoGheTheoSoDoMoi(
                        mayBayId,
                        (Integer) spTongHang.getValue(),
                        (Integer) spGheTrai.getValue(),
                        (Integer) spGhePhai.getValue(),
                        dsKhoang
                );

                MayBayDTO mbUpdate = new MayBayDTO();
                mbUpdate.setMayBayId(mayBayId);
                mbUpdate.setTenMayBay(String.valueOf(model.getValueAt(row, 1)));
                mbUpdate.setKieuMayBay(String.valueOf(model.getValueAt(row, 2)));
                mbUpdate.setTongSoGhe(daTao);
                mbUpdate.setSoTang(1);
                mayBayBUS.capNhatMayBay(mbUpdate);

                JOptionPane.showMessageDialog(dialog, "Thành công! Đã tạo " + daTao + " ghế.");
                dialog.dispose();
                reload();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(btnCancel);
        actions.add(btnOk);

        dialog.add(pnlTop, BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(actions, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void openEditSeatDialog() {
        
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn 1 máy bay trước.");
            return;
        }

        final int mayBayId = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
        if (chuyenBayBUS.mayBayDaDuocGanChuyenBay(mayBayId)) {
    JOptionPane.showMessageDialog(
            this,
            "Máy bay này đã được tạo chuyến bay nên không thể chỉnh sửa ghế.",
            "Không thể sửa ghế",
            JOptionPane.WARNING_MESSAGE
    );
    return;
}

        if (gheDAO.findByMayBay(mayBayId).isEmpty()) {
            JOptionPane.showMessageDialog(this, "Máy bay này chưa có ghế. Hãy dùng 'Tạo ghế' trước.");
            return;
        }

        final JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Chỉnh sửa ghế theo hạng - Máy bay ID=" + mayBayId,
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setSize(980, 560);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        final HangGheItem[] danhSachHangGhe = {
                new HangGheItem(3, "First Class"),
                new HangGheItem(2, "Business"),
                new HangGheItem(4, "Premium Economy"),
                new HangGheItem(1, "Economy")
        };

        final JPanel pnlConfigList = new JPanel();
        pnlConfigList.setLayout(new BoxLayout(pnlConfigList, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(pnlConfigList);

        JButton btnAddConfig = AdminTheme.createActionButton("+ Thêm hạng", AdminTheme.ButtonRole.NEUTRAL);
        final List<EditConfigRow> configRows = new ArrayList<>();

        // đang cố định 2-2 đúng theo bus/render bạn đang dùng
        JSpinner spGheTrai = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));
        JSpinner spGhePhai = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));
        spGheTrai.setEnabled(false);
        spGhePhai.setEnabled(false);
        ((JSpinner.DefaultEditor) spGheTrai.getEditor()).getTextField().setEditable(false);
        ((JSpinner.DefaultEditor) spGhePhai.getEditor()).getTextField().setEditable(false);

        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTop.add(new JLabel("Chỉnh sửa ghế theo hạng"));
        pnlTop.add(Box.createHorizontalStrut(14));
        pnlTop.add(btnAddConfig);
        pnlTop.add(Box.createHorizontalStrut(14));
        // pnlTop.add(new JLabel("Ghế trái:"));
        // pnlTop.add(spGheTrai);
        // pnlTop.add(new JLabel("Ghế phải:"));
        // pnlTop.add(spGhePhai);

        java.util.function.BiConsumer<HangGheItem, Integer> addEditRowUI = (hangItem, soLuong) -> {
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
            JComboBox<HangGheItem> cbHangGhe = new JComboBox<>(danhSachHangGhe);
            if (hangItem != null) cbHangGhe.setSelectedItem(findHangItemById(danhSachHangGhe, hangItem.id));

            JSpinner spTongGhe = new JSpinner(new SpinnerNumberModel(Math.max(soLuong, 1), 1, 1000, 1));
            JButton btnRemove = AdminTheme.createActionButton("Xóa hạng", AdminTheme.ButtonRole.NEUTRAL);

            rowPanel.add(new JLabel("Hạng:"));
            rowPanel.add(cbHangGhe);
            rowPanel.add(new JLabel("Tổng ghế mới:"));
            rowPanel.add(spTongGhe);
            rowPanel.add(btnRemove);

            pnlConfigList.add(rowPanel);
            pnlConfigList.revalidate();
            pnlConfigList.repaint();

            EditConfigRow configData = new EditConfigRow(cbHangGhe, spTongGhe, rowPanel);
            configRows.add(configData);

            btnRemove.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(
                        dialog,
                        "Bạn có chắc muốn xóa hạng ghế này khỏi cấu hình?",
                        "Xác nhận xóa hạng",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm != JOptionPane.YES_OPTION) return;

                pnlConfigList.remove(rowPanel);
                configRows.remove(configData);
                pnlConfigList.revalidate();
                pnlConfigList.repaint();
            });
        };

        // nạp cấu hình hiện tại từ ghế đang có
        for (HangGheItem item : danhSachHangGhe) {
            int count = gheDAO.countByMayBayAndHangGhe(mayBayId, item.id);
            if (count > 0) {
                addEditRowUI.accept(item, count);
            }
        }

        btnAddConfig.addActionListener(e -> addEditRowUI.accept(danhSachHangGhe[0], 1));

        JButton btnSave = AdminTheme.createActionButton("Lưu thay đổi", AdminTheme.ButtonRole.NEUTRAL);
        JButton btnCancel = AdminTheme.createActionButton("Hủy", AdminTheme.ButtonRole.NEUTRAL);

        btnSave.addActionListener(e -> {
            try {
                if (configRows.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Cấu hình phải có ít nhất 1 hạng ghế.");
                    return;
                }

                Set<Integer> usedHangIds = new HashSet<>();
                List<CauHinhKhoangGheDTO> dsKhoangMoi = new ArrayList<>();

                for (EditConfigRow rowData : configRows) {
                    HangGheItem item = (HangGheItem) rowData.cbHangGhe.getSelectedItem();
                    if (item == null) continue;

                    if (usedHangIds.contains(item.id)) {
                        JOptionPane.showMessageDialog(dialog, "Hạng " + item.ten + " bị trùng.");
                        return;
                    }
                    usedHangIds.add(item.id);

                    int tongGheMoi = (Integer) rowData.spTongGhe.getValue();
                    if (tongGheMoi <= 0) {
                        JOptionPane.showMessageDialog(dialog, "Tổng ghế mỗi hạng phải lớn hơn 0.");
                        return;
                    }

                    dsKhoangMoi.add(new CauHinhKhoangGheDTO(item.id, item.ten, tongGheMoi));
                }

                int confirm = JOptionPane.showConfirmDialog(
                        dialog,
                        "Việc sửa ghế sẽ tạo lại toàn bộ sơ đồ ghế của máy bay này.\nBạn có chắc muốn tiếp tục?",
                        "Xác nhận sửa ghế",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm != JOptionPane.YES_OPTION) return;

                int tongMoi = gheGenBUS.capNhatSoLuongGheTheoHang(
                        mayBayId,
                        (Integer) spGheTrai.getValue(),
                        (Integer) spGhePhai.getValue(),
                        dsKhoangMoi
                );

                MayBayDTO mbUpdate = new MayBayDTO();
                mbUpdate.setMayBayId(mayBayId);
                mbUpdate.setTenMayBay(String.valueOf(model.getValueAt(row, 1)));
                mbUpdate.setKieuMayBay(String.valueOf(model.getValueAt(row, 2)));
                mbUpdate.setTongSoGhe(tongMoi);
                mbUpdate.setSoTang(1);
                mayBayBUS.capNhatMayBay(mbUpdate);

                JOptionPane.showMessageDialog(dialog, "Đã cập nhật sơ đồ ghế thành công!");
                dialog.dispose();
                reload();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(btnCancel);
        actions.add(btnSave);

        dialog.add(pnlTop, BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(actions, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private HangGheItem findHangItemById(HangGheItem[] ds, int id) {
        for (HangGheItem item : ds) {
            if (item.id == id) return item;
        }
        return ds[0];
    }

    private static class ConfigRow {
        JComboBox<HangGheItem> cbHangGhe;
        JSpinner spTongGhe;
        JPanel panel;

        ConfigRow(JComboBox<HangGheItem> cbHangGhe, JSpinner spTongGhe, JPanel panel) {
            this.cbHangGhe = cbHangGhe;
            this.spTongGhe = spTongGhe;
            this.panel = panel;
        }
    }

    private static class EditConfigRow {
        JComboBox<HangGheItem> cbHangGhe;
        JSpinner spTongGhe;
        JPanel panel;

        EditConfigRow(JComboBox<HangGheItem> cbHangGhe, JSpinner spTongGhe, JPanel panel) {
            this.cbHangGhe = cbHangGhe;
            this.spTongGhe = spTongGhe;
            this.panel = panel;
        }
    }

    private static class HangGheItem {
        int id;
        String ten;

        HangGheItem(int id, String ten) {
            this.id = id;
            this.ten = ten;
        }

        @Override
        public String toString() {
            return ten;
        }
    }

    private void clearForm() {
        txtTen.setText("");
        txtKieu.setText("");
        table.clearSelection();
    }

    public void applyPermissions(List<Integer> actionIds) {
        btnAdd.setVisible(actionIds.contains(ActionConstants.THEM));
        btnUpdate.setVisible(actionIds.contains(ActionConstants.SUA));
        btnDelete.setVisible(actionIds.contains(ActionConstants.XOA));
        btnGen.setVisible(actionIds.contains(ActionConstants.TAO_GHE));
        btnEditSeat.setVisible(actionIds.contains(ActionConstants.TAO_GHE));
        btnExport.setVisible(actionIds.contains(ActionConstants.XUAT_EXCEL));
        btnImport.setVisible(actionIds.contains(ActionConstants.NHAP_EXCEL));
        revalidate();
        repaint();
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

    public void reloadData() {
        reload();
        clearForm();
        table.clearSelection();
        revalidate();
        repaint();
    }
}