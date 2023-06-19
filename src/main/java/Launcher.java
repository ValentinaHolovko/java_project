import connection.JdbcConnection;
import connection.JdbcConnectionImplementation;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static db.DatabaseOperations.*;
import static java.sql.Connection.TRANSACTION_REPEATABLE_READ;

public class
Launcher {


    private static final String DB_NAME = "admin";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "admin";


    private static final String CONNECTION_URL = String.format(
            "jdbc:postgresql:%s", DB_NAME);

    public static void main(String[] args) throws SQLException, URISyntaxException {
        JdbcConnection jdbcConnection = JdbcConnectionImplementation.create(CONNECTION_URL, DB_USER, DB_PASSWORD);
        Connection connection = jdbcConnection.getConnection();
        System.out.println(connection.getTransactionIsolation());
        connection.setTransactionIsolation(TRANSACTION_REPEATABLE_READ);
        System.out.println(connection.getTransactionIsolation());

        Statement statement = connection.createStatement();

        createTable(statement, generateTableQuery(PERSON_TABLE_NAME, createPersonTableRequest()));
        fillPersonTableData(statement, getPersonData("db_source.csv"));
        browseData(statement.executeQuery(SELECT_FROM_PERSON_QUERY)).forEach(System.out::println);
    }

}
