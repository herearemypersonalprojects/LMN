/*
 * Created on 20 sept. 2012
 *
 * Copyright Travelsoft, 2012.
 */
package com.travelsoft.lastminute.catalog.serp;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.data.PageLayoutComponent;
import com.travelsoft.cameleo.cms.processor.controller.AbstractStructuredController;
import com.travelsoft.cameleo.cms.processor.controller.WebProcessEnvironment;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.IContainerContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.data.LastMinuteDeparture;

/**
 * <p>Title: LastMinuteSearchPageController.java.</p>
 * <p>Description: .</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Travelsoft</p>
 *
 * @author thiago.bohme
 */
public class LastMinuteSearchPageController
extends AbstractStructuredController<PageLayoutComponent, WebProcessEnvironment> {

    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(LastMinuteSearchPageController.class);

    /**
     * Process business logic and produce the data model.
     *
     * @param context the component's context
     */
    public void fillComponentContext(IComponentContext<PageLayoutComponent> context) {
        String departureCity = getEnvironment().getRequest().getParameter(Constants.Common.DEPARTURE_CITY);
        context.write(Constants.Common.DEPARTURE_CITY, departureCity);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received request for a last minute search from: '" + departureCity + "'.");
        }

        if (StringUtils.isBlank(departureCity)) {
            LOGGER.warn("Last minute search couldn't be done from a blank departure city request parameter.");

        } else {
            List<LastMinuteDeparture> departures =
                    SearchLastMinuteDepartures.searchLastMinutesDepartures(context, departureCity);
            int idxProduct = 1;
            if (departures != null && departures.size() > 0) {
                for (LastMinuteDeparture departure : departures) {
                    IContainerContext<?> containerContext =
                            context.appendContainerContext(Constants.Containers.LMN_SEARCH_RESULT_ITEM_CONTAINER);
                    containerContext.write(Constants.Context.LAST_MINUTE_DEPARTURE, departure);
                    containerContext.write(Constants.Context.ITEM_INDEX, idxProduct++);
                }
            }
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