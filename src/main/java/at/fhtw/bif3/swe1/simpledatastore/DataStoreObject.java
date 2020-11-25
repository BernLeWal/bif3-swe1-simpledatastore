package at.fhtw.bif3.swe1.simpledatastore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class DataStoreObject extends AbstractDataStore {
    @Override
    public List<PlaygroundPointRecord> read() {
        try (ObjectInputStream reader = new ObjectInputStream(input) )
        {
            return (List<PlaygroundPointRecord>) reader.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(List<PlaygroundPointRecord> data) {
        try (ObjectOutputStream writer = new ObjectOutputStream(output) )
        {
            writer.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
