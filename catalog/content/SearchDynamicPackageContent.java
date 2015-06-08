/*
 * Created on 10 nov. 2011
 *
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.content;

import com.travelsoft.cameleo.catalog.data.Criterion;
import com.travelsoft.cameleo.catalog.data.CriterionValue;
import com.travelsoft.cameleo.catalog.data.Document;
import com.travelsoft.cameleo.catalog.data.MainZone;
import com.travelsoft.cameleo.catalog.data.PageLayoutComponent;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchCriteria;
import com.travelsoft.cameleo.catalog.data.TransverseContents;
import com.travelsoft.cameleo.catalog.data.TransverseContentsSearchResponse;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.transContents.AbstractSearchTransContents;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.Util;

/**
 * <p>
 * Titre : SearchDynamicPackageContent.
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
public class SearchDynamicPackageContent extends AbstractSearchTransContents {

    /**
     * Process business logic and produce the data model to fill the product
     * description.
     *
     * @param context
     *            the component's context
     */
    @Override
    public void fillComponentContext(
            IComponentContext<PageLayoutComponent> context) {

        // TODO Refactor this method
        PublishedProductSearchCriteria criteriaConstraints = (PublishedProductSearchCriteria) context
                .lookup(Constants.Context.FINAL_CRITERIA_CONSTRAINTS);
        Criterion[] criterions = criteriaConstraints.getCustomCriteria();
        if (criterions != null && criterions.length > 0) {
            for (Criterion criterion : criterions) {
                String criterionCode = criterion.getCode();
                if (Constants.CriterionConstants.CRITERION_DESTIONATION_CODE
                        .equals(criterionCode)) {
                    CriterionValue[] criterionValues = criterion.getValue();
                    if (criterionValues != null && criterionValues.length == 1) {
                        String destinationValue = criterionValues[0].getCode();
                        String transContentsCriteria = Util
                                .appendString(
                                        Constants.TransContentsConstants.CATEGORY_CODE,
                                        Constants.CriterionConstants.CRITERION_EQUAL,
                                        Constants.TransContentsConstants.CRITERIA_DP_CODE,
                                        Constants.CriterionConstants.CRITERION_SEPARATOR,
                                        Constants.CriterionConstants.CRITERION_CUSTOMER_FIELD,
                                        Constants.CriterionConstants.CRITERION_DESTIONATION_CODE,
                                        Constants.CriterionConstants.CRITERION_EQUAL,
                                        destinationValue,
                                        Constants.CriterionConstants.CRITERION_SEPARATOR,
                                        "fin",
                                        Constants.CriterionConstants.CRITERION_EQUAL,
                                        "0",
                                        Constants.CriterionConstants.CRITERION_SEPARATOR,
                                        "lin",
                                        Constants.CriterionConstants.CRITERION_EQUAL,
                                        "1");

                        TransverseContentsSearchResponse transContentsResponse = this
                                .searchTransContent(transContentsCriteria);

                        if (transContentsResponse != null && transContentsResponse.getResultsNumber() > 0) {

                            TransverseContents transverseContent = transContentsResponse.getTransverseContents(0);

                            Document[] documents = transverseContent.getDocument();
                            if (documents != null && documents.length > 0) {
                                Document document = documents[0];
                                MainZone dpLink = Util.getEditoMainZone(document, "dpLink");
                                if (dpLink != null) {
                                    context.write("dpLink", Util.getZoneContent(dpLink));
                                }
                                MainZone dpContent = Util.getEditoMainZone(document, "dpContent");
                                if (dpContent != null) {
                                    context.write("dpContent", Util.getZoneContent(dpContent));
                                }

                                MainZone dpMinProduct = Util.getEditoMainZone(document, "dpMinProduct");
                                if (dpMinProduct != null) {
                                    String strDPMinProduct = Util.getZoneContent(dpMinProduct);
                                    try {
                                        int numDPMinProduct = Integer.parseInt(strDPMinProduct);
                                        Integer resultsNumber  = (Integer) context.lookup(
                                            Constants.Context.RESULTS_NUMBER);
                                        if (resultsNumber < numDPMinProduct) {
                                            context.write("displayDPBlock", "true");
                                        } else {
                                            context.write("displayDPBlock", "false");
                                        }
                                    } catch (NumberFormatException nfe) {
                                        context.write("displayDPBlock", "true");
                                    }
                                } else {
                                    context.write("displayDPBlock", "true");
                                }
                            }
                        }
                    }
                    break;
                }
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
    public void preview(IComponentContext<PageLayoutComponent> context,
            InjectionData injectionData) throws PageNotFoundException {

    }
}
