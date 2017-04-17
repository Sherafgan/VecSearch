package db;

import com.vectorization.driver.Vectorization;
import com.vectorization.driver.VectorizationConnection;

/**
 * @author Sherafgan Kandov (sherafgan.kandov@gmail.com)
 * @version 4/14/17
 */
public class VectorizationDBConnection extends Vectorization {
    @Override
    public VectorizationConnection getConnection() {
        return new VectorizationConnection("localhost", 4567);
    }
}
