package cz.cervenka.p2p_project.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Properties;

public class ApplicationConfig {

    private static final String CONFIG_FILE = "src/main/resources/application.properties";
    private static final Properties properties = new Properties();

    static {
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load application.properties: " + e.getMessage());
        }
    }

    public static String get(String key) {
        return properties.getProperty(key, "").trim();
    }

    public static int getInt(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Missing configuration key: " + key);
        }
        return Integer.parseInt(value.trim());
    }

    public static void setIP(String ip) {
        properties.setProperty("server.host.address", ip);
    }
}
