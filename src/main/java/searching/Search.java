package searching;

import indexing.Vectorizer;
import org.apache.log4j.BasicConfigurator;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import vectorization.client.Client;

import java.io.File;
import java.util.ArrayList;

/**
 * @author Sherafgan Kandov (sherafgan.kandov@gmail.com)
 * @version 3/19/17
 */
public class Search {
    private final static String GNC_MODEL_PATH = "models/GoogleNews-vectors-negative300.bin.gz";

    private final static Integer K_NEAREST = 10;

    public static void main(String[] args) {
        BasicConfigurator.configure();

        File gModel = new File(GNC_MODEL_PATH);
        Word2Vec word2Vec = WordVectorSerializer.readWord2VecModel(gModel);

        String searchText = "a person plays a video game centered around ice age the movie";

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

        Client client = new Client("VecSearchData", "localhost", 4567);
        client.sendRequest("find " + K_NEAREST + " nearest to [" + searchSentenceVectorString + "] in videoSpace ");
        //READ INPUT|OR|OUTPUT STREAM :D
        client.sendRequest("exit");
        //Work with with read stream results
    }
}
