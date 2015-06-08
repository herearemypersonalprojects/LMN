/*
 * Created on 2 sept. 2011
 *
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.searchengine;

import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.data.ContentLayoutComponent;
import com.travelsoft.cameleo.catalog.data.SearchEngine;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.data.TrackingData;

/**
 * <p>Titre : SearchEngineBuilder.</p>
 * <p>Description : .</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Travelsoft</p>
 * @author zouhair.mechbal
 */
public class SearchEngineBuilder extends AbstractSearchEngineBuilder {


    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(SearchEngineBuilder.class);


    /**
     * Processes business logic and produces the data model to use in the search engine view.
     *
     * @param context the component's context
     */
    public void fillComponentContext(IComponentContext<ContentLayoutComponent> context) {
        SearchEngine searchEngine = buildSearchEngine("searchEngineCriteria", context);
        SearchEngineUtil searchEngineUtil = new SearchEngineUtil();
        searchEngineUtil.retrieveTopDestinationAndDepartureCities(context, searchEngine);
        context.write("searchEngine", searchEngine);
        String currentPage = (String) context.lookup("currentPage");
        if (currentPage == null) {
        	context.write("queryString", "s_aj=2");
        }
        Object tData = context.lookup(Constants.Context.TRACKING_DATA);
        if (tData instanceof TrackingData) {
            TrackingData trackingData = (TrackingData) tData;
            trackingData.getSerp().setSearchEngine(searchEngine);
        } else {
            LOGGER.warn("I was expecting a TrackingData instance in the context, but found " + tData
                + ". TrackingData will not be collected at this moment.");
        }
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
}
