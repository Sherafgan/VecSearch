package db;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 * @author Sherafgan Kandov (sherafgan.kandov@gmail.com)
 * @version 4/16/17
 */
public class PostgreSQLJDBC {
    public static JSONObject retrieveVideoDetails(String videoID) {
        JSONObject videoDetails = new JSONObject();

        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/postgres",
                            "postgres", "postgres");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sqlQuery = "SELECT * FROM videos WHERE video_id='" + videoID + "'";
            ResultSet rs = stmt.executeQuery(sqlQuery);
            while (rs.next()) {
//                videoDetails.append("video_id", rs.getString("video_id"));
                videoDetails.append("url", rs.getString("url"));
                JSONArray segments = new JSONArray();
                segments.put(rs.getFloat("start_time"));
                segments.put(rs.getFloat("end_time"));
                videoDetails.append("segments", segments);
//                videoDetails.append("open time", rs.getFloat("start_time"));
//                videoDetails.append("end time", rs.getFloat("end_time"));
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        return videoDetails;
    }

    public static JSONArray retrieveVideoDetails(List<String> videoIDs) {
        JSONArray videosDetails = new JSONArray();
        for (int i = 0; i < videoIDs.size(); i++) {
            videosDetails.put(retrieveVideoDetails(videoIDs.get(i)));
        }
        return videosDetails;
    }
}
