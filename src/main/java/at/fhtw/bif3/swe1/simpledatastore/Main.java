package at.fhtw.bif3.swe1.simpledatastore;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static final String DOWNLOAD_URL = "https://data.wien.gv.at/daten/geo?service=WFS&request=GetFeature&version=1.1.0&typeName=ogdwien:SPIELPLATZPUNKTOGD&srsName=EPSG:4326&outputFormat=csv";

    public static void main(String[] args) {
        System.out.println("Init? [Y/n]");
        Scanner sc = new Scanner(System.in);
        var input = sc.nextLine();
        if ( "y".equalsIgnoreCase(input) ) {
            load();
        }
        
        //readBinaryData();
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

            var file = new FileWriter( "custom.csv", StandardCharsets.UTF_8 );
            writeCollectionAsCsv(data, file);

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
            BufferedReader reader = new BufferedReader( new InputStreamReader( stream, StandardCharsets.UTF_8 ));
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

                for (int partNr=0; partNr<8; partNr++) {
                    StringBuilder readPart = new StringBuilder();
                    boolean isPartOver = false;
                    while (!isPartOver) {
                        char character = (char)reader.read();
                        if (character == ',')
                        {
                            isPartOver = true;
                        }
                        else if (character == '\r' || character == '\n')
                        {
                            reader.mark(1);
                            char lineFeed = (char)reader.read();
                            if (lineFeed != '\n')
                            {
                                reader.reset(); // equivalent for peek (in C#)
                            }

                            isPartOver = true;
                        }
                        else if (character == '\"')
                        {
                            do
                            {
                                character = (char)reader.read();
                                if (character == -1)
                                {
                                    isPartOver = true;
                                    isContentOver = true;
                                    break;
                                }
                                else if (character == '\"')
                                {
                                    break;
                                }
                                else
                                {
                                    readPart.append((char)character); // because character is of type int
                                }
                            } while (character != '\"');
                        }
                        else if (character == -1)
                        {
                            isPartOver = true;
                            isContentOver = true;
                        }
                        else
                        {
                            readPart.append((char)character);
                        }
                    }

                    if (isContentOver)
                    {
                        // last line is not taken over
                        break;
                    }

//                    System.out.println("\t\t" + partNr + ": " + readPart.toString());
                    switch( partNr ) {
                        case 0:
                            currentItemFId = readPart.toString();
                            break;
                        case 1:
                            currentItemObjectId = (readPart.length()==0) ? null : Integer.parseInt(readPart.toString());
                            if( currentItemObjectId.intValue()==493697 )
                                System.out.println("*\n");
                            break;
                        case 2:
                            currentItemShape = readPart.toString();
                            break;
                        case 3:
                            currentItemAnlName = readPart.toString();
                            break;
                        case 4:
                            currentItemBezirk = (readPart.length()==0) ? null : Integer.parseInt( readPart.toString() );
                            break;
                        case 5:
                            currentItemSpielplatzDetail = readPart.toString();
                            break;
                        case 6:
                            currentItemTypDetail = readPart.toString();
                            break;
                        case 7:
                            currentItemSeAnnoCadData = readPart.toString();
                            break;
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

                if( !reader.ready() )
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
        PrintWriter writer = new PrintWriter( file );
        writer.println("FID,OBJECTID,SHAPE,ANL_NAME,BEZIRK,SPIELPLATZ_DETAIL,TYP_DETAIL,SE_ANNO_CAD_DATA");

        for( var item : data ) {
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
        if( content==null )
            return "";
        String str = content.toString();
        if( str.contains(",") )
            return String.format("\"%s\"", str);
        return str;
    }

}
