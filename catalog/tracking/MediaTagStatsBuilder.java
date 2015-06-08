/*
 * Created on 18 oct. 2011
 *
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.tracking;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.Cookie;

import org.apache.commons.lang.RandomStringUtils;

import com.travelsoft.cameleo.catalog.data.ContentLayoutComponent;
import com.travelsoft.cameleo.cms.processor.controller.AbstractStructuredController;
import com.travelsoft.cameleo.cms.processor.controller.WebProcessEnvironment;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.Util;
import com.travelsoft.lastminute.data.MediaTagTrackingStats;


/**
 * Fill all tracking variables.
 */
public abstract class MediaTagStatsBuilder
    extends AbstractStructuredController<ContentLayoutComponent, WebProcessEnvironment> {

    /**
     * Fill Omniture tracking object with variables that will be used in all pages.
     *
     * @param context the component's context
     */
    @Override
    public void fillComponentContext(IComponentContext<ContentLayoutComponent> context)  {

    }

    /**
     * Create MediaTagTrackingStats object with common variables.
     * @param mediaTagStats the mediaTagStats object to be filled
     */
    protected void fillMediaTagBaseStats(MediaTagTrackingStats mediaTagStats) {

        mediaTagStats.setRandom13(RandomStringUtils.random(13, false, true));
        mediaTagStats.setRandom15(RandomStringUtils.random(15, false, true));

        // if in cookie we have depMonth, that means that in latest search
        // was executed with this parameter.
        Cookie depMonthCookie = Util.getCookie(this.getEnvironment(),
            Constants.Common.DEP_MONTH_COOKIE);
        if (depMonthCookie != null && depMonthCookie.getMaxAge() != 0) {
            String depMonth = depMonthCookie.getValue();
            mediaTagStats.setDepMonth(depMonth);
        }

        // if in cookie we have trip type, that means that in latest search
        // was executed with this parameter.
        Cookie tripTypeCookie = Util.getCookie(this.getEnvironment(),
            Constants.Common.TRIP_TYPE_COOKIE);
        if (tripTypeCookie != null && tripTypeCookie.getMaxAge() != 0) {
            String tripType = tripTypeCookie.getValue();
            try {
                tripType = URLDecoder.decode(tripType, Constants.Common.ENCODING_UTF8);
                mediaTagStats.setTripTypeLabel(tripType);
            } catch (UnsupportedEncodingException uee) {
                // error will not be thrown with utf8 encoding
            }
        }

        // if in cookie we have hotel star, that means that in latest search
        // was executed with this parameter.
        Cookie hotelStarCookie = Util.getCookie(this.getEnvironment(),
            Constants.Common.HOTEL_STARS_COOKIE);
        if (hotelStarCookie != null && hotelStarCookie.getMaxAge() != 0) {
            String hotelStar = hotelStarCookie.getValue();
            mediaTagStats.setStarsNmb(hotelStar);
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
