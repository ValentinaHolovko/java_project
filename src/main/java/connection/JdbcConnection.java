package connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface JdbcConnection {
    boolean releaseConnection(Connection connection);

    List<Connection> getConnectionPool();

    Connection getConnection() throws SQLException;

    int getSize();

    String getUrl();

    String getUser();

    String getPassword();

    void shutdown() throws SQLException;

}
