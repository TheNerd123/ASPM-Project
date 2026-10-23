//import static org.mockito.Answers.values;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class databaseInterface {

    static String port = "5432";
    static String database = "mydb";
    static String username = "postgres";
    static String password = "Void";
    static final String JDBC_URL = String.format("jdbc:postgresql://localhost:%s/%s?currentSchema=public&user=%s&password=%s", 
                                                    port, database, username, password);

    public static void main(String[]args) throws Exception {

        var connection = DriverManager.getConnection(JDBC_URL);
        String table = "USERS";
        String columns = "name";
        String data = "(id SERIAL PRIMARY KEY, name VARCHAR(255))";
        String[] arr;

        createTable(connection, table, data);

        data = "'John Wick'";
        insertRecord(connection, table, columns, data);
        readRecords(connection, table);
        deleteRecords(connection, table);
        arr = new String[] {"Batman", "Iron Man", "Dr. Strange"};
        insertRecord(connection, table, columns, arr);
        data = "'Tony Stark'";
        updateRecord(connection, table, columns, data, "WHERE name = 'Iron Man'");
        readRecords(connection, table);
        dropTable(connection, table);
    }

    private static void dropTable(Connection connection, String table) throws SQLException {
        var dropSql = "DROP TABLE IF EXISTS " + table;
        var statement = connection.prepareStatement(dropSql);
        statement.execute();

        System.out.println("Table dropped!");
    }

    private static void deleteRecords(Connection connection, String table) throws SQLException {
        var deleteSql = "DELETE FROM " + table;         //ADD CONDITION PARAMETER
        var statement = connection.createStatement();
        statement.execute(deleteSql);

        System.out.println("Records deleted!");
    }

    private static void updateRecord(Connection connection, String table, String column, String replace, String condition) throws SQLException {
        var updateSql = String.format("UPDATE %s SET %s = %s %s", table, column, replace, condition);
        var statement = connection.prepareStatement(updateSql);
        statement.executeUpdate();

        System.out.println("Record updated!");
    }

    private static void readRecords(Connection connection, String table) throws SQLException {
        var selectSql = "SELECT * from " + table;           //ADD WHERE AND COLUMN PARAMETERS
        var statement = connection.createStatement();
        var resultSet = statement.executeQuery(selectSql);
        while (resultSet.next()) {
            System.out.println(resultSet.getString("name"));
        }
    }

    private static void insertRecord(Connection connection, String table, String columns, String record) throws SQLException {
        var insertSql = String.format("INSERT INTO %s (%s) VALUES (%s)", table, columns, record);
        var statement = connection.prepareStatement(insertSql);
        statement.execute();

        System.out.println("Record added.");
    }

    private static void insertRecord(Connection connection, String table, String columns, String[] records) throws SQLException {
        var insertSql = String.format("INSERT INTO %s (%s) VALUES (?)", table, columns);
        var statement = connection.prepareStatement(insertSql);
        int recordCount = 0;
        for (recordCount = 0; recordCount < records.length; recordCount++) {
            statement.setString(1, records[recordCount]);
            statement.execute();
        }

        System.out.println(String.format("%d records added.", recordCount));
    }

    private static void createTable(Connection connection, String table, String columns) throws SQLException {
        var statement = connection.createStatement();
        var createTableStatement = String.format("CREATE TABLE IF NOT EXISTS %s %s", table, columns);
        statement.execute(createTableStatement);

        System.out.println("Table created!");
    }
}
