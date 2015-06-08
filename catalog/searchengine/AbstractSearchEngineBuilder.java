/*
 * Created on 19 oct. 2011
 *
 * Copyright Travelsoft 2011</p>
 */
package com.travelsoft.lastminute.catalog.searchengine;

import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.data.ContentLayoutComponent;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchCriteria;
import com.travelsoft.cameleo.catalog.data.SearchEngine;
import com.travelsoft.cameleo.catalog.interfaces.ServicesFactory;
import com.travelsoft.cameleo.catalog.interfaces.exceptions.TechnicalException;
import com.travelsoft.cameleo.catalog.interfaces.search.PublishedSearchEngineServicesInterface;
import com.travelsoft.cameleo.cms.processor.controller.AbstractStructuredController;
import com.travelsoft.cameleo.cms.processor.controller.WebProcessEnvironment;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.Util;


/**
 * <p>Titre : AbstractSearchEngineBuilder.</p>
 * Description :
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Company : Travelsoft</p>
 * @author MengMeng
 * @version
 *
 */
public abstract class AbstractSearchEngineBuilder extends
        AbstractStructuredController<ContentLayoutComponent, WebProcessEnvironment> {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(AbstractSearchEngineBuilder.class);

    /**
     * Build search engine by given criteria configuration name.
     * @param criteriaConfigName criteria configuration name
     * @param context the component context
     * @return searchEngine search engine
     */
    protected SearchEngine buildSearchEngine(String criteriaConfigName,
            IComponentContext<ContentLayoutComponent> context) {
        SearchEngine searchEngine = null;
        try {
            PublishedSearchEngineServicesInterface engineServices = ServicesFactory.getPublishedSearchEngineServices();
            PublishedProductSearchCriteria searchEngineCriteria = Util.retrieveSearchCriteria(context,
                    criteriaConfigName);

            PublishedProductSearchCriteria[] multiConstraints = new PublishedProductSearchCriteria[1];
            multiConstraints[0] = searchEngineCriteria;
            searchEngine = engineServices.getSearchEngine(multiConstraints, Constants.CAMELEO_CACHE_MANAGER);
        } catch (TechnicalException e) {
            LOGGER.error("Error while trying to retrieve the search engine.", e);
        }
        return searchEngine;
    }
}
