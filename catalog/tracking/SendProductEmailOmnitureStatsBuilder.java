/*
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.tracking;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.travelsoft.cameleo.catalog.data.ContentLayoutComponent;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.data.ProductOmnitureStats;

/**
 * <p>Titre : SendProductEmailOmnitureStatsBuilderp.</p>
 * <p>Description : Fill send product email omniture variables.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Travelsoft</p>
 * @author dusan.spaic
 */
public class SendProductEmailOmnitureStatsBuilder extends OmnitureStatsBuilder {


    /**
     * Used in popup send email page.
     * Fill Omniture tracking object with all needed variable informations.
     *
     * @param context the component's context
     */
    @Override
    public void fillComponentContext(IComponentContext<ContentLayoutComponent> context)  {

        constructOmnitureBaseStats();
        HttpServletRequest rq = this.getEnvironment().getRequest();

        String productId = (String) rq.getParameter("pid");
        List<ProductOmnitureStats> productList = new ArrayList<ProductOmnitureStats>();
        ProductOmnitureStats product = new ProductOmnitureStats();
        product.setId(productId);
        productList.add(product);
        omnitureStats.setProducts(productList.toArray(new ProductOmnitureStats[productList.size()]));

        String toCode = (String) rq.getParameter("to");
        omnitureStats.setToCode(toCode);

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
