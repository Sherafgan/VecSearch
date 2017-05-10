package parsing;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Sherafgan Kandov (sherafgan.kandov@gmail.com)
 * @version 4/16/17
 */
public class JSONtoSQL {
    private static final String VIDEOS_JSON_DATA_PATH = "data/ready/videos_array.json";
    private static final String VIDEOS_SQL_DUMP_PATH = "data/videos_dump.sql";

    public static void main(String[] args) throws IOException {
        String videosJSONfile = CleanerMerger.readJsonFile(VIDEOS_JSON_DATA_PATH);
        JSONArray videosJSONArray = JSON.parseArray(videosJSONfile);
        String dump = "CREATE TABLE videos (\n"
                + "\tid SERIAL NOT NULL,\n"
                + "\t video_id VARCHAR(255) NOT NULL,\n"
                + "\turl VARCHAR(255) NOT NULL,\n"
                + "\tstart_time FLOAT,\n"
                + "\tend_time FLOAT\n"
                + ");\n\n"

                + "INSERT INTO videos (video_id, url, start_time, end_time)\n"
                + "VALUES\n";

        JSONObject firstVideoInfo = (JSONObject) videosJSONArray.get(0);
        dump = dump + "\t('" + firstVideoInfo.getString("video_id") + "', '"
                + firstVideoInfo.getString("url") + "', " + firstVideoInfo.get("open time") + ", "
                + firstVideoInfo.get("end time") + ")";

        for (int i = 1; i < videosJSONArray.size(); i++) {
            JSONObject currentVideoInfo = (JSONObject) videosJSONArray.get(i);
            dump = dump + ", \n \t('" + currentVideoInfo.getString("video_id") + "', '"
                    + currentVideoInfo.getString("url") + "', " + currentVideoInfo.get("open time") + ", "
                    + currentVideoInfo.get("end time") + ")";
        }
        dump = dump + ";\n\n";

        writeSQLdump(dump, VIDEOS_SQL_DUMP_PATH);
    }

    public static void writeSQLdump(String toWrite, String path) throws IOException {
        FileWriter fwForVideos = new FileWriter(path);
        BufferedWriter bwForVideos = new BufferedWriter(fwForVideos);
        bwForVideos.write(toWrite);
        bwForVideos.flush();
        bwForVideos.close();
    }
}
