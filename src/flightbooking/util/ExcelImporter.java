package flightbooking.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.FileInputStream;

public class ExcelImporter {

    public static void importToTable(JTable table, java.awt.Component parent) {

        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Chọn file Excel");

            if (chooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) return;

            File file = chooser.getSelectedFile();

            FileInputStream fis = new FileInputStream(file);
            Workbook wb = new XSSFWorkbook(fis);
            Sheet sheet = wb.getSheetAt(0);

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            boolean isHeader = true;

            for (Row row : sheet) {

                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                Object[] data = new Object[row.getLastCellNum()];

                for (int i = 0; i < row.getLastCellNum(); i++) {
                    Cell cell = row.getCell(i);
                    data[i] = (cell == null) ? "" : cell.toString();
                }

                model.addRow(data);
            }

            wb.close();
            fis.close();

            JOptionPane.showMessageDialog(parent, "Import Excel thành công!");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Lỗi import Excel: " + e.getMessage());
        }
    }
}