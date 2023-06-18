package db;


import io.ReadFileSingleton;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class DatabaseOperations {
    public static final String DROP_TABLE_IF_EXISTS_QUERY = "drop table if exists %s;\n";
    public static final String CREATE_TABLE_QUERY_PART = "create table %s (";
    public static final String NOT_NULL = "NOT NULL";
    public static final String PRIMARY_KEY = "PRIMARY KEY";
    // MySQLite feature for primary key
//    public static final String AUTOINCREMENT = "AUTO INCREMENT";

    public static final String ID_COLUMN_NAME = "id";
    public static final String FIRST_NAME_COLUMN_NAME = "firstName";
    public static final String LAST_NAME_COLUMN_NAME = "lastName";
    public static final String SELECT_FROM_PERSON_QUERY = "SELECT * FROM person";
    // SQLite
//    public static final String CONNECTION_URL_DRIVER = "jdbc:sqlite:sample.db";
    // Postgres
//    public static final String CONNECTION_URL_DRIVER =
//        "jdbc:postgresql://localhost/admin?user=admin&password=admin&ssl=false";
    public static final String CREATE_TABLE_CLOSE_BRACKET = " );";
    public static final String PERSON_TABLE_NAME = "Person";
    public static final String INSERT_INTO_PATTERN = "insert into %s(";
    public static final String END_BRACKET = ")";
    public static final String VALUES_PATTERN = " values(";
    public static final String DELIMITER = ", ";
    public static final String CSV_DELIMITER = ",";
    private static final String SINGLE_QUOTE = "'";

    public static void fillPersonTableData(Statement statement, List<Person> persons) throws SQLException {
        for (Person person : persons) {
            List<Field> existedFields = getExistedFields(Person.class, person);
            String fieldsString = existedFields.stream()
                    .map(Field::getName)
                    .map(DatabaseOperations::getFieldName)
                    .collect(Collectors.joining(", "));
            String valuesString = existedFields.stream()
                    .map(x -> getValueFromField(x, person))
                    .collect(Collectors.joining(", "));
            statement.executeUpdate(insertQueryString(PERSON_TABLE_NAME, fieldsString, valuesString));

        }


    }

    @SuppressWarnings("StringBufferReplaceableByString")
    private static <T> String getValueFromField(Field field, T object) {
        String result = "";
        try {
            result = new StringBuilder().append(SINGLE_QUOTE)
                    .append(field.get(object))
                    .append(SINGLE_QUOTE)
                    .toString();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    @SuppressWarnings("SameParameterValue")
    private static <T> List<Field> getExistedFields(Class<T> personClass, T object) {
        return Arrays.stream(personClass.getDeclaredFields())
                .filter(x -> {
                    boolean state = true;
                    try {
                        x.setAccessible(true);
                        state = Objects.nonNull(x.get(object));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    return state;
                }).toList();


    }

    @SuppressWarnings({"StringBufferReplaceableByString", "SameParameterValue"})
    private static String insertQueryString(String tableName,
                                            String fields,
                                            String values
    ) {
        return new StringBuilder()
                .append(String.format(INSERT_INTO_PATTERN, tableName))
                .append(fields)
                .append(END_BRACKET)
                .append(VALUES_PATTERN)
                .append(values)
                .append(END_BRACKET)
                .toString();
    }

    private static String getFieldName(String s) {
        return s.substring(s.indexOf(".") + 1);
    }

    @SuppressWarnings("SameParameterValue")
    public static List<Person> getPersonData(String resourceFileName) throws URISyntaxException {
        return getDataFromCsv(resourceFileName, Person::parsePerson);
    }

    public static void createTable(Statement statement, String query) throws SQLException {
        statement.executeUpdate(query);
    }

    @SuppressWarnings("SameParameterValue")
//    public static Statement makeConnection(String connectionUrl) throws SQLException {
//        return Optional.of(DriverManager.getConnection(connectionUrl).createStatement())
//                .orElseThrow(() -> new SQLException(SQL_CONNECTION_ERROR));
//    }

    public static List<String> browseData(ResultSet resultSet) throws SQLException {
        List<String> result = new ArrayList<>();
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        while (resultSet.next()) {
            result.add(IntStream.range(1, resultSetMetaData.getColumnCount() + 1)
                    .mapToObj(columnIndex -> getResultSet(resultSet, columnIndex))
                    .map(String::valueOf)
                    .collect(Collectors.joining(DELIMITER)));
        }
        return result;

    }

    @Nullable
    private static String getResultSet(ResultSet resultSet, Integer columnIndex) {
        String result = "";
        try {
            result = resultSet.getString(columnIndex);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static <T> List<T> getDataFromCsv(String resourceFileName, Function<String[], T> mappingDataMethod)
            throws URISyntaxException {
        return ReadFileSingleton.getInstance().readFileSource(
                        Objects.requireNonNull(DatabaseOperations.class.getClassLoader().getResource(resourceFileName)).toURI())
                .stream()
                .map(String::valueOf)
                .map(x -> x.split(CSV_DELIMITER))
                .map(mappingDataMethod)
                .toList();
    }

    public static List<QueryColumnDataStructure> createPersonTableRequest() {
        return List.of(
                new QueryColumnDataStructure(
                        ID_COLUMN_NAME,
                        JDBCType.VARCHAR,
                        Optional.of(List.of(/*AUTOINCREMENT, */NOT_NULL, PRIMARY_KEY))),
                new QueryColumnDataStructure(
                        FIRST_NAME_COLUMN_NAME,
                        JDBCType.VARCHAR,
                        Optional.empty()),
                new QueryColumnDataStructure(
                        LAST_NAME_COLUMN_NAME,
                        JDBCType.VARCHAR,
                        Optional.empty()));

    }

    @SuppressWarnings({"StringBufferReplaceableByString", "SameParameterValue"})
    public static String generateTableQuery(String tableName,
                                            List<QueryColumnDataStructure> queryColumnDescription) {
        return new StringBuilder()
                .append(String.format(DROP_TABLE_IF_EXISTS_QUERY, tableName))
                .append(String.format(CREATE_TABLE_QUERY_PART, tableName))
                .append(queryColumnDescription.stream()
                        .map(QueryColumnDataStructure::toString)
                        .collect(Collectors.joining(DELIMITER)))
                .append(CREATE_TABLE_CLOSE_BRACKET)
                .toString();
    }
}
