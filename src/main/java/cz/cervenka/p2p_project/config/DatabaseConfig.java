package cz.cervenka.p2p_project.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DatabaseConfig {
    private static HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(ConfigLoader.get("db.url"));
        config.setUsername(ConfigLoader.get("db.username"));
        config.setPassword(ConfigLoader.get("db.password"));
        config.setDriverClassName(ConfigLoader.get("db.driver"));

        // Optional configurations from properties
        config.setMaximumPoolSize(ConfigLoader.getInt("db.maxPoolSize"));
        config.setMinimumIdle(ConfigLoader.getInt("db.minIdle"));
        config.setIdleTimeout(ConfigLoader.getLong("db.idleTimeout"));
        config.setConnectionTimeout(ConfigLoader.getLong("db.connectionTimeout"));

        dataSource = new HikariDataSource(config);
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static void closeDataSource() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}