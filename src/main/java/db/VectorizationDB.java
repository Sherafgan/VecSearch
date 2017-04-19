package db;

import com.vectorization.core.Vector;
import com.vectorization.core.collection.VectorSpace;
import com.vectorization.core.vectors.SEDVector;
import com.vectorization.driver.VectorizationConnection;
import com.vectorization.driver.builders.StatementBuilders;

import java.util.*;

/**
 * @author Sherafgan Kandov (sherafgan.kandov@gmail.com)
 * @version 4/14/17
 */
public class VectorizationDB {
    private static VectorizationDBConnection app = new VectorizationDBConnection();
    private static VectorizationConnection connection = app.getConnection();

    public static Set<String> retrieveNearestVectorsIds(int kNearest, String searchSentenceVector) {
        Set<String> result = new HashSet<>();

        VectorSpace DBResponse = StatementBuilders
                .find(kNearest)
                .nearestTo("[" + searchSentenceVector + "]")
                .in("videos")
                .execute(connection);

        System.out.println(DBResponse.toString().toUpperCase());

        if (!DBResponse.toString().equals("empty")) {
            Iterator<Vector> iterator = DBResponse.iterator();
            while (iterator.hasNext()) {
                SEDVector sedVector = (SEDVector) iterator.next();
                String id = sedVector.id();
                String[] id_splitted = id.split("_");
                id = id_splitted[0];
                result.add(id);
            }
            return result;
        } else {
            return null;
        }
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
