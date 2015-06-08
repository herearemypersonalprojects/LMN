/*
 *
 * Copyright Travelsoft, 2013.
 */
package com.travelsoft.lastminute.catalog.serp;

import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.data.PageLayoutComponent;
import com.travelsoft.cameleo.catalog.data.PublishedProduct;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.product.AbstractFillProduct;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.data.SmallProductDisplayable;

/**
 * <p>Titre : FillProductItem.</p>
 * Description : Fill the product item.
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Company : Travelsoft</p>
 * @author Quoc-Anh Le
 * @version
 *
 */
public class FillRelatedProductItem extends AbstractFillProduct {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(FillRelatedProductItem.class);

    /**
     * Process business logic and produce the data model to fill the product item.
     * @param context the component's context
     */
    @Override
    public void fillComponentContext(IComponentContext<PageLayoutComponent> context) {
        PublishedProduct product = (PublishedProduct) context.lookup(Constants.Context.RELATED_PUBLISHED_PRODUCT);
        if (product == null) {
            return;
        }
        SmallProductDisplayable productDisplayable = constructProductDisplayable(product, context);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("display the small displayable product : " + productDisplayable.convertToString());
        }

        context.write(Constants.Context.RELATED_SMALL_PRODUCT_DISPLAYABLE, productDisplayable);
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
