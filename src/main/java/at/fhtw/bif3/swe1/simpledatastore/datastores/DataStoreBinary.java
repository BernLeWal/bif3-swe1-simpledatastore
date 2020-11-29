package at.fhtw.bif3.swe1.simpledatastore.datastores;

import at.fhtw.bif3.swe1.simpledatastore.model.PlaygroundPointRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataStoreBinary extends AbstractDataStore {
    private InputStream indexInput;
    private OutputStream indexOutput;

    public void openWriteIndexFile(String filename) throws FileNotFoundException {
        indexOutput = new FileOutputStream(filename);
    }

    public void openReadIndexFile(String filename) throws FileNotFoundException {
        indexInput = new FileInputStream(filename);
    }

    public List<PlaygroundPointRecord> read(int searchObjectId) {
        List<PlaygroundPointRecord> data = new ArrayList<>();

        try (DataInputStream reader = new DataInputStream(input);
             DataInputStream indexReader = new DataInputStream(indexInput) )
        {
            while ( indexReader.available()>0 ) {
                var position = indexReader.readLong();
                var objectId = indexReader.readInt();

                if (searchObjectId!=0) {
                    if (objectId != searchObjectId) {
                        continue;
                    }

                    System.out.printf("found at position: %d\n", position);
                    reader.skipBytes((int)position);
                }

                var currentItem = new PlaygroundPointRecord(
                        reader.readUTF(),
                        (reader.readBoolean() ? reader.readInt() : null),
                        reader.readUTF(),
                        reader.readUTF(),
                        (reader.readBoolean() ? reader.readInt() : null),
                        reader.readUTF(),
                        reader.readUTF(),
                        reader.readUTF()
                );
//                System.out.println(currentItem);
                data.add(currentItem);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return data;
    }

    @Override
    public List<PlaygroundPointRecord> read() {
        return read(0);
    }

    @Override
    public void write(List<PlaygroundPointRecord> data) {
        try (DataOutputStream writer = new DataOutputStream( output );
             DataOutputStream indexWriter = new DataOutputStream(indexOutput) )
        {
            for (var item : data) {
                if (item.objectId() != null) {
                    indexWriter.writeLong(writer.size());
                    indexWriter.writeInt(item.objectId());
                }
                writer.writeUTF( item.fId() );
                writer.writeBoolean( item.objectId()!=null );
                writer.writeInt( ( item.objectId()!=null ) ? item.objectId() : 0);
                writer.writeUTF( item.shape() );
                writer.writeUTF( item.anlName() );
                writer.writeBoolean( item.bezirk()!=null );
                writer.writeInt( ( item.bezirk()!=null ) ? item.bezirk() : 0);
                writer.writeUTF( item.spielplatzDetail() );
                writer.writeUTF( item.typDetail() );
                writer.writeUTF( item.seAnnoCadData());
            }
            writer.flush();
            indexWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
