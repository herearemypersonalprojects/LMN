/*
 * Copyright Travelsoft, 2014.
 *
 */
package com.travelsoft.lastminute.catalog.review;


/**
 * <p>Title: ReviewServiceException.java</p>.
 * <p>Description: return exception </p>
 * <p>Copyright: Copyright (c) 2014</p>
 * <p>Company: Travelsoft</p>
 *
 * @author Quoc-Anh LE
 */
public class ReviewServiceException extends Exception {

    /** <code>serialVersionUID</code> field. */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor without parameter.
     */
    public ReviewServiceException() {
        super();
    }

    /**
     * Constructor with parameters.
     *
     * @param theMessage the given message
     * @param theCause the given cause
     */
    public ReviewServiceException(String theMessage, Exception theCause) {
        super(theMessage, theCause);
    }

    /**
     * Constructor with message parameter.
     * @param theMessage the given message
     */
    public ReviewServiceException(String theMessage) {
        super(theMessage);
    }

    /**
     * Contructor with exception parameter.
     * @param theCause the given cause
     */
    public ReviewServiceException(Exception theCause) {
        super(theCause);
    }

}
