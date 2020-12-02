package at.fhtw.bif3.swe1.simpledatastore.dao;

import at.fhtw.bif3.swe1.simpledatastore.model.PlaygroundPointData;

import java.security.cert.CertificateParsingException;
import java.sql.*;
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
     */
    // PostgreSQL documentation: https://www.postgresqltutorial.com/postgresql-create-table/
    public static void initDb() {
        // re-create the database
        try (Connection connection = getConnection("")) {
            executeSql(connection, "DROP DATABASE simpledatastore", true );
            executeSql(connection,  "CREATE DATABASE simpledatastore", true );
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // create the table
        // PostgreSQL documentation: https://www.postgresqltutorial.com/postgresql-create-table/
        try (Connection connection = getConnection()) {
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
        try (Connection connection = getConnection() ) {
            PreparedStatement statement = connection.prepareStatement("""
                INSERT INTO playgroundpoints 
                (fid, objectid, shape, anlname, bezirk, spielplatzdetail, typdetail, seannocaddata) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?);
                """ );
            statement.setString(1, playgroundPointData.getFId() );
            statement.setInt( 2, playgroundPointData.getObjectId() );
            statement.setString(3, playgroundPointData.getShape() );
            statement.setString( 4, playgroundPointData.getAnlName() );
            statement.setInt( 5, playgroundPointData.getBezirk() );
            statement.setString( 6, playgroundPointData.getSpielplatzDetail() );
            statement.setString( 7, playgroundPointData.getTypDetail() );
            statement.setString( 8, playgroundPointData.getSeAnnoCadData() );
            statement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void update(PlaygroundPointData playgroundPointData, String[] params) {
        try (Connection connection = getConnection() ) {
            PreparedStatement statement = connection.prepareStatement("""
                UPDATE public.playgroundpoints 
                SET fid = ?, shape = ?, anlname = ?, bezirk = ?, spielplatzdetail = ?, typdetail = ?, seannocaddata = ? 
                WHERE objectid = ?;
                """);
            statement.setString(1, playgroundPointData.getFId() );
            statement.setString(2, playgroundPointData.getShape() );
            statement.setString( 3, playgroundPointData.getAnlName() );
            statement.setInt( 4, playgroundPointData.getBezirk() );
            statement.setString( 5, playgroundPointData.getSpielplatzDetail() );
            statement.setString( 6, playgroundPointData.getTypDetail() );
            statement.setString( 7, playgroundPointData.getSeAnnoCadData() );
            statement.setInt( 8, playgroundPointData.getObjectId() );
            statement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void delete(PlaygroundPointData playgroundPointData) {
        try (Connection connection = getConnection() ) {
            PreparedStatement statement = connection.prepareStatement("""
                DELETE FROM public.playgroundpoints 
                WHERE objectid = ?;
                """);
            statement.setInt( 1, playgroundPointData.getObjectId() );
            statement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    private static Connection getConnection(String database) throws SQLException {
        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + database, "postgres", "");
    }

    private static Connection getConnection() throws SQLException {
        return getConnection("simpledatastore");
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
