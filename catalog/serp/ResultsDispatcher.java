/*
 * Created on 2 sept. 2011
 *
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.serp;

import com.travelsoft.cameleo.catalog.data.ContentLayoutComponent;
import com.travelsoft.cameleo.cms.processor.controller.AbstractController;
import com.travelsoft.cameleo.cms.processor.controller.WebProcessEnvironment;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.util.Constants;

/**
 * <p>Titre : ResultsDispatcher.</p>
 * Description : The dispatcher by results number
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Company : Travelsoft</p>
 * @author MengMeng
 * @version
 *
 */
public class ResultsDispatcher extends AbstractController<ContentLayoutComponent, WebProcessEnvironment> {

    /**
     * Process business logic and produce the data model to dispatcher search results.
     * @param context the component's context
     * @throws PageNotFoundException If the page is not found
     */
    public void process(IComponentContext<ContentLayoutComponent> context) throws PageNotFoundException {

        Boolean displayAlternatifProducts = (Boolean) context.lookup("displayAlternatifProducts");

        if (displayAlternatifProducts != null && displayAlternatifProducts) {
            context.appendContainerContext(Constants.Containers.SEARCH_WITHOUTRESULTS_CONTAINER);
        } else {
        	
            context.appendContainerContext(Constants.Containers.SEARCH_WITHRESULTS_CONTAINER);
        }
        
        this.processContainers(context);
    }

    /**
     * Produces a data model mock.
     *
     * @param context
     *            the component's context
     * @param injectionData
     *            the injection data
     */
    public void preview(IComponentContext<ContentLayoutComponent> context, InjectionData injectionData) {
    }
}
