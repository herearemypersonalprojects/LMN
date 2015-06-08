/**
 * CompareMediaTagStatsBuilder
 */
package com.travelsoft.lastminute.catalog.tracking;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.data.ContentLayoutComponent;
import com.travelsoft.cameleo.catalog.data.Country;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.data.CompareProductDisplayable;
import com.travelsoft.lastminute.data.CompareProductDisplayableList;
import com.travelsoft.lastminute.data.MediaTagTrackingStats;
import com.travelsoft.lastminute.data.SmallProductDisplayable;
import com.travelsoft.lastminute.data.SmallProductDisplayableList;

/**
 * Media tag object builder.
 */
public class CompareMediaTagStatsBuilder extends MediaTagStatsBuilder {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(CompareMediaTagStatsBuilder.class);

    /**
     * Process business logic and produce the data model.
     *
     * @param context the component's context
     */
    public void fillComponentContext(IComponentContext<ContentLayoutComponent> context) {

        MediaTagTrackingStats mediaTagStats = (MediaTagTrackingStats) context.lookup("mediaTagStats");
        if (mediaTagStats != null && mediaTagStats.getRandom13() != null) {
            // media tag was already build for this page (this is second time that this builder
            // was called). No need to build the object again.
            return;
        }

        fillMediaTagBaseStats(mediaTagStats);

        SmallProductDisplayableList mpdList
            = (SmallProductDisplayableList) context.lookup(Constants.Context.PRODUCT_DISPLAYABLE_LIST);
        SmallProductDisplayable[] smallProdDisplayables = mpdList.getSmallProductDisplayables();

        // destination city
        List<String> destCitiesList = new ArrayList<String>();
        for (SmallProductDisplayable spd : smallProdDisplayables) {
            String destCities = spd.getDestinationCities();
            if (destCities.indexOf(Constants.Common.SEPARATOR_COMMA) == -1
                            && !destCitiesList.contains(destCities)) {
                destCitiesList.add(destCities);
            }
        }
        if (!destCitiesList.isEmpty() && destCitiesList.size() == 1) {
            String destCity = destCitiesList.get(0).trim().replaceAll(" ", Constants.Common.SEPARATOR_UNDERSCORE);
            try {
                destCity = URLEncoder.encode(destCity, Constants.Common.ENCODING_UTF8);
            } catch (UnsupportedEncodingException uee) {
                LOGGER.error("UnsupportedEncodingException while trying to encode :", uee);
            }
            mediaTagStats.setDestCityLabel(destCity.toLowerCase());
        }

        // set dest city label
        CompareProductDisplayableList compareDisplayableList
            = (CompareProductDisplayableList) context.lookup(Constants.Context.COMPARE_DISPLAYABLE_LIST);
        CompareProductDisplayable[] displayables = compareDisplayableList.getCompareProductDisplayables();
        List<String> destCountryCodeList = new ArrayList<String>();
        for (CompareProductDisplayable displayable : displayables) {
            Country[] destCountry = displayable.getDestCountry();
            if (destCountry != null) {
                for (Country country : destCountry) {
                    String destCoutryCode = country.getCode();
                    if (!destCountryCodeList.contains(destCoutryCode)) {
                        destCountryCodeList.add(destCoutryCode);
                    }
                }
            }
        }

        if (!destCountryCodeList.isEmpty() && destCountryCodeList.size() == 1) {
            mediaTagStats.setDestCountryCode(destCountryCodeList.get(0).toLowerCase());
        }

        context.write("mediaTagStats", mediaTagStats);
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
