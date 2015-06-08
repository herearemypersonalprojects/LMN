/**
 *
 */
package com.travelsoft.lastminute.catalog.serp;

import com.travelsoft.cameleo.catalog.data.Criterion;
import com.travelsoft.cameleo.catalog.data.CriterionValue;
import com.travelsoft.cameleo.catalog.data.Document;
import com.travelsoft.cameleo.catalog.data.PageLayoutComponent;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchCriteria;
import com.travelsoft.cameleo.catalog.data.TransverseContents;
import com.travelsoft.cameleo.catalog.data.TransverseContentsSearchResponse;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.lastminute.catalog.seo.SeoTool;
import com.travelsoft.lastminute.catalog.transContents.AbstractSearchTransContents;
import com.travelsoft.lastminute.catalog.util.Constants;

/**
 * <p>Titre : FillDestinationInfo.</p>
 * Description :
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Company : Travelsoft</p>
 * @author MengMeng
 * @version
 *
 */
public class SearchAndFillDestinationInfo extends AbstractSearchTransContents {

    /**
     * Process business logic and produce the data model to search and fill the destination info.
     * @param context the component's context
     */
    @Override
    public void fillComponentContext(IComponentContext<PageLayoutComponent> context) {
        PublishedProductSearchCriteria criteriaConstraints = (PublishedProductSearchCriteria) context
                .lookup(Constants.Context.FINAL_CRITERIA_CONSTRAINTS);
        Criterion[] criterions = criteriaConstraints.getCustomCriteria();
        boolean showDestinationInfo = false;
        if (criterions != null && criterions.length > 0) {
            for (Criterion criterion : criterions) {
                String criterionCode = criterion.getCode();
                if (Constants.CriterionConstants.CRITERION_DESTIONATION_CODE.equals(criterionCode)) {
                    CriterionValue[] criterionValues = criterion.getValue();

                    String destinationValue = SeoTool.getUniqueDestination(criterionValues);
                    if (destinationValue != null) {
                        TransverseContentsSearchResponse transContentsResponse = 
                        		searchTransContentsByDestiantion(
                        				'#' + destinationValue, // '#' pour correspondance exacte uniquement
                        				Constants.TransContentsConstants.DESTINATION_INFO_CATEGORY_CODE);

                        if (transContentsResponse != null && transContentsResponse.getResultsNumber() > 0) {

                            TransverseContents destinationInfoTransContent = transContentsResponse
                                .getTransverseContents(0);

                            showDestinationInfo = true;
                            Document[] documents = destinationInfoTransContent.getDocument();
                            if (documents != null && documents.length > 0) {
                                Document document = documents[0];
                                writeContext(context, document, "title");
                                writeContext(context, document, "mainPicture");
                                writeContext(context, document, "resume");
                                writeContext(context, document, "flag");
                                writeContext(context, document, "url");
                                writeContext(context, document, "backgroundPicture");
                                writeContext(context, document, "backgroundPictureUrl");
                            }
                        }
                    }
                    break;
                }
            }
        }
        context.write("showDestinationInfo", showDestinationInfo);
    }


}
