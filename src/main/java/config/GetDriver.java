package config;

public class GetDriver {
    String path;

    public GetDriver() {
        this.path = System.getProperty("user.dir");
        System.setProperty("webdriver.chrome.driver", path + "/chromedriver");
    }

    public String getPath() {
        return path;
    }
}
