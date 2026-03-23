package flightbooking.gui.admin;

import flightbooking.bus.HanhKhachBUS;
import flightbooking.dto.HanhKhachDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class PnlQuanLyHanhKhach extends JPanel {

    private final HanhKhachBUS bus = new HanhKhachBUS();

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID","Họ tên","Ngày sinh","Số giấy tờ","Quốc tịch"},0
    ){
        @Override
        public boolean isCellEditable(int row,int column){
            return false;
        }
    };

    private final JTable table = new JTable(model);

    private final JTextField txtHoTen = new JTextField();
    private final JTextField txtNgaySinh = new JTextField();
    private final JTextField txtSoGiayTo = new JTextField();
    private final JTextField txtQuocTich = new JTextField();

    private final JButton btnThem = new JButton("Thêm");
    private final JButton btnSua = new JButton("Sửa");
    private final JButton btnXoa = new JButton("Xóa");
    private final JButton btnRefresh = new JButton("Refresh");

    public PnlQuanLyHanhKhach(){

        setLayout(new BorderLayout());

        table.setRowHeight(25);
        add(new JScrollPane(table),BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(4,2,10,10));

        form.add(new JLabel("Họ tên"));
        form.add(txtHoTen);

        form.add(new JLabel("Ngày sinh (yyyy-MM-dd)"));
        form.add(txtNgaySinh);

        form.add(new JLabel("Số giấy tờ"));
        form.add(txtSoGiayTo);

        form.add(new JLabel("Quốc tịch"));
        form.add(txtQuocTich);

        JPanel actions = new JPanel();

        actions.add(btnThem);
        actions.add(btnSua);
        actions.add(btnXoa);
        actions.add(btnRefresh);

        JPanel south = new JPanel(new BorderLayout());
        south.add(form,BorderLayout.CENTER);
        south.add(actions,BorderLayout.SOUTH);

        add(south,BorderLayout.SOUTH);

        btnThem.addActionListener(e -> them());
        btnSua.addActionListener(e -> sua());
        btnXoa.addActionListener(e -> xoa());
        btnRefresh.addActionListener(e -> loadData());

        table.getSelectionModel().addListSelectionListener(e -> fillForm());

        loadData();
    }

    private void loadData(){

        model.setRowCount(0);

        List<HanhKhachDTO> list = bus.getAll();

        for(HanhKhachDTO hk : list){
            model.addRow(new Object[]{
                    hk.getHanhKhachId(),
                    hk.getHoTen(),
                    hk.getNgaySinh(),
                    hk.getSoGiayTo(),
                    hk.getQuocTich()
            });
        }
    }

    private void fillForm(){

        int row = table.getSelectedRow();

        if(row == -1) return;

        txtHoTen.setText(model.getValueAt(row,1).toString());

        Object ns = model.getValueAt(row,2);
        txtNgaySinh.setText(ns == null ? "" : ns.toString());

        txtSoGiayTo.setText(model.getValueAt(row,3)==null?"":model.getValueAt(row,3).toString());
        txtQuocTich.setText(model.getValueAt(row,4)==null?"":model.getValueAt(row,4).toString());
    }

    private void them(){

        try{

            HanhKhachDTO hk = new HanhKhachDTO();

            hk.setHoTen(txtHoTen.getText());

            if(!txtNgaySinh.getText().isEmpty())
                hk.setNgaySinh(LocalDate.parse(txtNgaySinh.getText()));

            hk.setSoGiayTo(txtSoGiayTo.getText());
            hk.setQuocTich(txtQuocTich.getText());

            bus.add(hk);

            loadData();

        }catch(Exception e){
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }

    private void sua(){

        int row = table.getSelectedRow();

        if(row==-1) return;

        try{

            int id = (int) model.getValueAt(row,0);

            HanhKhachDTO hk = new HanhKhachDTO();

            hk.setHanhKhachId(id);
            hk.setHoTen(txtHoTen.getText());

            if(!txtNgaySinh.getText().isEmpty())
                hk.setNgaySinh(LocalDate.parse(txtNgaySinh.getText()));

            hk.setSoGiayTo(txtSoGiayTo.getText());
            hk.setQuocTich(txtQuocTich.getText());

            bus.update(hk);

            loadData();

        }catch(Exception e){
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }

    private void xoa(){

        int row = table.getSelectedRow();

        if(row==-1) return;

        int id = (int) model.getValueAt(row,0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Xóa hành khách?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );

        if(confirm==JOptionPane.YES_OPTION){
            bus.delete(id);
            loadData();
        }
    }
}