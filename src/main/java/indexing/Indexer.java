package indexing;

import javafx.util.Pair;
import vectorization.client.Client;

import java.io.IOException;

/**
 * @author Sherafgan Kandov (sherafgan.kandov@gmail.com)
 * @version 3/18/17
 */
public class Indexer {
    private static final int TOTAL_DESCRIPTIONS_PER_VIDEO = 20;
    private static final int DESCRIPTIONS_PER_VIDEO = 15; // 5/19 Index/Test split

    private final static String DB_INDEX_DATA_FILENAME = "tmpFiles/dbIndexData_" + DESCRIPTIONS_PER_VIDEO
            + "-" + (TOTAL_DESCRIPTIONS_PER_VIDEO - DESCRIPTIONS_PER_VIDEO) + "_split" + ".txt";

    public static void main(String[] args) throws IOException {
        IO io = new IO(DB_INDEX_DATA_FILENAME);
        Client client = new Client("VecSearchData", "localhost", 4567);
        client.sendRequest("create space videoSpace with dimensionality 300");
        int counter = 0;
//        for (int i = 0; i < videoIdAndVector.size(); i += 2) { // ORIGINAL
        for (int i = 0; i < 105; i++) { // indexing 7 videos with 15 annotation per video
            Pair<String, String> videoIdAndVector = io.readNextPair();
            String request = "insert " + videoIdAndVector.getKey() + "_" + (++counter) + " "
                    + "=[" + videoIdAndVector.getValue() + "] into videoSpace";
            if (counter == DESCRIPTIONS_PER_VIDEO) {
                counter = 0;
            }
            client.sendRequest(request);
            System.out.println("[INFO] INDEXED: " + (i + 1));
        }
        io.close();
        client.sendRequest("exit");
    }
}
