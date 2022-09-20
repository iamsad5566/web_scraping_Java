package module;

import config.EnvironmentVariable;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class DataProcessor {
    private static DataProcessor uniqueInstance;

    private DataProcessor() {
    }

    public static DataProcessor getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new DataProcessor();
        }

        return new DataProcessor();
    }

    public String reConstructStringByMember(String s) throws FileNotFoundException {
        s = s.replaceAll(" ", "");
        var memberList = (ArrayList<String>) EnvironmentVariable.getInstance().data.get("member");
        for (String name : memberList) {
            if (s.contains(name)) {
                String num;
                try {
                    num = s.split(name + "\\)")[1];
                } catch (ArrayIndexOutOfBoundsException e) {
                    num = s.split(name + "）")[1];
                }
                s = s.split("實發金額")[0];
                s += (name + "//" + num);
                break;
            }
        }

        return s;
    }
}
