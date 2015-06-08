/*
 * Created on 9 nov. 2011
 *
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.product;

import com.travelsoft.cameleo.catalog.data.CriterionValue;
import com.travelsoft.cameleo.catalog.data.Document;
import com.travelsoft.cameleo.catalog.data.MainZone;
import com.travelsoft.cameleo.catalog.data.PageLayoutComponent;
import com.travelsoft.cameleo.catalog.data.PublishedProduct;
import com.travelsoft.cameleo.catalog.data.TransverseContents;
import com.travelsoft.cameleo.catalog.data.TransverseContentsSearchResponse;
import com.travelsoft.cameleo.catalog.data.Zone;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.lastminute.catalog.seo.SeoTool;
import com.travelsoft.lastminute.catalog.transContents.AbstractSearchTransContents;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.Util;

/**
 * <p>Titre : ProductDataRetriever.</p>
 * <p>Description : .</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Travelsoft</p>
 * @author zouhair.mechbal
 */
public class ProductDataRetriever extends AbstractSearchTransContents {

    /**
     * Process business logic and produce the data model to fill the product description.
     * @param context the component's context
     */
    public void fillComponentContext(IComponentContext<PageLayoutComponent> context) {
        PublishedProduct product = (PublishedProduct) context.lookup(Constants.Context.PUBLISHED_PRODUCT);
        this.retrieveWheatherInfoContent(context, product);
        this.retrieveToInfo(context, product);
    }

    /**
     * Retrieves the weather info content.
     * @param context The context
     * @param product The product
     */
    private void retrieveWheatherInfoContent(IComponentContext<PageLayoutComponent> context, PublishedProduct product) {
        CriterionValue[] criterionValues = Util.getCriterionValues(product, Constants.CriterionConstants.CRITERION_DESTIONATION_CODE);
        if (criterionValues != null)  {
        	String destinationCode = SeoTool.getUniqueDestination(criterionValues);
            if (destinationCode != null) {
                String transContentsCriteria = Util.appendString(
                        Constants.TransContentsConstants.CATEGORY_CODE,
                        Constants.CriterionConstants.CRITERION_EQUAL,
                        "meteo",
                        Constants.CriterionConstants.CRITERION_SEPARATOR,
                        Constants.CriterionConstants.CRITERION_CUSTOMER_FIELD,
                        Constants.CriterionConstants.CRITERION_DESTIONATION_CODE,
                        Constants.CriterionConstants.CRITERION_EQUAL, destinationCode,
                        Constants.CriterionConstants.CRITERION_SEPARATOR,
                        Constants.CriterionConstants.CRITERION_SEPARATOR, "fin",
                        Constants.CriterionConstants.CRITERION_EQUAL, "0",
                        Constants.CriterionConstants.CRITERION_SEPARATOR, "lin",
                        Constants.CriterionConstants.CRITERION_EQUAL, "1");
                TransverseContentsSearchResponse transContentsResponse = searchTransContent(transContentsCriteria);
                if (transContentsResponse != null && transContentsResponse.getTransverseContents() != null
                        && transContentsResponse.getTransverseContents().length > 0) {
                    TransverseContents transverseContent =  transContentsResponse.getTransverseContents()[0];
                    Document[] documents = transverseContent.getDocument();
                    if (documents != null && documents.length > 0) {
                        Document document = documents[0];
                        MainZone urlMainZone = Util.getEditoMainZone(document, "mainPicture");
                        if (urlMainZone != null) {
                            context.write("wheatherImage", Util.getZoneContent(urlMainZone));
                        }
                    }
                }
            }
        }

    }

    /**
     * Retrieves the to information from the transverse content.
     * @param product The product
     * @param context The context
     */
    private void retrieveToInfo(IComponentContext<PageLayoutComponent> context, PublishedProduct product) {
        String toCode = product.getTechnicalInfo().getTourOperator().getCode();
        String transContentsCriteria = Util.appendString(Constants.TransContentsConstants.CATEGORY_CODE,
            Constants.CriterionConstants.CRITERION_EQUAL, Constants.TransContentsConstants.TO_CATEGORY_CODE,
            Constants.CriterionConstants.CRITERION_SEPARATOR, Constants.TransContentsConstants.CRITERIA_TO_NAME_CODE,
            Constants.CriterionConstants.CRITERION_EQUAL, toCode, Constants.CriterionConstants.CRITERION_SEPARATOR,
            Constants.CriterionConstants.CRITERION_SEPARATOR, "fin", Constants.CriterionConstants.CRITERION_EQUAL, "0",
            Constants.CriterionConstants.CRITERION_SEPARATOR, "lin", Constants.CriterionConstants.CRITERION_EQUAL, "1");
        TransverseContentsSearchResponse transContentsResponse = searchTransContent(transContentsCriteria);
        if (transContentsResponse != null && transContentsResponse.getResultsNumber() > 0) {
            TransverseContents toContent = transContentsResponse.getTransverseContents(0);
            if (toContent != null) {
                Document[] documents = toContent.getDocument();
                if (documents != null && documents.length > 0) {
                    this.retrieveToInfo(documents[0], context);
                }
            }
        }
    }

    /**
     * Retrieves to information from the document and adds to data in the context.
     * @param document The document
     * @param context The context
     */
    private void retrieveToInfo(Document document, IComponentContext<PageLayoutComponent> context) {
        MainZone toMainZone = Util.getEditoMainZone(document, "to");
        if (toMainZone != null && toMainZone.getZones() != null) {

            Zone[] zoneArray = toMainZone.getZones().getZone();
            for (Zone zone : zoneArray) {
                String toName = Util.getSubZoneContent(zone, "name");
                if (toName != null && !"".equals(toName)) {
                    context.write("toName", toName);
                }

                String toAddress = Util.getSubZoneContent(zone, "address");
                if (toAddress != null && !"".equals(toAddress)) {
                    context.write("toAddress", toAddress);
                }

                String toPostaleCode = Util.getSubZoneContent(zone, "postalCode");
                if (toPostaleCode != null && !"".equals(toPostaleCode)) {
                    context.write("toPostaleCode", toPostaleCode);
                }

                String toCity = Util.getSubZoneContent(zone, "city");
                if (toCity != null && !"".equals(toCity)) {
                    context.write("toCity", toCity);
                }

                String toCountry = Util.getSubZoneContent(zone, "country");
                if (toCountry != null && !"".equals(toCountry)) {
                    context.write("toCountry", toCountry);
                }
            }
        }
    }
}
