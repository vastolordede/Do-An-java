package flightbooking.gui.admin.pnl;

import flightbooking.bus.GheGeneratorBUS;
import flightbooking.bus.MayBayBUS;
import flightbooking.dao.GheDAO;
import flightbooking.dto.CauHinhKhoangGheDTO;
import flightbooking.dto.MayBayDTO;
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

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Tên máy bay", "Kiểu máy bay", "Tổng ghế"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

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
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildTop(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> fillForm());

        reload();
    }

    private JComponent buildTop() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints lc = makeLc();
        GridBagConstraints fc = makeFc();

        lc.gridx = 0;
        lc.gridy = 0;
        form.add(makeLabel("Tên máy bay"), lc);

        fc.gridx = 1;
        fc.gridy = 0;
        styleField(txtTen);
        form.add(txtTen, fc);

        lc.gridx = 2;
        lc.gridy = 0;
        form.add(makeLabel("Kiểu máy bay"), lc);

        fc.gridx = 3;
        fc.gridy = 0;
        styleField(txtKieu);
        form.add(txtKieu, fc);

        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnGen = new JButton("Tạo ghế cho máy bay đang chọn");
        btnExport = new JButton("Xuất Excel");
        btnImport = new JButton("Nhập Excel");

        btnAdd.addActionListener(e -> addMayBay());
        btnUpdate.addActionListener(e -> updateMayBay());
        btnDelete.addActionListener(e -> deleteMayBay());
        btnGen.addActionListener(e -> openGenSeatDialog());
        btnExport.addActionListener(e -> ExcelExporter.export(table, this));
        btnImport.addActionListener(e -> ExcelImporter.importToTable(table, this));

        return wrapWithActions(form, btnAdd, btnUpdate, btnDelete, btnExport, btnImport, btnGen);
    }

    private void reload() {
        model.setRowCount(0);

        List<MayBayDTO> ds = mayBayBUS.dsMayBay();
        for (MayBayDTO m : ds) {
            model.addRow(new Object[]{
                    m.getMayBayId(),
                    m.getTenMayBay(),
                    m.getKieuMayBay(),
                    m.getTongSoGhe()
            });
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

            if (dto.getSoTang() == null) {
                dto.setSoTang(1);
            }
            if (dto.getTongSoGhe() == null) {
                dto.setTongSoGhe(0);
            }

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
            if (tongSoGheObj != null) {
                dto.setTongSoGhe(Integer.parseInt(String.valueOf(tongSoGheObj)));
            } else {
                dto.setTongSoGhe(0);
            }

            if (dto.getSoTang() == null) {
                dto.setSoTang(1);
            }

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

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa máy bay này không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            mayBayBUS.xoaMayBay(id);
            reload();
            clearForm();
            JOptionPane.showMessageDialog(this, "Xóa máy bay thành công!");
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof org.postgresql.util.PSQLException) {
                org.postgresql.util.PSQLException sqlEx =
                        (org.postgresql.util.PSQLException) cause;

                if ("23503".equals(sqlEx.getSQLState())) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Không thể xóa máy bay này vì đang được liên kết với dữ liệu khác.\n"
                                    + "Hãy xóa dữ liệu liên quan trước.",
                            "Lỗi ràng buộc dữ liệu",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
            }

            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // CHỈ HIỂN THỊ PHẦN openGenSeatDialog + ConfigRow (phần còn lại giữ nguyên file bạn)

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

    final HangGheItem[] danhSachHangGhe = new HangGheItem[]{
            new HangGheItem(3, "First Class"),
            new HangGheItem(2, "Business"),
            new HangGheItem(4, "Premium Economy"),
            new HangGheItem(1, "Economy")
    };

    final JPanel pnlConfigList = new JPanel();
    pnlConfigList.setLayout(new BoxLayout(pnlConfigList, BoxLayout.Y_AXIS));

    JScrollPane scrollPane = new JScrollPane(pnlConfigList);

    JButton btnAddConfig = new JButton("+ Thêm hạng ghế");
    final List<ConfigRow> configRows = new ArrayList<>();

    // ===== INPUT CHUNG =====
    JSpinner spTongHang = new JSpinner(new SpinnerNumberModel(20, 1, 200, 1));
    JSpinner spGheTrai = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));
    JSpinner spGhePhai = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));

    spGheTrai.setEnabled(false);
spGhePhai.setEnabled(false);

((JSpinner.DefaultEditor) spGheTrai.getEditor()).getTextField().setEditable(false);
((JSpinner.DefaultEditor) spGhePhai.getEditor()).getTextField().setEditable(false);

    JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
    pnlTop.add(btnAddConfig);
    pnlTop.add(new JLabel("Tổng hàng:"));
    pnlTop.add(spTongHang);
    pnlTop.add(new JLabel("Ghế trái:"));
    pnlTop.add(spGheTrai);
    pnlTop.add(new JLabel("Ghế phải:"));
    pnlTop.add(spGhePhai);

    // ===== ADD ROW =====
    Runnable addRowUI = () -> {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));

        JComboBox<HangGheItem> cbHangGhe = new JComboBox<>(danhSachHangGhe);
        JSpinner spTongGhe = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));
        JButton btnRemove = new JButton("Xóa");

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

    JButton btnOk = new JButton("Bắt đầu tạo");
    JButton btnCancel = new JButton("Hủy");

    btnOk.addActionListener(e -> {
        try {
            if (!gheDAO.findByMayBay(mayBayId).isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Máy bay đã có ghế. Xóa ghế cũ trước.");
                return;
            }

            Set<Integer> usedHangIds = new HashSet<>();
            List<CauHinhKhoangGheDTO> dsKhoang = new ArrayList<>();

            for (ConfigRow rowData : configRows) {

                HangGheItem item = (HangGheItem) rowData.cbHangGhe.getSelectedItem();

                if (usedHangIds.contains(item.id)) {
                    JOptionPane.showMessageDialog(dialog,
                            "Hạng " + item.ten + " bị trùng.");
                    return;
                }
                usedHangIds.add(item.id);

                int tongSoGhe = (Integer) rowData.spTongGhe.getValue();

                CauHinhKhoangGheDTO dto =
                        new CauHinhKhoangGheDTO(item.id, item.ten, tongSoGhe);

                dsKhoang.add(dto);
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

            JOptionPane.showMessageDialog(dialog,
                    "Thành công! Đã tạo " + daTao + " ghế.");
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

// ===== DTO ROW =====
private static class ConfigRow {
    JComboBox<HangGheItem> cbHangGhe;
    JSpinner spTongGhe;
    JPanel panel;

    public ConfigRow(JComboBox<HangGheItem> cbHangGhe,
                     JSpinner spTongGhe,
                     JPanel panel) {
        this.cbHangGhe = cbHangGhe;
        this.spTongGhe = spTongGhe;
        this.panel = panel;
    }
}

    private void clearForm() {
        txtTen.setText("");
        txtKieu.setText("");
        table.clearSelection();
    }

    

    private static class HangGheItem {
        int id;
        String ten;

        public HangGheItem(int id, String ten) {
            this.id = id;
            this.ten = ten;
        }

        @Override
        public String toString() {
            return ten;
        }
    }

    public void applyPermissions(List<Integer> actionIds) {
        btnAdd.setVisible(actionIds.contains(ActionConstants.THEM));
        btnUpdate.setVisible(actionIds.contains(ActionConstants.SUA));
        btnDelete.setVisible(actionIds.contains(ActionConstants.XOA));
        btnGen.setVisible(actionIds.contains(ActionConstants.TAO_GHE));
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
        for (JButton b : buttons) {
            actions.add(b);
        }

        JPanel wrap = new JPanel(new BorderLayout(0, 8));
        wrap.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        wrap.add(form, BorderLayout.CENTER);
        wrap.add(actions, BorderLayout.SOUTH);
        return wrap;
    }
}