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

    private final static String DB_INDEX_DATA_FILENAME = "tmpFiles/test_rounding_vectors_improved_13x20_dbIndexData_"
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

        int counter = 0;
        for (int i = 0; i < 195; i++) { //indexing 13 videos with 15 annotations per video
            Pair<String, String> videoIdAndVector = io.readNextPair();
            System.out.println(StatementBuilders
                    .insert(Vectors.createVector(videoIdAndVector.getKey() + "_" + (++counter),
                            Vectorizer.convertStringVectorToListOfDoubles(videoIdAndVector.getValue())))
                    .into("videos")
                    .execute(connection));

            if (counter == DESCRIPTIONS_PER_VIDEO) {
                counter = 0;
            }

            System.out.println("[INFO] INDEXED: " + (i + 1));
        }
        io.close();
    }
}
