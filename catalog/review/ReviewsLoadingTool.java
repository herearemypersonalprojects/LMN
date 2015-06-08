/*
 * Copyright Travelsoft, 2014.
 *
 */
package com.travelsoft.lastminute.catalog.review;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;



/**
 * <p>Title: ReviewsLoadingTool.java</p>.
 * <p>Description: load client reviews from a xml file </p>
 * <p>Copyright: Copyright (c) 2014</p>
 * <p>Company: Travelsoft</p>
 *
 * @author Quoc-Anh LE
 */
public class ReviewsLoadingTool {
    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(ReviewsLoadingTool.class);

    /** No constructor for utility class. */
    private ReviewsLoadingTool() {
        throw new UnsupportedOperationException("No constructor for utility class");
    }

    /**
     * Loads reviews of code <code> code </code> from an XML file.
     *
     * @param fileName the complete XML file name
     * @param encoding the encoding used in the file
     * @param clazz the class of the productReviews
     * @param <T> the type of review beans
     * @return the object representation of the configuration
     * @throws ReviewServiceException
     *           if unable to open or access the file contents
     */
    public static <T> T loadReviews(String fileName, String encoding,
        Class<T> clazz) throws ReviewServiceException {
        File file = null;
        try {
            file = new File(fileName);
        } catch (NullPointerException e) {
            throw new ReviewServiceException("May be the file name is null", e);
        }

        try {
            return (T) (MarshallerModel.castorUnmarshal(file, clazz));
        } catch (FileNotFoundException e) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(".loadReview: Failed to find" + "\n\tFile Name = [" + fileName + "]");
            }
            throw new ReviewServiceException("Failed to find" + "\n\tFile Name = [" + fileName + "]");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(".loadConfiguration: Unsupported Encoding Exception" + "\n\tFile Name=[" + fileName + "]", e);
            throw new ReviewServiceException(" Unsupported Encoding Exception=[" + fileName + "]", e);
        } catch (Exception e) {
            LOGGER.error(".loadConfiguration: Failed to unmarshal reviews." + "\n\tFile Name=[" + fileName + "]",
                e);
            throw new ReviewServiceException("Failed to unmarshal reviews." + "\n\tFile Name=[" + fileName
                + "]", e);

        }
    }
}
