/*
 * Created on 23 déc. 2011 Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.bo.scheduler.filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.travelsoft.cameleo.catalog.data.Criterion;
import com.travelsoft.cameleo.catalog.data.CriterionValue;
import com.travelsoft.cameleo.catalog.data.ProductSearchCriteria;
import com.travelsoft.cameleo.catalog.data.ProductSearchResponse;
import com.travelsoft.cameleo.catalog.interfaces.ServicesFactory;
import com.travelsoft.cameleo.catalog.interfaces.exceptions.TechnicalException;
import com.travelsoft.cameleo.catalog.interfaces.products.ProductServicesInterface;

/**
 * <p>
 * Titre : FilterNoPushedDepartureDatesJob.
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
public class FilterNoPushedDepartureDatesJob extends FilterDepartureDatesJob {


    /** The logger . */
    private static final Logger LOGGER = Logger.getLogger(FilterNoPushedDepartureDatesJob.class);


    /**
     * Job execution method.
     *
     * @param context : the Job context
     * @throws JobExecutionException : job exception
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    public void execute(JobExecutionContext context) throws JobExecutionException {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("In FilterNoPushedDepartureDatesJob class");
        }

        try {
            ArrayList<String> productIds = new ArrayList<String>();
            ArrayList<String> productWithPushingIds = new ArrayList<String>();

            // 1. Search all products that has departure dates for the next day or 2 days
            ProductSearchCriteria criteria = new ProductSearchCriteria();
            criteria.setResponseFormat(com.travelsoft.cameleo.catalog.interfaces.Constants.JUST_ID_FORMAT);
            this.setMinAndMaxDepartureDates(criteria, 1);
            Criterion criterion = new Criterion();
            criterion.setCode("D0");
            CriterionValue criterionValue = new CriterionValue();
            criterionValue.setCode("OUI");
            criterion.setValue(new CriterionValue[]{criterionValue});
            criteria.setCustomCriteria(new Criterion[]{criterion});
            ProductSearchResponse productResponse = this.searchProducts(criteria);

            // Perform the filtering on the matched products
            for (String productCode : productResponse.getProductInternalCode()) {
                productIds.add(productCode);
            }

            // 2. Search products that have a valid pushing and then we extract products that not have a pushing
            criteria.setPushingCategory("D0");
            criteria.setResultWithPushings(true);

            ProductSearchResponse productResponseWithPushing = this.searchProducts(criteria);

            for (String productCode : productResponseWithPushing.getProductInternalCode()) {
                productWithPushingIds.add(productCode);
            }

            List<String> productsToFilter = this.subtract(productIds, productWithPushingIds);
            if (productsToFilter != null && productsToFilter.size() != 0) {
                this.filterProducts((ArrayList<String>) productsToFilter, criteria);
                String[] productsToFilterArray = productsToFilter.toArray(new String[productsToFilter.size()]);
                this.removeCriteria(productsToFilterArray, criterion);
            }
        } catch (TechnicalException e) {
            LOGGER.error("Error while trying to close departure dates.", e);
        }
    }

    /**
     * Removes the D0 criteria.
     * @param productsIds The products codes
     * @param criterion The criterion
     */
    private void removeCriteria(String[] productsIds, Criterion criterion) {
        try {
            ProductServicesInterface productServices = ServicesFactory.getProductServices();
            productServices.updateCriteria(productsIds, new Criterion[] {}, new Criterion[] {criterion});
        } catch (TechnicalException e) {
            LOGGER.error("Unable to remove criteria.", e);
        }
    }
}
