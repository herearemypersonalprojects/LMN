/*
 * Created on 2 janv. 2012
 *
 * Copyright Travelsoft, 2012.
 */
package com.travelsoft.lastminute.catalog.resource;

/**
 * <p>Titre : ResourceRequest.</p>
 * <p>Description : .</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Travelsoft</p>
 * @author zouhair.mechbal
 */
public class ResourceRequest {

    /**  The canonicalAddress. */
    private String canonicalAddress;

    /** The file name (without extension). */
    private String resourceName;

    /** The file extension. */
    private String fileExtension;

    /** The server name. */
    private String serverName;

    /**
     * Getter for <code>canonicalAddress</code>.
     *
     * @return Returns the canonicalAddress.
     */
    public String getCanonicalAddress() {
        return canonicalAddress;
    }

    /**
     * Setter for <code>canonicalAddress</code>.
     *
     * @param canonicalAddress The canonicalAddress to set.
     */
    public void setCanonicalAddress(String canonicalAddress) {
        this.canonicalAddress = canonicalAddress;
    }

    /**
     * Getter for <code>resourceName</code>.
     *
     * @return Returns the resourceName.
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * Setter for <code>resourceName</code>.
     *
     * @param resourceName The resourceName to set.
     */
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    /**
     * Getter for <code>fileExtension</code>.
     *
     * @return Returns the fileExtension.
     */
    public String getFileExtension() {
        return fileExtension;
    }

    /**
     * Setter for <code>fileExtension</code>.
     *
     * @param fileExtension The fileExtension to set.
     */
    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    /**
     * Getter for <code>serverName</code>.
     *
     * @return Returns the serverName.
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * Setter for <code>serverName</code>.
     *
     * @param serverName The serverName to set.
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /**
     * Returns a hash code value for the object.
     * This method is supported for the benefit of hashtables such as those provided by java.util.Hashtable.
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((canonicalAddress == null) ? 0 : canonicalAddress.hashCode());
        result = prime * result + ((fileExtension == null) ? 0 : fileExtension.hashCode());
        result = prime * result + ((resourceName == null) ? 0 : resourceName.hashCode());
        return result;
    }



    /**
     * Indicates whether some other object is "equal to" this one.
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ResourceRequest other = (ResourceRequest) obj;
        if (canonicalAddress == null) {
            if (other.canonicalAddress != null)
                return false;
        } else if (!canonicalAddress.equals(other.canonicalAddress))
            return false;
        if (fileExtension == null) {
            if (other.fileExtension != null)
                return false;
        } else if (!fileExtension.equals(other.fileExtension))
            return false;
        if (resourceName == null) {
            if (other.resourceName != null)
                return false;
        } else if (!resourceName.equals(other.resourceName))
            return false;
        if (serverName == null) {
            if (other.serverName != null)
                return false;
        } else if (!serverName.equals(other.serverName))
            return false;
        return true;
    }

}
