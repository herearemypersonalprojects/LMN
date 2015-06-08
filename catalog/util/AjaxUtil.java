/*
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * An util class for ajax requests.
 */
public final class AjaxUtil {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(AjaxUtil.class);


    /**
     * Default constructor.
     */
    private AjaxUtil() {
        throw new UnsupportedOperationException();
    }


    /**
     * Prints a message to the http response.
     *
     * @param response The http response.
     * @param message The message to print.
     */
    public static void printMessage(HttpServletResponse response, String message) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.print(message);
        } catch (IOException ioe) {
            LOGGER.error("IOException in printMessage : " + ioe.getMessage(), ioe);
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }
}
