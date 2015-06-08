/*
 * Copyright Travelsoft, 2014.
 */
package com.travelsoft.lastminute.catalog.review;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;


import org.exolab.castor.xml.Unmarshaller;



/**
 * <p>Title: MarshallerModel.java</p>.
 * <p>Description: fill java objects from a xml file </p>
 * <p>Copyright: Copyright (c) 2014</p>
 * <p>Company: Travelsoft</p>
 *
 * @author Quoc-Anh LE
 */
public class MarshallerModel {
    /** UTF-8 encoding. */
    public static final String ENCODING = "UTF-8";

    /**
     * Unmarshalls a class from the given file name.
     * @param string the name of the file to read
     * @param clazz the class of the entity to unmarshall
     * @param <T> the type of the unmarshalled data
     * @return the unmarshalled object
     * @throws Exception in case of failure
     */
    public static final <T> T castorUnmarshal(String string,
      Class<T> clazz) throws Exception {
        return castorUnmarshal(new File(string), clazz);
    }

    /**
     * Unmarshals a class from the given file name.
     * @param file the file to read
     * @param clazz the class of the entity to unmarshal
     * @param <T> the type of the unmarshalled data
     * @return the unmarshalled object
     * @throws Exception in case of failure
     */
    @SuppressWarnings("unchecked")
    public static final <T> T castorUnmarshal(File file,
        Class<T> clazz) throws Exception {
        T result = null;

        FileInputStream fis = new FileInputStream(file);
        Reader reader = new BufferedReader(new InputStreamReader(fis, ENCODING));

        result = (T) Unmarshaller.unmarshal(clazz, reader);

        return result;
    }
}
