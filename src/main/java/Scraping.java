import config.GetDriver;

import module.Browsing;
import module.ExcelManipulate;
import module.InputProcessor;
import obj.Time;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.lang.*;

public class Scraping {
    public static void main(String[] args) throws Exception {
        // Get target month and last month from the input
        InputProcessor inputProcessor = new InputProcessor(args);
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