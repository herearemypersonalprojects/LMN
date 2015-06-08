/**
 *
 */
package com.travelsoft.lastminute.catalog.product;

import java.util.List;

import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.data.City;
import com.travelsoft.cameleo.catalog.data.Country;
import com.travelsoft.cameleo.catalog.data.Document;
import com.travelsoft.cameleo.catalog.data.MainZone;
import com.travelsoft.cameleo.catalog.data.PageLayoutComponent;
import com.travelsoft.cameleo.catalog.data.PublishedProduct;
import com.travelsoft.cameleo.catalog.data.TechnicalInfo;
import com.travelsoft.cameleo.catalog.data.TransverseContents;
import com.travelsoft.cameleo.catalog.data.TransverseContentsSearchResponse;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.lastminute.catalog.transContents.AbstractSearchTransContents;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.Util;
import com.travelsoft.lastminute.data.Paragraph;
import com.travelsoft.lastminute.data.ProductSupplementDisplayable;
import com.travelsoft.lastminute.data.SmallProductDisplayable;

/**
 * <p>Titre : FillProductDescription.</p>
 * Description :
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Company : Travelsoft</p>
 * @author MengMeng
 * @version
 *
 */
public class FillProductDescription extends AbstractSearchTransContents {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(FillProductDescription.class);

    /**
     * Process business logic and produce the data model to fill the product description.
     * @param context the component's context
     */
    @Override
    public void fillComponentContext(IComponentContext<PageLayoutComponent> context) {
        PublishedProduct product = (PublishedProduct) context.lookup(Constants.Context.PUBLISHED_PRODUCT);
        ProductSupplementDisplayable productSupDisplayable = constructProductSupplementEdito(product);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("display the product supplement displayable product : "
                    + productSupDisplayable.convertToString());
        }
        // Gets the destination country
        String destinationCode = Util.getCriterionValue(product,
                Constants.CriterionConstants.CRITERION_DESTIONATION_CODE, true);
        if (destinationCode != null && destinationCode.split(Constants.Common.CRITERION_SEPARATOR).length > 0) {
            destinationCode = destinationCode.split(Constants.Common.CRITERION_SEPARATOR)[0];
        }

        this.searchPassengersInfo(product, productSupDisplayable, destinationCode);
        this.searchFormality(product, productSupDisplayable, destinationCode);
        this.searchFlightsInfo(product, productSupDisplayable);

        fillEquipementPicto(product, productSupDisplayable);

        context.write(Constants.Context.PRODUCT_SUPPLEMENT_DISPLAYABLE, productSupDisplayable);
        SmallProductDisplayable productDisplayable = (SmallProductDisplayable) context
                .lookup(Constants.Context.SMALL_PRODUCT_DISPLAYABLE);
        context.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, productDisplayable);
    }

    /**
     *
     * Search formality information from transverse content.
     * @param product The product
     * @param productSupDisplayable The ProductSupDisplayable instance object
     * @param destinationCountryCode The country code
     */
    private void searchFormality(PublishedProduct product, ProductSupplementDisplayable productSupDisplayable,
            String destinationCountryCode) {
        String transContentsCriteria = Util.appendString(
                Constants.TransContentsConstants.CATEGORY_CODE,
                Constants.CriterionConstants.CRITERION_EQUAL,
                Constants.TransContentsConstants.FORMALITY_CATEGORY_CODE,
                Constants.CriterionConstants.CRITERION_SEPARATOR,
                Constants.CriterionConstants.CRITERION_CUSTOMER_FIELD,
                Constants.CriterionConstants.CRITERION_DESTIONATION_CODE,
                Constants.CriterionConstants.CRITERION_EQUAL, destinationCountryCode,
                Constants.CriterionConstants.CRITERION_SEPARATOR, "fin",
                Constants.CriterionConstants.CRITERION_EQUAL, "0",
                Constants.CriterionConstants.CRITERION_SEPARATOR, "lin",
                Constants.CriterionConstants.CRITERION_EQUAL, "1");

        TransverseContentsSearchResponse transContentsResponse = searchTransContent(transContentsCriteria);
        if (transContentsResponse != null && transContentsResponse.getResultsNumber() > 0) {
            TransverseContents passengerInfoTransContent = transContentsResponse.getTransverseContents(0);
            if (passengerInfoTransContent != null) {
                Document[] documents = passengerInfoTransContent.getDocument();
                if (documents != null && documents.length > 0) {
                    Paragraph[] paragraphs = this.getProductSupplementParagraphs("formalite", documents[0]);
                    if (paragraphs != null) {
                        productSupDisplayable.setFormalityInfo(paragraphs);
                    }
                }
            }
        }
    }

    /**
     * Search flights information from transverse content.
     *
     * @param product The product
     * @param productSupDisplayable The <code>ProductSupDisplayable</code> instance object
     */
    private void searchFlightsInfo(PublishedProduct product, ProductSupplementDisplayable productSupDisplayable) {
        String tode = null;
        String cityCode = null;
        TechnicalInfo techInfo = product.getTechnicalInfo();
        if (techInfo != null && techInfo.getTourOperator() != null) {
            tode = techInfo.getTourOperator().getCode();
        }

        Country[] countries = product.getTechnicalInfo().getCountry();
        if (countries != null && countries.length > 0) {
            City[] cities = countries[0].getCity();
            if (cities != null && cities.length > 0) {
                cityCode = cities[0].getCode();
            }
        }

        String transContentsCriteria = Util.appendString(Constants.TransContentsConstants.CATEGORY_CODE,
                Constants.CriterionConstants.CRITERION_EQUAL, Constants.TransContentsConstants.FLIGHTS_CATEGORY_CODE,
                Constants.CriterionConstants.CRITERION_SEPARATOR,
                Constants.TransContentsConstants.CRITERIA_TO_NAME_CODE,
                Constants.CriterionConstants.CRITERION_EQUAL,
                tode, Constants.CriterionConstants.CRITERION_SEPARATOR,
                Constants.CriterionConstants.CRITERION_SEPARATOR,
                Constants.TransContentsConstants.CRITERIA_CITY_CODE,
                Constants.CriterionConstants.CRITERION_EQUAL,
                cityCode,
                Constants.CriterionConstants.CRITERION_SEPARATOR, "fin",
                Constants.CriterionConstants.CRITERION_EQUAL,
                "0", Constants.CriterionConstants.CRITERION_SEPARATOR, "lin",
                Constants.CriterionConstants.CRITERION_EQUAL, "1");

        TransverseContentsSearchResponse transContentsResponse = searchTransContent(transContentsCriteria);
        if (transContentsResponse != null && transContentsResponse.getResultsNumber() > 0) {
            TransverseContents passengerInfoTransContent = transContentsResponse.getTransverseContents(0);
            if (passengerInfoTransContent != null) {
                Document[] documents = passengerInfoTransContent.getDocument();
                if (documents != null && documents.length > 0) {
                    Paragraph[] paragraphs = this.getProductSupplementParagraphs("flights", documents[0]);
                    if (paragraphs != null) {
                        productSupDisplayable.setFlightsInfo(paragraphs);
                    }
                }
            }
        }
    }

    /**
     * Search and fill the passengers information from transverse content.
     *
     * @param product The product
     * @param destinationCode The country code
     * @param productSupDisplayable The <code>ProductSupDisplayable</code> instance object
     * @return transverseContents The <code>TransverseContent</code> instance object
     */
    private TransverseContents searchPassengersInfo(PublishedProduct product,
            ProductSupplementDisplayable productSupDisplayable, String destinationCode) {

        String transContentsCriteria = Util.appendString(Constants.TransContentsConstants.CATEGORY_CODE,
                Constants.CriterionConstants.CRITERION_EQUAL,
                Constants.TransContentsConstants.PASSENGER_INFO_CATEGORY_CODE,
                Constants.CriterionConstants.CRITERION_SEPARATOR,
                Constants.CriterionConstants.CRITERION_CUSTOMER_FIELD,
                Constants.CriterionConstants.CRITERION_DESTIONATION_CODE, Constants.CriterionConstants.CRITERION_EQUAL,
                destinationCode, Constants.CriterionConstants.CRITERION_SEPARATOR,
                Constants.CriterionConstants.CRITERION_SEPARATOR, "fin", Constants.CriterionConstants.CRITERION_EQUAL,
                "0", Constants.CriterionConstants.CRITERION_SEPARATOR, "lin",
                Constants.CriterionConstants.CRITERION_EQUAL, "1");

        TransverseContentsSearchResponse transContentsResponse = searchTransContent(transContentsCriteria);
        if (transContentsResponse != null && transContentsResponse.getResultsNumber() > 0) {
            TransverseContents passengerInfoTransContent = transContentsResponse.getTransverseContents(0);
            if (passengerInfoTransContent != null) {
                Document[] documents = passengerInfoTransContent.getDocument();
                if (documents != null && documents.length > 0) {
                    Paragraph[] paragraphs = this.getProductSupplementParagraphs("passengerInfo", documents[0]);
                    if (paragraphs != null) {
                        productSupDisplayable.setPassengerInfo(paragraphs);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Construct the supplement displayable product.
     * @param product the published product
     * @return productSup ProductSupplementDisplayable
     */
    private ProductSupplementDisplayable constructProductSupplementEdito(PublishedProduct product) {
        ProductSupplementDisplayable productSup = new ProductSupplementDisplayable();
        Document edito = product.getEdito();
        if (edito != null) {
            Paragraph[] paragraphs = getProductSupplementParagraphs("descriptive", edito);
            if (paragraphs != null) {
                productSup.setDescriptions(paragraphs);
            }

            paragraphs = getProductSupplementParagraphs("include", edito);
            if (paragraphs != null) {
                productSup.setIncludes(paragraphs);
            }


            paragraphs = getProductSupplementParagraphs("expertOpinion", edito);
            if (paragraphs != null) {
                productSup.setExperts(paragraphs);
            }

            paragraphs = getProductSupplementParagraphs("information", edito);
            if (paragraphs != null) {
                productSup.setInformation(paragraphs);
            }
        }
        return productSup;
    }

    /**
     * Get paragraphs for product supplement by main zone name.
     * @param mainZoneName the main zone name
     * @param edito the document from product
     * @return paragraphs the paragraphs
     */
    private Paragraph[] getProductSupplementParagraphs(String mainZoneName, Document edito) {
        MainZone mainZone = Util.getEditoMainZone(edito, mainZoneName);
        Paragraph[] paragraphs = Util.getParagraphs(mainZone);
        boolean hasContent = Util.hasContentInParagraphTable(paragraphs);
        if (hasContent) {
            return paragraphs;
        }
        return null;
    }

    /**
     * fill product equipement picto.
     * @param product the PublishedProduct
     * @param productSupDisplayable ProductSupplementDisplayable
     */
    private void fillEquipementPicto(PublishedProduct product,
            ProductSupplementDisplayable productSupDisplayable) {
        List<String> equipementPictoCodes = Util.getCriterionValueLabelList(product,
                Constants.CriterionConstants.EQUIPEMENT_PICTO, true);
        List<String> equipementPictoLabels = Util.getCriterionValueLabelList(product,
                Constants.CriterionConstants.EQUIPEMENT_PICTO, false);
        if (equipementPictoCodes != null && equipementPictoCodes.size() > 0) {
            String mediaLibraryPath = Util.getMedialibraryPathByFileName(Constants.Config.EQUIPEMENT_FILE_NAME);
            int index = 0;
            for (String pictoCode : equipementPictoCodes) {
                String pictoImageUrl = mediaLibraryPath + pictoCode + Constants.Common.IMAGE_FORMAT_GIF;
                String pictoLable = equipementPictoLabels.get(index);
                com.travelsoft.lastminute.data.CodeLabel picto = new com.travelsoft.lastminute.data.CodeLabel();
                picto.setCode(pictoImageUrl);
                picto.setLabel(pictoLable);
                productSupDisplayable.addEquipement(picto);
                index++;
            }
        }
    }
}
