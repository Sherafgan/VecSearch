package util;

import db.VectorizationDB;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;

import java.io.File;

/**
 * @author Sherafgan Kandov (sherafgan.kandov@gmail.com)
 * @version 4/18/17
 */
public class StartupPipeline {
    private final static String GNC_MODEL_PATH = "models/GoogleNews-vectors-negative300.bin.gz";

    private static final File gModel = new File(GNC_MODEL_PATH);
    private static final Word2Vec word2Vec = WordVectorSerializer.readWord2VecModel(gModel);

    public static void load() {
        VectorizationDB.open();
        word2Vec.getWordVector("Test");
    }

    public static Word2Vec getWord2Vec() {
        return word2Vec;
    }
}
