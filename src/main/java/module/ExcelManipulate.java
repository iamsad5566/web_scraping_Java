package module;

import config.EnvironmentVariable;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

public class ExcelManipulate {
    private static ExcelManipulate excelManipulate;
    static Workbook wb = new HSSFWorkbook();

    private ExcelManipulate() {

    }

    public static ExcelManipulate getInstance() {
        if (excelManipulate == null) {
            excelManipulate = new ExcelManipulate();
        }

        return excelManipulate;
    }

    public void saveDataInExcel(List<String> list, String path) throws IOException {
        Sheet firstSheet = createFirstSheet(list);
        Sheet secondSheet = createSecondSheet(list);
        Sheet thirdSheet = createThirdSheet(firstSheet, secondSheet);
        createFourthSheet(firstSheet, thirdSheet);

        StringBuilder sb = new StringBuilder();
        String[] date_arr = new Date().toString().split(" ");
        sb.append(date_arr[1]).append('_').append(date_arr[2]);
        FileOutputStream fileOut = new FileOutputStream(path + "/" + sb + ".xls");
        wb.write(fileOut);
        fileOut.close();
    }

    private Sheet createFirstSheet(List<String> list) throws FileNotFoundException {
        Sheet first = wb.createSheet("各類所得（受試者費）");
        Row title_row = first.createRow(0);
        title_row.setHeight((short) (40 * 20));
        Cell title_cell = title_row.createCell(0);

        // Set up the header
        String headers[] = new String[]{"報帳條碼", "經費或計畫名稱", "計畫代碼", "經費別", "金額", "報帳日", "傳票號碼",
                "付款資料", "報帳ID", "列印次數", "受款人", "備註", "稅前金額"};

        Row header_row = first.createRow(1);
        header_row.setHeight((short) (20 * 24));

        // 只留有稅前金額的 data
        var experimentList = new ArrayList<String>();
        for (String s : list) {
            if (s.contains("受試者費")) {
                s = DataProcessor.getInstance().reConstructStringByMember(s);
                experimentList.add(s);
            }
        }

        //建立單元格的 顯示樣式
        CellStyle style = createStyle(title_cell, "各類所得（受試者費）");
        first.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));

        // 把 header 的每一個 string 加上
        for (int i = 0; i < headers.length; i++) {
            //設定列寬   基數為256
            first.setColumnWidth(i, 30 * 128);
            Cell cell = header_row.createCell(i);
            //應用樣式到  單元格上
            cell.setCellStyle(style);
            cell.setCellValue(headers[i]);
        }

        // Fill in the data
        for (int i = 0; i < experimentList.size(); i++) {
            Row row = first.createRow(i + 2);
            row.setHeight((short) (20 * 20)); //設定行高  基數為20
            for (int j = 0; j < experimentList.get(i).split("//").length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(experimentList.get(i).split("//")[j]);
            }
        }
        return first;
    }

    private Sheet createSecondSheet(List<String> list) {
        Sheet second = wb.createSheet("各計畫當月總支出");
        // Sheet2 title
        Row title_row = second.createRow(0);
        title_row.setHeight((short) (40 * 20));
        Cell title_cell = title_row.createCell(0);

        String headers[] = new String[]{"報帳條碼", "經費或計畫名稱", "計畫代碼", "經費別", "金額", "報帳日", "傳票號碼",
                "付款資料", "報帳ID", "列印次數", "受款人", "備註"};
        Row header_row = second.createRow(1);
        header_row.setHeight((short) (20 * 24));

        //建立單元格的 顯示樣式
        CellStyle style = createStyle(title_cell, "計畫經費報帳");
        second.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));

        for (int i = 0; i < headers.length; i++) {
            //設定列寬   基數為256
            second.setColumnWidth(i, 30 * 128);
            Cell cell = header_row.createCell(i);
            //應用樣式到  單元格上
            cell.setCellStyle(style);
            cell.setCellValue(headers[i]);
        }

        for (int i = 0; i < list.size(); i++) {
            Row row = second.createRow(i + 2);
            row.setHeight((short) (20 * 20)); //設定行高  基數為20
            for (int j = 0; j < list.get(i).split("//").length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(list.get(i).split("//")[j]);
            }
        }
        return second;
    }

    private Sheet createThirdSheet(Sheet first, Sheet second) {
        Sheet third = wb.createSheet("受試者費以外的支出");
        // Sheet3 title
        Row title_row = third.createRow(0);
        title_row.setHeight((short) (40 * 20));
        Cell title_cell = title_row.createCell(0);

        String headers[] = new String[]{"報帳條碼", "經費或計畫名稱", "計畫代碼", "經費別", "金額", "報帳日", "傳票號碼",
                "付款資料", "報帳ID", "列印次數", "受款人", "備註"};
        Row header_row = third.createRow(1);
        header_row.setHeight((short) (20 * 24));

        //建立單元格的 顯示樣式
        CellStyle style = createStyle(title_cell, "受試者費外之支出");
        third.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));

        for (int i = 0; i < headers.length; i++) {
            //設定列寬   基數為256
            third.setColumnWidth(i, 30 * 128);
            Cell cell = header_row.createCell(i);
            //應用樣式到  單元格上
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }

        var barCode = new ArrayList<String>();
        for (int i = 2; first.getRow(i) != null; i++) {
            barCode.add(first.getRow(i).getCell(0).getStringCellValue());
        }

        int targetRow = 2;
        for (int i = 2; second.getRow(i) != null; i++) {
            if (barCode.contains(second.getRow(i).getCell(0).getStringCellValue())) {
                continue;
            }

            Row row = third.createRow(targetRow);
            row.setHeight((short) (20 * 20));
            for (int j = 0; j < headers.length; j++) {
                Cell cell = row.createCell(j);
                try {
                    cell.setCellValue(second.getRow(i).getCell(j).getStringCellValue());
                } catch(NullPointerException n) {
                    System.out.println(i);
                }
                cell.setCellStyle(style);
            }
            targetRow++;
        }
        return third;
    }

    private void createFourthSheet(Sheet first, Sheet third) throws FileNotFoundException {
        Sheet fourth = wb.createSheet("個人當月代墊總和");
        // Sheet4 title
        Row title_row = fourth.createRow(0);
        title_row.setHeight((short) (40 * 20));
        Cell title_cell = title_row.createCell(0);

        var header = (ArrayList<String>) EnvironmentVariable.getInstance().data.get("member");
        Row header_row = fourth.createRow(1);
        header_row.setHeight((short) (20 * 24));

        //建立單元格的 顯示樣式
        CellStyle style = createStyle(title_cell, "個人當月代墊總和");
        fourth.addMergedRegion(new CellRangeAddress(0, 0, 0, header.size() - 1));

        for (int i = 0; i < header.size(); i++) {
            fourth.setColumnWidth(i, 30 * 128);
            Cell cell = header_row.createCell(i);
            cell.setCellValue(header.get(i));
            cell.setCellStyle(style);
        }

        // 要算每個人代墊筆數的 array
        int[] rowNum = new int[header.size()];
        Arrays.fill(rowNum, 2);

        int index = 2;
        for (int i = 2; first.getRow(i) != null; i++, index++) {
            Row row = fourth.createRow(index);
            row.setHeight((short) (20 * 20));
            for (int j = 0; j < header.size(); j++) {
                row.createCell(j);
            }
        }

        for (int i = 1; third.getRow(i) != null; i++, index++) {
            Row row = fourth.createRow(index);
            row.setHeight((short) (20 * 20));
            for (int j = 0; j < header.size(); j++) {
                row.createCell(j);
            }
        }

        //  從第一個 column 開始
        for (int i = 0; i < header.size(); i++) {

            // 抓 first sheet，第二個 row 以後的資料
            for (int j = 2; first.getRow(j) != null; j++) {
                Cell target = fourth.getRow(rowNum[i]).getCell(i);
                try {
                    String name = first.getRow(j).getCell(11).getStringCellValue();
                    String money = first.getRow(j).getCell(12).getStringCellValue();
                    if (name.contains(header.get(i))) {
                        target.setCellValue(money);
                        rowNum[i]++;
                    }
                } catch (NullPointerException e) {
                    System.out.println(e);
                }
            }

            // 抓 third sheet 第一個 row 以後的資料
            for (int j = 1; third.getRow(j) != null; j++) {
                Cell target = fourth.getRow(rowNum[i]).getCell(i);
                String name = third.getRow(j).getCell(11).getStringCellValue();
                String money = third.getRow(j).getCell(4).getStringCellValue();
                if (name.contains(header.get(i))) {
                    target.setCellValue(money);
                    rowNum[i]++;
                }
            }
        }

        // 計算每個人代墊總金額
        Row row = fourth.createRow(index);
        row.setHeight((short) (20 * 20));

        // 從第一個 column 開始
        for (int i = 0; i < header.size(); i++) {
            int sum = 0;

            // rowNum 記錄每一個人代墊幾筆
            for (int j = 2; j <= rowNum[i]; j++) {
                String sValue = fourth.getRow(j).getCell(i).getStringCellValue();
                StringBuilder sb = new StringBuilder();
                for (char c : sValue.toCharArray()) {
                    if (Character.isDigit(c)) {
                        sb.append(c);
                    }
                }
                if (sb.length() != 0) {
                    sum += Integer.valueOf(sb.toString());
                }
            }
            Cell cell = fourth.getRow(index).createCell(i);
            cell.setCellValue(sum);
        }
    }

    private CellStyle createStyle(Cell titleCell, String title) {
        //建立單元格的 顯示樣式
        CellStyle style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER); //水平方向上的對其方式
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);    //垂直方向上的對其方式
        titleCell.setCellStyle(style);
        titleCell.setCellValue(title);
        return style;
    }
}
