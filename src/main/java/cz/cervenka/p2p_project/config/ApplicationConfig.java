package cz.cervenka.p2p_project.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Application configuration loader.
 * <p>
 * This class loads application-specific properties from the configuration file.
 * If the file is not found, it applies default values to allow the application to run.
 * </p>
 */
public class ApplicationConfig {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);
    private static final String CONFIG_FILE = "src/main/resources/application.properties";
    //private static final String CONFIG_FILE = "application.properties";

    //private static final String CONFIG_FILE = "application.properties";
    private static final Properties properties = new Properties();

    static {
        loadProperties();
    }

    /**
     * Loads the application properties from the configuration file.
     * If the file is missing, it applies default settings.
     */
    private static void loadProperties() {
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
            logger.info("Loaded application properties from {}", CONFIG_FILE);
        } catch (IOException e) {
            logger.error("Configuration file not found! Using default settings.", e);
            setDefaultProperties();
        }
    }

    /**
     * Sets default properties for the application in case the configuration file is missing.
     */
    private static void setDefaultProperties() {
        properties.setProperty("server.host.address", "127.0.0.1");
        logger.info("Applied default settings for configuration.");
    }

    /**
     * Retrieves the value of a property.
     *
     * @param key The property key.
     * @return The value of the property as a string.
     */
    public static String get(String key) {
        return properties.getProperty(key, "").trim();
    }

    /**
     * Retrieves an integer value from the properties.
     *
     * @param key The property key.
     * @return The integer value of the property.
     * @throws RuntimeException if the key is missing.
     */
    public static int getInt(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            logger.error("Missing configuration key: {}", key);
            throw new RuntimeException("Missing configuration key: " + key);
        }
        return Integer.parseInt(value.trim());
    }

    /**
     * Sets the IP address for the server host dynamically.
     *
     * @param ip The dynamically acquired IP address.
     */
    public static void setIP(String ip) {
        properties.setProperty("server.host.address", ip);
    }
}
