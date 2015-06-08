/*
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.product;

import org.apache.commons.lang.StringEscapeUtils;

import com.travelsoft.cameleo.catalog.data.PublishedProductSearchCriteria;
import com.travelsoft.nucleus.cache.generic.CacheKey;

/**
 * <p>Title: LastminuteProductKey.</p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Travelsoft</p>
 * @author fernando.mendez
 */
public class LastminuteProductKey implements CacheKey {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /** The product id. */
    private final String pid;

    /** The published product search criteria. */
    private final PublishedProductSearchCriteria searchCriteria;

    /**
     *
     * @param pid
     * @param searchCriteria
     */
    public LastminuteProductKey(String pid, PublishedProductSearchCriteria searchCriteria) {
        super();
        this.pid = pid;
        this.searchCriteria = searchCriteria;
    }

    /**
     * @return the pid
     */
    public String getPid() {
        return pid;
    }

    /**
     * @return the searchCriteria
     */
    public PublishedProductSearchCriteria getSearchCriteria() {
        return (PublishedProductSearchCriteria) searchCriteria.clone();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pid == null) ? 0 : pid.hashCode());
        result = prime * result + ((searchCriteria == null) ? 0 : searchCriteria.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LastminuteProductKey other = (LastminuteProductKey) obj;
        if (pid == null) {
            if (other.pid != null)
                return false;
        } else if (!pid.equals(other.pid))
            return false;
        if (searchCriteria == null) {
            if (other.searchCriteria != null)
                return false;
        } else if (!searchCriteria.equals(other.searchCriteria))
            return false;
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "LastminuteProductKey [pid=" + pid + ", searchCriteria="
                + StringEscapeUtils.escapeHtml(searchCriteria.convertToString()) + "]";
    }

}
