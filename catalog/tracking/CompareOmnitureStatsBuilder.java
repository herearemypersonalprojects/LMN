/*
 * Created on 18 oct. 2011
 *
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.tracking;

import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import com.travelsoft.cameleo.catalog.data.ContentLayoutComponent;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.data.ProductOmnitureStats;
import com.travelsoft.lastminute.data.SmallProductDisplayable;
import com.travelsoft.lastminute.data.SmallProductDisplayableList;

/**
 * <p>Titre : CompareOmnitureStatsBuilder.</p>
 * <p>Description : Fill all tracking variables used on compare page.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Travelsoft</p>
 * @author dusan.spaic
 */
public class CompareOmnitureStatsBuilder extends OmnitureStatsBuilder {


    /**
     * Used in compare page
     * Fill Omniture tracking object with all needed variable informations.
     *
     * @param context the component's context
     */
    @Override
    public void fillComponentContext(IComponentContext<ContentLayoutComponent> context)  {

        constructOmnitureBaseStats();

        // fill omniture object with infos that will be used on compare page
        Set<String> toCodesSet = new TreeSet<String>();
        SmallProductDisplayableList comparedProducts
            = (SmallProductDisplayableList) context.lookup(Constants.Context.PRODUCT_DISPLAYABLE_LIST);
        SmallProductDisplayable[] spDisplayables = comparedProducts.getSmallProductDisplayables();
        for (int i = 0; i < spDisplayables.length; i++) {
            SmallProductDisplayable spDisplayable = spDisplayables[i];
            ProductOmnitureStats product = new ProductOmnitureStats();
            product.setId(spDisplayable.getId());
            toCodesSet.add(spDisplayable.getToCode());
            omnitureStats.addProducts(product);
        }
        String toCodes = StringUtils.join(toCodesSet, "::");
        omnitureStats.setToCode(toCodes);

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
