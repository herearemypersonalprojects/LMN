/*
 * Created on 18 oct. 2011
 *
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.tracking;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.data.ContentLayoutComponent;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.messages.MessageRetriever;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.TrackingUtil;
import com.travelsoft.lastminute.catalog.util.Util;
import com.travelsoft.lastminute.data.MediaTagTrackingStats;
import com.travelsoft.lastminute.data.TrackingData;

/**
 * Media tag object builder.
 */
public class SerpMediaTagStatsBuilder extends MediaTagStatsBuilder {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(SerpMediaTagStatsBuilder.class);

    /**
     * Process business logic and produce the data model.
     *
     * @param context the component's context
     */
    @Override
    public void fillComponentContext(IComponentContext<ContentLayoutComponent> context) {

        MediaTagTrackingStats mediaTagStats = (MediaTagTrackingStats) context.lookup("mediaTagStats");
        if (mediaTagStats != null && mediaTagStats.getRandom13() != null) {
            // media tag was already build for this page (this is second time that this builder
            // was called). No need to build the object again.
            return;
        }

        fillMediaTagBaseStats(mediaTagStats);
        HttpServletRequest rq = this.getEnvironment().getRequest();

        Object tData = context.lookup(Constants.Context.TRACKING_DATA);
        if (tData instanceof TrackingData) {
            TrackingData trackingData = (TrackingData) tData;

            // Get destination country. Only if 1 country destination was selected.
            // Country criteria code is replaced by ISO code from countryCodes.properties
            List<String> destCountryCodes = TrackingUtil.getDestinationLabel(rq, trackingData,
                Constants.Common.DESTINATION_QUERY_PARAMETER, true, false);
            if (destCountryCodes != null && destCountryCodes.size() == 1) {

                String isoCode = countryCodeRetriever(destCountryCodes.get(0));
                if (isoCode != null) {
                    mediaTagStats.setDestCountryCode(isoCode.toLowerCase());
                }
            }

            // Get dest city without accents in lower case. Only if 1 city dest was selected.
            List<String> destCitiesList = TrackingUtil.getDestinationLabel(rq, trackingData,
                Constants.Common.DESTINATION_QUERY_PARAMETER, false, true);
            if (destCitiesList == null || destCitiesList.isEmpty()) {
                // try to get city from the region search (aff_c.de)
                destCitiesList = TrackingUtil.getDestinationLabel(rq, trackingData,
                    Constants.Common.REFINEMENT_DESTINATION_CITY_PARAMETER, false, true);
            }
            if (destCitiesList != null) {

                if (destCitiesList.size() == 1) {
                    String cityLabel = destCitiesList.get(0).trim().replaceAll(" ",
                        Constants.Common.SEPARATOR_UNDERSCORE);
                    try {
                        cityLabel = URLEncoder.encode(cityLabel, Constants.Common.ENCODING_UTF8);
                    } catch (UnsupportedEncodingException uee) {
                        LOGGER.error("UnsupportedEncodingException while trying to encode :", uee);
                    }
                    mediaTagStats.setDestCityLabel(cityLabel.toLowerCase());
                }

                // Destination city as a search criteria was used: add the first corresponding country
                // if it was not already added.
                if (mediaTagStats.getDestCountryCode() == null
                                && destCountryCodes != null && !destCountryCodes.isEmpty()) {
                    String isoCode = countryCodeRetriever(destCountryCodes.get(0));
                    if (isoCode != null) {
                        mediaTagStats.setDestCountryCode(isoCode.toLowerCase());
                    }
                }
            }

            /*
             * At this moment we have trackingData filled with serp infos.
             * Check if in search box we used trip type or hotel stars criteria :
             * if yes, edit or create cookie with this value (this cookie will be used)
             * in all other pages media tags). If not, remove existing cookie.
             *
             */
            mediaTagStats = TrackingUtil.addTripTypeFromRqToCookie(mediaTagStats, this.getEnvironment(), trackingData);
            mediaTagStats = TrackingUtil.addHotelStarsFromRqToCookie(mediaTagStats, this.getEnvironment(), trackingData);

        }

        context.write("mediaTagStats", mediaTagStats);
    }

    /**
     * Retrieve mapped ISO country code.
     *
     * @param countryCriteriaCode the criteria country code
     * @return {@link String}
     */
    private String countryCodeRetriever(String countryCriteriaCode) {
        MessageRetriever retriever = new MessageRetriever(Util.getPageIdentifierInstance(),
            "countryCodes.properties");
        return retriever.createMessage(countryCriteriaCode, null);
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
    public void preview(IComponentContext<ContentLayoutComponent> context,
        InjectionData injectionData) throws PageNotFoundException {
    }
}
