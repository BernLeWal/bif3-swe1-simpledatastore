package at.fhtw.bif3.swe1.simpledatastore.datastores;

import at.fhtw.bif3.swe1.simpledatastore.model.PlaygroundPointRecord;

import java.io.*;
import java.net.URL;
import java.util.List;

public abstract class AbstractDataStore {
    protected InputStream input;
    protected OutputStream output;

    public void openReadUrl(String url) throws IOException {
        var conn = new URL(url).openConnection();
        this.input = conn.getInputStream();
    }

    public void openReadFile(String filename) throws FileNotFoundException {
        var file = new File(filename);
        this.input = new FileInputStream( file );
    }

    public void openWriteFile(String filename) throws FileNotFoundException {
        var file = new File(filename);
        this.output = new FileOutputStream( file );
    }

    public void openWriteConsole() {
        this.output = System.out;
    }

    public abstract List<PlaygroundPointRecord> read();

    public abstract void write(List<PlaygroundPointRecord> data);
}
