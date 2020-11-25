package at.fhtw.bif3.swe1.simpledatastore;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static final String DOWNLOAD_URL = "https://data.wien.gv.at/daten/geo?service=WFS&request=GetFeature&version=1.1.0&typeName=ogdwien:SPIELPLATZPUNKTOGD&srsName=EPSG:4326&outputFormat=csv";

    protected List<PlaygroundPointRecord> data;

    public void run() {
        System.out.println("Init? [Y/n]");
        Scanner sc = new Scanner(System.in);
        var input = sc.nextLine();
        if ("y".equalsIgnoreCase(input)) {
            loadDataFromWeb();
        }

        readAndQueryBinaryData();
    }

    /**
     * Loads austrian open data from the website, parses the data and serializes it in different formats
     * based on an internal mapping.
     */
    private void loadDataFromWeb() {
        try {
            // HTTP: download file from https://www.data.gv.at/katalog/dataset/spielplatze-standorte-wien/resource/d7477bee-cfc3-45c0-96a1-5911e0ae122c
            data = DataStoreText.readCsv(new URL(DOWNLOAD_URL).openConnection().getInputStream());
//            data.stream().forEach(System.out::println);

            var file = new File("custom.csv");
            DataStoreText.writeCsv(data, new PrintStream(file));

            // Query with Stream-API
            var objectIds = data.stream()
                    .filter(item -> Objects.nonNull(item.objectId()))
                    .map(PlaygroundPointRecord::objectId)
                    .collect(Collectors.toList());
            System.out.println("min objectid: " + Collections.min(objectIds));
            System.out.println("max objectid: " + Collections.max(objectIds));

            // File handling: preparation for databases (index file)
            DataStoreBinary.writeBinary(data, new FileOutputStream("custom.dat"), new FileOutputStream("custom.idx.dat"));

        } catch (IOException e) {
            // IGNORED - I promise, I will enter the correct URL in den sourcecode
            throw new RuntimeException(e);
        }
    }

    private void readAndQueryBinaryData() {
        System.out.println("Enter an object id: ");
        Scanner sc = new Scanner(System.in);
        var searchObjectId = sc.nextInt();

        try {
            data = DataStoreBinary.readBinary(searchObjectId, new FileInputStream("custom.dat"), new FileInputStream("custom.idx.dat"));
            DataStoreText.writeCsv(data, System.out );
        } catch (FileNotFoundException e) {
            // IGNORED - I swear I will use the correct filenames ;-)
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }


}
