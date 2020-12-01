package at.fhtw.bif3.swe1.simpledatastore;

import at.fhtw.bif3.swe1.simpledatastore.datastores.*;
import at.fhtw.bif3.swe1.simpledatastore.model.PlaygroundPointData;
import at.fhtw.bif3.swe1.simpledatastore.model.PlaygroundPointRecord;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static final String DOWNLOAD_URL = "https://data.wien.gv.at/daten/geo?service=WFS&request=GetFeature&version=1.1.0&typeName=ogdwien:SPIELPLATZPUNKTOGD&srsName=EPSG:4326&outputFormat=csv";

    private List<PlaygroundPointRecord> data;

    public void run() {
        System.out.println("Init? [Y/n]");
        Scanner sc = new Scanner(System.in);
        var input = sc.nextLine();
        if ("y".equalsIgnoreCase(input)) {
            loadDataFromWeb();

            writeDataToCsvFile();
            writeDataToBinaryFile();
            writeDataToObjectBinaryFile();
            writeDataToJSONFile();
            writeDataToXMLFile();
        }

        System.out.println("Enter an object id: ");
        var searchObjectId = sc.nextInt();

        readDataFromBinaryFile(searchObjectId);
        //readDataFromObjectBinaryFile(searchObjectId);
        //readDataFromJSONFile(searchObjectId);
        //readDataFromXMLFile(searchObjectId);

        if( data!=null && data.size() > 0 ) {
            DataStoreCsv dsCsv = new DataStoreCsv();
            dsCsv.openWriteConsole();
            dsCsv.write(data);
        }
    }

    /**
     * Loads austrian open data from the website, parses the data and serializes it in different formats
     * based on an internal mapping.
     */
    private void loadDataFromWeb() {
        try {
            // HTTP: download file from https://www.data.gv.at/katalog/dataset/spielplatze-standorte-wien/resource/d7477bee-cfc3-45c0-96a1-5911e0ae122c
            DataStoreCsv dsCsv = new DataStoreCsv();
            dsCsv.openReadUrl(DOWNLOAD_URL);
            data = dsCsv.read();
//            data.forEach(System.out::println);

            // Query with Stream-API
            var objectIds = data.stream()
                    .filter(item -> Objects.nonNull(item.objectId()))
                    .map(PlaygroundPointRecord::objectId)
                    .collect(Collectors.toList());
            System.out.println("min objectid: " + Collections.min(objectIds));
            System.out.println("max objectid: " + Collections.max(objectIds));
        } catch (IOException e) {
            // IGNORED - I promise, I will enter the correct URL in den sourcecode
            throw new RuntimeException(e);
        }
    }

    private void writeDataToCsvFile() {
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

    private void writeDataToBinaryFile() {
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

    private void writeDataToObjectBinaryFile() {
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

    private void writeDataToJSONFile() {
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

    private void writeDataToXMLFile() {
        try {
            DataStoreXML dsXML = new DataStoreXML();
            dsXML.openWriteFile("custom.xml");
            dsXML.write(data);
        } catch (FileNotFoundException e) {
            // IGNORED
            throw new RuntimeException(e);
        }
    }



    private void readDataFromBinaryFile(int searchObjectId) {
        try {
            DataStoreBinary dsBinary = new DataStoreBinary();
            dsBinary.openReadFile("custom.dat");
            dsBinary.openReadIndexFile("custom.idx.dat");
            data = dsBinary.read(searchObjectId);
        } catch (FileNotFoundException e) {
            // IGNORED - I swear I will use the correct filenames ;-)
            throw new RuntimeException(e);
        }
    }

    private void readDataFromObjectBinaryFile(int searchObjectId) {
        try {
            DataStoreObject dsObject = new DataStoreObject();
            dsObject.openReadFile("custom.obj");
            data = dsObject.read();

            if( searchObjectId>0 ) {
                data = data.stream().filter(item -> item.objectId().equals(searchObjectId)).collect(Collectors.toList());
            }
        } catch (FileNotFoundException e) {
            // IGNORED - I swear I will use the correct filenames ;-)
            throw new RuntimeException(e);
        }
    }

    private void readDataFromJSONFile(int searchObjectId) {
        try {
            DataStoreJSON dsJSON = new DataStoreJSON();
            dsJSON.openReadFile("custom.json");

            List<PlaygroundPointData> data2 = dsJSON.readData();
            if( searchObjectId>0 ) {
                data2 = data2.stream()
                        .filter(item -> item.getObjectId().equals(searchObjectId))
                        .collect(Collectors.toList());
            }
            data2.forEach(System.out::println);
            data = null;
        } catch (FileNotFoundException e) {
            // IGNORED - I swear I will use the correct filenames ;-)
            throw new RuntimeException(e);
        }
    }

    private void readDataFromXMLFile(int searchObjectId) {
        try {
            DataStoreXML dsXML = new DataStoreXML();
            dsXML.openReadFile("custom.xml");

            List<PlaygroundPointData> data2 = dsXML.readData();
            if( searchObjectId>0 ) {
                data2 = data2.stream()
                        .filter(item -> item.getObjectId().equals(searchObjectId))
                        .collect(Collectors.toList());
            }
            data2.forEach(System.out::println);
            data = null;
        } catch (FileNotFoundException e) {
            // IGNORED - I swear I will use the correct filenames ;-)
            throw new RuntimeException(e);
        }
    }



    public static void main(String[] args) {
        new Main().run();
    }
}
