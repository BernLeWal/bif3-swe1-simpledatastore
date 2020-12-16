package at.fhtw.bif3.swe1.simpledatastore.dao;

import at.fhtw.bif3.swe1.simpledatastore.model.PlaygroundPointData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public class PlaygroundPointDaoDb implements Dao<PlaygroundPointData> {

    /**
     * initializes the database with its tables
     */
    // PostgreSQL documentation: https://www.postgresqltutorial.com/postgresql-create-table/
    public static void initDb() {
        // re-create the database
        try (Connection connection = DbConnection.getInstance().connect("")) {
            DbConnection.executeSql(connection, "DROP DATABASE simpledatastore", true );
            DbConnection.executeSql(connection,  "CREATE DATABASE simpledatastore", true );
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // create the table
        // PostgreSQL documentation: https://www.postgresqltutorial.com/postgresql-create-table/
        try {
            DbConnection.getInstance().executeSql("""
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
        Connection connection = DbConnection.getInstance().getConnection();
        try ( PreparedStatement statement = connection.prepareStatement("""
                SELECT fid, objectid, shape, anlname, bezirk, spielplatzdetail, typdetail, seannocaddata 
                FROM playgroundpoints 
                WHERE objectid=?
                """)
        ) {
            statement.setInt( 1, id );
            ResultSet resultSet = statement.executeQuery();
            if( resultSet.next() ) {
                return Optional.of( new PlaygroundPointData(
                        resultSet.getString(1),
                        resultSet.getInt( 2 ),
                        resultSet.getString( 3 ),
                        resultSet.getString( 4 ),
                        resultSet.getInt( 5 ),
                        resultSet.getString( 6 ),
                        resultSet.getString( 7 ),
                        resultSet.getString( 8 )
                ) );
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Collection<PlaygroundPointData> getAll() {
        ArrayList<PlaygroundPointData> result = new ArrayList<>();
        Connection connection = DbConnection.getInstance().getConnection();
        try ( PreparedStatement statement = connection.prepareStatement("""
                SELECT fid, objectid, shape, anlname, bezirk, spielplatzdetail, typdetail, seannocaddata 
                FROM playgroundpoints 
                """)
        ) {
            ResultSet resultSet = statement.executeQuery();
            while( resultSet.next() ) {
                result.add( new PlaygroundPointData(
                        resultSet.getString(1),
                        resultSet.getInt( 2 ),
                        resultSet.getString( 3 ),
                        resultSet.getString( 4 ),
                        resultSet.getInt( 5 ),
                        resultSet.getString( 6 ),
                        resultSet.getString( 7 ),
                        resultSet.getString( 8 )
                ) );
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    @Override
    public void save(PlaygroundPointData playgroundPointData) {
        Connection connection = DbConnection.getInstance().getConnection();
        try ( PreparedStatement statement = connection.prepareStatement("""
                INSERT INTO playgroundpoints 
                (fid, objectid, shape, anlname, bezirk, spielplatzdetail, typdetail, seannocaddata) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?);
                """ )
        ) {
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
    public void update(PlaygroundPointData playgroundPoint, String[] params) {
        // update the item
        playgroundPoint.setFId( Objects.requireNonNull( params[0], "fId cannot be null" ) );
        playgroundPoint.setObjectId( Integer.parseInt(Objects.requireNonNull( params[1], "ObjectId cannot be null" ) ) );
        playgroundPoint.setShape( Objects.requireNonNull( params[2] ) );
        playgroundPoint.setAnlName( Objects.requireNonNull( params[3] ) );
        playgroundPoint.setBezirk( Integer.parseInt(Objects.requireNonNull( params[4], "Bezirk cannot be null" ) ) );
        playgroundPoint.setSpielplatzDetail( Objects.requireNonNull( params[5], "SpielplatzDetail cannot be null" ) );
        playgroundPoint.setTypDetail( Objects.requireNonNull( params[6], "TypDetail cannot be null" ) );
        playgroundPoint.setSeAnnoCadData( params[7] );

        // persist the updated item
        Connection connection = DbConnection.getInstance().getConnection();
        try ( PreparedStatement statement = connection.prepareStatement("""
                UPDATE playgroundpoints 
                SET fid = ?, shape = ?, anlname = ?, bezirk = ?, spielplatzdetail = ?, typdetail = ?, seannocaddata = ? 
                WHERE objectid = ?;
                """)
        ) {
            statement.setString(1, playgroundPoint.getFId() );
            statement.setString(2, playgroundPoint.getShape() );
            statement.setString( 3, playgroundPoint.getAnlName() );
            statement.setInt( 4, playgroundPoint.getBezirk() );
            statement.setString( 5, playgroundPoint.getSpielplatzDetail() );
            statement.setString( 6, playgroundPoint.getTypDetail() );
            statement.setString( 7, playgroundPoint.getSeAnnoCadData() );
            statement.setInt( 8, playgroundPoint.getObjectId() );
            statement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void delete(PlaygroundPointData playgroundPointData) {
        Connection connection = DbConnection.getInstance().getConnection();
        try (
             PreparedStatement statement = connection.prepareStatement("""
                DELETE FROM playgroundpoints 
                WHERE objectid = ?;
                """)
        ) {
            statement.setInt( 1, playgroundPointData.getObjectId() );
            statement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
