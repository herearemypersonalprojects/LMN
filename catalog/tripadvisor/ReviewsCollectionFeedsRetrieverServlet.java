package com.travelsoft.lastminute.catalog.tripadvisor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.travelsoft.lastminute.catalog.util.Util;


/**
 *
 * The servlet that retrieves reviews collection feeds.
 *
 */
public class ReviewsCollectionFeedsRetrieverServlet extends HttpServlet {

    /** Default serial version uid. */
    private static final long serialVersionUID = 1L;

    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(ReviewsCollectionFeedsRetrieverServlet.class);

    /**
     *
     * Overriding method.
     *
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse)
     * @param request the request sent by the client to the server
     * @param response the response sent by the server to the client
     * @throws IOException if an IO exception occurres
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.doPost(request, response);
    }

    /**
     *
     * Overriding method.
     *
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse)
     * @param request the request sent by the client to the server
     * @param response the response sent by the server to the client
     * @throws IOException if an IO exception occurres
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.getReviewsCollectionFeeds(request, response);
    }

    /**
     *
     * Method to get XML feeds from the TripAdvisor server.
     *
     * @param request the request sent by the client to the server
     * @param response the response sent by the server to the client
     * @throws IOException if an IO exception occurres
     */
    public void getReviewsCollectionFeeds(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String d = dateFormat.format(date).toString();

        String server = Util.getConfigValue("TRIPADVISOR_XMLFEEDSURL");
        String username = Util.getConfigValue("TRIPADVISOR_XMLFEEDSURL_LOGIN");
        String password = Util.getConfigValue("TRIPADVISOR_XMLFEEDSURL_PWD");
        String srcDir = "";
        String fileName = "RCP_ReviewsDaily_lastminute_" + d + ".xml";

        if (StringUtils.isNotBlank(Util.getConfigValue("TRIPADVISOR_FEEDSFILENAME"))) {
            fileName = Util.getConfigValue("TRIPADVISOR_FEEDSFILENAME");
        }


        String folderFullPath = Util.getConfigValue("TRIPADVISOR_FOLDERFULLPATH");
        String folderName = Util.getConfigValue("TRIPADVISOR_FOLDERNAME");
        String zipFileFullPath = folderFullPath + fileName + ".zip";
        String folderToSave = folderFullPath + folderName;

        LOGGER.debug(">> Begin to import tripAdvisor reviews from " + server);
        StringWriter content = new StringWriter();
        int num = new ReviewsRetriever().importTripAdvisorReviews(server,
            username, password, srcDir, fileName, zipFileFullPath, folderToSave, content);

        PrintWriter out = response.getWriter();
        out.println("-----------INPUT------------");
        out.println("Get the XML file: " + fileName + " from the server: " + server);
        out.println("Size: " + content.toString() + " byte(s)");
        out.println("");
        out.println("");
        out.println("");
        out.println("-----------OUTPUT------------");
        out.println("Number of retrieved TripAdvisor reviews: " + num);
        out.println("Save results to the repository " + folderToSave);

        LOGGER.debug(">> End to import tripAdvisor reviews from " + server);
    }
}
