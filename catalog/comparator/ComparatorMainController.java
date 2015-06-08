/*
 * Created on 12 oct. 2011
 *
 * Copyright Travelsoft 2011</p>
 */
package com.travelsoft.lastminute.catalog.comparator;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;

import org.apache.log4j.Logger;
import org.exolab.castor.types.Date;

import com.travelsoft.cameleo.catalog.data.City;
import com.travelsoft.cameleo.catalog.data.Country;
import com.travelsoft.cameleo.catalog.data.Disponibility;
import com.travelsoft.cameleo.catalog.data.Document;
import com.travelsoft.cameleo.catalog.data.MainZone;
import com.travelsoft.cameleo.catalog.data.PageLayoutComponent;
import com.travelsoft.cameleo.catalog.data.PublishedProduct;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchCriteria;
import com.travelsoft.cameleo.catalog.interfaces.ServicesFactory;
import com.travelsoft.cameleo.catalog.interfaces.exceptions.TechnicalException;
import com.travelsoft.cameleo.catalog.interfaces.search.PublishedProductSearchServicesInterface;
import com.travelsoft.cameleo.cms.processor.controller.WebProcessEnvironment;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.product.AbstractFillProduct;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.Util;
import com.travelsoft.lastminute.data.CompareProductDisplayable;
import com.travelsoft.lastminute.data.CompareProductDisplayableList;
import com.travelsoft.lastminute.data.MediaTagTrackingStats;
import com.travelsoft.lastminute.data.Paragraph;
import com.travelsoft.lastminute.data.SmallProductDisplayable;
import com.travelsoft.lastminute.data.SmallProductDisplayableList;
import com.travelsoft.nucleus.cache.generic.CacheManager;
import com.travelsoft.nucleus.cache.jboss.implementations.JbossCacheManagerFactory;

/**
 * <p>Titre : ComparatorMainController.</p>
 * Description :
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Company : Travelsoft</p>
 * @author MengMeng
 * @version
 *
 */
public class ComparatorMainController extends AbstractFillProduct {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(ComparatorMainController.class);

    /**
     * Process business logic and produce the data model to retrieve the products cookie for the comparator.
     * @param context the component's context
     */
    @Override
    public void fillComponentContext(IComponentContext<PageLayoutComponent> context) {
        WebProcessEnvironment webEnvironment = this.getEnvironment();
        int index = 0;
        if (webEnvironment != null) {

            // set new mediaTagStats object that will be filled only once for both tags in the page
            MediaTagTrackingStats mediaTagTrackingData = new MediaTagTrackingStats();
            context.write("mediaTagStats", mediaTagTrackingData);

            Cookie comparatorCookie = Util.getCookie(webEnvironment, Constants.Comparator.COOKIE_NAME);
            if (comparatorCookie != null) {
                List<String> productsId = retriveProductsId(comparatorCookie);
                if (productsId != null && productsId.size() > 0) {
                    PublishedProductSearchCriteria productSearchCriteria = Util.retrieveSearchCriteria(context,
                            "services.comparator.searchCriteriaById");
                    CacheManager cacheManager = JbossCacheManagerFactory
                            .getCacheManager(Constants.Common.CAMELEO_CACHE);
                    PublishedProductSearchServicesInterface productService = ServicesFactory
                    .getPublishedProductSearchServices();
                    List<SmallProductDisplayable> productDisplayList = new ArrayList<SmallProductDisplayable>();
                    List<CompareProductDisplayable> compareDisplayList = new ArrayList<CompareProductDisplayable>();

                    for (String pid : productsId) {
                        PublishedProduct product = null;
                        try {
                            product = productService.getPublishedProduct(pid,
                                    productSearchCriteria, cacheManager);

                        } catch (TechnicalException e) {
                            LOGGER.error("can not get the product by id :" + pid, e);
                        }
                        if (product != null) {
                            SmallProductDisplayable productDisplayable = constructProductDisplayable(product, context);
                            productDisplayable.setParams(String.valueOf(++index));
                            CompareProductDisplayable compareProductDisplayable = constructCompareDisplayable(product);
                            compareProductDisplayable.setIndex(String.valueOf(index));

                            productDisplayList.add(productDisplayable);
                            compareDisplayList.add(compareProductDisplayable);
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("The product " + product.getCode() + " is in comparator.");
                            }
                        }
                    }
                    if (productDisplayList != null && productDisplayList.size() > 0) {
                        SmallProductDisplayable[] productDisplayTable = new SmallProductDisplayable[productDisplayList
                                .size()];
                        CompareProductDisplayable[] compareTable = new CompareProductDisplayable[productDisplayList
                                .size()];
                        for (int i = 0; i < productDisplayList.size(); i++) {
                            productDisplayTable[i] = productDisplayList.get(i);
                            compareTable[i] = compareDisplayList.get(i);
                        }
                        SmallProductDisplayableList productDisplayableList = new SmallProductDisplayableList();
                        productDisplayableList.setSmallProductDisplayables(productDisplayTable);
                        CompareProductDisplayableList compareDisplayableList = new CompareProductDisplayableList();
                        compareDisplayableList.setCompareProductDisplayables(compareTable);

                        context.write(Constants.Context.PRODUCT_DISPLAYABLE_LIST, productDisplayableList);
                        context.write(Constants.Context.COMPARE_DISPLAYABLE_LIST, compareDisplayableList);
                    }
                }
            }
            context.write(Constants.Context.CURRENT_PAGE, Constants.Context.COMPARE_PAGE);
            Util.addBrandContext(this.getEnvironment().getRequest(), context);
        }
    }

    /**
     * Construct the compare displayable product.
     * @param product published product
     * @return compareDisplayable the compare product displayable
     */
    private CompareProductDisplayable constructCompareDisplayable(PublishedProduct product) {
        CompareProductDisplayable compareDisplayable = new CompareProductDisplayable();
        Disponibility baseDispo = product.getBasePriceDisponibility();
        if (baseDispo != null) {
            Date departDate = baseDispo.getDepartureDate();
            String departureDateString = Util.retrieveDateMessage(departDate);
            compareDisplayable.setBaseDispoDate(departureDateString);
            City departCity = baseDispo.getDepartureCity();
            String departCityLabel = departCity.getLabel();
            compareDisplayable.setBaseDispoCity(departCityLabel);
        }
        List<String> typeVoyageValues = Util.getCriterionValueLabelList(product,
                Constants.CriterionConstants.CRITERION_TYPE_VOYAGE_CODE, false);
        if (typeVoyageValues != null && typeVoyageValues.size() > 0) {
            String[] typeVoyageTable = Util.convertListToArray(typeVoyageValues);
            compareDisplayable.setTypeFomules(typeVoyageTable);
        }

        List<String> thematicValueCodes = Util.getCriterionValueLabelList(product,
                Constants.CriterionConstants.CRITERION_THEMATIC_CODE, true);
        if (thematicValueCodes != null && thematicValueCodes.size() > 0) {
            for (int i = 0; i < thematicValueCodes.size(); i++) {
                String valueCode = thematicValueCodes.get(i);
                valueCode = Util.appendString(Constants.CriterionConstants.CRITERION_THEMATIC_CODE, ".", valueCode);
                thematicValueCodes.set(i, valueCode);
            }
        }
        if (thematicValueCodes.size() > 0) {
            String[] criteriaValueCodes = Util.convertListToArray(thematicValueCodes);
            compareDisplayable.setCritereValueCode(criteriaValueCodes);
        }
        Document edito = product.getEdito();
        if (edito != null) {
            fillIncludeAndNoInclude(edito, compareDisplayable);
        }

        // set countries
        if (product.getTechnicalInfo() != null && product.getTechnicalInfo().getCountry() != null) {
            Country[] countries = product.getTechnicalInfo().getCountry();
            if (countries.length > 0) {
                compareDisplayable.setDestCountry(countries);
            }
        }

        // set holiday type
        List<String> holidayTypeLabelList
            = Util.getCriterionValueLabelList(product, Constants.CriterionConstants.STAY_TYPE_CODE, false);
        if (holidayTypeLabelList != null && holidayTypeLabelList.size() == 1) {
            compareDisplayable.setHolidayTypeLabel(holidayTypeLabelList.get(0));
        }

        return compareDisplayable;
    }

    /**
     * Fill the include and not include contents retrieving from edito.
     * @param edito document
     * @param compareDisplayable compare displayer
     */
    private void fillIncludeAndNoInclude(Document edito, CompareProductDisplayable compareDisplayable) {
        MainZone mainZone = Util.getEditoMainZone(edito, "include");
        Paragraph[] paragraphs = Util.getParagraphs(mainZone);
        boolean hasContent = Util.hasContentInParagraphTable(paragraphs);
        if (hasContent) {
            compareDisplayable.setIncludes(paragraphs);
        }

    }


    /**
     * Retrieve the products id from the cookie.
     * @param comparatorCookie the comparator cookie
     * @return productsId products id list
     */
    private List<String> retriveProductsId(Cookie comparatorCookie) {
        List<String> productsId = null;
        String products = comparatorCookie.getValue();
        if (products != null && !"".equals(products)) {
            productsId = new ArrayList<String>();
            String[] productsTable = products.split(Constants.Comparator.COOKIE_SEPARATOR);
            for (String pid : productsTable) {
                if (!"".equals(pid)) {
                    productsId.add(pid);
                }
            }
        }
        return productsId;
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
