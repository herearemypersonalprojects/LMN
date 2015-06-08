/*
 * Created on 26 sept. 2012
 *
 * Copyright Travelsoft, 2012.
 */
package com.travelsoft.lastminute.catalog.util;

import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.data.PageLayoutComponent;
import com.travelsoft.cameleo.cms.processor.controller.AbstractStructuredController;
import com.travelsoft.cameleo.cms.processor.controller.WebProcessEnvironment;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.serp.LastMinuteSearchPageController;
import com.travelsoft.lastminute.data.LastMinuteDeparturesCities;

/**
 * <p>Title: LastMinuteSearchesLinksPageController.java.</p>
 * <p>Description: .</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Travelsoft</p>
 *
 * @author thiago.bohme
 */
public class LastMinuteSearchesLinksPageController
extends AbstractStructuredController<PageLayoutComponent, WebProcessEnvironment> {

    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(LastMinuteSearchPageController.class);

    /**
     * Process business logic and produce the data model.
     *
     * @param context the component's context
     */
    public void fillComponentContext(IComponentContext<PageLayoutComponent> context) {
        LastMinuteDeparturesCities depCities = Util.searchLastMinuteDeparturesCities();
        if (depCities != null) {
            context.write(Constants.Context.LAST_MINUTE_DEPARTURES_CITIES, depCities);
        }
    }

    /**
     * Produces a data model mock.
     *
     * @param context
     *            the component's context
     * @param injectionData
     *            the injection data
     * @throws PageNotFoundException
     *             If the page is not found
     */
    public void preview(IComponentContext<PageLayoutComponent> context, InjectionData injectionData)
        throws PageNotFoundException {

    }
}