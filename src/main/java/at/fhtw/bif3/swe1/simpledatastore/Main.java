package at.fhtw.bif3.swe1.simpledatastore;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static final String DOWNLOAD_URL = "https://data.wien.gv.at/daten/geo?service=WFS&request=GetFeature&version=1.1.0&typeName=ogdwien:SPIELPLATZPUNKTOGD&srsName=EPSG:4326&outputFormat=csv";

    public static void main(String[] args) {
        System.out.println("Init? [Y/n]");
        Scanner sc = new Scanner(System.in);
        var input = sc.nextLine();
        if ("y".equalsIgnoreCase(input)) {
            load();
        }

        readBinaryData();
    }

    /**
     * Loads austrian open data from the website, parses the data and serializes it in different formats
     * based on an internal mapping.
     */
    private static void load() {
        try {
            // HTTP: download file from https://www.data.gv.at/katalog/dataset/spielplatze-standorte-wien/resource/d7477bee-cfc3-45c0-96a1-5911e0ae122c
            var data = readStreamAsCsv(new URL(DOWNLOAD_URL).openConnection().getInputStream());
//            data.stream().forEach(System.out::println);

            var file = new FileWriter("custom.csv", StandardCharsets.UTF_8);
            writeCollectionAsCsv(data, file);

            // Query with Stream-API
            var objectIds = data.stream()
                    .filter(item -> Objects.nonNull(item.objectId()))
                    .map(item -> item.objectId())
                    .collect(Collectors.toList());
            System.out.println("min objectid: " + Collections.min(objectIds));
            System.out.println("max objectid: " + Collections.max(objectIds));

            // File handling: preparation for databases (index file)
            FileOutputStream writeStreamBinary = new FileOutputStream("custom.dat");
            FileOutputStream writeStreamIndexBinary = new FileOutputStream("custom.idx.dat");
            writeCollectionAsBinary(data, writeStreamBinary, writeStreamIndexBinary);

        } catch (IOException e) {
            // IGNORED - I promise, I will enter the correct URL in den sourcecode
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads the data from the stream in csv format and maps it to a list of objects (ORMapper).
     */
    private static List<PlaygroundPointRecord> readStreamAsCsv(InputStream stream) {
        ArrayList<PlaygroundPointRecord> list = new ArrayList<>();

        // first line is the header (we already know and store for debugging purpose)
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            var header = reader.readLine();

            boolean isContentOver = false;
            while (!isContentOver) {
                String currentItemFId = null;
                Integer currentItemObjectId = null;
                String currentItemShape = null;
                String currentItemAnlName = null;
                Integer currentItemBezirk = null;
                String currentItemSpielplatzDetail = null;
                String currentItemTypDetail = null;
                String currentItemSeAnnoCadData = null;

                for (int partNr = 0; partNr < 8; partNr++) {
                    StringBuilder readPart = new StringBuilder();
                    boolean isPartOver = false;
                    while (!isPartOver) {
                        char character = (char) reader.read();
                        if (character == ',') {
                            isPartOver = true;
                        } else if (character == '\r' || character == '\n') {
                            reader.mark(1);
                            char lineFeed = (char) reader.read();
                            if (lineFeed != '\n') {
                                reader.reset(); // equivalent for peek (in C#)
                            }

                            isPartOver = true;
                        } else if (character == '\"') {
                            do {
                                character = (char) reader.read();
                                if (character == -1) {
                                    isPartOver = true;
                                    isContentOver = true;
                                    break;
                                } else if (character == '\"') {
                                    break;
                                } else {
                                    readPart.append(character); // because character is of type int
                                }
                            } while (character != '\"');
                        } else if (character == -1) {
                            isPartOver = true;
                            isContentOver = true;
                        } else {
                            readPart.append(character);
                        }
                    }

                    if (isContentOver) {
                        // last line is not taken over
                        break;
                    }

//                    System.out.println("\t\t" + partNr + ": " + readPart.toString());
                    switch (partNr) {
                        case 0 -> currentItemFId = readPart.toString();
                        case 1 -> currentItemObjectId = (readPart.length() == 0) ? null : Integer.parseInt(readPart.toString());
                        case 2 -> currentItemShape = readPart.toString();
                        case 3 -> currentItemAnlName = readPart.toString();
                        case 4 -> currentItemBezirk = (readPart.length() == 0) ? null : Integer.parseInt(readPart.toString());
                        case 5 -> currentItemSpielplatzDetail = readPart.toString();
                        case 6 -> currentItemTypDetail = readPart.toString();
                        case 7 -> currentItemSeAnnoCadData = readPart.toString();
                    }

                }

                if (!isContentOver) {
                    PlaygroundPointRecord record = new PlaygroundPointRecord(
                            currentItemFId,
                            currentItemObjectId,
                            currentItemShape,
                            currentItemAnlName,
                            currentItemBezirk,
                            currentItemSpielplatzDetail,
                            currentItemTypDetail,
                            currentItemSeAnnoCadData);
                    list.add(record);
//                    System.out.println(record);
                }

                if (!reader.ready())
                    break;  // EOF
            }

            System.out.printf("Read %d items.\n", list.size());
            return list;
        } catch (IOException e) {
            // IGNORED
            throw new RuntimeException(e);
        }
    }

    private static void writeCollectionAsCsv(List<PlaygroundPointRecord> data, FileWriter file) {
        PrintWriter writer = new PrintWriter(file);
        writer.println("FID,OBJECTID,SHAPE,ANL_NAME,BEZIRK,SPIELPLATZ_DETAIL,TYP_DETAIL,SE_ANNO_CAD_DATA");

        for (var item : data) {
            writer.printf("%s,%s,%s,%s,%s,%s,%s,%s\n",
                    escape(item.fId()),
                    escape(item.objectId()),
                    escape(item.shape()),
                    escape(item.anlName()),
                    escape(item.bezirk()),
                    escape(item.spielplatzDetail()),
                    escape(item.typDetail()),
                    escape(item.seAnnoCadData()));
        }
        writer.flush();
        writer.close();
    }

    private static String escape(Object content) {
        if (content == null)
            return "";
        String str = content.toString();
        if (str.contains(","))
            return String.format("\"%s\"", str);
        return str;
    }

    private static void writeCollectionAsBinary(List<PlaygroundPointRecord> data, FileOutputStream outputStream, FileOutputStream indexOutputStream) {
        DataOutputStream writer = new DataOutputStream( outputStream );
        DataOutputStream indexWriter = new DataOutputStream(indexOutputStream);

        try {
            for (var item : data) {
                if (item.objectId() != null) {
                    indexWriter.writeLong(writer.size());
                    indexWriter.writeInt(item.objectId());
                }
                writer.write( item.fId().getBytes( StandardCharsets.UTF_8 ) );
                writer.writeBoolean( item.objectId()!=null );
                writer.writeInt( ( item.objectId()!=null ) ? item.objectId() : 0);
                writer.write( item.shape().getBytes( StandardCharsets.UTF_8 ) );
                writer.write( item.anlName().getBytes( StandardCharsets.UTF_8 ) );
                writer.writeBoolean( item.bezirk()!=null );
                writer.writeInt( ( item.bezirk()!=null ) ? item.bezirk() : 0);
                writer.write( item.spielplatzDetail().getBytes( StandardCharsets.UTF_8 ) );
                writer.write( item.typDetail().getBytes( StandardCharsets.UTF_8 ) );
                writer.write( item.seAnnoCadData().getBytes( StandardCharsets.UTF_8 ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void readBinaryData() {
    }

}
