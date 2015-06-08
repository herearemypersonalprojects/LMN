/*
 * Created on 20 sept. 2011
 *
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.serp;

import com.travelsoft.cameleo.catalog.data.PageLayoutComponent;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchCriteria;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchResponse;
import com.travelsoft.cameleo.catalog.interfaces.ServicesFactory;
import com.travelsoft.cameleo.catalog.interfaces.exceptions.TechnicalException;
import com.travelsoft.cameleo.catalog.interfaces.search.PublishedProductSearchServicesInterface;
import com.travelsoft.cameleo.cms.processor.controller.AbstractStructuredController;
import com.travelsoft.cameleo.cms.processor.controller.WebProcessEnvironment;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.util.Constants;

/**
 * <p>Titre : AbstractSearchProducts.</p>
 * <p>Description : .</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Travelsoft</p>
 * @author zouhair.mechbal
 */
public abstract class AbstractSearchProducts
        extends AbstractStructuredController<PageLayoutComponent, WebProcessEnvironment> {

     /**
     * Process the published product search.
     * @param searchCriteria The search criteria
     * @return The PublishedProductSearchResponse
     * @throws TechnicalException if error occurs
     */
    protected PublishedProductSearchResponse getPublishedProductSearchResponse(
            PublishedProductSearchCriteria searchCriteria) throws TechnicalException {
        PublishedProductSearchServicesInterface publishedProductSearchServices =
            ServicesFactory.getPublishedProductSearchServices();
        return publishedProductSearchServices.searchProducts(searchCriteria, Constants.CAMELEO_CACHE_MANAGER);
    }

    /**
     * Process the published product search.
     * @param searchCriteria The final criteria
     * @param excludedProductCodes The product codes to exclude from the search
     * @return The PublishedProductSearchResponse The <code>PublishedProductSearchResponse</code> instance object
     * @throws TechnicalException if error occurs
     */
    protected PublishedProductSearchResponse getPublishedProductSearchResponse(
            PublishedProductSearchCriteria searchCriteria,
            String[] excludedProductCodes) throws TechnicalException {
        if (searchCriteria.getCode() != null && searchCriteria.getCode().length > 0
                && excludedProductCodes != null && excludedProductCodes.length > 0) {
            PublishedProductSearchResponse searchResponse = new PublishedProductSearchResponse();
            searchResponse.setResultsNumber(0);
            return searchResponse;
        }

        if (searchCriteria.getCode() == null || searchCriteria.getCode().length == 0) {
            searchCriteria.setCode(excludedProductCodes);
        }

        int firstResultIndex = searchCriteria.getFirstResultIndex();
        int lastResultIndex = searchCriteria.getLastResultIndex();
        firstResultIndex = firstResultIndex - excludedProductCodes.length;
        if (firstResultIndex < 0) {
            firstResultIndex = 0;
        }
        lastResultIndex = lastResultIndex - excludedProductCodes.length;
        if (lastResultIndex < 0) {
            lastResultIndex = 0;
        }
        searchCriteria.setFirstResultIndex(firstResultIndex);
        searchCriteria.setLastResultIndex(lastResultIndex);
        return getPublishedProductSearchResponse(searchCriteria);
    }

    /**
     *  Produces a data model mock.
     *
     * @param context the component's context
     * @param injectionData the injection data
     * @throws PageNotFoundException if page is not found
     */
    public void preview(IComponentContext<PageLayoutComponent> context,
            InjectionData injectionData) throws PageNotFoundException {
    }
}
