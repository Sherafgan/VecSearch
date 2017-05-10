package indexing;

import com.vectorization.core.vectors.Vectors;
import com.vectorization.driver.VectorizationConnection;
import com.vectorization.driver.builders.StatementBuilders;
import db.VectorizationDBConnection;
import javafx.util.Pair;
import util.IO;

import java.io.IOException;

/**
 * @author Sherafgan Kandov (sherafgan.kandov@gmail.com)
 * @version 4/17/17
 */
public class Indexer {
    private static final int TOTAL_DESCRIPTIONS_PER_VIDEO = 20;
    private static final int DESCRIPTIONS_PER_VIDEO = 15; // 15/5 Index/Test split

    private static final int DESCRIPTIONS_PER_VIDEO_FOR_NAME_OF_FIle = 20;

    private final static String DB_INDEX_DATA_FILENAME = "tmpFiles/test_13x20_dbIndexData_"
            + DESCRIPTIONS_PER_VIDEO_FOR_NAME_OF_FIle
            + "-" + (TOTAL_DESCRIPTIONS_PER_VIDEO - DESCRIPTIONS_PER_VIDEO_FOR_NAME_OF_FIle) + "_split" + ".txt";

    public static void main(String[] args) throws IOException {
        IO io = new IO(DB_INDEX_DATA_FILENAME);

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
        System.out.println(StatementBuilders
                .insert(Vectors.createVector(videoIdAndVector.getKey() + "_" + (counter),
                        Vectorizer.convertStringVectorToListOfDoubles(videoIdAndVector.getValue())))
                .into("videos")
                .execute(connection));
        String previousID = videoIdAndVector.getKey();

        for (int i = 1; i < 990; i++) { //indexing 66 videos with 15 annotations per video
            if (io.hasNextPair()) {
                videoIdAndVector = io.getNextPair();
                if (videoIdAndVector.getKey().equals(previousID)) {
                    counter++;
                } else {
                    counter = 1;
                }

                System.out.println(StatementBuilders
                        .insert(Vectors.createVector(videoIdAndVector.getKey() + "_" + (counter),
                                Vectorizer.convertStringVectorToListOfDoubles(videoIdAndVector.getValue())))
                        .into("videos")
                        .execute(connection));

                previousID = videoIdAndVector.getKey();

                if (counter == DESCRIPTIONS_PER_VIDEO) {
                    for (int j = 0; j < (TOTAL_DESCRIPTIONS_PER_VIDEO - DESCRIPTIONS_PER_VIDEO); j++) {
                        io.hasNextPair();
                        io.getNextPair();
                    }
                    counter = 0;
                }

                System.out.println("[INFO] INDEXED: " + (i + 1));
            } else {
                System.out.println("[INFO] FAILED INDEXING: " + (i + 1));
            }
        }
        io.close();
    }
}
