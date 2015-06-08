/*
 * Created on 21 sept. 2011
 *
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.serp;

import com.travelsoft.cameleo.catalog.data.PublishedProductSearchCriteria;
import com.travelsoft.lastminute.catalog.util.Constants;

/**
 * <p>
 * Titre : SearchProductUtil.
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
public final class SearchProductUtil {

    /** Constructor for <code>SearchProductUtil</code>. */
    private SearchProductUtil() {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes the prefix from each request parameters.
     *
     * @param queryString
     *            The query string parameters
     * @param prefix
     *            The prefix in the query parameters
     * @return The search product criteria
     */
    public static String removePrefixedQueryString(String queryString, String prefix) {
        StringBuffer criteriaQueryString = new StringBuffer();
        if (queryString != null) {
            String[] chains = queryString.split(Constants.Common.SEPARATOR_AMP);
            for (String param : chains) {
                if (param != null && param.startsWith(prefix)) {
                    criteriaQueryString = criteriaQueryString
                      .append(Constants.Common.SEPARATOR_AMP).append(param.substring(prefix.length()));
                }
            }
        }
        return criteriaQueryString.toString();

    }

    /**
     * Method overrides the the search criteria.
     *
     * @param searchCriteriaToOverride The search criteria to be override
     * @param searchCriteria The final search criteria
     */
    public static void overrideCriteria(
            PublishedProductSearchCriteria searchCriteriaToOverride,
            PublishedProductSearchCriteria searchCriteria) {

        PublishedProductSearchCriteria clonedSearchCriteriaToOverride =
            (PublishedProductSearchCriteria) searchCriteriaToOverride.clone();


        if (clonedSearchCriteriaToOverride.getMaxDepartureDate() != null) {
            searchCriteria.setMaxDepartureDate(clonedSearchCriteriaToOverride.getMaxDepartureDate());
        }

        if (clonedSearchCriteriaToOverride.getCustomCriteria() != null) {
            searchCriteria.setCustomCriteria(clonedSearchCriteriaToOverride.getCustomCriteria());
        }

        if (clonedSearchCriteriaToOverride.getMinDepartureDate() != null) {
            searchCriteria.setMinDepartureDate(clonedSearchCriteriaToOverride.getMinDepartureDate());
        }

        if (clonedSearchCriteriaToOverride.getDepartureCityCode() != null) {
            searchCriteria.setDepartureCityCode(clonedSearchCriteriaToOverride.getDepartureCityCode());
        }


        if (clonedSearchCriteriaToOverride.hasMaxDurationInNights()) {
            searchCriteria.setMaxDurationInNights(clonedSearchCriteriaToOverride.getMaxDurationInNights());
        }

        if (clonedSearchCriteriaToOverride.hasMinDurationInNights()) {
            searchCriteria.setMinDurationInNights(clonedSearchCriteriaToOverride.getMinDurationInNights());
        }

        searchCriteria.setSortBy(null);
        searchCriteria.setOpinionCriteria(null);
        searchCriteria.setHotelCriteria(null);
    }
}
