package indexing;

import com.alibaba.fastjson.*;
import org.apache.log4j.BasicConfigurator;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import parsing.CleanerMerger;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author Sherafgan Kandov (sherafgan.kandov@gmail.com)
 * @version 2/3/17
 */
public class Vectorizer {
    private final static String SENTENCES_JSON_ARRAY_PATH = "data/ready/sentences_array.json";
    private final static String GNC_MODEL_PATH = "models/GoogleNews-vectors-negative300.bin.gz";

    public static void main(String[] args) throws IOException {
        BasicConfigurator.configure();

        File gModel = new File(GNC_MODEL_PATH);
        Word2Vec word2Vec = WordVectorSerializer.readWord2VecModel(gModel);

//        Map<Integer, double[]> videoIDtoVector = new HashMap<>();

        List<String> videoIds = new LinkedList<>();
        List<double[]> videoVectors = new LinkedList<>();

        String sentencesJSON = CleanerMerger.readJsonFile(SENTENCES_JSON_ARRAY_PATH);
        JSONArray sentences = JSON.parseArray(sentencesJSON);
//        ArrayList<double[]> twentyVideoCaptionVectors = new ArrayList<>();
        int counter = 0;
        for (int i = 0; i < sentences.size(); i++) {
            JSONObject sentence = (JSONObject) sentences.get(i);
            String caption = (String) sentence.get("caption");
            String[] words = caption.split("\\s+");
            List<double[]> wordsVectors = new LinkedList<>();

            for (String word : words) {
                double[] wordVector = word2Vec.getWordVector(word);
                if ((wordVector != null) && (wordVector.length == 300)) {
                    wordsVectors.add(wordVector);
                }
            }

            if (wordsVectors.size() > 0) {
                double[] sentenceVector = getAverageOfVectors(wordsVectors);

                String tmpVideoID = (String) sentence.get("video_id");
//            tmpVideoID = tmpVideoID.replaceAll("\\D+", "");
//            int videoID = Integer.parseInt(tmpVideoID);
//            videoIDtoVector.put(videoID, sentenceVector);

                videoIds.add(tmpVideoID);
                videoVectors.add(sentenceVector);

                System.out.println(i + " of 200000 DONE!!!");
            } else {
                System.out.println(i + " of 200000 FAILED!!!");
            }

            counter++;
            if (counter == 15) {
                i = i + 5;
            }
        }

        //iterate through map and write out redis batch file :D
//        writeOutDbDataDump(videoIDtoVector);
        writeOutDbIndexData(videoIds, videoVectors);
    }

    private static void writeOutDbIndexData(List<String> videoIds, List<double[]> videoVectors) throws IOException {
        FileWriter fw = new FileWriter("tmpFiles/dbIndexData.txt");
        BufferedWriter bw = new BufferedWriter(fw);

        for (int i = 0; i < videoVectors.size(); i++) {
            bw.write(videoIds.get(i) + "\r\n");
            String vectorToWrite = convertDoubleArrayVectorToString(videoVectors.get(i));
            bw.write(vectorToWrite);
        }
        bw.flush();
        bw.close();
    }

    private static void writeOutDbDataDump(Map<Integer, double[]> videoIDtoVector) throws IOException {
        FileWriter fw = new FileWriter("tmpFiles/batch.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        Iterator iterator = videoIDtoVector.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            String toWrite = convertDoubleArrayVectorToString((double[]) pair.getValue());
            bw.write(toWrite);
            iterator.remove(); // avoids a ConcurrentModificationException
        }
        bw.flush();
        bw.close();
    }

    public static String convertDoubleArrayVectorToString(double[] vector) {
        String vectorString = "";
        for (double d : vector) {
            d = round(d, 2);
            if (vectorString.equals("")) {
                vectorString += d;
            } else {
                vectorString += ", " + d;
            }
        }
        vectorString += "\r\n";
        return vectorString;
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
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
        return avrOfVectors;
    }
}
