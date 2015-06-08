/*
 * Created on 22 sept. 2011
 *
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.searchengine;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.data.ContentLayoutComponent;
import com.travelsoft.cameleo.catalog.data.Criterion;
import com.travelsoft.cameleo.catalog.data.CriterionValue;
import com.travelsoft.cameleo.catalog.data.EngineCriterionValue;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchCriteria;
import com.travelsoft.cameleo.catalog.data.SearchEngine;
import com.travelsoft.cameleo.catalog.interfaces.ServicesFactory;
import com.travelsoft.cameleo.catalog.interfaces.exceptions.TechnicalException;
import com.travelsoft.cameleo.catalog.interfaces.search.PublishedSearchEngineServicesInterface;
import com.travelsoft.cameleo.cms.processor.controller.AbstractStructuredController;
import com.travelsoft.cameleo.cms.processor.controller.WebProcessEnvironment;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.seo.SeoTool;
import com.travelsoft.lastminute.catalog.serp.SearchProductUtil;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.Util;
import com.travelsoft.lastminute.data.SeoData;
import com.travelsoft.lastminute.data.TrackingData;

/**
 * <p>
 * Titre : RefinementSearchEngineBuilder.
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
public class RefinementSearchEngineBuilder
        extends AbstractStructuredController<ContentLayoutComponent, WebProcessEnvironment> {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(RefinementSearchEngineBuilder.class);

    /**
     * Processes business logic and produces the data model to use in the search
     * engine view.
     *
     * @param context the component's context
     */
    public void fillComponentContext(IComponentContext<ContentLayoutComponent> context) {

        try {
            PublishedSearchEngineServicesInterface engineServices = ServicesFactory
                    .getPublishedSearchEngineServices();

            PublishedProductSearchCriteria searchEngineCriteria = (PublishedProductSearchCriteria) context
                    .lookup(Constants.Context.CURRENT_CRITERIA_CONSTRAINTS);

            PublishedProductSearchCriteria refinementSearchEngineCriteria =
                Util.retrieveSearchCriteria(context, "refinementSearchEngineCriteria");
            SearchProductUtil.overrideCriteria(searchEngineCriteria, refinementSearchEngineCriteria);

            PublishedProductSearchCriteria[] searchEngineConstraints =
                new PublishedProductSearchCriteria[] {refinementSearchEngineCriteria};
            SearchEngine searchEngine = engineServices.getSearchEngine(searchEngineConstraints, Constants.CAMELEO_CACHE_MANAGER);

            if (isSearchQueryWithDestination(searchEngineCriteria)) {
                context.write("displayDestinationCities", true);
            }

            String queryString = (String) context.lookup("queryString");
            if (queryString != null && queryString.contains("aff_")) {
                 PublishedProductSearchCriteria finalCriteria = (PublishedProductSearchCriteria) context
                        .lookup(Constants.Context.FINAL_CRITERIA_CONSTRAINTS);
                PublishedProductSearchCriteria[] refinementSearchEngineConstraints =
                    new PublishedProductSearchCriteria[] {finalCriteria};
                SearchEngine refinementSearchEngine =
                    engineServices.getSearchEngine(refinementSearchEngineConstraints, Constants.CAMELEO_CACHE_MANAGER);

                SearchEngine searchEngineToUpdate = (SearchEngine) searchEngine.clone();
                this.mergeRefinementCriterion(refinementSearchEngine, searchEngineToUpdate);
                context.write("refinementSearchEngine", searchEngineToUpdate);
                fillTrackingData(context, searchEngine);
                setSeoData(searchEngineToUpdate, context);
            } else {
                context.write("refinementSearchEngine", searchEngine);
                fillTrackingData(context, searchEngine);
                setSeoData(searchEngine, context);
            }



        } catch (TechnicalException e) {
            LOGGER.error("Error while trying to retrieve the search engine.", e);
        }
    }

    /**
     *
     * Method to setup value to SeoData.
     *
     * @param refinementSearchEngine sd
     * @param context sd
     */
    private void setSeoData(SearchEngine refinementSearchEngine,
        IComponentContext<ContentLayoutComponent> context) {
        String codeParam = SeoTool.getUniqueStayType(this.getEnvironment().getRequest());
        SeoData seoData = (SeoData) context.lookup("seoData");
        for (int i = 0; i < refinementSearchEngine.getCriterion().length; i++) {
            if (refinementSearchEngine.getCriterion(i).getCode().equalsIgnoreCase("tvoyages")) {
                for (int j = 0; j < refinementSearchEngine.getCriterion(i).getValue().length; j++) {
                    if (refinementSearchEngine.getCriterion(i).
                                    getValue(j).getCode().equalsIgnoreCase(codeParam)) {
                        seoData.setStayType(refinementSearchEngine.getCriterion(i).
                            getValue(j).getLabel());
                        break;
                    }
                }
            }
        }
    }

    /**
     * Completes the tracking data (if available in the current context).
     * @param context the current component context
     * @param searchEngine the search engine to set
     */
    private void fillTrackingData(IComponentContext<ContentLayoutComponent> context, SearchEngine searchEngine) {
        Object tData = context.lookup(Constants.Context.TRACKING_DATA);
        if (tData instanceof TrackingData) {
            TrackingData trackingData = (TrackingData) tData;
            trackingData.getSerp().setRefinementSearchEngine(searchEngine);
        } else {
            LOGGER.warn("I was expecting a TrackingData instance in the context, but found " + tData
                + ". TrackingData will not be collected at this moment.");
        }
    }

    /**
     * Checks if the search query is with destination cities.
     * @param searchEngineCriteria The search engine criteria
     * @return true if the search query is with destination cities
     */
    private boolean isSearchQueryWithDestination(PublishedProductSearchCriteria searchEngineCriteria) {
        Criterion[] customCriteria = searchEngineCriteria.getCustomCriteria();
        if (customCriteria != null) {
            for (Criterion criterion : customCriteria) {
                if (criterion != null && "de".equals(criterion.getCode())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param refinementSearchEngine The refinement search engine
     * @param searchEngineToUpdate The search engine to update
     */
    private void mergeRefinementCriterion(SearchEngine refinementSearchEngine,
                                          SearchEngine searchEngineToUpdate) {
        Map<String, CriterionValue[]> refinementSearchEngineAsMap = this
                .getCriterionAsMap(refinementSearchEngine);
        Map<String, CriterionValue[]> searchEngineToUpdateAsMap = this
                .getCriterionAsMap(searchEngineToUpdate);

        this.mergeCurrentCriterion(refinementSearchEngineAsMap, searchEngineToUpdateAsMap);
        Iterator<Entry<String, CriterionValue[]>> iterator = searchEngineToUpdateAsMap.entrySet().iterator();
        searchEngineToUpdate.removeAllCriterion();
        while (iterator.hasNext()) {
            Entry<String, CriterionValue[]> entry = iterator.next();
            Criterion criterion = new Criterion();
            criterion.setCode(entry.getKey());
            // This sucks because of the list-to-array-to-list conversion.
            criterion.setValue(entry.getValue());
            searchEngineToUpdate.addCriterion(criterion);
        }
    }

    /**
     * @param refinementEngineCriteriaAsMap The refinement search engine as map
     * @param searchEngineToUpdateAsMap The search engine as map to update
     */
    private void mergeCurrentCriterion(
            Map<String, CriterionValue[]> refinementEngineCriteriaAsMap,
            Map<String, CriterionValue[]> searchEngineToUpdateAsMap) {

        Iterator<Entry<String, CriterionValue[]>> iterator = searchEngineToUpdateAsMap.entrySet().iterator();
        while(iterator.hasNext()) {
            Entry<String, CriterionValue[]> entry = iterator.next();
            String engineKey = entry.getKey();
            CriterionValue[] valueArray = entry.getValue();
            if (!refinementEngineCriteriaAsMap.containsKey(engineKey)) {
                for (CriterionValue criterionValue : valueArray) {
                    if (criterionValue instanceof EngineCriterionValue) {
                        ((EngineCriterionValue) criterionValue).setEntityNb(0);
                    }
                }
            } else {
                for (CriterionValue engineCriterionValueToUpdate : valueArray) {
                    if (!checkIfCriterionValueIsPresent(
                            engineCriterionValueToUpdate, refinementEngineCriteriaAsMap.get(engineKey))) {
                        if (engineCriterionValueToUpdate instanceof EngineCriterionValue) {
                            ((EngineCriterionValue) engineCriterionValueToUpdate).setEntityNb(0);
                        }
                    } else {
                        CriterionValue[] refinementEngineCriteriaValues = refinementEngineCriteriaAsMap.get(engineKey);
                        for (CriterionValue refinementEngineCriteriaValue : refinementEngineCriteriaValues) {
                            if (refinementEngineCriteriaValue.getCode().equals(
                                    engineCriterionValueToUpdate.getCode())) {
                                if (engineCriterionValueToUpdate instanceof EngineCriterionValue
                                        && refinementEngineCriteriaValue instanceof EngineCriterionValue) {
                                    ((EngineCriterionValue) engineCriterionValueToUpdate)
                                            .setEntityNb(((EngineCriterionValue) refinementEngineCriteriaValue)
                                                    .getEntityNb());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @param criterionValue The criterion value object
     * @param criterionValues The criterion value array object
     * @return true if the criterion value is in the criterion value array
     */
    private boolean checkIfCriterionValueIsPresent(CriterionValue criterionValue,
                                                   CriterionValue[] criterionValues) {
        for (CriterionValue refinementCriterionValue : criterionValues) {
            if (refinementCriterionValue.getCode().equals(criterionValue.getCode())) {
                return true;
            }
        }
        return false;
    }


    /**
     * @param searchEngine  The <code>SearchEngine</code> instance object
     * @return The search engine as map
     */
    private Map<String, CriterionValue[]> getCriterionAsMap(
            SearchEngine searchEngine) {
        Map<String, CriterionValue[]> engineCriteriaAsMap = new LinkedHashMap<String, CriterionValue[]>();

        Criterion[] criterionList = searchEngine.getCriterion();
        for (Criterion criterion : criterionList) {
            engineCriteriaAsMap.put(criterion.getCode(), criterion.getValue());
        }
        return engineCriteriaAsMap;
    }

    /**
     * Produces a data model mock.
     *
     * @param context
     *            the component's context
     * @param injectionData
     *            the injection data
     * @throws PageNotFoundException
     *             if page is not found
     */
    public void preview(IComponentContext<ContentLayoutComponent> context,
            InjectionData injectionData) throws PageNotFoundException {

    }

}
