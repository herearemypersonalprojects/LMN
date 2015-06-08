/*
 * Created on 2 janv. 2012
 *
 * Copyright Travelsoft, 2012.
 */
package com.travelsoft.lastminute.catalog.resource;

import org.apache.log4j.Logger;

import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.nucleus.cache.generic.Cache;
import com.travelsoft.nucleus.cache.generic.CacheException;
import com.travelsoft.nucleus.cache.generic.CacheManager;

/**
 * <p>Titre : ResourceService.</p>
 * <p>Description : .</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Travelsoft</p>
 * @author zouhair.mechbal
 */
public class ResourceService {

    /** Logger. */
    private static Logger LOGGER = Logger.getLogger(ResourceService.class);

    /** The cache manager (optional). */
    private CacheManager cacheManager;

    /**
     * Constructor for <code>ResourceService</code>.
     */
    public ResourceService() {
        this.cacheManager = Constants.CAMELEO_CACHE_MANAGER;
    }

    /**
     * Builds a web file resource relative URL.
     *
     * @param serverName The server name
     * @param canonicalAddress the canonical address
     * @param resourceName the file name (without extension)
     * @param fileExtension the file extension
     * @param request The servlet request
     * @return a file resource relative URL
     */
    public String buildWebFileResourceUrl(String serverName, String canonicalAddress, String resourceName, String fileExtension) {
        return this.retrieveResource(serverName, canonicalAddress, resourceName, fileExtension);
    }

    /**
     * Builds a complete local file resource relative URL.
     *
     * @param canonicalAddress the canonical address
     * @param resourceName the file name (without extension)
     * @param fileExtension the file extension
     * @return a file resource relative URL
     */
    public String buildCompleteLocalFileResourceUrl(String serverName, String canonicalAddress, String resourceName, String fileExtension) {
        FileResource resource = this.retrieveFileResource(serverName, canonicalAddress, resourceName, fileExtension);
        if (resource == null) {
            return null;
        }
        return resource.getPhysicalUrl();
    }


    /**
     * Method retrieves a resource from a given parameters.
     * @param serverName The server name
     * @param canonicalAddress the canonical address
     * @param resourceName the file name (without extension)
     * @param fileExtension the file extension
     * @return a resource location
     */
    private String retrieveResource(String serverName, String canonicalAddress, String resourceName, String fileExtension) {

        FileResource resource = this.retrieveFileResource(serverName, canonicalAddress, resourceName, fileExtension);
        if (resource == null) {
            return null;
        }
        return resource.getRelativeUrl();
    }

    /**
     * Easily provides a file resource for a given resource name.
     *
     * @param serverName The server name
     * @param canonicalAddress the canonicalAddress
     * @param resourceName the file name (without extension)
     * @param fileExtension the file extension
     * @return the <code>FileResource</code>
     */
    public FileResource retrieveFileResource(String serverName, String canonicalAddress, String resourceName, String fileExtension) {
        ResourceRequest request = new ResourceRequest();
        request.setCanonicalAddress(canonicalAddress);
        request.setResourceName(resourceName);
        request.setFileExtension(fileExtension);
        request.setServerName(serverName);
        FileResource resource = this.retrieveResource(request);
        return resource;
    }

    /**
     * @param request The resource request
     * @return The file resource
     */
    private FileResource retrieveResource(ResourceRequest request) {

        FileResource fileResource = new FileResource();

        Cache resourcesCache = null;

        if (cacheManager != null) {
            resourcesCache = cacheManager.getCache(Constants.Context.STATIC_FILE_CACHE_NAME);
        }

        ResourceCacheKey resourceCacheKey = new ResourceCacheKey(request);
        ResourceCacheValue resourceCacheValue = null;
        if (resourcesCache != null) {
            try {
                resourceCacheValue = (ResourceCacheValue) resourcesCache.retrieve(resourceCacheKey);
            } catch (CacheException e) {
                LOGGER.warn("CacheException when retrieving a resource for request " + request, e);
            }
        }
        if (resourceCacheValue != null) {
            return resourceCacheValue.getFileResource();
        }

        fileResource = fileResource.initialize(request);

        if (resourcesCache != null) {
            ResourceCacheValue newResourceCacheValue = new ResourceCacheValue(fileResource);
            try {
                resourcesCache.addOrUpdate(resourceCacheKey, newResourceCacheValue, null);
            } catch (CacheException e) {
                LOGGER.warn("Failed to update the resources cache with key:\n" + resourceCacheKey + "\nand value:\n"
                    + newResourceCacheValue, e);
            }
        }
        return fileResource;
    }
}
