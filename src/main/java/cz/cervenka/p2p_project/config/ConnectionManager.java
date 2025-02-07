package cz.cervenka.p2p_project.config;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionManager {

    /**
     * Retrieves an established connection with the data source.
     * @return Connection to the specific data source.
     */
    public static Connection getConnection() throws SQLException {
        return DatabaseConfig.getDataSource().getConnection();
    }
}
