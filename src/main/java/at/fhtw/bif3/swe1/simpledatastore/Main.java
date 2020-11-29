package at.fhtw.bif3.swe1.simpledatastore;

import at.fhtw.bif3.swe1.simpledatastore.datastores.*;
import at.fhtw.bif3.swe1.simpledatastore.model.PlaygroundPointData;
import at.fhtw.bif3.swe1.simpledatastore.model.PlaygroundPointRecord;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    public static final String DOWNLOAD_URL = "https://data.wien.gv.at/daten/geo?service=WFS&request=GetFeature&version=1.1.0&typeName=ogdwien:SPIELPLATZPUNKTOGD&srsName=EPSG:4326&outputFormat=csv";

    public static void main(String[] args) {
        try {
            System.out.println("Init? [Y/n]");
            Scanner sc = new Scanner(System.in);
            var input = sc.nextLine();
            if ("y".equalsIgnoreCase(input) || input.length() == 0) {
                List<PlaygroundPointRecord> data = loadDataFromWeb();

                writeDataToCsvFile(data);
                writeDataToBinaryFile(data);
                writeDataToObjectBinaryFile(data);
                writeDataToJSONFile(data);
                writeDataToXMLFile(data);
            }

            System.out.println("Enter an object id: ");
            var searchObjectId = sc.nextInt();

            writeHeadingToConsole("CSV");
            writeDataToConsoleRecord(readDataFromCsvFile(searchObjectId));
            writeHeadingToConsole("Binary");
            writeDataToCsvConsole(readDataFromBinaryFile(searchObjectId));
            writeHeadingToConsole("Object-Binary");
            writeDataToCsvConsole(readDataFromObjectBinaryFile(searchObjectId));
            writeHeadingToConsole("JSON");
            writeDataToConsoleData(readDataFromJSONFile(searchObjectId));
            writeHeadingToConsole("XML");
            writeDataToConsoleData(readDataFromXMLFile(searchObjectId));

        } catch (FileNotFoundException e) {
            // IGNORED - I swear I will use the correct filenames ;-)
            return;
        }
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

    private static void writeDataToCsvFile(List<PlaygroundPointRecord> data) throws FileNotFoundException {
        DataStoreCsv dsCsv = new DataStoreCsv();
        dsCsv.openWriteFile("custom.csv");
        dsCsv.write(data);
    }

    private static void writeDataToCsvConsole(List<PlaygroundPointRecord> data) {
        if (data != null && data.size() > 0) {
            DataStoreCsv dsCsv = new DataStoreCsv();
            dsCsv.openWriteConsole();
            dsCsv.write(data);
        }
    }

    private static void writeHeadingToConsole(String heading) {
        System.out.println();
        System.out.println();
        System.out.println(heading);
        System.out.println("_______________________________________________________________________");
        System.out.println();
    }

    private static void writeDataToConsoleRecord(List<PlaygroundPointRecord> data) {
        data.forEach(System.out::println);
    }

    private static void writeDataToConsoleData(List<PlaygroundPointData> data) {
        data.forEach(System.out::println);
    }

    private static void writeDataToBinaryFile(List<PlaygroundPointRecord> data) throws FileNotFoundException {
        // File handling: preparation for databases (index file)
        DataStoreBinary dsBin = new DataStoreBinary();
        dsBin.openWriteFile("custom.dat");
        dsBin.openWriteIndexFile("custom.idx.dat");
        dsBin.write(data);
    }

    private static void writeDataToObjectBinaryFile(List<PlaygroundPointRecord> data) throws FileNotFoundException {
        DataStoreObject dsObject = new DataStoreObject();
        dsObject.openWriteFile("custom.obj");
        dsObject.write(data);
    }

    private static void writeDataToJSONFile(List<PlaygroundPointRecord> data) throws FileNotFoundException {
        DataStoreJSON dsJSON = new DataStoreJSON();
        //dsJSON.openWriteConsole();
        dsJSON.openWriteFile("custom.json");
        dsJSON.write(data);
    }

    private static void writeDataToXMLFile(List<PlaygroundPointRecord> data) throws FileNotFoundException {
        DataStoreXML dsXML = new DataStoreXML();
        dsXML.openWriteFile("custom.xml");
        dsXML.write(data);
    }

    private static List<PlaygroundPointRecord> filterRecordsForSearchObjectId(List<PlaygroundPointRecord> data, int searchObjectId) {
        if (searchObjectId > 0) {
            return data.stream()
                    .filter(item -> item.objectId().equals(searchObjectId))
                    .collect(Collectors.toList());
        }

        return data;
    }

    private static List<PlaygroundPointData> filterDataForSearchObjectId(List<PlaygroundPointData> data, int searchObjectId) {
        if (searchObjectId > 0) {
            return data.stream()
                    .filter(item -> item.getObjectId().equals(searchObjectId))
                    .collect(Collectors.toList());
        }

        return data;
    }

    private static List<PlaygroundPointRecord> readDataFromCsvFile(int searchObjectId) throws FileNotFoundException {
        DataStoreCsv dsCsv = new DataStoreCsv();
        dsCsv.openReadFile("custom.csv");
        return filterRecordsForSearchObjectId(dsCsv.read(), searchObjectId);
    }

    private static List<PlaygroundPointRecord> readDataFromBinaryFile(int searchObjectId) throws FileNotFoundException {
        DataStoreBinary dsBinary = new DataStoreBinary();
        dsBinary.openReadFile("custom.dat");
        dsBinary.openReadIndexFile("custom.idx.dat");
        return dsBinary.read(searchObjectId);
    }

    private static List<PlaygroundPointRecord> readDataFromObjectBinaryFile(int searchObjectId) throws FileNotFoundException {
        DataStoreObject dsObject = new DataStoreObject();
        dsObject.openReadFile("custom.obj");
        return filterRecordsForSearchObjectId(dsObject.read(), searchObjectId);
    }

    private static List<PlaygroundPointData> readDataFromJSONFile(int searchObjectId) throws FileNotFoundException {
        DataStoreJSON dsJSON = new DataStoreJSON();
        dsJSON.openReadFile("custom.json");
        return filterDataForSearchObjectId(dsJSON.readData(), searchObjectId);
    }

    private static List<PlaygroundPointData> readDataFromXMLFile(int searchObjectId) throws FileNotFoundException {
        DataStoreXML dsXML = new DataStoreXML();
        dsXML.openReadFile("custom.xml");
        return filterDataForSearchObjectId(dsXML.readData(), searchObjectId);
    }
}
