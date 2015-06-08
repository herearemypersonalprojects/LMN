/*
r * Created on 24 nov. 2011
 *
 * Copyright Travelsoft 2011</p>
 */
package com.travelsoft.lastminute.catalog.transContents;

import com.travelsoft.cameleo.catalog.data.Criterion;
import com.travelsoft.cameleo.catalog.data.CriterionValue;
import com.travelsoft.cameleo.catalog.data.Document;
import com.travelsoft.cameleo.catalog.data.LayoutComponent;
import com.travelsoft.cameleo.catalog.data.MainZone;
import com.travelsoft.cameleo.catalog.data.PageLayoutComponent;
import com.travelsoft.cameleo.catalog.data.PublishedProduct;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchCriteria;
import com.travelsoft.cameleo.catalog.data.TransverseContents;
import com.travelsoft.cameleo.catalog.data.TransverseContentsSearchResponse;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.Util;

/**
 * <p>Titre : BackgrandRetreiver.</p>
 * Description :
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Company : Travelsoft</p>
 * @author MengMeng
 * @version
 *
 */
public class BackgroundRetreiver extends AbstractSearchTransContents {

    /**
     * Process business logic and produce the data model to search and fill the background in.
     * @param context the component's context
     */
    public void fillComponentContext(IComponentContext<PageLayoutComponent> context) {
        LayoutComponent layout = (LayoutComponent) context.getAssociatedLayout();
        String modelRef = layout.getModelRef().getCode();
        String activeKey = "";
        String destinationValue = "";
        if (modelRef != null && modelRef.indexOf("serp") != -1) {
            activeKey = Constants.Config.RESULTS_BACKGROUND_ACTIVE;
            PublishedProductSearchCriteria criteriaConstraints = (PublishedProductSearchCriteria) context
                    .lookup(Constants.Context.FINAL_CRITERIA_CONSTRAINTS);
            Criterion[] criterions = criteriaConstraints.getCustomCriteria();
            if (criterions != null && criterions.length > 0) {
                for (Criterion criterion : criterions) {
                    String criterionCode = criterion.getCode();
                    if (Constants.CriterionConstants.CRITERION_DESTIONATION_CODE.equals(criterionCode)) {
                        CriterionValue[] criterionValues = criterion.getValue();
                        if (criterionValues != null && criterionValues.length == 1) {
                            destinationValue = criterionValues[0].getCode();
                        }
                        break;
                    }
                }
            }
        } else if (modelRef != null && modelRef.indexOf("product") != -1) {
            activeKey = Constants.Config.PRODUCT_BACKGROUND_ACTIVE;
            PublishedProduct product = (PublishedProduct) context.lookup(Constants.Context.PUBLISHED_PRODUCT);
            if (product != null) {
                destinationValue = Util.getCriterionValue(product,
                        Constants.CriterionConstants.CRITERION_DESTIONATION_CODE, true);
            }
        }
        if (destinationValue != null && !"".equals(destinationValue)) {
            TransverseContentsSearchResponse transContentsResponse = 
            		searchTransContentsByDestiantion(
            				'#' + destinationValue, // '#' pour correspondance exacte uniquement
            				Constants.TransContentsConstants.BACKGROUND_CATEGORY_CODE);

            if (transContentsResponse != null && transContentsResponse.getResultsNumber() > 0) {

                TransverseContents destinationInfoTransContent = transContentsResponse.getTransverseContents(0);

                Document[] documents = destinationInfoTransContent.getDocument();
                if (documents != null && documents.length > 0) {
                    Document document = documents[0];
                    writeContext(context, document, "backgroundPictureUrl");
                    MainZone titleMainZone = Util.getEditoMainZone(document, activeKey);
                    String activeValue = Util.getZoneContent(titleMainZone);
                    if (activeValue != null && "true".equals(activeValue)) {
                        writeContext(context, document, "backgroundPicture");
                    }
                }
            }
        }
    }
}
