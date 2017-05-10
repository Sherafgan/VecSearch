package eval;

import com.vectorization.core.collection.VectorSpace;
import com.vectorization.driver.VectorizationConnection;
import com.vectorization.driver.builders.StatementBuilders;
import db.VectorizationDBConnection;
import javafx.util.Pair;
import util.IO;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Sherafgan Kandov (sherafgan.kandov@gmail.com)
 * @version 4/24/17
 */
public class DHA {
    private static final int TOTAL_DESCRIPTIONS_PER_VIDEO = 20;
    private static final int DESCRIPTIONS_PER_VIDEO = 20;
    private static final String DB_TEST_DATA_FILENAME = "tmpFiles/test_rounding_vectors_improved_66x20_dbIndexData_"
            + DESCRIPTIONS_PER_VIDEO
            + "-" + (TOTAL_DESCRIPTIONS_PER_VIDEO - DESCRIPTIONS_PER_VIDEO) + "_split" + ".txt";

    private static final int kNearest = 7;

    public static void main(String[] args) throws IOException {
        IO io = new IO(DB_TEST_DATA_FILENAME);

        VectorizationDBConnection app = new VectorizationDBConnection();
        VectorizationConnection connection = app.getConnection();
        connection.connect();
        System.out.println(StatementBuilders
                .login("admin")
                .with("admin")
                .execute(connection));

        System.out.println(StatementBuilders
                .use("VecSearchData")
                .execute(connection));

        System.out.println(StatementBuilders
                .create("videos")
                .withDimensionality(300)
                .execute(connection));

        int counter = 1;
        io.hasNextPair();
        Pair<String, String> videoIdAndVector = io.getNextPair();

        VectorSpace DBResponse = StatementBuilders
                .find(kNearest)
                .nearestTo("[" + videoIdAndVector.getValue() + "]")
                .in("videos")
                .execute(connection);

        List<Double> videosDHAs = new LinkedList<>();

        List<Boolean> hits = new LinkedList<>();

        if (!DBResponse.toString().equals("empty")) {
            hits.add(true);
        } else {
            hits.add(false);
        }

        String previousID = videoIdAndVector.getKey();

        for (int i = 1; i < 1320 && io.hasNextPair(); i++) { //indexing 66 videos with 15 annotations per video
            videoIdAndVector = io.getNextPair();

            if (!videoIdAndVector.getKey().equals(previousID)) {
                int tmp = 0;
                for (boolean b : hits) {
                    if (b) {
                        tmp++;
                    }
                }
                double thisDHA = tmp / counter;
                videosDHAs.add(thisDHA);

                System.out.println("[INFO] DHA FOR VIDEO #" + videosDHAs.size() + " DONE !!!");

                counter = 0;
                hits = new LinkedList<>();
            }

            DBResponse = StatementBuilders
                    .find(kNearest)
                    .nearestTo("[" + videoIdAndVector.getValue() + "]")
                    .in("videos")
                    .execute(connection);

            if (!DBResponse.toString().trim().equals("empty")) {
                hits.add(true);
            } else {
                hits.add(false);
            }
            counter++;

            previousID = videoIdAndVector.getKey();
        }
        io.close();

        double tmp = 0;
        for (double d : videosDHAs) {
            tmp += d;
        }

        double finalDHA = tmp / videosDHAs.size();

        System.out.println("########################################");
        System.out.println("DHA  ---->  " + finalDHA + "");
        System.out.println("########################################");
    }
}
