package com.travelsoft.lastminute.catalog.tripadvisor;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.travelsoft.lastminute.catalog.util.Util;


/**
 *
 * The servlet that will retrieve reviews from the TripAdvisor server.
 *
 */
public class ReviewsRetrieverServlet extends HttpServlet {

    /** Default serial version uid. */
    private static final long serialVersionUID = 1L;

    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(ReviewsRetrieverServlet.class);

    /**
     *
     * Overriding method.
     *
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse)
     * @param request the request sent by the client to the server
     * @param response the response sent by the server to the client
     * @throws ServletException if a servlet exception occurres
     * @throws IOException if an IO exception occurres
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
     * @throws ServletException if a servlet exception occurres
     * @throws IOException if an IO exception occurres
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            this.getTripAdvisorReviews(request, response);
        } catch (Exception e) {
            LOGGER.error("Error while trying to get TripAdvisor reviews: " + e.getMessage());
        }
    }

    /**
     *
     * Method to retrieve TripAdvisor reviews.
     *
     * @param request the request sent by the client to the server
     * @param response the response sent by server to the client
     * @throws IOException if an IO exception occurres
     */
    private void getTripAdvisorReviews(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        LOGGER.debug(">> Begin of retrieving TripAdvisor reviews");
        String source = Util.getConfigValue("TRIPADVISOR_XMLURL");
        String fileName = Util.getConfigValue("TRIPADVISOR_XMLFILENAME");

        /** for test only
        out.println(this.getInitParameter("staticRoot"));

        File file = new File(this.getInitParameter("staticRoot") + "/test160520141630");
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream f = new FileOutputStream(file);
        ObjectOutputStream s = new ObjectOutputStream(f);
        s.writeObject("this is file test!");
        s.flush();
        s.close();
        */
        int num = new ReviewsRetriever().getAndSaveTripAdvisorReviews(source, fileName);
        out.println("Number of retrieved TripAdvisor reviews: " + num);

        LOGGER.debug(">> End of retrieving TripAdvisor reviews");
        out.flush();
        out.close();

    }
}
