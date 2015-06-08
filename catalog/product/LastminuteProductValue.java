/*
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.product;

import org.apache.commons.lang.StringEscapeUtils;

import com.travelsoft.cameleo.catalog.data.PublishedProduct;
import com.travelsoft.nucleus.cache.generic.IServiceProcedure;
import com.travelsoft.nucleus.cache.generic.RefreshableCacheValue;

/**
 * <p>Title: LastminuteProductValue.</p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Travelsoft</p>
 * @author fernando.mendez
 */
public class LastminuteProductValue extends
    RefreshableCacheValue<LastminuteProductKey, LastminuteProductValue, PublishedProduct> {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /** The proxified product. */
    private final PublishedProduct product;

    /**
     *
     * @param refreshProcedure
     * @param product
     */
    public LastminuteProductValue(
            IServiceProcedure<LastminuteProductKey, LastminuteProductValue, PublishedProduct> refreshProcedure,
            PublishedProduct product) {
        super(refreshProcedure);
        this.product = product;
    }

    /** @return the cached product. */
    public PublishedProduct getCachedObject() {
        return this.product;
    }

    /**
     * @return A string representation of this instance (HTML compatible)
     */
    public String getDescription() {
        return StringEscapeUtils.escapeHtml(this.product == null ? "no data" : this.product.convertToString());
    }

}
