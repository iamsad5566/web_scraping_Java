package config;

import java.io.FileNotFoundException;

public class GetDriver {
    String path;

    public GetDriver() throws FileNotFoundException {
        this.path = System.getProperty("user.dir");
        switch(EnvironmentVariable.getInstance().data.get("os").toString()) {
            case "mac":
                System.setProperty("webdriver.chrome.driver", path + "/chromedriver");
            case "windows":
                System.setProperty("webdriver.chrome.driver", path + "/chromedriver.exe");
        }
    }

    public String getPath() {
        return path;
    }
}
