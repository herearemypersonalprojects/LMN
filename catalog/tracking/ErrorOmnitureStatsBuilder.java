/*
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.tracking;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.travelsoft.cameleo.catalog.data.ContentLayoutComponent;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.TrackingUtil;

/**
 * <p>Titre : ErrorOmnitureStatsBuilder.</p>
 * <p>Description : Fill error page omniture variables.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Travelsoft</p>
 * @author dusan.spaic
 */
public class ErrorOmnitureStatsBuilder extends OmnitureStatsBuilder {


    /**
     * Used in product details page.
     * Fill Omniture tracking object with all needed variable informations.
     *
     * @param context the component's context
     */
    @Override
    public void fillComponentContext(IComponentContext<ContentLayoutComponent> context)  {

        constructOmnitureBaseStats();
        HttpServletRequest rq = this.getEnvironment().getRequest();

        omnitureStats.setDepDateWithFlex(TrackingUtil.getDepartureDateWithFlex(rq));
        omnitureStats.setLeadTime(TrackingUtil.getLeadTime(rq));
        omnitureStats.setStars(getStarNmb(rq));
        omnitureStats.setMaxPrice(TrackingUtil.getPriceRange(rq));

        Integer errorCode = (Integer) rq.getAttribute("javax.servlet.error.status_code");
        if (errorCode != null) {
            omnitureStats.setErrorCode(errorCode);
        }

        // For next variables i need searchEngine. They will not be added.
        // depCityLabel, formulaLabel, regionLabel, tripTypeLabel, destlabel

        context.write(Constants.OmnitureConstants.OMNITURE_STATS, omnitureStats);
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
