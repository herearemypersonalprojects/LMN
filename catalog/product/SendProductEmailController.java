/**
 * SendProductEmailController.
 */
package com.travelsoft.lastminute.catalog.product;

import com.travelsoft.cameleo.catalog.data.PageLayoutComponent;
import com.travelsoft.cameleo.cms.processor.controller.AbstractStructuredController;
import com.travelsoft.cameleo.cms.processor.controller.WebProcessEnvironment;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.Util;

/**
 * Media tag object builder.
 */
public class SendProductEmailController extends AbstractStructuredController<PageLayoutComponent,
    WebProcessEnvironment> {

    /**
     * Process business logic and produce the data model.
     *
     * @param context the component's context
     */
    public void fillComponentContext(IComponentContext<PageLayoutComponent> context) {
        context.write(Constants.Context.CURRENT_PAGE, Constants.Context.PRODUCT_EMAIL_PAGE);
        Util.addBrandContext(this.getEnvironment().getRequest(), context);
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
    public void preview(IComponentContext<PageLayoutComponent> context,
        InjectionData injectionData) throws PageNotFoundException {
    }
}
