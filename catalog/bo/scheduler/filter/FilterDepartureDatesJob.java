/*
 * Created on 23 déc. 2011 Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.bo.scheduler.filter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

import com.travelsoft.cameleo.catalog.data.Criterion;
import com.travelsoft.cameleo.catalog.data.CriterionValue;
import com.travelsoft.cameleo.catalog.data.ProductFilter;
import com.travelsoft.cameleo.catalog.data.ProductSearchCriteria;
import com.travelsoft.cameleo.catalog.data.ProductSearchResponse;
import com.travelsoft.cameleo.catalog.data.TourOperator;
import com.travelsoft.cameleo.catalog.interfaces.ServicesFactory;
import com.travelsoft.cameleo.catalog.interfaces.Constants.FilterConstants;
import com.travelsoft.cameleo.catalog.interfaces.exceptions.TechnicalException;
import com.travelsoft.cameleo.catalog.interfaces.filter.FilterServicesInterface;
import com.travelsoft.cameleo.catalog.interfaces.search.ProductSearchServicesInterface;
import com.travelsoft.cameleo.catalog.interfaces.setup.SetupServicesInterface;
import com.travelsoft.nucleus.cache.generic.CacheManager;
import com.travelsoft.nucleus.cache.jboss.implementations.JbossCacheManagerFactory;

/**
 * <p>
 * Titre : FilterDepartureDatesJob.
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
public class FilterDepartureDatesJob implements StatefulJob {

    /** The logger . */
    private static final Logger LOGGER = Logger.getLogger(FilterDepartureDatesJob.class);

    /** The cache manager. */
    private static final String CAMELEO_TREE_CACHE = "CameleoTreeCache";

    /** The date formater. */
    private SimpleDateFormat dateFormater = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);


    /**
     * Job execution method.
     *
     * @param context : the Job context
     * @throws JobExecutionException : job exception
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    public void execute(JobExecutionContext context) throws JobExecutionException {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("In FilterDepartureDatesJob class");
        }

        try {
            ArrayList<String> productIds = new ArrayList<String>();

            // 1. Search all products that has departure dates for the next day or 2 days
            ProductSearchCriteria criteria = this.getSearchCriteria();

            this.setMinAndMaxDepartureDates(criteria, 1);

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

    /**
     * Retrieves the search criteria.
     * @return The search criteria
     */
    protected ProductSearchCriteria getSearchCriteria() {
        ProductSearchCriteria criteria = new ProductSearchCriteria();
        criteria.setResponseFormat(com.travelsoft.cameleo.catalog.interfaces.Constants.JUST_ID_FORMAT);
        Criterion criterion = new Criterion();
        criterion.setCode("D0");
        CriterionValue criterionValue = new CriterionValue();
        criterionValue.setCode("!OUI");
        criterion.setValue(new CriterionValue[]{criterionValue});
        criteria.setCustomCriteria(new Criterion[]{criterion});
        return criteria;
    }

    /**
     * Filters products.
     * @param productIds The products codes to filter
     * @param criteria The product search criteria
     * @throws TechnicalException if errors occurs
     */
    protected void filterProducts(ArrayList<String> productIds, ProductSearchCriteria criteria) throws TechnicalException {
        FilterServicesInterface filterServicesInterface = ServicesFactory.getFilterServices();
        ProductFilter productFilter = new ProductFilter();
        productFilter.setAction(FilterConstants.ACTION_NE_PAS_IMPORTER);
        productFilter.setType(FilterConstants.DISPONIBILITY);
        productFilter.setFromNbDays(FilterConstants.WHATEVER_VALUE);
        productFilter.setToNbDays(FilterConstants.WHATEVER_VALUE);
        productFilter.setFromNbNights(FilterConstants.WHATEVER_VALUE);
        productFilter.setToNbNights(FilterConstants.WHATEVER_VALUE);
        productFilter.setFromDate(dateFormater.format(criteria.getMinDepartureDate().toDate()));
        productFilter.setToDate(dateFormater.format(criteria.getMaxDepartureDate().toDate()));
        String[] tourOperatorCodes = criteria.getTourOperatorCode();
        if (tourOperatorCodes != null && tourOperatorCodes.length > 0) {
            TourOperator to = new TourOperator();
        	to.setCode(tourOperatorCodes[0]);
        	productFilter.setTourOperator(new TourOperator[] {to});
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Products identifier to filter : " + productIds.toString());
        }

        Integer filteredProductsCount = filterServicesInterface.applyFilter(productFilter, productIds, new ProductSearchCriteria());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Filtered products : " + filteredProductsCount);
        }
    }

    /**
     * Search products from a given criteria.
     *
     * @param criteria The criteria
     * @return The search response
     * @throws TechnicalException if errors occurs
     */
    protected ProductSearchResponse searchProducts(ProductSearchCriteria criteria) throws TechnicalException {
        ProductSearchServicesInterface prodSearchServices = ServicesFactory.getProductSearchServices();
        return prodSearchServices.searchProducts(criteria);
    }

    /**
     * Sets min and max departure dates.
     *
     * @param criteria The product search criteria
     */
    protected void setMinAndMaxDepartureDates(ProductSearchCriteria criteria, int day) {
        try {
            Calendar calendar = Calendar.getInstance();
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY
                || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                day = day + 1;
            }
            calendar.add(Calendar.DATE, day);
            String departureMaxDateStr = dateFormater.format(calendar.getTime());

            Calendar currentCalendar = Calendar.getInstance();
            String departureMinDateStr = dateFormater.format(currentCalendar.getTime());

            if (departureMinDateStr != null) {
                criteria.setMinDepartureDate(new org.exolab.castor.types.Date(dateFormater.parse(departureMinDateStr)));
            }

            if (departureMaxDateStr != null) {
                criteria.setMaxDepartureDate(new org.exolab.castor.types.Date(dateFormater.parse(departureMaxDateStr)));
            }

        } catch (ParseException e) {
            LOGGER.error(e.toString(), e);
        }
    }


    /**
     * Returns a new {@link Collection} containing <tt>a - b.
     * The cardinality of each element <i>e in the returned {@link Collection}
     * will be the cardinality of <i>e in a minus the cardinality
     * of <i>e in b, or zero, whichever is greater.
     *
     * @param a the collection to subtract from, must not be null
     * @param b the collection to subtract, must not be null
     * @return a new collection with the results
     * @see Collection#removeAll
     */
    public List subtract(final Collection a, final Collection b) {
        List<String> list = new ArrayList<String>(a);
        for (Iterator it = b.iterator(); it.hasNext();) {
            list.remove(it.next());
        }
        return list;
    }

    /**
     * Retrieves configuration value from a given key.
     * @param key The configuration key
     * @return The configuration value
     */
    protected String getConfigValue(String key) {
        CacheManager cacheManager = JbossCacheManagerFactory.getCacheManager(CAMELEO_TREE_CACHE);
        SetupServicesInterface productService = ServicesFactory.getSetupServices();
        try {
            return productService.getConfigValue(key, cacheManager);
        } catch (TechnicalException e) {
            LOGGER.fatal("Technical Exception calling getConfigValue()", e);
        }
        return null;
    }
}
