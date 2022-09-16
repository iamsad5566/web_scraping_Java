import java.util.*;
import java.util.NoSuchElementException;

import config.EnvironmentVariable;
import config.GetDriver;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import module.Browsing;
import module.ExcelManipulate;
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
    public static void main(String[] args) throws Exception {
        // Get target month and last month from the input
        InputProcessor inputProcessor = new InputProcessor();
        Time thisMonth = inputProcessor.getThisMonth();
        Time lastMonth = inputProcessor.getLastMonth(thisMonth);
        System.out.printf("Target year: %S, month: %S\n", thisMonth.getYear(),
                thisMonth.getMonth());

        // Start chrome driver
        GetDriver getDriver = new GetDriver();
        WebDriver driver = new ChromeDriver();
        var list = Browsing.getInstance().BrowseController(driver, thisMonth, lastMonth);

        if (list.size() > 0) {
            ExcelManipulate.getInstance().saveDataInExcel(list, getDriver.getPath());
        } else {
            System.out.println("No data!");
        }
    }
}