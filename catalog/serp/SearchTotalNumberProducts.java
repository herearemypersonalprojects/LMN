/*
 * Created on 7 aout. 2012
 *
 * Copyright Travelsoft, 20112
 */
package com.travelsoft.lastminute.catalog.serp;

import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.data.PageLayoutComponent;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchCriteria;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchResponse;
import com.travelsoft.cameleo.catalog.interfaces.exceptions.TechnicalException;
import com.travelsoft.cameleo.cms.processor.controller.AbstractStructuredController;
import com.travelsoft.cameleo.cms.processor.controller.WebProcessEnvironment;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.Util;

/**
 * <p>Titre : SearchTotalNumberProducts.</p>
 * <p>Description : .</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Travelsoft</p>
 * @author zouhair.mechbal
 */
public class SearchTotalNumberProducts extends AbstractSearchProducts {

	/** Logger. */
    private static final Logger LOGGER = Logger.getLogger(SearchTotalNumberProducts.class);

    /**
     * Processes business logic and produces the data model to use in the search engine view.
     *
     * @param context the component's context
     */
    public void fillComponentContext(IComponentContext<PageLayoutComponent> context) {
		try {
			PublishedProductSearchCriteria searchCriteria = Util.retrieveSearchCriteria(context,
							Constants.Config.DEFAULT_CRITERIA_KEY);
			PublishedProductSearchResponse publishedProductSearchResponse = this.getPublishedProductSearchResponse(searchCriteria);
			if (publishedProductSearchResponse != null) {
				context.write(Constants.Context.TOTAL_RESULTS_NUMBER, publishedProductSearchResponse.getResultsNumber());
			}
		} catch (TechnicalException e) {
			LOGGER.error("Error when the total product number is retrieved.", e);
		}
    }
}