/*
 * Created on 10 août 2012
 *
 * Copyright Travelsoft, 2012.
 */
package com.travelsoft.lastminute.catalog.util;

import java.util.List;

/**
 * <p>Title: CollectionUtil.java.</p>
 * <p>Description: .</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Travelsoft</p>
 *
 * @author thiago.bohme
 */
public final class CollectionUtil {

    /** No constructor for utility class. */
    private CollectionUtil() {
    }

    /**
     * @param list
     *            the list to check
     * @return a boolean indicating if the input list is null or empty.
     */
    public static boolean isBlank(List<?> list) {
        return list == null || list.isEmpty();
    }

    /**
     * @param list
     *            the list to check
     * @return a boolean indicating if the input list is not null nor empty.
     */
    public static boolean isNotBlank(List<?> list) {
        return !isBlank(list);
    }

    /**
     * @param array
     *            the array to check
     * @return a boolean indicating if the input array does not contain any valid element
     *         (null or size 0 or only one element which is null).
     */
    public static boolean isBlank(Object[] array) {
        return array == null || array.length == 0 || (array.length == 1 && array[0] == null);
    }

    /**
     * @param array
     *            the array to check
     * @return a boolean indicating if the input array contains at least one valid element
     *         (null or size 0 or only one element which is null).
     */
    public static boolean isNotBlank(Object[] array) {
        return !isBlank(array);
    }
}
