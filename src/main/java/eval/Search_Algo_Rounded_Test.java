package eval;

import db.VectorizationDB;
import javafx.util.Pair;
import util.IO;

import java.io.IOException;
import java.util.Set;

/**
 * @author Sherafgan Kandov (sherafgan.kandov@gmail.com)
 * @version 4/17/17
 */
public class Search_Algo_Rounded_Test {
    private static final int TOTAL_DESCRIPTIONS_PER_VIDEO = 20;
    private static final int DESCRIPTIONS_PER_VIDEO = 20;
    private static final String TEST_VECTORS_PATH = "tmpFiles/test_13x20_dbIndexData_"
            + DESCRIPTIONS_PER_VIDEO
            + "-" + (TOTAL_DESCRIPTIONS_PER_VIDEO - DESCRIPTIONS_PER_VIDEO) + "_split" + ".txt";

    public static void main(String[] args) throws IOException {
        VectorizationDB.open();
        IO io = new IO(TEST_VECTORS_PATH);
        for (int i = 0; i < 1320; i++) {
            if (io.hasNextPair()) {
                Pair<String, String> pair = io.getNextPair();
                System.out.print("[INFO] SEARCH " + (i + 1) + ": ");
                Set<String> videoIDs = VectorizationDB.retrieveNearestVectorsIds(7, pair.getValue());
                System.out.println();
            }

        }
        VectorizationDB.close();
    }
}
