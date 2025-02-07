package cz.cervenka.p2p_project.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration loader for application settings.
 * <p>
 * This class loads the application properties from the configuration file {@code application.properties}.
 * If the file is missing or an error occurs while loading, it falls back to default values
 * to ensure the application can run without external configuration.
 * </p>
 */
public class ConfigLoader {

    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);
    private static final Properties properties = new Properties();

    static {
        loadProperties();
    }

    /**
     * Loads the application properties from the configuration file.
     * If the file is missing, it applies default settings.
     */
    private static void loadProperties() {
        String configFile = "application.properties";

        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(configFile)) {
            if (input != null) {
                properties.load(input);
                logger.info("Loaded configuration from {}", configFile);
            } else {
                logger.warn("Configuration file not found! Using default settings.");
                setDefaultProperties();
            }
        } catch (IOException e) {
            logger.error("Error loading configuration file! Using default settings.", e);
            setDefaultProperties();
        }
    }

    /**
     * Sets default properties for the application to use when the configuration file is missing.
     */
    private static void setDefaultProperties() {
        properties.setProperty("server.port", "65525");
        properties.setProperty("thread_pool.size", "10");
        properties.setProperty("thread_pool.maxQueueSize", "50");

        properties.setProperty("db.url", "jdbc:mysql://localhost:3306/p2p_banking");
        properties.setProperty("db.username", "root");
        properties.setProperty("db.password", "");
        properties.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
        properties.setProperty("db.maxPoolSize", "10");
        properties.setProperty("db.minIdle", "2");
        properties.setProperty("db.idleTimeout", "30000");
        properties.setProperty("db.connectionTimeout", "30000");
    }

    /**
     * Retrieves the value of a property from the configuration file.
     *
     * @param key The property key.
     * @return The value of the property.
     */
    public static String get(String key) {
        return properties.getProperty(key);
    }

    /**
     * Retrieves the integer value of a property.
     *
     * @param key The property key.
     * @return The integer value of the property.
     */
    public static int getInt(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    /**
     * Retrieves the long value of a property.
     *
     * @param key The property key.
     * @return The long value of the property.
     */
    public static long getLong(String key) {
        return Long.parseLong(properties.getProperty(key));
    }
}
