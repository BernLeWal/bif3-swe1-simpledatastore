package at.fhtw.bif3.swe1.simpledatastore;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DataStoreCsv extends AbstractDataStore {
    /**
     * Reads the data from the stream in csv format and maps it to a list of objects (ORMapper).
     */
    @Override
    public List<PlaygroundPointRecord> read() {
        ArrayList<PlaygroundPointRecord> list = new ArrayList<>();

        // first line is the header (we already know and store for debugging purpose)
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
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

    @Override
    public void write(List<PlaygroundPointRecord> data) {
        PrintWriter writer = new PrintWriter(output);
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
    }

    private static String escape(Object content) {
        if (content == null)
            return "";
        String str = content.toString();
        if (str.contains(","))
            return String.format("\"%s\"", str);
        return str;
    }

}
