/*
 * Created on 2 janv. 2012
 *
 * Copyright Travelsoft, 2012.
 */
package com.travelsoft.lastminute.catalog.resource;

import com.travelsoft.nucleus.cache.generic.CacheValue;

/**
 * <p>Titre : ResourceCacheValue.</p>
 * <p>Description : .</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Travelsoft</p>
 * @author zouhair.mechbal
 */
public class ResourceCacheValue implements CacheValue {

    /** Serial version uid. */
    private static final long serialVersionUID = 1L;

    /** The resource to cache. */
    private FileResource fileResource;

    /**
     * Public default constructor.
     * @param resourceToCache the resource to cache.
     */
    public ResourceCacheValue(FileResource resourceToCache) {
        super();
        this.setFileResource(resourceToCache);
    }

    /**
     * @return The cached object
     */
    public Object getCachedObject() {
        return this.fileResource;
    }

    /**
     * @return The description
     */
    public String getDescription() {
        return "";
    }

    /**
     * Getter for <code>fileResource</code>.
     *
     * @return Returns the fileResource.
     */
    public FileResource getFileResource() {
        return fileResource;
    }

    /**
     * Setter for <code>fileResource</code>.
     *
     * @param fileResource The fileResource to set.
     */
    public void setFileResource(FileResource fileResource) {
        this.fileResource = fileResource;
    }
}