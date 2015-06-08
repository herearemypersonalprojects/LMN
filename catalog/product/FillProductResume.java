/**
 *
 */
package com.travelsoft.lastminute.catalog.product;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.travelsoft.cameleo.catalog.data.PageLayoutComponent;
import com.travelsoft.cameleo.catalog.data.PublishedProduct;
import com.travelsoft.cameleo.catalog.data.TechnicalInfo;
import com.travelsoft.cameleo.cms.processor.controller.AbstractStructuredController;
import com.travelsoft.cameleo.cms.processor.controller.WebProcessEnvironment;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.tripadvisor.ReviewsRetriever;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.Util;
import com.travelsoft.lastminute.data.SmallProductDisplayable;

/**
 * <p>Titre : FillProduct.</p>
 * Description :
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Company : Travelsoft</p>
 * @author MengMeng
 * @version
 *
 */
public class FillProductResume extends AbstractStructuredController<PageLayoutComponent, WebProcessEnvironment> {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(FillProductResume.class);


    /**
     * Process business logic and produce the data model to get the product.
     * @param context the component's context
     */
    public void fillComponentContext(IComponentContext<PageLayoutComponent> context) {
        PublishedProduct product = (PublishedProduct) context.lookup(Constants.Context.PUBLISHED_PRODUCT);
        SmallProductDisplayable productDisplayable = (SmallProductDisplayable) context
                .lookup(Constants.Context.SMALL_PRODUCT_DISPLAYABLE);
        TechnicalInfo techInfo = product.getTechnicalInfo();
        if (techInfo != null && techInfo.getTourOperator() != null) {
            String providerLabel = techInfo.getTourOperator().getLabel();
            productDisplayable.setToLabel(providerLabel);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("display the small displayable product : " + productDisplayable.convertToString());
        }

        String pid = productDisplayable.getId();
        String fixedId = Util.getConfigValue("TRIPADVISOR_PARTNERID_REPLACED");
        String testedId = Util.getConfigValue("TRIPADVISOR_PARTNERID_TEST");
        if (StringUtils.isNotBlank(fixedId) && StringUtils.isNotBlank(testedId) && testedId.equalsIgnoreCase(pid)) {
            pid = fixedId;
        }

        String key = Util.getConfigValue("TRIPADVISOR_JSONKEY");
        String url = Util.getConfigValue("TRIPADVISOR_JSONURL") + pid;
        String jsonUrl = url + "?key=" + key;

        String folderFullPath = Util.getConfigValue("TRIPADVISOR_FOLDERFULLPATH");
        String folderName = Util.getConfigValue("TRIPADVISOR_FOLDERNAME");
        String htmlFolder = folderFullPath + folderName + productDisplayable.getId();
        String reviewsJson =  htmlFolder + "/reviews.json";

        Util.getJson(jsonUrl, reviewsJson);

        JSONObject json = Util.getJson(reviewsJson);

        ReviewsRetriever rr = new ReviewsRetriever();
        if (json != null) {
            productDisplayable.setNum_reviews(rr.getString(json, "num_reviews"));
            productDisplayable.setRatingImageUrl(rr.getString(json, "ratingImageUrl"));
            productDisplayable.setRating(rr.getString(json, "rating"));
        }

        context.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, productDisplayable);

    }


    /**
     * Produces a data model mock.
     *
     * @param context
     *            the component's context
     * @param injectionData
     *            the injection data
     * @throws PageNotFoundException If the page is not found
     */
    public void preview(IComponentContext<PageLayoutComponent> context, InjectionData injectionData)
            throws PageNotFoundException {
    }

}
