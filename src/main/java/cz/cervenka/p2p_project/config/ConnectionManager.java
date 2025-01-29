package cz.cervenka.p2p_project.config;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionManager {

    public static Connection getConnection() throws SQLException {
        return DatabaseConfig.getDataSource().getConnection();
    }
}
