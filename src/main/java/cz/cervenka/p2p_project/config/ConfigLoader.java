package cz.cervenka.p2p_project.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    private static final Properties properties = new Properties();

    /*
    * Retrieves and loads configuration file of application
    */
    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new IOException("Configuration file 'application.properties' not found in classpath.");
            }
            properties.load(input);
        } catch (IOException ex) {
            System.err.println("Failed to load configuration: " + ex.getMessage());
            throw new ExceptionInInitializerError(ex);
        }
    }


    /**
     * Retrieves value of configuration file property based on given key.
     * @param key Specific identifier of property in configuration file.
     * @return Value of property in configuration file.
     */
    public static String get(String key) {
        return properties.getProperty(key);
    }

    /**
     * Retrieves value of configuration file property based on given key.
     * @param key Specific identifier of property in configuration file.
     * @return Integer value of property in configuration file.
     */
    public static int getInt(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    /**
     * Retrieves value of configuration file property based on given key.
     * @param key Specific identifier of property in configuration file.
     * @return Long value of property in configuration file.
     */
    public static long getLong(String key) {
        return Long.parseLong(properties.getProperty(key));
    }
}