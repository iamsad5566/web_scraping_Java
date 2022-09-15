package config;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

public class EnvironmentVariable {
    InputStream inputStream;
    static EnvironmentVariable env;
    public Map<String, Object> data;

    public EnvironmentVariable() throws FileNotFoundException {
        this.inputStream = new FileInputStream("config.yml");
        Yaml yaml = new Yaml();
        this.data = yaml.load(inputStream);
    }

    public static EnvironmentVariable getInstance() throws FileNotFoundException {
        env = new EnvironmentVariable();
        return env;
    }
}
