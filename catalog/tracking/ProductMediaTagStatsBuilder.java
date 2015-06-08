/**
 * ProductMediaTagStatsBuilder.
 */
package com.travelsoft.lastminute.catalog.tracking;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.data.ContentLayoutComponent;
import com.travelsoft.cameleo.catalog.data.Country;
import com.travelsoft.cameleo.catalog.data.PublishedProduct;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.data.MediaTagTrackingStats;
import com.travelsoft.lastminute.data.SmallProductDisplayable;

/**
 * Media tag object builder.
 */
public class ProductMediaTagStatsBuilder extends MediaTagStatsBuilder {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(ProductMediaTagStatsBuilder.class);

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

        // fill mediaTag object with infos from published product
        PublishedProduct publishedProduct
            = (PublishedProduct) context.lookup(Constants.Context.PUBLISHED_PRODUCT);
        Country[] countries = publishedProduct.getTechnicalInfo().getCountry();
        if (countries != null) {
            for (Country country : countries) {
                mediaTagStats.setDestCountryCode(country.getCode().toLowerCase());
                break;
            }
        }


        // get city label from displayable product : in displayable product city label is stored
        // with accent (thats why we are not using publishedProduct object from context - there city is
        // stored without accent)
        SmallProductDisplayable productDisplayable = (SmallProductDisplayable) context
          .lookup(Constants.Context.SMALL_PRODUCT_DISPLAYABLE);
        String destCities = productDisplayable.getDestinationCities();
        if (destCities.indexOf(Constants.Common.SEPARATOR_COMMA) == -1) {
            // there were no multiple cities
            String destCityLabel = destCities.replaceAll(" ", Constants.Common.SEPARATOR_UNDERSCORE);
            try {
                destCityLabel = URLEncoder.encode(destCityLabel, Constants.Common.ENCODING_UTF8);
                mediaTagStats.setDestCityLabel(destCityLabel.toLowerCase());
            } catch (UnsupportedEncodingException uee) {
                LOGGER.error("UnsupportedEncodingException while trying to encode :", uee);
            }
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
