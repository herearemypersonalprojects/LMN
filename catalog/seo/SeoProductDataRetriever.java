/**
 *
 */
package com.travelsoft.lastminute.catalog.seo;

import com.travelsoft.cameleo.catalog.data.PageLayoutComponent;
import com.travelsoft.cameleo.catalog.data.PublishedProduct;
import com.travelsoft.cameleo.cms.processor.controller.AbstractStructuredController;
import com.travelsoft.cameleo.cms.processor.controller.WebProcessEnvironment;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.data.SeoData;
import com.travelsoft.lastminute.data.SmallProductDisplayable;

/**
 * <p>
 * Titre : SeoProductDataRetriever.
 * </p>
 * <p>
 * Description : .
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Travelsoft
 * </p>
 *
 * @author zouhair.mechbal
 */
public class SeoProductDataRetriever extends AbstractStructuredController<PageLayoutComponent, WebProcessEnvironment> {

    /**
     * Process business logic and produce the data model.
     *
     * @param context the component's context
     */
    public void fillComponentContext(IComponentContext<PageLayoutComponent> context) {
        SeoData seoData = (SeoData) context.lookup("seoData");
        String rewriteParameter = this.getEnvironment().getRequestParameter("rewrite");
        if ("true".equals(rewriteParameter)) {
            seoData.setRewrite(true);
        }
        if (seoData != null) {
            SmallProductDisplayable product = (SmallProductDisplayable) context.lookup(Constants.Context.SMALL_PRODUCT_DISPLAYABLE);
            if (product != null) {
                PublishedProduct publishedProduct = (PublishedProduct) context.lookup(Constants.Context.PUBLISHED_PRODUCT);
                seoData.setProductName(product.getTitle());
                seoData.setDestinationCountry(SeoTool.getUniqueDestination(publishedProduct, "country"));
                seoData.setDestinationCity(SeoTool.getUniqueDestination(publishedProduct, "city"));
                seoData.setStayType(SeoTool.getUniqueStayType(publishedProduct));
                seoData.setSeoProductName(SeoTool.getProductNameSeoUrl(product.getTitle()));
            } else {
                seoData.setDestinationCountry(SeoTool.getUniqueDestinationCountry(this.getEnvironment().getRequest()));
                seoData.setDestinationCity(SeoTool.getUniqueDestinationCity(this.getEnvironment().getRequest()));
                seoData.setStayType(SeoTool.getUniqueStayType(this.getEnvironment().getRequest()));
            }
        }
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
    public void preview(IComponentContext<PageLayoutComponent> context, InjectionData injectionData) throws PageNotFoundException {
    }
}
