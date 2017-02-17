package indexer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.BasicConfigurator;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import parser.Parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author	Sherafgan Kandov (sherafgan.kandov@gmail.com)
 * @version	2/3/17
 */
public class Indexer {
    private final static String SENTENCES_JSON_ARRAY_PATH = "data/ready/sentences_array.json";
    private final static String GNC_MODEL_PATH = "models/GoogleNews-vectors-negative300.bin.gz";

    public static void main(String[] args) throws IOException {
        BasicConfigurator.configure();

        File gModel = new File(GNC_MODEL_PATH);
        Word2Vec word2Vec = WordVectorSerializer.readWord2VecModel(gModel);

        String exampleOutput = "";

        String sentencesJSON = Parser.readJsonFile(SENTENCES_JSON_ARRAY_PATH);
        JSONArray sentences = JSON.parseArray(sentencesJSON);
        JSONObject sentence = (JSONObject) sentences.get(0);
        String caption = (String) sentence.get("caption");
        String[] words = caption.split("\\s+");

        exampleOutput += "-----------------------------------------------------------------------------------" + "\n";
        exampleOutput += "caption: " + caption + "\n";
        exampleOutput += "-----------------------------------------------------------------------------------" + "\n\n";

        ArrayList<double[]> wordsVectorized = new ArrayList<>();
        for (String word : words) {
            wordsVectorized.add(word2Vec.getWordVector(word));
        }

        exampleOutput += "-----------------------------------------------------------------------------------" + "\n";
        exampleOutput += "HERE ARE SAMPLE SENTENCE's WORDS VECTORIZED" + "\n";
        exampleOutput += "-----------------------------------------------------------------------------------" + "\n\n";
        for (int i = 0; i < words.length; i++) {
            exampleOutput += words[i] + " : " + Arrays.toString(wordsVectorized.get(i)) + "\n\n";
        }
        exampleOutput += "-----------------------------------------------------------------------------------" + "\n";
        exampleOutput += "WORDS' MEAN:" + "\n";
        exampleOutput += "-----------------------------------------------------------------------------------" + "\n";
        exampleOutput += word2Vec.getWordVectorsMean(Arrays.asList(words)) + "\n";

        writeOutStringToFile(exampleOutput, "ExampleOutPut.txt");
    }

    private static void writeOutStringToFile(String fileContent, String fileName) throws IOException {
        FileWriter fw = new FileWriter(fileName);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(fileContent);
        bw.flush();
        bw.close();
    }
}
