import connection.ConnectionPool;
import connection.ConnectionPoolImplementation;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static db.DatabaseOperations.*;

public class Launcher {


    private static final String HOST_NAME = "localhost";
    private static final String DB_NAME = "admin";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "admin";

//    private static final String CONNECTION_URL = String.format(
//            "jdbc:sqlite:%s", DB_NAME);
    private static final String CONNECTION_URL = String.format(
            "jdbc:postgresql:%s", DB_NAME);

    public static void main(String[] args) throws SQLException, URISyntaxException {
        ConnectionPool connectionPool = ConnectionPoolImplementation.create(CONNECTION_URL, DB_USER, DB_PASSWORD);
        Connection connection = connectionPool.getConnection();
        Statement statement = connection.createStatement();
        createTable(statement, generateTableQuery(PERSON_TABLE_NAME, createPersonTableRequest()));
        fillPersonTableData(statement, getPersonData("db_source.csv"));
        browseData(statement.executeQuery(SELECT_FROM_PERSON_QUERY)).forEach(System.out::println);
    }

}
