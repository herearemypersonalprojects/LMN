/*
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.travelsoft.cameleo.catalog.data.Criterion;
import com.travelsoft.cameleo.catalog.data.CriterionValue;
import com.travelsoft.cameleo.catalog.data.PublishedProduct;
import com.travelsoft.cameleo.catalog.data.SearchEngine;
import com.travelsoft.cameleo.cms.processor.controller.WebProcessEnvironment;
import com.travelsoft.lastminute.data.MediaTagTrackingStats;
import com.travelsoft.lastminute.data.SerpTrackingData;
import com.travelsoft.lastminute.data.TrackingData;

/**
 * <p>Titre : Tracking Util.</p>
 * <p>Description : .</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Travelsoft</p>
 * @author dusan.spaic
 */
public class TrackingUtil {

    /** Milliseconds in day. */
    private static final int MILLISECONDS_IN_DAY = 1000 * 60 * 60 * 24;

    /**
     * Get the selected trip type label.
     * @param rq the request
     * @param trackingData the trackingData
     * @return {@link String}
     */
    public static List<String> getTripTypeLabel(HttpServletRequest rq, TrackingData trackingData) {
        String rqParam = Constants.Common.REFINEMENT_SEARCH_ENGINE_PREFIX
                        + Constants.CriterionConstants.CRITERION_CUSTOMER_FIELD
                        + Constants.CriterionConstants.STAY_TYPE_CODE;
        String rqTripTypeParam = (String) rq.getParameter(rqParam);

        SerpTrackingData std = (SerpTrackingData) trackingData.getSerp();
        SearchEngine seRefinement = (SearchEngine) std.getRefinementSearchEngine();

        Criterion tripTypeCriterion = getSpecificCriterion(seRefinement,
            Constants.CriterionConstants.STAY_TYPE_CODE);
        if (rqTripTypeParam == null || tripTypeCriterion == null || tripTypeCriterion.getValue() == null) {
            return null;
        }

        CriterionValue[] tripTypeValues = tripTypeCriterion.getValue();
        List<String> tripTypeLabelsList = new ArrayList<String>();

        String[] tripTypeParams = StringUtils.split(rqTripTypeParam,
            Constants.Common.SEPARATOR_COMMA);
        for (String tripTypeParam : tripTypeParams) {
            for (CriterionValue tripTypeValue : tripTypeValues) {
                if (tripTypeValue.getCode().equals(tripTypeParam)) {
                    tripTypeLabelsList.add(tripTypeValue.getLabel());
                    break;
                }
            }
        }

        if (tripTypeLabelsList.isEmpty()) {
            return null;
        }

        return tripTypeLabelsList;
    }


    /**
     * Get the selected region label. If the selected region was maroc.marrakech,
     * label will be Maroc | Marrakech
     * @param rq the request
     * @param trackingData the trackingData
     * @return {@link String}
     */
    public static String getRegionLabel(HttpServletRequest rq, TrackingData trackingData) {
        String rqParam = Constants.Common.REFINEMENT_DESTINATION_CITY_PARAMETER;
        String rqRegionParam = (String) rq.getParameter(rqParam);

        SerpTrackingData std = (SerpTrackingData) trackingData.getSerp();
        SearchEngine seRefinement = (SearchEngine) std.getRefinementSearchEngine();

        Criterion destCriterion = getSpecificCriterion(seRefinement,
            Constants.CriterionConstants.CRITERION_DESTIONATION_CODE);
        if (rqRegionParam == null || destCriterion == null || destCriterion.getValue() == null) {
            return null;
        }

        CriterionValue[] destValues = destCriterion.getValue();
        List<String> regionLabelsList = new ArrayList<String>();

        String[] regionParams = StringUtils.split(rqRegionParam, Constants.Common.SEPARATOR_COMMA);
        for (String countryCityRegion : regionParams) {
            String cityLabel = null;
            String countryLabel = null;
            String countryCode = countryCityRegion.split("\\.")[0];
            for (CriterionValue destValue : destValues) {
                if (destValue.getCode().equals(countryCityRegion)) {
                    cityLabel = destValue.getLabel();
                } else if (destValue.getCode().equals(countryCode)) {
                    countryLabel = destValue.getLabel();
                }
                if (cityLabel != null && countryLabel != null) {
                    regionLabelsList.add(countryLabel + "|" + cityLabel);
                    break;
                }
            }
        }

        if (regionLabelsList.isEmpty()) {
            return null;
        }
        return Util.removeAccent(StringUtils.join(regionLabelsList, " :: "));
    }


    /**
     * Get searched destination cities labels.
     * If criterion value has point in its code, than the label will corresponds to the city.
     * @param rq the request
     * @param trackingData the trackingData
     * @param rqParam the request parameter we are checking (s_c.de or aff_c.de)
     * @param country the boolean indicating if country should be searched in the dest values
     * @param city the boolean indicating if city should be searched in the dest values
     * @return {@link List<String>}
     */
    public static List<String> getDestinationLabel(HttpServletRequest rq,
        TrackingData trackingData, String rqParam, boolean country, boolean city) {

        String destParam = (String) rq.getParameter(rqParam);

        SerpTrackingData std = (SerpTrackingData) trackingData.getSerp();
        SearchEngine se = (SearchEngine) std.getRefinementSearchEngine();

        Criterion destCriterion = getSpecificCriterion(se, Constants.CriterionConstants.CRITERION_DESTIONATION_CODE);
        if (destParam == null || destCriterion == null || destCriterion.getValue() == null) {
            return null;
        }

        CriterionValue[] destValues = destCriterion.getValue();
        List<String> destList = new ArrayList<String>();

        String[] destParams = StringUtils.split(destParam, Constants.Common.SEPARATOR_COMMA);
        for (String oneSearchedDest : destParams) {
            for (CriterionValue destValue : destValues) {
                if (destValue.getCode().equals(oneSearchedDest)) {

                    // we found searched destination. Check if we need only cities, only countries or both
                    if (country && city) {
                        // we dont care if criteria is city or country.
                        destList.add(destValue.getLabel());
                    } else if (!city) {
                        String countryCode = destValue.getCode();
                        if (countryCode.indexOf(".") > -1) {
                            // we need only country code
                            destList.add(countryCode.substring(0, countryCode.indexOf(".")));
                        } else {
                            destList.add(countryCode);
                        }
                    } else if (!country && destValue.getCode().indexOf(".") > -1) {
                        // we need only cities and this criteria is city criteria
                        destList.add(destValue.getLabel());
                    }
                    break;
                }
            }
        }

        if (destList.isEmpty()) {
            return null;
        }
        return destList;
    }


    /**
     * Get hotel stars used in search.
     * @param rq the request
     * @return {@link String}
     */
    public static String[] getStarsNmbList(HttpServletRequest rq) {

        String paramName = Constants.Common.REFINEMENT_SEARCH_ENGINE_PREFIX
                        + Constants.CriterionConstants.CRITERION_CUSTOMER_FIELD
                        + Constants.CriterionConstants.CRITERION_CATEGORY_CODE;
        String categories = rq.getParameter(paramName);
        if (categories == null) {
            return null;
        }
        String[] catSplit = StringUtils.split(categories, Constants.Common.SEPARATOR_COMMA);
        for (int i = 0; i < catSplit.length; i++) {
            catSplit[i] = catSplit[i].substring(catSplit[i].length() - 1, catSplit[i].length());
        }
        Arrays.sort(catSplit);
        return catSplit;
    }


    /**
     * Get product criterion city labels.
     * @param product the published product
     * @param critCode the criterion code
     * @return labelsList the value label list
     */
    public static List<String> getCriterionDestCityLabelList(PublishedProduct product, String critCode) {
        List<String> destCityLabelList = new ArrayList<String>();
        if (product.getTechnicalInfo() == null || product.getTechnicalInfo().getCriterion() == null) {
            return destCityLabelList;
        }
        Criterion[] criterion = product.getTechnicalInfo().getCriterion();
        for (Criterion crit : criterion) {
            if (crit != null && critCode.equals(crit.getCode())) {
                int valueCount = crit.getValueCount();
                for (int i = 0; i < valueCount; i++) {
                    CriterionValue criterionValue = crit.getValue(i);
                    String code = criterionValue.getCode();
                    String label = null;
                    if (code.indexOf(".") > -1) {
                        // this is city
                        label = criterionValue.getLabel();
                    }
                    if (label != null && !destCityLabelList.contains(label)) {
                        destCityLabelList.add(label);
                    }
                }
                return destCityLabelList;
            }
        }
        return destCityLabelList;
    }


    /**
     * If departure month was used in the search, put it into the cookie so that it can be used
     * on other pages. If depMonth was not used in the search, remove this cookie.
     *
     * Executed with each new search.
     *
     * @param webEnvironment the webEnvironment
     * @param trackingData the TrackingData
     * @return {@link String searched departure month}
     */
    public static MediaTagTrackingStats addDepMonthFromRqToCookie(WebProcessEnvironment webEnvironment,
        TrackingData trackingData) {

        MediaTagTrackingStats mediaTagTrackingData = new MediaTagTrackingStats();
        HttpServletRequest rq = webEnvironment.getRequest();

        // add searched depMonth from rq to cookie and set it to trackingData object
        String my = rq.getParameter(Constants.CriterionConstants.CRITERION_MONTH_YEAR);
        String month = null;
        if (my != null) {
            // change existing or create a new cookie
            month = my.split("/")[0];
            editOrCreateCookie(Constants.Common.DEP_MONTH_COOKIE, month, webEnvironment);
        } else {
            Util.deleteCookie(webEnvironment, Constants.Common.DEP_MONTH_COOKIE);
        }

        mediaTagTrackingData.setDepMonth(month);
        return mediaTagTrackingData;
    }


    /**
     * If trip (holiday) type was used in the search, put it into the cookie so that it can be used
     * on other pages. If it was not used in the search, remove this cookie.
     *
     * @param mediaTagTrackingData the MediaTagTrackingStats
     * @param webEnvironment the WebProcessEnvironment
     * @param trackingData the TrackingData
     * @return {@link MediaTagTrackingStats}
     */
    public static MediaTagTrackingStats addTripTypeFromRqToCookie(MediaTagTrackingStats mediaTagTrackingData,
        WebProcessEnvironment webEnvironment, TrackingData trackingData) {

        HttpServletRequest rq = webEnvironment.getRequest();

        String tripType = null;
        List<String> tripTypeLabelList = getTripTypeLabel(rq, trackingData);
        if (tripTypeLabelList != null && tripTypeLabelList.size() == 1) {
            tripType = tripTypeLabelList.get(0);
            tripType = tripType.replaceAll(" ", Constants.Common.SEPARATOR_UNDERSCORE);
            tripType = tripType.replaceAll(Constants.Common.SEPARATOR_AMP,
                Constants.Common.SEPARATOR_UNDERSCORE);
            try {
                // value will be encoded because of control character in cookie value exception
                tripType = URLEncoder.encode(tripType, Constants.Common.ENCODING_UTF8);
                editOrCreateCookie(Constants.Common.TRIP_TYPE_COOKIE, tripType, webEnvironment);
            } catch (UnsupportedEncodingException uee) {
                // error will not be thrown with utf8 encoding
            }

        } else {
            Util.deleteCookie(webEnvironment, Constants.Common.TRIP_TYPE_COOKIE);
        }

        mediaTagTrackingData.setTripTypeLabel(tripType);
        return mediaTagTrackingData;
    }


    /**
     * Add searched hotel star into the cookie (if star
     * was selected from search box) or remove it if not.
     *
     * @param mediaTagTrackingData the MediaTagTrackingStats
     * @param webEnvironment the WebProcessEnvironment
     * @param trackingData the TrackingData
     * @return {@link String}}
     */
    public static MediaTagTrackingStats addHotelStarsFromRqToCookie(MediaTagTrackingStats mediaTagTrackingData,
        WebProcessEnvironment webEnvironment, TrackingData trackingData) {

        HttpServletRequest rq = webEnvironment.getRequest();
        String star = null;
        String[] stars = TrackingUtil.getStarsNmbList(rq);
        if (stars != null && stars.length == 1) {
            star = stars[0];
            editOrCreateCookie(Constants.Common.HOTEL_STARS_COOKIE, star, webEnvironment);
        } else {
            Util.deleteCookie(webEnvironment, Constants.Common.HOTEL_STARS_COOKIE);
        }

        mediaTagTrackingData.setStarsNmb(star);
        return mediaTagTrackingData;
    }


    /**
     * Edit existing cookie or create new cookie for the given key and value.
     *
     * @param key the key
     * @param value the value
     * @param webEnvironment {@link WebProcessEnvironment}
     */
    private static void editOrCreateCookie(String key, String value, WebProcessEnvironment webEnvironment) {
        Cookie cookie = Util.getCookie(webEnvironment, key);
        if (cookie != null) {
            cookie.setValue(value);
        } else {
            cookie = new Cookie(key, value);
        }
        HttpServletResponse rp = webEnvironment.getResponse();
        cookie.setPath("/");
        rp.addCookie(cookie);
    }


    /**
     * Get depDate with flexibility value.
     * @param rq the request
     * @return {@link String}
     */
    public static String getDepartureDateWithFlex(HttpServletRequest rq) {
        String day = rq.getParameter(Constants.CriterionConstants.CRITERION_DAYS);
        String my = rq.getParameter(Constants.CriterionConstants.CRITERION_MONTH_YEAR);
        if (day == null || my == null) {
            return null;
        }
        StringBuffer depDateWithFlex = new StringBuffer();
        depDateWithFlex.append(day);
        depDateWithFlex.append("/");
        depDateWithFlex.append(my);
        String flexibility = (String) rq.getParameter(Constants.CriterionConstants.CRITERION_FLEXIBILITY);
        if (flexibility != null) {
            depDateWithFlex.append(":");
            depDateWithFlex.append(flexibility);
        }
        return depDateWithFlex.toString();
    }


    /**
     * Get difference in days between today and dep date.
     * @param rq the request
     * @return {@link String}
     */
    public static String getLeadTime(HttpServletRequest rq) {

        String day = rq.getParameter(Constants.CriterionConstants.CRITERION_DAYS);
        String my = rq.getParameter(Constants.CriterionConstants.CRITERION_MONTH_YEAR);
        if (day == null && my == null) {
            // departure was not selected in search.
            return null;
        }

        Calendar depDateCalendar = TrackingUtil.getDepDateCalendar(rq);
        Calendar today = Calendar.getInstance();
        long diff = depDateCalendar.getTimeInMillis() - today.getTimeInMillis();
        long leadTime = diff / MILLISECONDS_IN_DAY;
        return Long.toString(leadTime);
    }


    /**
     * Get price range used search. 0 - 1499
     * @param rq the request
     * @return {@link String}
     */
    public static String getPriceRange(HttpServletRequest rq) {

        String rqParam = Constants.Common.REFINEMENT_SEARCH_ENGINE_PREFIX
                        + Constants.CriterionConstants.MAX_PRICE_CODE;
        String maxPrice = rq.getParameter(rqParam);
        if (maxPrice == null) {
            return null;
        }
        int currentMaxPrice = 0;
        if (maxPrice.contains(Constants.Common.SEPARATOR_COMMA)) {
            // multiple max prices were selected. Use the largest one.
            String[] maxPriceSplit = StringUtils.split(maxPrice,
                Constants.Common.SEPARATOR_COMMA);
            for (int i = 0; i < maxPriceSplit.length; i++) {
                int currentPrice = Integer.parseInt(maxPriceSplit[i]);
                if (currentPrice > currentMaxPrice) {
                    currentMaxPrice = currentPrice;
                }
            }
        } else {
            currentMaxPrice = Integer.parseInt(maxPrice);
        }

        return Integer.toString(currentMaxPrice);
    }

    /**
     * Retrieve criterion with the given code.
     * @param seRefinement the SearchEngine
     * @param critCode the specific criteria code.
     * @return {@link Criterion}
     */
    private static Criterion getSpecificCriterion(SearchEngine seRefinement, String critCode) {
        if (seRefinement == null || seRefinement.getCriterion() == null) {
            return null;
        }
        Criterion[] criterion = seRefinement.getCriterion();
        for (Criterion oneCrit : criterion) {
            if (oneCrit.getCode().equals(critCode)) {
                return oneCrit;
            }
        }
        return null;
    }


    /**
     * Get difference in days between today and dep date.
     * @param rq the request
     * @return {@link String}
     */
    private static Calendar getDepDateCalendar(HttpServletRequest rq) {
        String day = rq.getParameter(Constants.CriterionConstants.CRITERION_DAYS);
        String my = rq.getParameter(Constants.CriterionConstants.CRITERION_MONTH_YEAR);

        Calendar depDateCalendar = Calendar.getInstance();
        if (day != null) {
            depDateCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
        }
        if (my != null) {
            String month = my.split("/")[0];
            String year = my.split("/")[1];
            depDateCalendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
            depDateCalendar.set(Calendar.YEAR, Integer.parseInt(year));
        }

        return depDateCalendar;
    }
}
