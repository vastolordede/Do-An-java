package flightbooking.util;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.awt.Component;

public class ExcelExporter {

    public static void export(JTable table, Component parent) {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Chọn nơi lưu Excel");

            if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) return;

            File file = chooser.getSelectedFile();
            if (!file.getName().endsWith(".xlsx")) {
                file = new File(file.getAbsolutePath() + ".xlsx");
            }

            Workbook wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet("Data");

            TableModel model = table.getModel();

            // Header
            Row header = sheet.createRow(0);
            for (int i = 0; i < model.getColumnCount(); i++) {
                header.createCell(i).setCellValue(model.getColumnName(i));
            }

            // Data
            for (int i = 0; i < model.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object val = model.getValueAt(i, j);
                    row.createCell(j).setCellValue(val == null ? "" : val.toString());
                }
            }

            FileOutputStream fos = new FileOutputStream(file);
            wb.write(fos);
            wb.close();
            fos.close();

            JOptionPane.showMessageDialog(parent, "Xuất Excel thành công!");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Lỗi xuất Excel: " + ex.getMessage());
        }
    }
}