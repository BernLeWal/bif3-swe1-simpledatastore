package at.fhtw.bif3.swe1.simpledatastore.dao;

import at.fhtw.bif3.swe1.simpledatastore.model.PlaygroundPointData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;

public class PlaygroundPointDaoDb implements Dao<PlaygroundPointData> {

    /**
     * Loads the PostgreSql JDBC-driver
     * Don't forget to add the dependency in the pom.xml, f.e.
     *         <dependency>
     *             <groupId>org.postgresql</groupId>
     *             <artifactId>postgresql</artifactId>
     *             <version>42.2.18.jre7</version>
     *         </dependency>
     */
    public PlaygroundPointDaoDb() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC driver not found");
            e.printStackTrace();
        }

    }

    /**
     * initializes the database with its tables
     * PostgreSQL documentation: https://www.postgresqltutorial.com/postgresql-create-table/
     */
    public static void initDb() {
        // re-create the database
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/","postgres","") ) {
            executeSql(connection, "DROP DATABASE simpledatastore", true );
            executeSql(connection,  "CREATE DATABASE simpledatastore" );
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // create the table
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/simpledatastore","postgres","") ) {
            executeSql(connection,  """
                CREATE TABLE IF NOT EXISTS PlaygroundPoints (
                    fId VARCHAR(50) NOT NULL,
                    objectId INT PRIMARY KEY, 
                    shape VARCHAR(50) NOT NULL,
                    anlName VARCHAR(50) NOT NULL,
                    bezirk INT NOT NULL,
                    spielplatzDetail VARCHAR(255) NOT NULL,
                    typDetail VARCHAR(255) NOT NULL,
                    seAnnoCadData VARCHAR(255)
                )
                """);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Override
    public Optional<PlaygroundPointData> get(int id) {
        return Optional.empty();
    }

    @Override
    public Collection<PlaygroundPointData> getAll() {
        return null;
    }

    @Override
    public void save(PlaygroundPointData playgroundPointData) {

    }

    @Override
    public void update(PlaygroundPointData playgroundPointData, String[] params) {

    }

    @Override
    public void delete(PlaygroundPointData playgroundPointData) {

    }


    private static boolean executeSql(Connection connection, String sql, boolean ignoreIfFails) throws SQLException {
        try {
            Statement statement = connection.createStatement();
            statement.execute(sql );
            return true;
        } catch (SQLException e) {
            if( !ignoreIfFails )
                throw e;
            return false;
        }
    }

    private static boolean executeSql(Connection connection, String sql) throws SQLException {
        return executeSql(connection, sql, false);
    }
}
