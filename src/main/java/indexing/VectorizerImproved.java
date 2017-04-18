package indexing;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import org.apache.log4j.BasicConfigurator;
import org.deeplearning4j.models.word2vec.Word2Vec;
import parsing.CleanerMerger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Sherafgan Kandov (sherafgan.kandov@gmail.com)
 * @version 4/18/17
 */
public class VectorizerImproved {
    private final static String GNC_MODEL_PATH = "models/GoogleNews-vectors-negative300.bin.gz";
    private final static String SENTENCES_JSON_ARRAY_PATH = "data/ready/sentences_array.json";

    private static final int TOTAL_DESCRIPTIONS_PER_VIDEO = 20;
    private static final int DESCRIPTIONS_PER_VIDEO = 20; // Index/Test split

    public static void main(String[] args) throws IOException {
        BasicConfigurator.configure();

//        File gModel = new File(GNC_MODEL_PATH);
//        Word2Vec word2Vec = WordVectorSerializer.readWord2VecModel(gModel);
        Word2Vec word2Vec = null;

        List<String> videoIds = new LinkedList<>();
        List<double[]> videoVectors = new LinkedList<>();

        String sentencesJSON = CleanerMerger.readJsonFile(SENTENCES_JSON_ARRAY_PATH);
        JSONArray sentences = JSON.parseArray(sentencesJSON);

        //BEGIN: MOST FREQUENT WORDS
        List<Set<String>> listOfMostFrequentWords = new LinkedList<>();
        for (int i = 1; i <= 13; i++) {
            List<String> videoTexts = new LinkedList<>();
            for (int j = (i - 1) * 20; j < 20 * i; j++) {
                JSONObject sentence = (JSONObject) sentences.get(j);
                videoTexts.add(sentence.get("caption").toString());
            }
            final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
            final java.util.List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(videoTexts);

            Set<String> mostFrequentWords = new HashSet<>();
            for (int k = 0; k < (wordFrequencies.size() * 35) / 100; k++) {
                mostFrequentWords.add(wordFrequencies.get(k).getWord());
            }
            listOfMostFrequentWords.add(mostFrequentWords);
        }
        //END

        int counter = 0;
        for (int i = 0; i < 260; i++) { // 13x20
            JSONObject sentence = (JSONObject) sentences.get(i);
            String caption = (String) sentence.get("caption");
            String[] words = caption.split("\\s+");
            List<double[]> wordsVectors = new LinkedList<>();

            for (String word : words) {
                Set<String> tmp = listOfMostFrequentWords.get(i / DESCRIPTIONS_PER_VIDEO);
                if (tmp.contains(word)) {
                    double[] wordVector = word2Vec.getWordVector(word);
                    if ((wordVector != null) && (wordVector.length == 300)) {
                        wordsVectors.add(wordVector);
                    }
                }
            }

            if (wordsVectors.size() > 0) {
                double[] sentenceVector = getAverageOfVectors(wordsVectors);

                String tmpVideoID = (String) sentence.get("video_id");

                videoIds.add(tmpVideoID);
                videoVectors.add(sentenceVector);

                System.out.println((i + 1) + " of 200000 DONE!!!");
            } else {
                System.out.println((i + 1) + " of 200000 FAILED!!!");
            }

            counter++;
            if (counter == DESCRIPTIONS_PER_VIDEO) {
                i = i + (TOTAL_DESCRIPTIONS_PER_VIDEO - DESCRIPTIONS_PER_VIDEO);
                counter = 0;
            }
        }
        writeOutDbIndexData(videoIds, videoVectors);
    }

    private static void writeOutDbIndexData(List<String> videoIds, List<double[]> videoVectors) throws IOException {
        FileWriter fw = new FileWriter("tmpFiles/test_rounding_vectors_improved_13x20_dbIndexData_" + DESCRIPTIONS_PER_VIDEO
                + "-" + (TOTAL_DESCRIPTIONS_PER_VIDEO - DESCRIPTIONS_PER_VIDEO) + "_split" + ".txt");
        BufferedWriter bw = new BufferedWriter(fw);

        for (int i = 0; i < videoVectors.size(); i++) {
            bw.write(videoIds.get(i) + "\r\n");
            String vectorToWrite = convertDoubleArrayVectorToString(videoVectors.get(i));
            bw.write(vectorToWrite);
        }
        bw.flush();
        bw.close();
    }

    public static String convertDoubleArrayVectorToString(double[] vector) {
        String vectorString = "";
        for (double d : vector) {
//            d = round(d, 2);
            if (vectorString.equals("")) {
                vectorString += d;
            } else {
                vectorString += ", " + d;
            }
        }
        vectorString += "\r\n";
        return vectorString;
    }

    public static List<Double> convertStringVectorToListOfDoubles(String vector) {
        List<Double> result = new LinkedList<>();
        String[] parsedVector = vector.trim().split(",");
        for (int i = 0; i < parsedVector.length; i++) {
            result.add(Double.parseDouble(parsedVector[i]));
        }
        return result;
    }

    public static double[] getAverageOfVectors(List<double[]> wordsVectors) {
        double[] avrOfVectors = new double[wordsVectors.get(0).length];
        double[] sumOfVectors = new double[wordsVectors.get(0).length];
        for (double[] vector : wordsVectors) {
            for (int i = 0; i < vector.length; i++) {
                sumOfVectors[i] += vector[i];
            }
        }
        for (int i = 0; i < sumOfVectors.length; i++) {
            avrOfVectors[i] = (sumOfVectors[i] / wordsVectors.size());
        }
        for (int i = 0; i < avrOfVectors.length; i++) {
            Double toRound = avrOfVectors[i];
            avrOfVectors[i] = round(toRound, 1);
        }
        return avrOfVectors;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
