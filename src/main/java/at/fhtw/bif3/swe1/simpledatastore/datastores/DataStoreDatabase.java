package at.fhtw.bif3.swe1.simpledatastore.datastores;

import at.fhtw.bif3.swe1.simpledatastore.model.PlaygroundPointRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataStoreDatabase extends AbstractDataStore {

    private Connection _connection = null;

    public void openConnection(String url, String user, String password) throws SQLException {
        _connection = DriverManager.getConnection(url, user, password);
    }

    public void closeConnection() throws SQLException {
        _connection.close();
        _connection = null;
    }

    @Override
    public List<PlaygroundPointRecord> read() {
        return read(-1);
    }

    public List<PlaygroundPointRecord> read(int id) {
        try {
            List<PlaygroundPointRecord> list = new ArrayList<>();
            PreparedStatement statement = _connection.prepareStatement("""
    select fid,
           objectid,
           shape,
           anlname,
           bezirk,
           spielplatzdetail,
           typdetail,
           seannocaddata
    from playgroundpoints
    where (objectId = -1 or objectid = ?)
    """);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                var data = new PlaygroundPointRecord(
                        resultSet.getString(1),
                        resultSet.getInt(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getInt(5),
                        resultSet.getString(6),
                        resultSet.getString(7),
                        resultSet.getString(8));
                list.add(data);
            }

            return list;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }


    @Override
    public void write(List<PlaygroundPointRecord> data) {
        try {
            // check intelliSense here
            _connection
                    .createStatement()
                    .execute("delete from playgroundpoints");

            PreparedStatement statement = _connection.prepareStatement("""
insert into playgroundpoints 
    (fid, objectid, shape, anlname, 
     bezirk, spielplatzdetail, typdetail, seannocaddata) 
values
    (?, ?, ?, ?, 
     ?, ?, ?, ?)
""");
            for(var item : data){
                statement.setString(1, item.fId());
                statement.setInt(2, item.objectId());
                statement.setString(3, item.shape());
                statement.setString(4, item.anlName());
                statement.setInt(5, item.bezirk() == null ? 0 : item.bezirk());
                statement.setString(6, item.spielplatzDetail());
                statement.setString(7, item.typDetail());
                statement.setString(8, item.seAnnoCadData());
                statement.execute();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
