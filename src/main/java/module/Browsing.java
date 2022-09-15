package module;

import config.EnvironmentVariable;
import obj.ObjProcessor;
import obj.Time;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Browsing {
    ObjProcessor objProcessor = new ObjProcessor();

    public List<String> BrowseController(WebDriver driver, Time thisMonthAndYear, Time lastMonthAndYear) throws FileNotFoundException {
        // Direct to NTU web
        driver.get("https://ntuacc.cc.ntu.edu.tw/acc/index.asp?campno=m&idtype=3");

        // Get env variables
        var envVariable = EnvironmentVariable.getInstance().data;

        // Let's Login
        driver.findElement(new By.ByXPath("//*[@id=\"bossid\"]")).sendKeys(objProcessor.toString(envVariable.get("bossid")));
        driver.findElement(new By.ByXPath("//*[@id=\"assid\"]")).sendKeys(objProcessor.toString(envVariable.get("assid")));
        driver.findElement(new By.ByXPath("//*[@id=\"asspwd\"]")).sendKeys(objProcessor.toString(envVariable.get("asspwd")));
        driver.findElement(new By.ByXPath("//*[@id=\"vsub\"]/td/input")).click();

        String targetYear = thisMonthAndYear.getYear();

        //  移動到報帳管理/計畫經費報帳
        driver.navigate().to("https://ntuacc.cc.ntu.edu.tw/acc/apply/list.asp?yearchoose=" + targetYear);





        return new ArrayList<String>();
    }

    public List<String> browseFunding(WebDriver driver) {
        for(; ;) {
            var plans = driver.findElements(By.tagName("tr"));

        }
    }
}
