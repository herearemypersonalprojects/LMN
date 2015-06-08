/*
 * Copyright Travelsoft, 2010.
 */
package com.travelsoft.lastminute.catalog.directives;

import com.travelsoft.nucleus.cache.generic.CacheValue;

/**
 * String cache value.
 */
public class StringCacheValue implements CacheValue {

    /** The serial version UID. */
    private static final long serialVersionUID = 1L;

    /** The data map. */
    private String stringValue;

    /**
     * Public default constructor.
     * @param stringPar The stringPar
     */
    public StringCacheValue(String stringPar) {
        super();
        this.stringValue = stringPar;
    }

    /** @return the cached instance */
    public Object getCachedObject() {
        return this.stringValue;
    }

    /** @return a human-readable description for this instance */
    public String getDescription() {
        if (stringValue == null) {
            return "No data!";
        }
        return stringValue;
    }

    /**
     * @return the stringValue
     */
    public String getString() {
        return this.stringValue;
    }

}
