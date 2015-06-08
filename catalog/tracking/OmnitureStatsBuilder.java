/*
 * Created on 18 oct. 2011
 *
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.tracking;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.data.ContentLayoutComponent;
import com.travelsoft.cameleo.catalog.data.User;
import com.travelsoft.cameleo.cms.processor.controller.AbstractStructuredController;
import com.travelsoft.cameleo.cms.processor.controller.WebProcessEnvironment;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.Util;
import com.travelsoft.lastminute.data.OmnitureTrackingStats;

/**
 * <p>Titre : TrackingBuilder.</p>
 * <p>Description : Fill all tracking variables.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Travelsoft</p>
 * @author dusan.spaic
 */
public abstract class OmnitureStatsBuilder
    extends AbstractStructuredController<ContentLayoutComponent, WebProcessEnvironment> {


    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(OmnitureStatsBuilder.class);

    /** Omniture stats object. */
    protected OmnitureTrackingStats omnitureStats;


    /**
     * Fill Omniture tracking object with variables that will be used in all pages.
     *
     * @param context the component's context
     */
    @Override
    public void fillComponentContext(IComponentContext<ContentLayoutComponent> context)  {
    }


    /**
     * Fill omniture object with base variables that are shared between the pages.
     */
    protected void constructOmnitureBaseStats() {

        HttpServletRequest rq = this.getEnvironment().getRequest();
        HttpServletResponse rp = this.getEnvironment().getResponse();
        HttpSession session = rq.getSession();

        omnitureStats = new OmnitureTrackingStats();
        omnitureStats.setServerName(rq.getServerName());

        // check partnerId
        if (rq.getParameter(Constants.OmnitureConstants.PARTNER_ID) != null) {
            // partnerId found in rq. Update cookie value.
            String partnerId = (String) rq.getParameter(Constants.OmnitureConstants.PARTNER_ID);
            omnitureStats.setPartnerId(partnerId);
            addCookie(rp, Constants.OmnitureConstants.PARTNER_ID, partnerId);
        } else {
            // check if partnerId exists in cookie.
            String partnerId = readCookie(Constants.OmnitureConstants.PARTNER_ID);
            if (partnerId != null) {
                omnitureStats.setPartnerId(partnerId);
            }
        }

        // check source
        if (rq.getParameter("source") != null) {
            // source found in rq. Update cookie value.
            String source = (String) rq.getParameter("source");
            if (source != null) {
            	try {
					addCookie(rp, Constants.OmnitureConstants.OMNITURE_SOURCE, URLEncoder.encode(source, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					LOGGER.error("Error while trying to encode the source with value : " + source);
				}
            }
        }

        String url = rq.getRequestURL().toString();
        omnitureStats.setUrl(url);
        omnitureStats.setCookieDomainPeriods(StringUtils.countMatches(url, "."));

        // current hour
        Calendar currCal = Calendar.getInstance();
        omnitureStats.setCurrentHour(currCal.get(Calendar.HOUR_OF_DAY));

        if (session.getAttribute(Constants.Common.USER_PARAMETER) != null) {
            User user = (User) session.getAttribute(Constants.Common.USER_PARAMETER);
            omnitureStats.setUserId(Integer.toString(user.getId()));
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


    /**
     * Add cookie to response.
     *
     * @param rp the HttpServletResponse
     * @param cookieName the cookie name
     * @param cookieValue the cookie value
     */
    private void addCookie(HttpServletResponse rp, String cookieName, String cookieValue) {
        try {
            Cookie newCookie = new Cookie(cookieName, cookieValue);
            newCookie.setPath("/");
            rp.addCookie(newCookie);
        } catch (Exception e) {
            LOGGER.error("Error occured while creating cookie with value:" + cookieValue);
        }
    }


    /**
     * Read cookie.
     *
     * @param cookieName the cookie name
     * @return {@link String}}
     */
    private String readCookie(String cookieName) {
        Cookie cookie = Util.getCookie(this.getEnvironment(), cookieName);
        if (cookie != null && cookie.getValue() != null) {
            return cookie.getValue();
        }
        return null;
    }
}
