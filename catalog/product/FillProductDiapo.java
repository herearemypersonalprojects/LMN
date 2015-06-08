/**
 *
 */
package com.travelsoft.lastminute.catalog.product;

import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.data.Document;
import com.travelsoft.cameleo.catalog.data.MainZone;
import com.travelsoft.cameleo.catalog.data.PageLayoutComponent;
import com.travelsoft.cameleo.catalog.data.PublishedProduct;
import com.travelsoft.cameleo.catalog.data.Zone;
import com.travelsoft.cameleo.catalog.data.Zones;
import com.travelsoft.cameleo.cms.processor.controller.AbstractStructuredController;
import com.travelsoft.cameleo.cms.processor.controller.WebProcessEnvironment;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.Util;
import com.travelsoft.lastminute.data.ProductSupplementDisplayable;

/**
 * <p>Titre : FillProductDiapo.</p>
 * Description :
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Company : Travelsoft</p>
 * @author MengMeng
 * @version
 *
 */
public class FillProductDiapo extends AbstractStructuredController<PageLayoutComponent, WebProcessEnvironment> {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(FillProductDiapo.class);

    /**
     * Process business logic and produce the data model to fill the product diaporama.
     * @param context the component's context
     */
    @Override
    public void fillComponentContext(IComponentContext<PageLayoutComponent> context) {
        PublishedProduct product = (PublishedProduct) context.lookup(Constants.Context.PUBLISHED_PRODUCT);
        ProductSupplementDisplayable productSupDisplayable = new ProductSupplementDisplayable();
        Document edito = product.getEdito();
        String[] diapoUrlList = null;
        if (edito != null) {
            MainZone diapoMainZone = Util.getEditoMainZone(edito, "diaporama");
            if (diapoMainZone != null) {
                Zones diapoZones = diapoMainZone.getZones();
                if (diapoZones != null) {
                    Zone[] diapoZoneTable = diapoZones.getZone();
                    if (diapoZoneTable != null && diapoZoneTable.length > 0) {
                        diapoUrlList = new String[diapoZoneTable.length];
                        int count = 0;
                        for (Zone zone : diapoZoneTable) {
                            String diapoUrl = zone.getValue().getContent();
                            diapoUrlList[count] = diapoUrl;
                            count++;
                        }
                        productSupDisplayable.setDiaporama(diapoUrlList);
                    }
                }
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("display the product supplement displayable product : "
                    + productSupDisplayable.convertToString());
        }
        context.write(Constants.Context.PRODUCT_SUPPLEMENT_DISPLAYABLE, productSupDisplayable);
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
