/*
 * Created on 24 sept. 2012
 *
 * Copyright Travelsoft, 2012.
 */
package com.travelsoft.lastminute.catalog.comparator;

import java.util.Comparator;

import com.travelsoft.lastminute.data.LastMinuteDeparture;

/**
 * <p>Title: LastMinuteDepartureComparator.java.</p>
 * <p>Description: .</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Travelsoft</p>
 *
 * @author thiago.bohme
 */
public class LastMinuteDepartureComparator implements Comparator<LastMinuteDeparture> {

    public int compare(LastMinuteDeparture d1, LastMinuteDeparture d2) {
        if (d1 != null && d2 != null
                && d1.getDate() != null && d2.getDate() != null
                && d1.getAvail() != null && d2.getAvail() != null
                && d1.getAvail().getTtcPrice() != null && d2.getAvail().getTtcPrice() != null) {

            int ret = d1.getDate().compareTo(d2.getDate()) - 1;
            if (ret != 0) {
                return ret;
            }
            return d1.getAvail().getTtcPrice().compareTo(d2.getAvail().getTtcPrice());
        }
        return 0;
    }

}
