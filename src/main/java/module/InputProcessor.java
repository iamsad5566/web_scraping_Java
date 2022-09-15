package module;

import obj.Time;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class InputProcessor {

    public Time getThisMonth() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("請輸入結帳年度：");
        String year = scanner.next();
        System.out.print("請輸入結帳月份：");
        String month = scanner.next();

        Time t = new Time();
        t.setYear(year);

        if (month.charAt(0) == '0' || month.charAt(0) == '1') {
            t.setMonth(month);
            return t;
        }

        t.setMonth(0 + month);
        return t;
    }

    public Time getLastMonth(Time currTime) {
        Time t = new Time();
        int lastMonth = Integer.parseInt(currTime.getMonth()) - 1;
        if (lastMonth == 0) {
            t.setMonth("12");
            t.setYear(Integer.toString(Integer.parseInt(currTime.getYear()) - 1));
            return t;
        } else if (lastMonth < 10) {
            t.setMonth("0" + lastMonth);
        } else {
            t.setMonth(Integer.toString(lastMonth));
        }
        t.setYear(currTime.getYear());
        return t;
    }
}
