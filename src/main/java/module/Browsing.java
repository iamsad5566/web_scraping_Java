package module;

import config.EnvironmentVariable;
import obj.ObjProcessor;
import obj.Time;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.interactions.Actions;

public class Browsing {

    ObjProcessor objProcessor = new ObjProcessor();

    private Browsing() {

    }

    public static Browsing getInstance() {
        return new Browsing();
    }

    public List<String> BrowseController(WebDriver driver, Time thisMonthAndYear,
                                         Time lastMonthAndYear) throws FileNotFoundException, InterruptedException {
        // Direct to NTU web
        driver.get("https://ntuacc.cc.ntu.edu.tw/acc/index.asp?campno=m&idtype=3");

        // Get env variables
        var envVariable = EnvironmentVariable.getInstance().data;

        // Let's Login
        driver.findElement(new By.ByXPath("//*[@id=\"bossid\"]"))
                .sendKeys(objProcessor.toString(envVariable.get("bossid")));
        driver.findElement(new By.ByXPath("//*[@id=\"assid\"]"))
                .sendKeys(objProcessor.toString(envVariable.get("assid")));
        driver.findElement(new By.ByXPath("//*[@id=\"asspwd\"]"))
                .sendKeys(objProcessor.toString(envVariable.get("asspwd")));
        driver.findElement(new By.ByXPath("//*[@id=\"vsub\"]/td/input")).click();

        String targetYear = thisMonthAndYear.getYear();

        //  移動到報帳管理/計畫經費報帳
        driver.navigate()
                .to("https://ntuacc.cc.ntu.edu.tw/acc/apply/list.asp?yearchoose=" + targetYear);

        var res = browseFunding(driver, thisMonthAndYear, lastMonthAndYear);
        Thread.sleep(1000);
        driver.quit();
        return res;
    }

    private List<String> browseFunding(WebDriver driver, Time thisMonthAndYear,
                                       Time lastMonthAndYear) {
        var list = new ArrayList<String>();
        for (; ; ) {
            boolean keepLooping = visitPlans(driver, thisMonthAndYear, lastMonthAndYear, list);
            if (!keepLooping) {
                break;
            }

            try {
                WebElement nextPage = driver.findElement(By.linkText("下一頁"));
                nextPage.click();
            } catch (RuntimeException e) {
                System.out.println("End");
                break;
            }
        }
        return list;
    }

    private boolean visitPlans(WebDriver driver, Time thisMonthAndYear, Time lastMonthAndYear,
                               ArrayList<String> list) {
        var plans = driver.findElements(By.tagName("tr"));
        for (int i = 0; i < plans.size(); i++) {
            String row = plans.get(i).getText();
            if (row.startsWith(thisMonthAndYear.getYear() + "T")) {
                StringBuilder sb = composeString(plans.get(i));
                if (sb.toString().split("//")[5].startsWith(lastMonthAndYear.getYear() + lastMonthAndYear.getMonth())) {
                    return false;
                }
                saveTarget(driver, plans.get(i), sb.toString(), list, thisMonthAndYear);
                plans = driver.findElements(By.tagName("tr"));
            }

        }
        return true;
    }

    private StringBuilder composeString(WebElement element) {
        StringBuilder sb = new StringBuilder();
        for (String s : element.getText().split(" ")) {
            if(!s.equals("各類所得")) {
                sb.append(s).append("//");
            }
        }
        return sb;
    }

    private void saveTarget(WebDriver driver, WebElement element, String string,
                            ArrayList<String> list, Time thisMonthAndYear) {
        if (string.split("//").length < 11 || !string.split("//")[5].startsWith(
                thisMonthAndYear.getYear() + thisMonthAndYear.getMonth())) {
            return;
        }

        // 點進去找 name
        Actions act = new Actions(driver);
        act.doubleClick(element).perform();

        String nameInfo = driver.findElement(
                By.xpath("/html/body/center/center/table[1]/tbody/tr[2]/td[2]")).getText();
        for (char c : nameInfo.toCharArray()) {
            if (c != ' ') {
                string += c;
            }
        }

        // 如果是受試者費，儲存稅前的金額
        if (string.contains("受試者費")) {
            string += driver.findElement(
                    By.xpath("/html/body/center/center/table[1]/tbody/tr[2]/td[3]")).getText();
        }

        // 存入 list
        list.add(string);
        driver.findElement(By.name("back")).click();
    }
}
