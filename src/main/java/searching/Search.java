package searching;

import db.VectorizationDB;
import indexing.Vectorizer;
import org.apache.log4j.BasicConfigurator;
import org.deeplearning4j.models.word2vec.Word2Vec;
import util.StartupPipeline;

import java.util.ArrayList;
import java.util.Set;

/**
 * @author Sherafgan Kandov (sherafgan.kandov@gmail.com)
 * @version 3/19/17
 */
public class Search {

    private final static Integer K_NEAREST = 10;

    public static Set<String> search(String searchText) {
        BasicConfigurator.configure();

        Word2Vec word2Vec = StartupPipeline.getWord2Vec();

        String[] words = searchText.split("\\s+");
        ArrayList<double[]> oneVideoVectors = new ArrayList<>();

        for (String word : words) {
            double[] wordVector = word2Vec.getWordVector(word);
            if (wordVector != null) {
                oneVideoVectors.add(wordVector);
            }
        }

        double[] searchSentenceVector = Vectorizer.getAverageOfVectors(oneVideoVectors);
        String searchSentenceVectorString = Vectorizer.convertDoubleArrayVectorToString(searchSentenceVector);

        return VectorizationDB.retrieveNearestVectorsIds(K_NEAREST, searchSentenceVectorString);
    }
}
