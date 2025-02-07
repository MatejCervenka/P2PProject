package cz.cervenka.p2p_project.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

/**
 * Database configuration using HikariCP for connection pooling.
 * <p>
 * Loads database connection properties from the configuration file. If any values
 * are missing, it falls back to default settings to ensure the application continues running.
 * </p>
 */
public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(ConfigLoader.get("db.url"));
        config.setUsername(ConfigLoader.get("db.username"));
        config.setPassword(ConfigLoader.get("db.password"));
        config.setDriverClassName(ConfigLoader.get("db.driver"));

        try {
            config.setMaximumPoolSize(ConfigLoader.getInt("db.maxPoolSize"));
            config.setMinimumIdle(ConfigLoader.getInt("db.minIdle"));
            config.setIdleTimeout(ConfigLoader.getLong("db.idleTimeout"));
            config.setConnectionTimeout(ConfigLoader.getLong("db.connectionTimeout"));
            logger.info("Database configuration loaded successfully.");
        } catch (NumberFormatException e) {
            logger.error("Invalid number format in database configuration. Using defaults.", e);
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setConnectionTimeout(30000);
        }

        dataSource = new HikariDataSource(config);
    }

    /**
     * Retrieves the data source for establishing connections to the database.
     *
     * @return Configured {@link DataSource} instance.
     */
    public static DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Closes the database connection pool when the application shuts down.
     */
    public static void closeDataSource() {
        if (dataSource != null) {
            dataSource.close();
            logger.info("Data source closed successfully.");
        }
    }
}
