import java.util.*;
import java.util.NoSuchElementException;

import config.EnvironmentVariable;
import config.GetDriver;
import module.InputProcessor;
import obj.Time;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import java.lang.*;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

public class Crawler {
    // Create a Workbook first so that all methods below can use
    static Workbook wb = new HSSFWorkbook();
    // Set the pathway of the chromedriver.exe and create a drive
    GetDriver getDriver = new GetDriver();

    public static void main(String[] args) throws Exception {
        // Get target month and last month from the input
        InputProcessor inputProcessor = new InputProcessor();
        Time thisMonth = inputProcessor.getThisMonth();
        Time lastMonth = inputProcessor.getLastMonth(thisMonth);

        System.out.printf("Year: %S, Month: %S\n", thisMonth.getYear(), thisMonth.getMonth());
        System.out.printf("Year: %S, Month: %S\n", lastMonth.getYear(), lastMonth.getMonth());

        WebDriver driver = new ChromeDriver();

        // Add the sheets
        Sheet first = wb.createSheet("各類所得（受試者費）");
        Sheet second = wb.createSheet("各計畫當月總支出");
        Sheet third = wb.createSheet("受試者費以外的支出");
        Sheet fourth = wb.createSheet("個人當月代墊總和");


//
//        // 移到報帳管理，建立sheet2
//        var list_funding = browser_funding(driver, thisMonth.getYear() + thisMonth.getMonth(), lastMonth.getYear() + lastMonth.getMonth());
//
//        // Build the first sheet
//        buildSheet1(list_funding, first);
//
//        // Build the second sheet
//        buildSheet2(list_funding, second, first);
//
//        // Build the third sheet，計算實驗室該月除了受試者費外的支出
//        buildSheet3(first, second, third);
//
//        // 計算每個人當月代墊
//        buildSheet4(first, third, fourth);
//
//
//        StringBuilder sb = new StringBuilder();
//        String[] date_arr = new Date().toString().split(" ");
//        sb.append(date_arr[1]).append('_').append(date_arr[2]);
//        FileOutputStream fileOut = new FileOutputStream(getDriver.getPath() + sb.toString() + ".xls");
//        wb.write(fileOut);
//        fileOut.close();
    }

    public static List<String> browser_funding(WebDriver driver, String thisMonth, String lastMonth) throws InterruptedException, NoSuchElementException {
        // Direct to NTU web
        driver.get("https://ntuacc.cc.ntu.edu.tw/acc/index.asp?campno=m&idtype=3");
        // Let's Login
        driver.findElement(new By.ByXPath("//*[@id=\"bossid\"]")).sendKeys("hsiehpj");
        driver.findElement(new By.ByXPath("//*[@id=\"assid\"]")).sendKeys("yenju0115");
        driver.findElement(new By.ByXPath("//*[@id=\"asspwd\"]")).sendKeys("110803Ben");
        driver.findElement(new By.ByXPath("//*[@id=\"vsub\"]/td/input")).click();

        String targetYear = thisMonth.substring(0, 3);

        //  移動到報帳管理/計畫經費報帳
        driver.navigate().to("https://ntuacc.cc.ntu.edu.tw/acc/apply/list.asp?yearchoose=" + targetYear);

        // List to return
        var list = new ArrayList<String>();

        // 直到抓到上月份的經費，break
        // 換網頁(網域)後會刪除原先儲存的資料，所以每換一次要重抓一次
        outer:
        for (; ; ) {
            var plans = driver.findElements(By.tagName("tr"));
            for (int i = 0; i < plans.size(); i++) {
                if (plans.get(i).getText().startsWith(targetYear + "T")) {
                    StringBuilder sb = new StringBuilder();

                    for (String s : plans.get(i).getText().split(" ")) {
                        if (!s.equals("各類所得") && !s.equals("勞健保月薪(勞退新制)")) {
                            sb.append(s).append(" ");
                        }
                    }

                    if (sb.toString().split(" ")[5].startsWith(lastMonth))
                        break outer;

                    if (sb.toString().split(" ").length < 11 || !sb.toString().split(" ")[5].startsWith(thisMonth))
                        continue;

                    // 點進去找 name
                    Actions act = new Actions(driver);
                    act.doubleClick(plans.get(i)).perform();
                    String nameInfo = driver.findElement(By.xpath("/html/body/center/center/table[1]/tbody/tr[2]/td[2]")).getText();
                    for (char c : nameInfo.toCharArray()) {
                        if (c != ' ')
                            sb.append(c);
                    }
                    // 以上為與 js 互動的 part

                    // 如果是受試者費，儲存稅前的金額
                    if (sb.toString().contains("受試者費"))
                        sb.append(" ").append(driver.findElement(By.xpath("/html/body/center/center/table[1]/tbody/tr[2]/td[3]")).getText());

                    // 存入 list
                    list.add(sb.toString());

                    System.out.println(sb);
                    driver.findElement(By.name("back")).click();

                    // 重抓一次所有計畫
                    plans = driver.findElements(By.tagName("tr"));
                }
            }

            try {
                WebElement nextPage = driver.findElement(By.linkText("下一頁"));
                nextPage.click();
            } catch (RuntimeException e) {
                System.out.println("End");
                break;
            }
        }

        Thread.sleep(1000);
        driver.quit();
        return list;
    }

    // First sheet
    public static void buildSheet1(List<String> list, Sheet first) throws Exception {
        //顯示標題
        Row title_row = first.createRow(0);
        title_row.setHeight((short) (40 * 20));
        Cell title_cell = title_row.createCell(0);

        // Set up the header
        String headers[] = new String[]{"報帳條碼", "經費或計畫名稱", "計畫代碼", "經費別", "金額", "報帳日", "傳票號碼", "付款資料", "報帳ID", "列印次數", "受款人", "備註", "稅前金額"};
        Row header_row = first.createRow(1);
        header_row.setHeight((short) (20 * 24));

        // 只留有稅錢金額的 data
        var experimentList = new ArrayList<String>();
        for (String s : list) {
            if (s.split(" ").length == headers.length)
                experimentList.add(s);
        }

        //建立單元格的 顯示樣式
        CellStyle style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER); //水平方向上的對其方式
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);    //垂直方向上的對其方式

        title_cell.setCellStyle(style);
        title_cell.setCellValue("各類所得（受試者費）");

        first.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));

        // 把 header 的每一個 string 加上
        for (int i = 0; i < headers.length; i++) {
            //設定列寬   基數為256
            first.setColumnWidth(i, 30 * 256);
            Cell cell = header_row.createCell(i);
            //應用樣式到  單元格上
            cell.setCellStyle(style);
            cell.setCellValue(headers[i]);
        }

        // Fill in the data
        for (int i = 0; i < experimentList.size(); i++) {
            Row row = first.createRow(i + 2);
            row.setHeight((short) (20 * 20)); //設定行高  基數為20
            for (int j = 0; j < experimentList.get(i).split(" ").length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(experimentList.get(i).split(" ")[j]);
            }
        }
    }

    // Second sheet
    public static void buildSheet2(List<String> list, Sheet second, Sheet first) {
        // Sheet2 title
        Row title_row = second.createRow(0);
        title_row.setHeight((short) (40 * 20));
        Cell title_cell = title_row.createCell(0);

        String headers[] = new String[]{"報帳條碼", "經費或計畫名稱", "計畫代碼", "經費別", "金額", "報帳日", "傳票號碼", "付款資料", "報帳ID", "列印次數", "受款人", "備註", "金額"};
        Row header_row = second.createRow(1);
        header_row.setHeight((short) (20 * 24));

        //建立單元格的 顯示樣式
        CellStyle style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER); //水平方向上的對其方式
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);    //垂直方向上的對其方式
        title_cell.setCellStyle(style);
        title_cell.setCellValue("計畫經費報帳");
        second.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));

        for (int i = 0; i < headers.length; i++) {
            //設定列寬   基數為256
            second.setColumnWidth(i, 30 * 256);
            Cell cell = header_row.createCell(i);
            //應用樣式到  單元格上
            cell.setCellStyle(style);
            cell.setCellValue(headers[i]);
        }

        for (int i = 0; i < list.size(); i++) {
            Row row = second.createRow(i + 2);
            row.setHeight((short) (20 * 20)); //設定行高  基數為20
            for (int j = 0; j < list.get(i).split(" ").length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(list.get(i).split(" ")[j]);
            }
        }
    }

    public static void buildSheet3(Sheet first, Sheet second, Sheet third) {
        String headers[] = new String[]{"報帳條碼", "經費或計畫名稱", "計畫代碼", "經費別", "金額", "報帳日", "傳票號碼", "付款資料", "報帳ID", "列印次數", "受款人", "備註"};
        Row header_row = third.createRow(0);
        header_row.setHeight((short) (20 * 24));

        //建立單元格的 顯示樣式
        CellStyle style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER); //水平方向上的對其方式
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);    //垂直方向上的對其方式

        for (int i = 0; i < headers.length; i++) {
            //設定列寬   基數為256
            third.setColumnWidth(i, 30 * 256);
            Cell cell = header_row.createCell(i);
            //應用樣式到  單元格上
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }

        var barCode = new ArrayList<String>();
        for (int i = 2; first.getRow(i) != null; i++)
            barCode.add(first.getRow(i).getCell(0).getStringCellValue());

        int targetRow = 1;
        for (int i = 2; second.getRow(i) != null; i++) {
            if (barCode.contains(second.getRow(i).getCell(0).getStringCellValue()))
                continue;

            Row row = third.createRow(targetRow);
            row.setHeight((short) (20 * 20));
            for (int j = 0; j < headers.length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(second.getRow(i).getCell(j).getStringCellValue());
                cell.setCellStyle(style);
            }
            targetRow++;
        }
    }

    public static void buildSheet4(Sheet first, Sheet third, Sheet fourth) {
        String[] header = {"彥茹", "家毓", "昀潔", "宇安", "昀安", "宇昕", "林懿", "品程", "Yaron"};
        Row header_row = fourth.createRow(0);
        header_row.setHeight((short) (20 * 24));

        for (int i = 0; i < header.length; i++) {
            Cell cell = header_row.createCell(i);
            cell.setCellValue(header[i]);
        }

        // 要算每個人代墊筆數的 array
        int[] rowNum = new int[header.length];
        Arrays.fill(rowNum, 1);

        int index = 1;
        for (int i = 2; first.getRow(i) != null; i++, index++) {
            Row row = fourth.createRow(index);
            row.setHeight((short) (20 * 20));
            for (int j = 0; j < header.length; j++)
                row.createCell(j);
        }

        for (int i = 1; third.getRow(i) != null; i++, index++) {
            Row row = fourth.createRow(index);
            row.setHeight((short) (20 * 20));
            for (int j = 0; j < header.length; j++)
                row.createCell(j);
        }

        //  從第一個 column 開始
        for (int i = 0; i < header.length; i++) {

            // 抓 first sheet，第二個 row 以後的資料
            for (int j = 2; first.getRow(j) != null; j++) {
                Cell target = fourth.getRow(rowNum[i]).getCell(i);
                String name = first.getRow(j).getCell(11).getStringCellValue();
                String money = first.getRow(j).getCell(12).getStringCellValue();
                if (name.contains(header[i])) {
                    target.setCellValue(money);
                    rowNum[i]++;
                }
            }

            // 抓 third sheet 第一個 row 以後的資料
            for (int j = 1; third.getRow(j) != null; j++) {
                Cell target = fourth.getRow(rowNum[i]).getCell(i);
                String name = third.getRow(j).getCell(11).getStringCellValue();
                String money = third.getRow(j).getCell(4).getStringCellValue();
                if (name.contains(header[i])) {
                    target.setCellValue(money);
                    rowNum[i]++;
                }
            }
        }

        // 計算每個人代墊總金額
        Row row = fourth.createRow(index);
        row.setHeight((short) (20 * 20));

        // 從第一個 column 開始
        for (int i = 0; i < header.length; i++) {
            int sum = 0;

            // rowNum 記錄每一個人代墊幾筆
            for (int j = 1; j <= rowNum[i]; j++) {
                String sValue = fourth.getRow(j).getCell(i).getStringCellValue();
                StringBuilder sb = new StringBuilder();
                for (char c : sValue.toCharArray()) {
                    if (Character.isDigit(c))
                        sb.append(c);
                }
                if (sb.length() != 0)
                    sum += Integer.valueOf(sb.toString());
            }
            Cell cell = fourth.getRow(index).createCell(i);
            cell.setCellValue(sum);
        }
    }
}