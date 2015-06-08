/*
 * Created on 2 janv. 2012 Copyright Travelsoft, 2012.
 */
package com.travelsoft.lastminute.catalog.resource;

import com.travelsoft.nucleus.cache.generic.CacheKey;

/**
 * <p>
 * Titre : ResourceCacheKey.
 * </p>
 * <p>
 * Description : .
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: Travelsoft
 * </p>
 *
 * @author zouhair.mechbal
 */
public class ResourceCacheKey implements CacheKey {


    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /** The resource request. */
    private ResourceRequest request;


    /**
     * Public default constructor.
     *
     * @param resourceRequest the resource request in the context of the given reservation profile
     */
    public ResourceCacheKey(ResourceRequest resourceRequest) {
        super();
        this.setRequest(resourceRequest);
    }


    /** @return Returns the request. */
    public ResourceRequest getRequest() {
        return this.request;
    }


    /** @param resourceRequest The request to set. */
    public void setRequest(ResourceRequest resourceRequest) {
        this.request = resourceRequest;
    }


    /**
     * Returns a hash code value for the object. This method is supported for the benefit of hashtables such as those
     * provided by java.util.Hashtable.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((request == null) ? 0 : request.hashCode());
        return result;
    }


    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        ResourceCacheKey other = (ResourceCacheKey) obj;
        if (request == null) {
            if (other.request != null) {
                return false;
            }
        } else if (!request.equals(other.request)) {
            return false;
        }
        return true;
    }
}
