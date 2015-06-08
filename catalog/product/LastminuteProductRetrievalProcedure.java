/*
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.data.PublishedProduct;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchCriteria;
import com.travelsoft.cameleo.catalog.interfaces.ServicesFactory;
import com.travelsoft.cameleo.catalog.interfaces.exceptions.TechnicalException;
import com.travelsoft.cameleo.catalog.interfaces.search.PublishedProductSearchServicesInterface;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.nucleus.cache.generic.CacheKeyValuePair;
import com.travelsoft.nucleus.cache.generic.IServiceProcedure;
import com.travelsoft.nucleus.cache.generic.IServiceVectorialProcedure;
import com.travelsoft.nucleus.cache.generic.ProcedureException;

/**
 * <p>Title: LastminuteProductRetrievalProcedure.</p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Travelsoft</p>
 * @author fernando.mendez
 */
public class LastminuteProductRetrievalProcedure implements
        IServiceProcedure<LastminuteProductKey, LastminuteProductValue, PublishedProduct>,
        IServiceVectorialProcedure<LastminuteProductKey, LastminuteProductValue, PublishedProduct> {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(LastminuteProductRetrievalProcedure.class);

    /**
     * Retrieves a published product through a service call.
     */
    public LastminuteProductValue process(LastminuteProductKey key) {
        PublishedProductSearchServicesInterface productService = ServicesFactory.getPublishedProductSearchServices();
        PublishedProduct publishedProduct;
        try {
            publishedProduct = productService.getPublishedProduct(key.getPid(), key.getSearchCriteria(),
                    Constants.CAMELEO_CACHE_MANAGER);
            LastminuteProductValue value = new LastminuteProductValue(this, publishedProduct);
            return value;
        } catch (TechnicalException e) {
            if (LOGGER.isEnabledFor(Level.WARN)) {
                LOGGER.warn("Failed to retrieve a published product for key " + key);
            }
            throw new ProcedureException("Failed to retrieve a published product", e);
        }

    }

    /** Retrieves a list of published products through a service call. */
    public List<CacheKeyValuePair<LastminuteProductKey, LastminuteProductValue, PublishedProduct>> process(
            List<LastminuteProductKey> keys) {
        PublishedProductSearchServicesInterface searchServices = ServicesFactory.getPublishedProductSearchServices();

        // Group keys by criteria in order to build a single service call.
        Map<PublishedProductSearchCriteria, List<LastminuteProductKey>> productLists = new HashMap<PublishedProductSearchCriteria, List<LastminuteProductKey>>();
        for (LastminuteProductKey key : keys) {
            PublishedProductSearchCriteria searchCriteria = key.getSearchCriteria();
            List<LastminuteProductKey> list = productLists.get(searchCriteria);
            if (list == null) {
                list = new ArrayList<LastminuteProductKey>();
                productLists.put(searchCriteria, list);
            }
            list.add(key);
        }

        List<CacheKeyValuePair<LastminuteProductKey, LastminuteProductValue, PublishedProduct>> result = new ArrayList<CacheKeyValuePair<LastminuteProductKey, LastminuteProductValue, PublishedProduct>>();
        Set<Entry<PublishedProductSearchCriteria, List<LastminuteProductKey>>> entrySet = productLists.entrySet();
        for (Entry<PublishedProductSearchCriteria, List<LastminuteProductKey>> entry : entrySet) {
            PublishedProductSearchCriteria criteria = entry.getKey();
            List<LastminuteProductKey> keyList = entry.getValue();

            // Project pids from the key list.
            int pidCount = keyList.size();
            String pidArray[] = new String[pidCount];
            for (int i = 0; i < pidCount; i++) {
                pidArray[i] = keyList.get(i).getPid();
            }

            try {
                PublishedProduct[] products = searchServices.getPublishedProducts(pidArray, criteria,
                        Constants.CAMELEO_CACHE_MANAGER);
                int productCount = products.length;
                for (int i = 0; i < productCount; i++) {
                    PublishedProduct publishedProduct = products[i];

                    result.add(new CacheKeyValuePair<LastminuteProductKey, LastminuteProductValue, PublishedProduct>(
                            keyList.get(i), new LastminuteProductValue(this, publishedProduct)));
                }
            } catch (TechnicalException e) {
                if (LOGGER.isEnabledFor(Level.WARN)) {
                    LOGGER.warn("Failed to retrieve product for ID list " + StringUtils.join(pidArray, ',')
                            + " and criteria " + criteria.convertToString());
                }
                throw new ProcedureException("Failed to retrieve a set of published products", e);
            }
        }

        return result;
    }

}
