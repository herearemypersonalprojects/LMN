/*
 * Created on 11 oct. 2011
 *
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.searchengine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.data.PageLayoutComponent;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchCriteria;
import com.travelsoft.cameleo.catalog.data.SearchEngine;
import com.travelsoft.cameleo.catalog.interfaces.ServicesFactory;
import com.travelsoft.cameleo.catalog.interfaces.exceptions.TechnicalException;
import com.travelsoft.cameleo.catalog.interfaces.search.PublishedSearchEngineServicesInterface;
import com.travelsoft.cameleo.catalog.taglib.constant.Constant;
import com.travelsoft.cameleo.catalog.taglib.utils.Utils;
import com.travelsoft.cameleo.cms.processor.controller.AbstractStructuredController;
import com.travelsoft.cameleo.cms.processor.controller.WebProcessEnvironment;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.serp.SearchProductUtil;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.Util;
import com.travelsoft.lastminute.data.CodeLabel;
import com.travelsoft.lastminute.data.CodeLabels;

/**
 * <p>Titre : SearchBoxEngine.</p>
 * <p>Description : .</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Travelsoft</p>
 * @author zouhair.mechbal
 */
public class SearchBoxEngine extends AbstractStructuredController<PageLayoutComponent, WebProcessEnvironment> {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(SearchBoxEngine.class);

    /**
     * Process business logic and produce the data model to get the the searchBox engine.
     * @param context the component's context
     */
    public void fillComponentContext(IComponentContext<PageLayoutComponent> context) {
        try {
            PublishedSearchEngineServicesInterface engineServices = ServicesFactory.getPublishedSearchEngineServices();
            String queryString = this.getEnvironment().getRequest().getQueryString();
            String criteriaQueryString = SearchProductUtil
                    .removePrefixedQueryString(queryString, Constants.Common.SEARCH_ENGINE_PREFIX);
            PublishedProductSearchCriteria currentSearchCriteria = Util
                        .retrieveSearchCriteria(context, Constants.Config.DEFAULT_CRITERIA_KEY);

            currentSearchCriteria = Utils.completeProductSearchCriteria(criteriaQueryString,
                     Constant.CRITERIA_SEPARATOR, Constant.CRITERION_SEPARATOR, Constant.OPTIONS_SEPARATOR,
                     currentSearchCriteria, Constants.CAMELEO_CACHE_MANAGER);

            SearchEngine searchBoxEngine = engineServices.getSearchEngine(
                    currentSearchCriteria, Constants.CAMELEO_CACHE_MANAGER);

            SearchEngineUtil searchEngineUtil = new SearchEngineUtil();
            searchEngineUtil.retrieveTopDestinationAndDepartureCities(context, searchBoxEngine);
            this.filterSearchBoxQueryParameters(context, criteriaQueryString);
            addDataInContext(context, searchBoxEngine);

        } catch (TechnicalException e) {
            LOGGER.error("Error while trying to retrieve the search engine.", e);
        }
    }

    /**
     * Adds data in the context.
     * @param context The context
     * @param searchBoxEngine The search engine object
     */
    private void addDataInContext(IComponentContext<PageLayoutComponent> context, SearchEngine searchBoxEngine) {
        context.write("queryString", this.getEnvironment().getRequest().getQueryString());
        HttpServletRequest request = this.getEnvironment().getRequest();
        context.write("contextPath", request.getScheme() + "://" + request.getServerName());
        context.write("searchBoxEngine", searchBoxEngine);
        String queryString = this.getEnvironment().getRequest().getQueryString();
        if (queryString == null || (!isRequestWithSearchCriteria())) {
            context.write("isRequestWithSearchCriteria", false);
        }

        String b2bparameter = this.getEnvironment().getRequest().getParameter("user");
        if (b2bparameter != null) {
        	context.write("b2b", true);
        }
        Util.addBrandContext(this.getEnvironment().getRequest(), context);
    }

    /**
     * Method retrieves if the request query has search engine criteria parameters.
     * @return true if the request query has search engine criteria parameters
     */
    private boolean isRequestWithSearchCriteria() {
        Map parameterMap = this.getEnvironment().getRequest().getParameterMap();
        if (parameterMap != null) {
            Iterator iterator = parameterMap.keySet().iterator();
            while (iterator.hasNext()) {
                String requestParameter = (String) iterator.next();
                if (requestParameter != null) {
                    if (requestParameter.startsWith(Constants.Common.REFINEMENT_SEARCH_ENGINE_PREFIX)
                            || requestParameter.startsWith(Constants.Common.SEARCH_ENGINE_PREFIX)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @param context
     *            The context
     * @param criteriaQueryString
     *            The search query in string format
     */
    private void filterSearchBoxQueryParameters(
            IComponentContext<PageLayoutComponent> context, String criteriaQueryString) {
        String searchBoxParameters = Util.retrieveConfigFromPageDescription(context, "searchBoxFormParameters");
        if (searchBoxParameters != null) {
            CodeLabels codeLabels = new CodeLabels();
            Map<String, String> criteriaQueryStringAsMap = splitCriteriaQueryString(criteriaQueryString);
            if (!criteriaQueryStringAsMap.isEmpty()) {
                String[] searchBoxParametersArray = searchBoxParameters.split(";");

                this.filterParameters(criteriaQueryStringAsMap, searchBoxParametersArray);

                if (!criteriaQueryStringAsMap.isEmpty()) {
                    Iterator<Entry<String, String>> iterator = criteriaQueryStringAsMap.entrySet().iterator();

                    while (iterator.hasNext()) {
                        Entry<String, String> entry = iterator.next();
                        CodeLabel codeLabel = new CodeLabel();
                        codeLabel.setCode(entry.getKey());
                        codeLabel.setLabel(entry.getValue());
                        codeLabels.addCodeLabel(codeLabel);

                    }
                }
                context.write("refinementQueryParameters", codeLabels);
            }
        }
    }

    /**
     * @param criteriaQueryStringAsMap The criteria query as map
     * @param searchBoxParametersArray The search box query parameters
     * @return
     */
    private void filterParameters(Map<String, String> criteriaQueryStringAsMap, String[] searchBoxParametersArray) {
        for (String searchBoxParameter : searchBoxParametersArray) {
            criteriaQueryStringAsMap.remove(searchBoxParameter);
        }
    }

    /**
     * Transforms the criteria query string as map.
     * @param criteriaQueryString The query string
     * @return the criteria query string as map
     */
    private Map<String, String> splitCriteriaQueryString(String criteriaQueryString) {
        Map<String, String> queryStringAsMap = new HashMap<String, String>();
        if (criteriaQueryString != null) {
            String[] criteriaParameterValueArray = criteriaQueryString.split(Constants.Common.SEPARATOR_AMP);
            if (criteriaParameterValueArray != null) {
                for (String criteriaParameterValue : criteriaParameterValueArray) {
                    if (criteriaParameterValue != null && criteriaParameterValue.split("=").length > 1) {
                        queryStringAsMap.put(
                                criteriaParameterValue.split("=")[0],
                                criteriaParameterValue.split("=")[1]);
                    }
                }
            }
        }
        return queryStringAsMap;
    }

    /**
     * Produces a data model mock.
     *
     * @param context the component's context
     * @param data the injection data
     * @throws PageNotFoundException If the page is not found
     */
    public void preview(IComponentContext<PageLayoutComponent> context, InjectionData data) throws PageNotFoundException {
    }

}
