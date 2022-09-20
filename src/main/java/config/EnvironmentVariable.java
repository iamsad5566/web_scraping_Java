package config;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

public class EnvironmentVariable {
    private static EnvironmentVariable uniqueInstance;
    InputStream inputStream;
    public Map<String, Object> data;

    private EnvironmentVariable() throws FileNotFoundException {
        this.inputStream = new FileInputStream("config.yml");
        Yaml yaml = new Yaml();
        this.data = yaml.load(inputStream);
    }

    public static EnvironmentVariable getInstance() throws FileNotFoundException {
        if (uniqueInstance == null) {
            uniqueInstance = new EnvironmentVariable();
        }
        return uniqueInstance;
    }
}
