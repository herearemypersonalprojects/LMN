/*
 * Created on 2 sept. 2011
 *
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.serp;

import java.util.Map;

import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.data.PageLayoutComponent;
import com.travelsoft.cameleo.catalog.data.PublishedProduct;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.product.AbstractFillProduct;
import com.travelsoft.lastminute.catalog.tripadvisor.ReviewsRetriever;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.Util;
import com.travelsoft.lastminute.data.Place;
import com.travelsoft.lastminute.data.SmallProductDisplayable;

/**
 * <p>Titre : FillProductItem.</p>
 * Description : Fill the product item.
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Company : Travelsoft</p>
 * @author MengMeng
 * @version
 *
 */
public class FillProductItem extends AbstractFillProduct {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(FillProductItem.class);

    /**
     * Process business logic and produce the data model to fill the product item.
     * @param context the component's context
     */
    @Override
    public void fillComponentContext(IComponentContext<PageLayoutComponent> context) {
        PublishedProduct product = (PublishedProduct) context.lookup(Constants.Context.PUBLISHED_PRODUCT);
        SmallProductDisplayable productDisplayable = constructProductDisplayable(product, context);

        String avgrating = (String) context.lookup("avgrating");
        String ratingimage = (String) context.lookup("ratingimage");

        productDisplayable.setRating(avgrating);
        productDisplayable.setRatingImageUrl(ratingimage);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("display the small displayable product : " + productDisplayable.convertToString());
        }
        boolean flyAndDrive = Util.isExistingFlyAndDriveCriteria(product);
        context.write(Constants.Context.FLY_AND_DRIVE, flyAndDrive);
        context.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, productDisplayable);
        try {
            String result = isProductWithVideo(product);
            if (result != null) {
                String playerID = result.split("###")[0];
                String contentID = result.split("###")[1];
                context.write(Constants.Context.PLAYER_ID, playerID);
                context.write(Constants.Context.CONTENT_ID, contentID);
            }
        } catch (Exception e) {
            LOGGER.error("Error while trying to retrieve the product");
        }
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
