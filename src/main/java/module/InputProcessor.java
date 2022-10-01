package module;

import obj.Time;


public class InputProcessor {
    String year;
    String month;

    public InputProcessor(String[] args) {
        this.year = args[0];
        this.month = args[1];
    }

    public Time getThisMonth() {

        Time t = new Time();
        t.setYear(this.year);

        if (this.month.charAt(0) == '0' || this.month.charAt(0) == '1') {
            t.setMonth(this.month);
            return t;
        }

        t.setMonth(0 + this.month);
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
