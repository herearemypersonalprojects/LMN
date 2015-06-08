/*
 * Copyright Travelsoft, 2013.
 */
package com.travelsoft.lastminute.catalog.product;

import com.travelsoft.cameleo.catalog.data.PageLayoutComponent;
import com.travelsoft.cameleo.cms.processor.controller.AbstractStructuredController;
import com.travelsoft.cameleo.cms.processor.controller.WebProcessEnvironment;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;


/**
 * <p>Title:  CustomerReviewController.</p>
 * Description: The product page controller
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: Travelsoft</p>
 * @author Quoc-Anh Le
 */
public class CustomerReviewController extends
    AbstractStructuredController<PageLayoutComponent, WebProcessEnvironment> {


    @Override
    public void fillComponentContext(IComponentContext<PageLayoutComponent> arg0) throws PageNotFoundException {
        WebProcessEnvironment webEnvironment = this.getEnvironment();
        if (webEnvironment != null) {
            String criteria = webEnvironment.getRequestParameter("criteria");
            String month = webEnvironment.getRequestParameter("month");
            String year = webEnvironment.getRequestParameter("year");
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
