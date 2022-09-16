package config;

import java.io.FileNotFoundException;

public class GetDriver {

    String path;

    public GetDriver() throws FileNotFoundException {
        this.path = System.getProperty("user.dir");
        switch (EnvironmentVariable.getInstance().data.get("OS").toString()) {
            case "windows":
                System.setProperty("webdriver.chrome.driver", path + "/chromedriver.exe");
            default:
                System.setProperty("webdriver.chrome.driver", path + "/chromedriver");
        }
    }

    public String getPath() {
        return path;
    }
}
