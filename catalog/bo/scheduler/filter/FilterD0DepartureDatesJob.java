/*
 * Created on 30 déc. 2011 Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.bo.scheduler.filter;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.travelsoft.cameleo.catalog.data.ProductSearchCriteria;
import com.travelsoft.cameleo.catalog.data.ProductSearchResponse;
import com.travelsoft.cameleo.catalog.interfaces.exceptions.TechnicalException;

/**
 * <p>
 * Titre : FilterD0DepartureDatesJob.
 * </p>
 * <p>
 * Description : .
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Travelsoft
 * </p>
 *
 * @author zouhair.mechbal
 */
public class FilterD0DepartureDatesJob extends FilterDepartureDatesJob {


    /** The logger . */
    private static final Logger LOGGER = Logger.getLogger(FilterD0DepartureDatesJob.class);


    /**
     * Job execution method.
     *
     * @param context : the Job context
     * @throws JobExecutionException : job exception
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    public void execute(JobExecutionContext context) throws JobExecutionException {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("In FilterD0DepartureDatesJob class");
        }

        try {
            ArrayList<String> productIds = new ArrayList<String>();

            // 1. Search all products that has departure dates for the next day or 2 days
            ProductSearchCriteria criteria = this.getSearchCriteria();

            this.setMinAndMaxDepartureDates(criteria, 0);

            // 2. Process search products
            ProductSearchResponse productResponse = this.searchProducts(criteria);

            for (String productCode : productResponse.getProductInternalCode()) {
                productIds.add(productCode);
            }

            // 3. Filter products
            if (productIds != null && productIds.size() != 0) {
                this.filterProducts(productIds, criteria);
            }
        } catch (TechnicalException e) {
            LOGGER.error("Error while trying to close departure dates.", e);
        }
    }
}
