package indexing;

import vectorization.client.Client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author  Sherafgan Kandov (sherafgan.kandov@gmail.com)
 * @version 3/18/17
 */
public class Indexer {
    private static final int TOTAL_DESCRIPTIONS_PER_VIDEO = 20;
    private static final int DESCRIPTIONS_PER_VIDEO = 5; // 5/19 Index/Test split

    private final static String DB_INDEX_DATA_FILENAME = "tmpFiles/dbIndexData_" + DESCRIPTIONS_PER_VIDEO
            + "-" + (TOTAL_DESCRIPTIONS_PER_VIDEO - DESCRIPTIONS_PER_VIDEO) + "_split" + ".txt";

    public static void main(String[] args) throws IOException {
        List<String> videoIdAndVector = readDbIndexData(DB_INDEX_DATA_FILENAME);
        Client client = new Client("VecSearchData", "localhost", 4567);
        client.sendRequest("create space videoSpace with dimensionality 300");
        int counter = 0;
//        for (int i = 0; i < videoIdAndVector.size(); i += 2) { // ORIGINAL
        for (int i = 0; i < 100; i += 2) { // DUMMY or i+=29 for unique from 15 duplicate descriptions
            String request = "insert " + videoIdAndVector.get(i) + "_" + (++counter) + " "
                    + "=[" + videoIdAndVector.get(i + 1) + "] into videoSpace";
            if (counter == DESCRIPTIONS_PER_VIDEO) {
                counter = 0;
            }
            client.sendRequest(request);
            System.out.println("[INFO] INDEXED: " + (i + 2) / 2);
        }
        client.sendRequest("exit");
    }

    private static List<String> readDbIndexData(String batchFileName) throws IOException {
        List<String> videoIdAndVector = new LinkedList<>();
        FileReader fileReader = new FileReader(batchFileName);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            videoIdAndVector.add(line);
        }
        return videoIdAndVector;
    }
}
