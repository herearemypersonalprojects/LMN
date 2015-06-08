/*
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.serp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;

import com.travelsoft.cameleo.catalog.data.PublishedProduct;

/**
 * <p>Title: ProductRankComparator.</p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Travelsoft</p>
 * @author fernando.mendez
 */
public class ProductRankComparator implements Comparator<PublishedProduct>, Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Compares two products assuming there is a sortInfo Integer on both of them. */
    public int compare(PublishedProduct o1, PublishedProduct o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null && o2 != null) {
            return -1;
        }
        if (o1 != null && o2 == null) {
            return 1;
        }
        int sortInfoCount1 = o1.getSortInfoCount();
        int sortInfoCount2 = o2.getSortInfoCount();
        if (sortInfoCount1 > sortInfoCount2) {
            return -1;
        }
        if (sortInfoCount2 > sortInfoCount1) {
            return 1;
        }
        if (sortInfoCount1 == 1 && 1 == sortInfoCount2) {
            Object sortInfo1 = o1.getSortInfo(0);
            Object sortInfo2 = o2.getSortInfo(0);
            if (sortInfo1 instanceof Integer && sortInfo2 instanceof Integer) {
                Integer int1 = (Integer) sortInfo1;
                Integer int2 = (Integer) sortInfo2;
                int sortInfoCompareValue = int1.compareTo(int2);
                if (sortInfoCompareValue != 0) {
                    return sortInfoCompareValue;
                }
                BigDecimal bp1 = o1.getBasePrice();
                BigDecimal bp2 = o2.getBasePrice();
                if (bp1 != null && bp2 != null) {
                    return bp1.compareTo(bp2);
                }

            }
        }
        return 0;
    }

}
