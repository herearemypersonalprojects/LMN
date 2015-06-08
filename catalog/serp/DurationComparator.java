/*
 * Created on 3 oct. 2011
 *
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.serp;

import java.io.Serializable;
import java.util.Comparator;

import com.travelsoft.lastminute.data.Duration;

/**
 * <p>Titre : DurationComparator.</p>
 * Description :
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Company : Travelsoft</p>
 * @author MengMeng
 * @version
 *
 */
public class DurationComparator implements Comparator<Duration>, Serializable {

    /** serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Compare two duration by days and nights.
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     * @param duration1 the first duration
     * @param duration2 the second duration
     * @return the result of the comparison
     */
    public int compare(Duration duration1, Duration duration2) {
        Integer days1 = duration1.getDays();
        Integer days2 = duration2.getDays();
        Integer nights1 = duration1.getNights();
        Integer nights2 = duration2.getNights();
        int comparison = 0;
        if (days1.equals(days2)) {
            comparison = days1.compareTo(days2);
        } else {
            comparison = nights1.compareTo(nights2);
        }
        return comparison;
    }

}
