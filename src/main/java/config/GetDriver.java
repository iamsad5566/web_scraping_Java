package config;

public class GetDriver {
    String path;

    public GetDriver() {
        this.path = System.getProperty("user.dir");
        try {
            System.setProperty("webdriver.chrome.driver", path + "/chromedriver");
        } catch (Exception e) {
            System.setProperty("webdriver.chrome.driver", path + "/chromedriver.exe");
        }
    }

    public String getPath() {
        return path;
    }
}
