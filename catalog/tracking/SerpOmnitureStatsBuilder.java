/*
 * Created on 18 oct. 2011
 *
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.tracking;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.travelsoft.cameleo.catalog.data.City;
import com.travelsoft.cameleo.catalog.data.ContentLayoutComponent;
import com.travelsoft.cameleo.catalog.data.MealPlan;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchCriteria;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchResponse;
import com.travelsoft.cameleo.catalog.data.SearchEngine;
import com.travelsoft.cameleo.catalog.interfaces.Constants.SortConstants;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.TrackingUtil;
import com.travelsoft.lastminute.catalog.util.Util;
import com.travelsoft.lastminute.data.SerpTrackingData;
import com.travelsoft.lastminute.data.TrackingData;

/**
 * <p>Titre : TrackingBuilder.</p>
 * <p>Description : Fill all tracking variables.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Travelsoft</p>
 * @author dusan.spaic
 */
public class SerpOmnitureStatsBuilder extends OmnitureStatsBuilder {

    /**
     * USED in search and compare pages
     * Fill Omniture tracking object with all needed variable informations.
     *
     * @param context the component's context
     */
    @Override
    public void fillComponentContext(IComponentContext<ContentLayoutComponent> context)  {

        constructOmnitureBaseStats();

        PublishedProductSearchResponse publishedProductSearchResponse = (PublishedProductSearchResponse) context
                        .lookup(Constants.Context.PRODUCT_SEARCH_RESPONSE);
        omnitureStats.setResultsNmb(Integer.toString(publishedProductSearchResponse.getResultsNumber()));

        HttpServletRequest rq = this.getEnvironment().getRequest();

        PublishedProductSearchCriteria searchEngineCriteria = (PublishedProductSearchCriteria) context
                        .lookup(Constants.Context.CURRENT_CRITERIA_CONSTRAINTS);
        omnitureStats.setSortLabel(getSortyByTrackingLabel(searchEngineCriteria));
        omnitureStats.setDepDateWithFlex(TrackingUtil.getDepartureDateWithFlex(rq));
        omnitureStats.setLeadTime(TrackingUtil.getLeadTime(rq));
        omnitureStats.setStars(getStarNmb(rq));
        omnitureStats.setMaxPrice(TrackingUtil.getPriceRange(rq));

        Object tData = context.lookup(Constants.Context.TRACKING_DATA);
        if (tData instanceof TrackingData) {
            TrackingData trackingData = (TrackingData) tData;
            SerpTrackingData std = (SerpTrackingData) trackingData.getSerp();
            int currPageNmb = std.getCurrentPageNmb();
            omnitureStats.setCurrentPageNmb(currPageNmb);

            omnitureStats.setDepCityLabel(getDepCityLabel(rq, trackingData));

            List<String> destLabelsList = TrackingUtil.getDestinationLabel(rq, trackingData,
                Constants.Common.DESTINATION_QUERY_PARAMETER, true, true);
            if (destLabelsList != null) {
                omnitureStats.setDestLabel(Util.removeAccent(StringUtils.join(destLabelsList, " :: ")));
            }

            omnitureStats.setFormulaLabel(getFormulaLabel(rq, trackingData));
            omnitureStats.setRegionLabel(TrackingUtil.getRegionLabel(rq, trackingData));

            List<String> tripTypeLabelList = TrackingUtil.getTripTypeLabel(rq, trackingData);
            if (tripTypeLabelList != null) {
                omnitureStats.setTripTypeLabel(Util.removeAccent(StringUtils.join(tripTypeLabelList, "|")));
            }
        }

        context.write(Constants.OmnitureConstants.OMNITURE_STATS, omnitureStats);
    }


    /**
     * Produces a data model mock.
     *
     * @param context the component's context
     * @param injectionData the injection data
     * @throws PageNotFoundException if page is not found
     */
    public void preview(IComponentContext<ContentLayoutComponent> context,
            InjectionData injectionData) throws PageNotFoundException {
    }


    /**
     * Get sort constant used in omniture.
     * @param searchEngineCriteria the searchEngineCriteria
     * @return {@link String}
     */
    private String getSortyByTrackingLabel(PublishedProductSearchCriteria searchEngineCriteria) {
        String category = Constants.CriterionConstants.CRITERION_CUSTOMER_FIELD
                        + Constants.CriterionConstants.CRITERION_CATEGORY_CODE;
        if (searchEngineCriteria.getSortBy() == null
            || SortConstants.SORT_BASE_PRICE_COEFF.equals(searchEngineCriteria.getSortBy())) {
            return Constants.OmnitureConstants.SORT_BASE_PRICE_COEFF;
        } else if (SortConstants.SORT_BASE_PRICE.equals(searchEngineCriteria.getSortBy())) {
            return Constants.OmnitureConstants.SORT_BASE_PRICE;
        } else if (SortConstants.SORT_BASE_PRICE_DESC.equals(searchEngineCriteria.getSortBy())) {
            return Constants.OmnitureConstants.SORT_BASE_PRICE_DESC;
        } else if (category.equals(searchEngineCriteria.getSortBy())) {
            return Constants.OmnitureConstants.SORT_STARS;
        }
        return null;
    }


    /**
     * Get hotel stars used in search. Format 1 - 2 - 5
     * @param rq the request
     * @return {@link String}
     */
    private String getStarNmb(HttpServletRequest rq) {

        String[] stars = TrackingUtil.getStarsNmbList(rq);
        return StringUtils.join(stars, " - ");
    }


    /**
     * Get label of selected depCity in search.
     * @param rq the request
     * @param trackingData the trackingData
     * @return {@link String}
     */
    private String getDepCityLabel(HttpServletRequest rq, TrackingData trackingData) {

        String destCity = (String) rq.getParameter(Constants.CriterionConstants.DESTINATION_CITY);
        if (destCity == null) {
            return null;
        }

        SerpTrackingData std = (SerpTrackingData) trackingData.getSerp();
        SearchEngine se = (SearchEngine) std.getSearchEngine();

        City[] cities = se.getDepartureCity();
        for (City city : cities) {
            if (city.getCode().equals(destCity)) {
                return Util.removeAccent(city.getLabel());
            }
        }

        return null;
    }


    /**
     * Get formula(meal) label that corresponds to the selected search formula.
     * @param rq the request
     * @param trackingData the trackingData
     * @return {@link String}
     */
    private String getFormulaLabel(HttpServletRequest rq, TrackingData trackingData) {
        String rqParam = Constants.Common.REFINEMENT_SEARCH_ENGINE_PREFIX
                         + Constants.CriterionConstants.MEAL_PLAN_CODE;
        String rqMealParam = (String) rq.getParameter(rqParam);
        if (rqMealParam == null) {
            return null;
        }

        String[] mealParams = StringUtils.split(rqMealParam, Constants.Common.SEPARATOR_COMMA);
        SerpTrackingData std = (SerpTrackingData) trackingData.getSerp();
        SearchEngine seRefinement = (SearchEngine) std.getRefinementSearchEngine();

        MealPlan[] mealPlans = seRefinement.getMealPlan();
        List<String> mealPlansLabelList = new ArrayList<String>();

        for (String mealParam : mealParams) {
            for (MealPlan oneMealPlan : mealPlans) {
                if (oneMealPlan.getCode().equals(mealParam)) {
                    mealPlansLabelList.add(oneMealPlan.getLabel());
                    break;
                }
            }
        }

        if (mealPlansLabelList.isEmpty()) {
            return null;
        }
        return Util.removeAccent(StringUtils.join(mealPlansLabelList, " :: "));
    }

}
