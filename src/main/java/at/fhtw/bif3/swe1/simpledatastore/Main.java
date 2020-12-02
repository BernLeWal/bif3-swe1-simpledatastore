package at.fhtw.bif3.swe1.simpledatastore;

import at.fhtw.bif3.swe1.simpledatastore.dao.PlaygroundPointDaoDb;
import at.fhtw.bif3.swe1.simpledatastore.datastores.*;
import at.fhtw.bif3.swe1.simpledatastore.model.PlaygroundPointData;
import at.fhtw.bif3.swe1.simpledatastore.model.PlaygroundPointRecord;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static final String DOWNLOAD_URL = "https://data.wien.gv.at/daten/geo?service=WFS&request=GetFeature&version=1.1.0&typeName=ogdwien:SPIELPLATZPUNKTOGD&srsName=EPSG:4326&outputFormat=csv";

    public static void main(String[] args) {
        System.out.println("Init? [Y/n]");
        Scanner sc = new Scanner(System.in);
        var input = sc.nextLine();
        if ("y".equalsIgnoreCase(input)) {
            List<PlaygroundPointRecord> data = loadDataFromWeb();
            //if( data!=null && data.size() > 0 ) {
            //    DataStoreCsv dsCsv = new DataStoreCsv();
            //    dsCsv.openWriteConsole();
            //    dsCsv.write(data);
            //}

            writeDataToCsvFile(data);
            writeDataToBinaryFile(data);
            writeDataToObjectBinaryFile(data);
            writeDataToJSONFile(data);
            writeDataToXMLFile(data);

            PlaygroundPointDaoDb.initDb();
            writeDataToDatabase(data);
        }

        System.out.println("Enter an object id: ");
        var searchObjectId = sc.nextInt();

        //List<PlaygroundPointRecord> data = readDataFromBinaryFile(searchObjectId);
        //List<PlaygroundPointRecord> data = readDataFromObjectBinaryFile(searchObjectId);

        //List<PlaygroundPointData> data = readDataFromJSONFile(searchObjectId);
        //List<PlaygroundPointData> data = readDataFromXMLFile(searchObjectId);

        List<PlaygroundPointData> data = readDataFromDatabase(searchObjectId);
        data.forEach( System.out::println );
    }

    /**
     * Loads austrian open data from the website, parses the data and serializes it in different formats
     * based on an internal mapping.
     */
    private static List<PlaygroundPointRecord> loadDataFromWeb() {
        try {
            // HTTP: download file from https://www.data.gv.at/katalog/dataset/spielplatze-standorte-wien/resource/d7477bee-cfc3-45c0-96a1-5911e0ae122c
            DataStoreCsv dsCsv = new DataStoreCsv();
            dsCsv.openReadUrl(DOWNLOAD_URL);
            List<PlaygroundPointRecord> data = dsCsv.read();
//            data.forEach(System.out::println);

            // Query with Stream-API
            var objectIds = data.stream()
                    .filter(item -> Objects.nonNull(item.objectId()))
                    .map(PlaygroundPointRecord::objectId)
                    .collect(Collectors.toList());
            System.out.println("min objectid: " + Collections.min(objectIds));
            System.out.println("max objectid: " + Collections.max(objectIds));

            return data;
        } catch (IOException e) {
            // IGNORED - I promise, I will enter the correct URL in den sourcecode
            throw new RuntimeException(e);
        }
    }

    private static void writeDataToCsvFile(List<PlaygroundPointRecord> data) {
        // File handling: preparation for databases (index file)
        try {
            DataStoreCsv dsCsv = new DataStoreCsv();
            dsCsv.openWriteFile("custom.csv");
            dsCsv.write(data);
        } catch (FileNotFoundException e) {
            // IGNORED
            throw new RuntimeException(e);
        }
    }

    private static void writeDataToBinaryFile(List<PlaygroundPointRecord> data) {
        // File handling: preparation for databases (index file)
        try {
            DataStoreBinary dsBin = new DataStoreBinary();
            dsBin.openWriteFile("custom.dat");
            dsBin.openWriteIndexFile("custom.idx.dat");
            dsBin.write(data);
        } catch (FileNotFoundException e) {
            // IGNORED
            throw new RuntimeException(e);
        }
    }

    private static void writeDataToObjectBinaryFile(List<PlaygroundPointRecord> data) {
        // Object handling
        try {
            DataStoreObject dsObject = new DataStoreObject();
            dsObject.openWriteFile("custom.obj");
            dsObject.write(data);
        } catch (FileNotFoundException e) {
            // IGNORED
            throw new RuntimeException(e);
        }
    }

    private static void writeDataToJSONFile(List<PlaygroundPointRecord> data) {
        try {
            DataStoreJSON dsJSON = new DataStoreJSON();
            //dsJSON.openWriteConsole();
            dsJSON.openWriteFile("custom.json");
            dsJSON.write(data);
        } catch (FileNotFoundException e) {
            // IGNORED
            throw new RuntimeException(e);
        }
    }

    private static void writeDataToXMLFile(List<PlaygroundPointRecord> data) {
        try {
            DataStoreXML dsXML = new DataStoreXML();
            dsXML.openWriteFile("custom.xml");
            dsXML.write(data);
        } catch (FileNotFoundException e) {
            // IGNORED
            throw new RuntimeException(e);
        }
    }

    private static void writeDataToDatabase(List<PlaygroundPointRecord> data) {
        PlaygroundPointDaoDb daoDb = new PlaygroundPointDaoDb();
        for( PlaygroundPointRecord item : data ) {
            System.out.println("  save item: " + item);
            daoDb.save( new PlaygroundPointData(
                    item.fId(),
                    item.objectId(),
                    item.shape(),
                    item.anlName(),
                    (item.bezirk()==null) ? 0 : item.bezirk(),
                    item.spielplatzDetail(),
                    item.typDetail(),
                    item.seAnnoCadData()
                    ) );
        }
    }



    private static List<PlaygroundPointRecord> readDataFromBinaryFile(int searchObjectId) {
        try {
            DataStoreBinary dsBinary = new DataStoreBinary();
            dsBinary.openReadFile("custom.dat");
            dsBinary.openReadIndexFile("custom.idx.dat");
            return dsBinary.read(searchObjectId);
        } catch (FileNotFoundException e) {
            // IGNORED - I swear I will use the correct filenames ;-)
            throw new RuntimeException(e);
        }
    }

    private static List<PlaygroundPointRecord> readDataFromObjectBinaryFile(int searchObjectId) {
        try {
            DataStoreObject dsObject = new DataStoreObject();
            dsObject.openReadFile("custom.obj");
            List<PlaygroundPointRecord> data = dsObject.read();

            if( searchObjectId>0 ) {
                data = data.stream().filter(item -> item.objectId().equals(searchObjectId)).collect(Collectors.toList());
            }
            return data;
        } catch (FileNotFoundException e) {
            // IGNORED - I swear I will use the correct filenames ;-)
            throw new RuntimeException(e);
        }
    }

    private static List<PlaygroundPointData> readDataFromJSONFile(int searchObjectId) {
        try {
            DataStoreJSON dsJSON = new DataStoreJSON();
            dsJSON.openReadFile("custom.json");

            List<PlaygroundPointData> data = dsJSON.readData();
            if( searchObjectId>0 ) {
                data = data.stream()
                        .filter(item -> item.getObjectId().equals(searchObjectId))
                        .collect(Collectors.toList());
            }
            return data;
        } catch (FileNotFoundException e) {
            // IGNORED - I swear I will use the correct filenames ;-)
            throw new RuntimeException(e);
        }
    }

    private static List<PlaygroundPointData> readDataFromXMLFile(int searchObjectId) {
        try {
            DataStoreXML dsXML = new DataStoreXML();
            dsXML.openReadFile("custom.xml");

            List<PlaygroundPointData> data = dsXML.readData();
            if( searchObjectId>0 ) {
                data = data.stream()
                        .filter(item -> item.getObjectId().equals(searchObjectId))
                        .collect(Collectors.toList());
            }
            return data;
        } catch (FileNotFoundException e) {
            // IGNORED - I swear I will use the correct filenames ;-)
            throw new RuntimeException(e);
        }
    }

    private static List<PlaygroundPointData> readDataFromDatabase(int searchObjectId) {
        PlaygroundPointDaoDb daoDb = new PlaygroundPointDaoDb();
        ArrayList<PlaygroundPointData> data = new ArrayList<>();
        if( searchObjectId>0 )
            data.add( daoDb.get( searchObjectId ).orElse( null ) );
        else
            data.addAll( daoDb.getAll() );

        return data;
    }
}
