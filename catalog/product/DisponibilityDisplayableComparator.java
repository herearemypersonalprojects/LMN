/*
 * Created on 11 oct. 2011
 *
 * Copyright Travelsoft 2011</p>
 */
package com.travelsoft.lastminute.catalog.product;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;

import com.travelsoft.lastminute.data.DisponibilityDisplayable;

/**
 * <p>Titre : DisponibilityDisplayableComparator.</p>
 * Description :
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Company : Travelsoft</p>
 * @author MengMeng
 * @version
 *
 */
public class DisponibilityDisplayableComparator implements Comparator<DisponibilityDisplayable>, Serializable {
    /** serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Compare two duration by days and nights.
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     * @param o1 the first DisponibilityDisplayable
     * @param o2 the second DisponibilityDisplayable
     * @return the result of the comparison
     */
    public int compare(DisponibilityDisplayable o1, DisponibilityDisplayable o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null && o2 != null) {
            return 1;
        }
        if (o1 != null && o2 == null) {
            return -1;
        }
        BigDecimal ttcPrice1 = o1.getPrice();
        BigDecimal ttcPrice2 = o2.getPrice();
        int comparison = 0;
        comparison = ttcPrice1.compareTo(ttcPrice2);
        return comparison;
    }

}
