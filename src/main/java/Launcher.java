import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Statement;

import static db.DatabaseOperations.*;

public class Launcher {
    public static void main(String[] args) throws SQLException, URISyntaxException {

        Statement statement = makeConnection(CONNECTION_URL_DRIVER);
        createTable(statement, generateTableQuery(PERSON_TABLE_NAME, createPersonTableRequest()));
        fillPersonTableData(statement, getPersonData("db_source.csv"));
        browseData(statement.executeQuery(SELECT_FROM_PERSON_QUERY)).forEach(System.out::println);
    }

}
