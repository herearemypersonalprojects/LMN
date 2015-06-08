/*
 * Created on 30 déc. 2011
 *
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.bo.scheduler.filter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.travelsoft.cameleo.catalog.data.ProductSearchCriteria;
import com.travelsoft.cameleo.catalog.data.ProductSearchResponse;
import com.travelsoft.cameleo.catalog.interfaces.exceptions.TechnicalException;



/**
 * <p>Titre : FilterD3DepartureDatesJob.</p>
 * <p>Description : .</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Travelsoft</p>
 * @author zouhair.mechbal
 */
public class FilterD3DepartureDatesJob extends FilterDepartureDatesJob {

    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(FilterD3DepartureDatesJob.class);

    /** The configuration key.*/
    private static final String FILTERED_TOS = "FILTERED_TOS";

    /** The separator. */
    private static final String SEPARATOR = ";";

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
            LOGGER.info("In FilterD3DepartureDatesJob class");
        }

        try {
            ArrayList<String> productIds = new ArrayList<String>();

            // 1. Search all products that has departure dates for the next day or 2 days
            ProductSearchCriteria criteria = this.getSearchCriteria();

            this.setMinAndMaxDepartureDates(criteria, 3);

            String tosToFilter = this.getConfigValue(FILTERED_TOS);
            if (tosToFilter != null) {
                String[] tos = tosToFilter.split(SEPARATOR);
                if (tos != null) {
                    criteria.setTourOperatorCode(tos);
                }
            }

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
    * Sets min and max departure dates.
    *
    * @param criteria The product search criteria
    */
   protected void setMinAndMaxDepartureDates(ProductSearchCriteria criteria, int day) {
       try {
           Calendar calendar = Calendar.getInstance();
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
}
