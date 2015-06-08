/*
 * Created on 2 janv. 2012
 *
 * Copyright Travelsoft, 2012.
 */
package com.travelsoft.lastminute.catalog.resource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.travelsoft.lastminute.catalog.util.Util;



/**
 * <p>Titre : FileResource.</p>
 * <p>Description : .</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Travelsoft</p>
 * @author zouhair.mechbal
 */
public class FileResource {

    /** Logger. */
    private static Logger LOGGER = Logger.getLogger(ResourceService.class);

    /** The default brand name. */
    private static final String DEFAULT_BRAND_NAME = "lastminute-catalog";

    /** The relative URL for this resource. */
    private String relativeUrl;

    /** The relative URL for this resource. */
    private String physicalUrl;

    /** The root for all the physical paths. */
    private String physicalPathRoot;


    /**
     * Initializes this resource with the first found physical file.
     * @param request the resource request
     * @param resourceLocations the list of resource locations
     * @return the file resource if found or created
     */
    public FileResource initialize(ResourceRequest request) {
        String[] locations = this.getResourceLocationDescriptors(request);
        if (locations != null) {
            for (String resourceLocation : locations) {
                String path = this.calculatePhysicalPathsForResourceLocation(request, this.getPhysicalPathRoot(), resourceLocation);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Resource physical path : " + path);
                }
                File file = new File(path);
                FileResource resource = new FileResource();
                if (file.canRead()) {
                    relativeUrl = this.buildRelativeUrl(resourceLocation, request);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("The relative url is : " + relativeUrl);
                    }
                    resource.physicalUrl = this.buildPhysicalUrl(resourceLocation, request);
                    resource.relativeUrl = relativeUrl;
                    return resource;
                }

            }
        }
        return null;
    }


    /**
     * Method builds a relative URL.
     * @param resourceLocation The resource location
     * @param request The resource request
     * @param pathContext The path context (The specific or generic directory)
     * @return The relative URL
     *
     */
    private String buildRelativeUrl(String resourceLocation, ResourceRequest request) {
        return getSubLocalFileDirectory(resourceLocation, request).concat(request.getCanonicalAddress()).concat("/")
            .concat(request.getResourceName()).concat(".").concat(request.getFileExtension());
    }

    /**
     * Method builds a physical URL.
     * @param resourceLocation The resource location
     * @param request The resource request
     * @param pathContext The path context (The specific or generic directory)
     * @return The relative URL
     *
     */
    private String buildPhysicalUrl(String resourceLocation, ResourceRequest request) {
        return "/shared/cs/web/".concat(resourceLocation).concat("/").concat(request.getCanonicalAddress()).concat("/")
            .concat(request.getResourceName()).concat(".").concat(request.getFileExtension());
    }

    /**
     * Gets a sub file directory.
     * @param resourceLocation The resource location
     * @param request The resource request
     * @return a sub file directory
     */
    private String getSubLocalFileDirectory(String resourceLocation, ResourceRequest request) {
        return "/shared-cs/".concat(resourceLocation).concat("/");
    }

    /**
     * Creates a file at the given resource location.
     *
     * @param request The resource request
     * @param physicalPathRoot The physical root directory
     * @param resourceLocation The resource location
     * @param pathContext The path context
     * @return a file at the given resource location
     */
    private String calculatePhysicalPathsForResourceLocation(ResourceRequest request, String physicalPathRoot,
        String resourceLocation) {
        return physicalPathRoot.concat(resourceLocation).concat("/").concat(request.getCanonicalAddress()).concat("/")
            .concat(request.getResourceName()).concat(".").concat(request.getFileExtension());
    }

    /**
     * @param request The resource request
     * @return a list of resource locations
     */
    private String[] getResourceLocationDescriptors(ResourceRequest request) {
        List<String> result = new ArrayList<String>();
        String brandName = this.getBrandName(request);
        if (brandName != null) {
            result.add(brandName);
        }
        result.add(DEFAULT_BRAND_NAME);
        return result.toArray(new String[result.size()]);
    }

    /**
     * Retrieves the brand name from the request
     * @param request The request
     * @return The brand name
     */
    private String getBrandName(ResourceRequest request) {
        String serverName = request.getServerName();
        if (serverName != null) {
            String configValue = Util.getConfigValue(serverName);
            if (!"".equals(configValue)) {
                return configValue;
            }
        }
        return null;
    }

    /**
     * Getter for <code>physicalPathRoot</code>.
     *
     * @return Returns the physicalPathRoot.
     */
    public String getPhysicalPathRoot() {
        if (physicalPathRoot == null) {
            this.physicalPathRoot = Util.getConfigValue("STATIC_ROOT_SHARED").concat("/").concat("cs/web/");
        }
        return physicalPathRoot;
    }

    /**
     * Setter for <code>relativeUrl</code>.
     *
     * @param relativeUrl The relativeUrl to set.
     */
    public void setRelativeUrl(String relativeUrl) {
        this.relativeUrl = relativeUrl;
    }

    /**
     * Getter for <code>relativeUrl</code>.
     *
     * @return Returns the relativeUrl.
     */
    public String getRelativeUrl() {
        return relativeUrl;
    }

    /**
     * Getter for <code>physicalUrl</code>.
     *
     * @return Returns the physicalUrl.
     */
    public String getPhysicalUrl() {
        return physicalUrl;
    }


    /**
     * Setter for <code>physicalUrl</code>.
     *
     * @param physicalUrl The physicalUrl to set.
     */
    public void setPhysicalUrl(String physicalUrl) {
        this.physicalUrl = physicalUrl;
    }
}
