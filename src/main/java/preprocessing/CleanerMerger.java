package preprocessing;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;

/**
 * @author Sherafgan Kandov (sherafgan.kandov@gmail.com)
 * @version 2/3/17
 */
public class CleanerMerger {
    private static final String TRAIN_RAW_DATA_PATH = "data/raw/train_val_annotation/train_val_videodatainfo.json";
    private static final String TEST_RAW_DATA_PATH = "data/raw/test_videodatainfo.json";

    private static final String READY_TO_USE_VIDEOS_PATH = "data/ready/videos_array.json";
    private static final String READY_TO_USE_SENTENCES_PATH = "data/ready/sentences_array.json";

    public static void main(String[] args) throws IOException {
        String trainRawDataString = readJsonFile(TRAIN_RAW_DATA_PATH);
        String testRawDataString = readJsonFile(TEST_RAW_DATA_PATH);
        JSONObject trainRawJSON = JSON.parseObject(trainRawDataString);
        JSONObject testRawJSON = JSON.parseObject(testRawDataString);

        JSONArray trainVideos = (JSONArray) trainRawJSON.get("videos");
        System.out.println("trainVideos size: " + trainVideos.size());
        JSONArray trainSentences = (JSONArray) trainRawJSON.get("sentences");
        System.out.println("trainSentences size: " + trainSentences.size());
        JSONArray testVideos = (JSONArray) testRawJSON.get("videos");
        System.out.println("testVideos size: " + testVideos.size());
        JSONArray testSentences = (JSONArray) testRawJSON.get("sentences");
        System.out.println("testSentences size: " + testSentences.size());
        System.out.println("----------------------------");

        JSONArray videos = new JSONArray();
        videos.addAll(trainVideos);
        videos.addAll(testVideos);
        System.out.println("videos together: " + videos.size());
        JSONArray sentences = new JSONArray();
        sentences.addAll(trainSentences);
        sentences.addAll(testSentences);
        System.out.println("sentences together: " + sentences.size());

        for (int i = 0; i < videos.size(); i++) {
            JSONObject tmp = (JSONObject) videos.get(i);
            tmp.remove("category");
            tmp.remove("split");
        }

        writeOutJSON(videos, READY_TO_USE_VIDEOS_PATH);
        writeOutJSON(sentences, READY_TO_USE_SENTENCES_PATH);
        writeOutSentencesTXT(sentences);
    }

    private static void writeOutSentencesTXT(JSONArray sentences) throws IOException {
        FileWriter fw = new FileWriter("data/ready/sentences.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        for (int i = 0; i < sentences.size(); i++) {
            JSONObject tmpObj = (JSONObject) sentences.get(i);
            String sentence = (String) tmpObj.get("caption");
            bw.write(sentence + ".\n");
        }
        bw.flush();
        bw.close();
    }

    public static void writeOutJSON(JSONArray jsonArray, String newJSONFilePath) throws IOException {
        FileWriter fwForVideos = new FileWriter(newJSONFilePath);
        BufferedWriter bwForVideos = new BufferedWriter(fwForVideos);
        bwForVideos.write(jsonArray.toJSONString());
        bwForVideos.flush();
        bwForVideos.close();
    }

    public static String readJsonFile(String jsonFilePath) throws IOException {
        FileReader fileReader = new FileReader(jsonFilePath);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String jsonContent = bufferedReader.readLine();
        bufferedReader.close();
        return jsonContent;
    }
}
