/**
 *
 */
package com.travelsoft.lastminute.catalog.seo;
import com.travelsoft.cameleo.catalog.data.Criterion;
import com.travelsoft.cameleo.catalog.data.CriterionValue;
import com.travelsoft.cameleo.catalog.data.Document;
import com.travelsoft.cameleo.catalog.data.MainZone;
import com.travelsoft.cameleo.catalog.data.PageLayoutComponent;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchCriteria;
import com.travelsoft.cameleo.catalog.data.TransverseContents;
import com.travelsoft.cameleo.catalog.data.TransverseContentsSearchResponse;
import com.travelsoft.cameleo.catalog.data.Zone;
import com.travelsoft.cameleo.catalog.data.Zones;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.lastminute.catalog.transContents.AbstractSearchTransContents;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.Util;
import com.travelsoft.lastminute.data.CodeLabel;
import com.travelsoft.lastminute.data.CodeLabels;

/**
 * <p>
 * Titre : SeoProductDataRetriever.
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
public class SeoLinksRetriever extends AbstractSearchTransContents {

    /**
     * Process business logic and produce the data model.
     *
     * @param context
     *            the component's context
     */
    public void fillComponentContext(IComponentContext<PageLayoutComponent> context) {

        PublishedProductSearchCriteria criteriaConstraints = (PublishedProductSearchCriteria) context
                .lookup(Constants.Context.FINAL_CRITERIA_CONSTRAINTS);

        if (criteriaConstraints != null) {
            Criterion[] criterions = criteriaConstraints.getCustomCriteria();

            if (criterions != null && criterions.length > 0) {
                for (Criterion criterion : criterions) {
                    String criterionCode = criterion.getCode();
                    if (Constants.CriterionConstants.CRITERION_DESTIONATION_CODE.equals(criterionCode)) {
                        CriterionValue[] criterionValues = criterion.getValue();
                        String destinationValue = SeoTool.getUniqueDestination(criterionValues);
                        if (destinationValue != null) {
                            TransverseContentsSearchResponse tcRespnse = 
                            		searchTransContentsByDestiantion(
                            				'#' + destinationValue, // '#' pour correspondance exacte uniquement
                            				"seo");

                            if (tcRespnse != null && tcRespnse.getResultsNumber() > 0) {

                                TransverseContents destinationInfoTransContent = tcRespnse.getTransverseContents(0);

                                Document[] documents = destinationInfoTransContent.getDocument();
                                if (documents != null && documents.length > 0) {
                                    Document document = documents[0];
                                    this.addSeoLinksInContext(context, document);
                                }
                            }

                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Adds the seo links data in the context.
     * @param context The context
     * @param document The document
     */
    private void addSeoLinksInContext(IComponentContext<PageLayoutComponent> context, Document document) {
        MainZone seoLinksMainZone = Util.getEditoMainZone(document, "seo");
        if (seoLinksMainZone != null && seoLinksMainZone.getZones() != null) {
            CodeLabels codeLabels = new CodeLabels();
            Zone[] zones = seoLinksMainZone.getZones().getZone();
            for (Zone zone : zones) {
                Zones subZones = zone.getZones();
                if (subZones != null) {
                    Zone subTitleZones = subZones.getZone(0);
                    String title = Util.getZoneContent(subTitleZones);
                    Zone subContentZones = subZones.getZone(1);
                    String content = Util.getZoneContent(subContentZones);
                    CodeLabel codeLabel = new CodeLabel();
                    codeLabel.setCode(content);
                    codeLabel.setLabel(title);
                    codeLabels.addCodeLabel(codeLabel);
                }
            }
            context.write("seoLinks", codeLabels);
        }
    }
}
