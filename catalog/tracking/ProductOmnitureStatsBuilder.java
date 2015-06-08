/*
 * Created on 18 oct. 2011
 *
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.tracking;

import java.util.ArrayList;
import java.util.List;

import com.travelsoft.cameleo.catalog.data.ContentLayoutComponent;
import com.travelsoft.cameleo.catalog.data.PublishedProduct;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.data.ProductOmnitureStats;

/**
 * <p>Titre : TrackingBuilder.</p>
 * <p>Description : Fill all tracking variables.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Travelsoft</p>
 * @author dusan.spaic
 */
public class ProductOmnitureStatsBuilder extends OmnitureStatsBuilder {


    /**
     * Used in product details page.
     * Fill Omniture tracking object with all needed variable informations.
     *
     * @param context the component's context
     */
    @Override
    public void fillComponentContext(IComponentContext<ContentLayoutComponent> context)  {

        constructOmnitureBaseStats();

        // fill omniture object with infos that will be used on product details page
        PublishedProduct publishedProduct = (PublishedProduct) context.lookup(Constants.Context.PUBLISHED_PRODUCT);
        List<ProductOmnitureStats> productList = new ArrayList<ProductOmnitureStats>();
        ProductOmnitureStats product = new ProductOmnitureStats();
        product.setId(publishedProduct.getCode());
        productList.add(product);
        omnitureStats.setProducts(productList.toArray(new ProductOmnitureStats[productList.size()]));
        omnitureStats.setToCode(publishedProduct.getTechnicalInfo().getTourOperator().getCode());
        context.write(Constants.OmnitureConstants.OMNITURE_STATS, omnitureStats);
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
