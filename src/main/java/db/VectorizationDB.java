package db;

import com.vectorization.core.collection.VectorSpace;
import com.vectorization.driver.VectorizationConnection;
import com.vectorization.driver.builders.StatementBuilders;

import java.util.List;

/**
 * @author Sherafgan Kandov (sherafgan.kandov@gmail.com)
 * @version 4/14/17
 */
public class VectorizationDB {
    private static VectorizationDBConnection app = new VectorizationDBConnection();
    private static VectorizationConnection connection = app.getConnection();

    public static List<String> retrieveNearestVectorsIds(int kNearest, String searchSentenceVector) {
        VectorSpace result = StatementBuilders
                .find(kNearest)
                .nearestTo("[" + searchSentenceVector + "]")
                .in("videos")
                .execute(connection);

        System.out.println(result.toString().toUpperCase());

        //TODO: convert result to List<Double>

        return null;
    }

    public static void open() {
        connection.connect();
        System.out.println(StatementBuilders
                .login("admin")
                .with("admin")
                .execute(connection));

        System.out.println(StatementBuilders
                .use("VecSearchData")
                .execute(connection));
    }

    public static void close() {
        connection.close();
    }
}
