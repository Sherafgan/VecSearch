package searching;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Sherafgan Kandov (sherafgan.kandov@gmail.com)
 * @version 4/16/17
 */
public class SearchSim {
    public static Set<String> search(String searchText) {
        //Vectorize searchText to searchTextVector
        //Search in VectorizationDB for k nearest vectors to searchTextVector
        //Retrieve IDs of nearest vectors
        //Search in SQL DB for detailed information of videos with those IDs
        //return results to frontend
        Set<String> videoIDs = new HashSet<>();
        videoIDs.add("video14");
        videoIDs.add("video345");
        videoIDs.add("video1001");
        videoIDs.add("video3451");
        videoIDs.add("video5671");
        return videoIDs;
    }
}
