package servlets;

import db.PostgreSQLJDBC;
import org.json.JSONArray;
import searching.Search;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * @author Sherafgan Kandov (sherafgan.kandov@gmail.com)
 * @version 4/16/17
 */
public class SearchServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String searchText = request.getParameter("searchText");

        //Get nearest videos' IDs
        Set<String> videoIDs = Search.search(searchText);

        //Retrieve video details from SQL DB
        JSONArray videosRetrieved = PostgreSQLJDBC.retrieveVideoDetails(videoIDs);

        //Send retrieved results to client
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        String JsonArrayToWrite = videosRetrieved.toString();
        response.getWriter().write(JsonArrayToWrite);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
