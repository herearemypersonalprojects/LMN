/**
 *
 */
package com.travelsoft.lastminute.catalog.transContents;

import java.util.Date;

import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.data.Document;
import com.travelsoft.cameleo.catalog.data.MainZone;
import com.travelsoft.cameleo.catalog.data.PageLayoutComponent;
import com.travelsoft.cameleo.catalog.data.TransverseContentsSearchCriteria;
import com.travelsoft.cameleo.catalog.data.TransverseContentsSearchResponse;
import com.travelsoft.cameleo.catalog.data.types.TransverseContentsPublicationStatusDef;
import com.travelsoft.cameleo.catalog.interfaces.ServicesFactory;
import com.travelsoft.cameleo.catalog.interfaces.exceptions.TechnicalException;
import com.travelsoft.cameleo.catalog.interfaces.transversecontents.TransverseContentsSearchServicesInterface;
import com.travelsoft.cameleo.catalog.taglib.constant.Constant;
import com.travelsoft.cameleo.catalog.taglib.utils.Utils;
import com.travelsoft.cameleo.cms.processor.controller.AbstractStructuredController;
import com.travelsoft.cameleo.cms.processor.controller.WebProcessEnvironment;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.Util;

/**
 * <p>Titre : AbstractSearchTransContents.</p>
 * Description :
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Company : Travelsoft</p>
 * @author MengMeng
 * @version
 *
 */
public abstract class AbstractSearchTransContents extends
    AbstractStructuredController<PageLayoutComponent, WebProcessEnvironment> {

    /** Logger. */
    private static Logger logger = Logger.getLogger(AbstractSearchTransContents.class);

    /**
     * Search the given type transverse content by the given criteria.
     * @param criteria the given criteria retrieved by search engine
     * @return transverseContentsSearchResponse transverse content response
     */
    protected TransverseContentsSearchResponse searchTransContent(String criteria) {
        TransverseContentsSearchResponse transverseContentsSearchResponse = null;
        if (criteria != null && !"".equals(criteria)) {
            if (logger.isDebugEnabled()) {
                logger.debug("start to search transverse content by citeria :" + criteria);
            }
            try {
                TransverseContentsSearchCriteria criteriasConstraints = Utils.completeTransverseContentsSearchCriteria(
                        criteria, Constants.CAMELEO_CACHE_MANAGER, Constant.CRITERIA_SEPARATOR,
                        Constant.CRITERION_SEPARATOR, Constant.OPTIONS_SEPARATOR);
                criteriasConstraints.setPublicationStatus(TransverseContentsPublicationStatusDef.PUBLISHED);
                criteriasConstraints.setInTrash(false);
                org.exolab.castor.types.Date todayCastorDate = new org.exolab.castor.types.Date(new Date());
                criteriasConstraints.setMinValidityDate(todayCastorDate);
                criteriasConstraints.setMaxValidityDate(todayCastorDate);
                TransverseContentsSearchServicesInterface transverseContent = ServicesFactory
                        .getTransverseContentsSearchServices();
                transverseContentsSearchResponse = transverseContent.searchTransverseContents(criteriasConstraints,
                        Constants.CAMELEO_CACHE_MANAGER);
                if (logger.isDebugEnabled()) {
                    logger.debug("TransContentsResponse: " + transverseContentsSearchResponse.convertToString());
                }
            } catch (TechnicalException e) {
                logger.error("Technical Exception calling searchTransverseContents()", e);
            }
        }
        return transverseContentsSearchResponse;
    }

    /**
     * Write the destination info in the context by given document.
     * @param context the web context
     * @param document the transverse content's document
     * @param attrName the attribute name in context
     */
    protected void writeContext(IComponentContext<PageLayoutComponent> context, Document document, String attrName) {
        MainZone titleMainZone = Util.getEditoMainZone(document, attrName);
        String title = Util.getZoneContent(titleMainZone);
        context.write(attrName, title);
    }

    /**
     * Search transverse contents by destination value.
     * @param destinationValue the destination value
     * @param transContentsCode the transverse contents category code
     * @return transContentsResponse the transContentsResponse
     */
    protected TransverseContentsSearchResponse searchTransContentsByDestiantion(String destinationValue,
            String transContentsCode) {
        String transContentsCriteria = Util.appendString(Constants.TransContentsConstants.CATEGORY_CODE,
                Constants.CriterionConstants.CRITERION_EQUAL, transContentsCode,
                Constants.CriterionConstants.CRITERION_SEPARATOR,
                Constants.CriterionConstants.CRITERION_CUSTOMER_FIELD,
                Constants.CriterionConstants.CRITERION_DESTIONATION_CODE, Constants.CriterionConstants.CRITERION_EQUAL,
                destinationValue, Constants.CriterionConstants.CRITERION_SEPARATOR, "fin",
                Constants.CriterionConstants.CRITERION_EQUAL, "0", Constants.CriterionConstants.CRITERION_SEPARATOR,
                "lin", Constants.CriterionConstants.CRITERION_EQUAL, "1");

        TransverseContentsSearchResponse transContentsResponse = this.searchTransContent(transContentsCriteria);
        return transContentsResponse;
    }

    /**
     * Produces a data model mock.
     *
     * @param context the component's context
     * @param injectionData the injection data
     * @throws PageNotFoundException If the page is not found
     */
    public void preview(IComponentContext<PageLayoutComponent> context, InjectionData injectionData)
            throws PageNotFoundException {

    }
}
