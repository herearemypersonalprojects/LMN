/*
 * Created on 5 sept. 2011
 *
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.serp;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.ejb.CreateException;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.data.Criterion;
import com.travelsoft.cameleo.catalog.data.CriterionValue;
import com.travelsoft.cameleo.catalog.data.Disponibility;
import com.travelsoft.cameleo.catalog.data.PageLayoutComponent;
import com.travelsoft.cameleo.catalog.data.PublishedProduct;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchCriteria;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchResponse;
import com.travelsoft.cameleo.catalog.data.Pushing;
import com.travelsoft.cameleo.catalog.data.PushingField;
import com.travelsoft.cameleo.catalog.data.TechnicalInfo;
import com.travelsoft.cameleo.catalog.data.User;
import com.travelsoft.cameleo.catalog.ejb.ServicesManager;
import com.travelsoft.cameleo.catalog.enterprise.interfaces.PushingServicesLocal;
import com.travelsoft.cameleo.catalog.interfaces.ServicesFactory;
import com.travelsoft.cameleo.catalog.interfaces.exceptions.TechnicalException;
import com.travelsoft.cameleo.catalog.interfaces.search.PublishedProductSearchServicesInterface;
import com.travelsoft.cameleo.catalog.interfaces.users.UserServicesInterface;
import com.travelsoft.cameleo.catalog.taglib.constant.Constant;
import com.travelsoft.cameleo.catalog.taglib.utils.Utils;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.lastminute.catalog.seo.SeoTool;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.TrackingUtil;
import com.travelsoft.lastminute.catalog.util.Util;
import com.travelsoft.lastminute.data.BrandData;
import com.travelsoft.lastminute.data.MediaTagTrackingStats;
import com.travelsoft.lastminute.data.SeoData;
import com.travelsoft.lastminute.data.SerpTrackingData;
import com.travelsoft.lastminute.data.TrackingData;

/**
 * <p>Titre : SearchProducts.</p>
 * <p>Description : .</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Travelsoft</p>
 * @author zouhair.mechbal
 */
public class SearchProductsNG extends AbstractSearchProducts {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(SearchProductsNG.class);

    /**
     * Processes business logic and produces the data model to use in the search engine view.
     *
     * @param context the component's context
     */
    public void fillComponentContext(IComponentContext<PageLayoutComponent> context) {

        this.addDataInContext(context);

        try {
            HttpServletRequest request = this.getEnvironment().getRequest();
            String queryString = request.getQueryString();
            String uniqueDestination = SeoTool.getUniqueDestinationCountry(request);
            int resultsNumber = this.processSearchProducts(context, queryString, uniqueDestination);

            if (resultsNumber == 0) {
                int alternatifResultsNumber = 0;
                String alternatifQueryString = null;

                if (uniqueDestination != null) {
                    alternatifQueryString = Util.appendString(Constants.Common.DESTINATION_QUERY_PARAMETER,
                            Constants.CriterionConstants.CRITERION_EQUAL, uniqueDestination);

                    alternatifResultsNumber =
                                    this.processSearchProducts(context, alternatifQueryString, uniqueDestination);

                } else {

                    alternatifQueryString = Util.retrieveConfigFromPageDescription(context, "alternatifQueryString");

                    alternatifResultsNumber =
                                    this.processSearchProducts(context, alternatifQueryString, uniqueDestination);
                }

                if (alternatifResultsNumber != 0) {
                    context.write("displayAlternatifProducts", true);
                    context.write("queryString", alternatifQueryString);
                    context.write("alternatifQueryString", alternatifQueryString);

                }
            }

        } catch (TechnicalException e) {
            LOGGER.error("Error occured while searching products.", e);
        }
    }

    /**
     * Process the products search.
     *
     * @param context The context
     * @param queryString The query in String format
     * @param destination The dest
     * @return the results number
     * @throws TechnicalException if errors occurs
     */
    private int processSearchProducts(IComponentContext<PageLayoutComponent> context,
            String queryString, String destination)
                    throws TechnicalException {

        int resultsNumber = 0;

        // Retrieve the current search criteria - Query search from the search engine
        PublishedProductSearchCriteria currentSearchCriteria = this.getCurrentCriteria(context, queryString);

        // Retrieve the final search criteria - Query search from the
        // refinement search engine
        PublishedProductSearchCriteria finalSearchCriteria = this.getFinalCriteria(context, currentSearchCriteria,
                queryString);

        // Products for the user response (this response is in OnlyHeaders format)
        PublishedProductSearchResponse userSearchResponse = this.getPublishedProductSearchResponse(finalSearchCriteria);

        // Products for the selection (this response contains complete products)
        PublishedProductSearchCriteria finalCriteria = (PublishedProductSearchCriteria) context
            .lookup(Constants.Context.FINAL_CRITERIA_CONSTRAINTS);
        PublishedProductSearchResponse selectionSearchResponse = null;
        if (finalCriteria.getUser() != null) {
            selectionSearchResponse = this.searchSelectionProducts(finalSearchCriteria, userSearchResponse, context);
        }

        // Processes the selection response, and updates the user response.
        // The user search response will not contain the selection products after this update.
        userSearchResponse = this.updateResponse(finalSearchCriteria, userSearchResponse, selectionSearchResponse);

        resultsNumber = userSearchResponse.getProductInternalCodeCount()
                + (selectionSearchResponse == null ? 0 : selectionSearchResponse.getProductInternalCodeCount());
        context.write(Constants.Context.RESULTS_NUMBER, resultsNumber);

        context.write(Constants.Context.DESTINATION, destination);
        context.write(Constants.Context.PRODUCT_SEARCH_RESPONSE, userSearchResponse);

        return resultsNumber;
    }

    /**
     * @param finalSearchCriteria
     * @param userSearchResponse
     * @param selectionSearchResponse
     * @return
     */
    private PublishedProductSearchResponse updateResponse(PublishedProductSearchCriteria finalSearchCriteria,
            PublishedProductSearchResponse userSearchResponse, PublishedProductSearchResponse selectionSearchResponse) {
        Set<String> selectionCodes = new HashSet<String>();
        int internalCodeCount = 0;
        if (selectionSearchResponse != null) {
            internalCodeCount = selectionSearchResponse.getProductInternalCodeCount();
            for (int i = 0; i < internalCodeCount; i++) {
                String internalCode = selectionSearchResponse.getProductInternalCode(i);
                selectionCodes.add(internalCode);
            }
        }
        PublishedProductSearchResponse restrictedResponse = this.calculateSetOperationProductIds(userSearchResponse,
                selectionCodes, false, -1);
        int firstIndex = 0;
        int lastIndex = 20;
        if (finalSearchCriteria.hasFirstResultIndex()) {
            firstIndex = finalSearchCriteria.getFirstResultIndex();
        }
        if (finalSearchCriteria.hasLastResultIndex()) {
            lastIndex = finalSearchCriteria.getLastResultIndex();
        }
        lastIndex -= internalCodeCount;
        if (firstIndex != 0) {
            firstIndex -= internalCodeCount;
        }

        return this.completeProductsInResponse(restrictedResponse, firstIndex, lastIndex, finalSearchCriteria);
    }

    /**
     * Finds up to this.getMaxPreferredProducts(context); selection products.
     * All operations are intended to scale linearly.
     * @param finalSearchCriteria
     * @param userSearchResponse
     * @param context
     * @return
     */
    private PublishedProductSearchResponse searchSelectionProducts(PublishedProductSearchCriteria finalSearchCriteria,
            PublishedProductSearchResponse userSearchResponse, IComponentContext<PageLayoutComponent> context) {

        boolean countryCriteriaPresent = isQueryWithDestinationOrDestCityCriteria(finalSearchCriteria,
                Constants.CriterionConstants.COUNTRY_CRITERION_CODE);
        boolean cityCriteriaPresent = isQueryWithDestinationOrDestCityCriteria(finalSearchCriteria,
                Constants.CriterionConstants.CITY_CRITERION_CODE);
        boolean isQueryWithoutDestination = !(countryCriteriaPresent || cityCriteriaPresent);

        PublishedProductSearchResponse countrySelection = null;
        PublishedProductSearchResponse citySelection = null;
        PublishedProductSearchResponse noCountrySelection = null;

        Set<String> productSet = buildProductIdSet(userSearchResponse);
        int maxSize = this.getMaxPreferredProducts(context);

        if (countryCriteriaPresent) {
            countrySelection = this.calculateSetOperationProductIds(this
                    .retrievePreferredProductsByDestinationCriteria(finalSearchCriteria, context,
                            Constants.CriterionConstants.COUNTRY_CRITERION_CODE), productSet, true, maxSize);
        }
        if (cityCriteriaPresent) {
            citySelection = this.calculateSetOperationProductIds(this
                    .retrievePreferredProductsByDestinationCriteria(finalSearchCriteria, context,
                            Constants.CriterionConstants.CITY_CRITERION_CODE), productSet, true, maxSize);
        }
        if (isQueryWithoutDestination) {
            noCountrySelection = this.calculateSetOperationProductIds(this
                    .retrievePreferredProductsByDestinationCriteria(finalSearchCriteria, context,
                            Constants.CriterionConstants.NODESTI_CRITERION_CODE), productSet, true, maxSize);
        }


        // Complete responses
        PublishedProductSearchResponse completeSelectionResponse = null;
        if (noCountrySelection != null) {
            completeSelectionResponse = this.completeProductsInResponse(noCountrySelection, 0, maxSize,
                    finalSearchCriteria);
        } else if (countrySelection != null && citySelection == null) {
            completeSelectionResponse = this.completeProductsInResponse(countrySelection, 0, maxSize,
                    finalSearchCriteria);
        } else if (countrySelection == null && citySelection != null) {
            completeSelectionResponse = this.completeProductsInResponse(citySelection, 0, maxSize, finalSearchCriteria);
        } else if (countrySelection != null && citySelection != null) {
            Integer countryPrecedence = null;
            Integer cityPrecedence = null;
            Integer currentWorstValue = null;
            TreeSet<PublishedProduct> resultSet = new TreeSet<PublishedProduct>(new ProductRankComparator());
            int topSize = maxSize + 1;
            int countrySelectionIndex = 0;
            int citySelectionIndex = 0;
            int countryResultCount = countrySelection.getProductInternalCodeCount();
            int cityResultCount = citySelection.getProductInternalCodeCount();
            while (resultSet.size() < topSize
                    && (countrySelectionIndex <= countryResultCount || citySelectionIndex <= cityResultCount)) {
                if (countryPrecedence == null && cityPrecedence == null) {
                    PublishedProduct countryProduct = this.peek(countrySelectionIndex++, countrySelection,
                            Constants.CriterionConstants.COUNTRY_CRITERION_CODE, finalSearchCriteria);
                    countryPrecedence = this.calculatePrecedence(countryProduct);
                    if (countryProduct != null && countryPrecedence != null) {
                        resultSet.add(countryProduct);
                        currentWorstValue = countryPrecedence;
                    }
                    PublishedProduct cityProduct = this.peek(citySelectionIndex++, citySelection,
                            Constants.CriterionConstants.CITY_CRITERION_CODE, finalSearchCriteria);
                    cityPrecedence = this.calculatePrecedence(cityProduct);
                    if (cityProduct != null && cityPrecedence != null) {
                        resultSet.add(cityProduct);
                    }
                    if (currentWorstValue == null) {
                        currentWorstValue = cityPrecedence;
                    } else if (cityPrecedence != null) {
                        currentWorstValue = Math.max(currentWorstValue, cityPrecedence);
                    }
                } else {
                    boolean skipCityPeek = false;
                    boolean skipCountryPeek = false;
                    // When both precedences are defined, decide which one to skip.
                    if (cityPrecedence != null && countryPrecedence != null) {
                        if (cityPrecedence.compareTo(countryPrecedence) <= 0) {
                            skipCountryPeek = true;
                        } else {
                            skipCityPeek = true;
                        }
                    }
                    if (!skipCityPeek) {
                        PublishedProduct cityProduct = this.peek(citySelectionIndex++, citySelection,
                                Constants.CriterionConstants.CITY_CRITERION_CODE, finalSearchCriteria);
                        cityPrecedence = this.calculatePrecedence(cityProduct);
                        if (cityProduct != null && cityPrecedence != null) {
                            resultSet.add(cityProduct);
                            currentWorstValue = Math.max(currentWorstValue, cityPrecedence);
                        }
                    }
                    if (!skipCountryPeek) {
                        PublishedProduct countryProduct = this.peek(countrySelectionIndex++, countrySelection,
                                Constants.CriterionConstants.COUNTRY_CRITERION_CODE, finalSearchCriteria);
                        countryPrecedence = this.calculatePrecedence(countryProduct);
                        if (countryProduct != null && countryPrecedence != null) {
                            resultSet.add(countryProduct);
                            currentWorstValue = Math.max(currentWorstValue, countryPrecedence);
                        }
                    }
                    // Can I still find one more better result?
                    if (resultSet.size() == maxSize && currentWorstValue != null) {
                        if (cityPrecedence != null && cityPrecedence.compareTo(currentWorstValue) < 0) {
                            continue;
                        } else if (countryPrecedence != null && countryPrecedence.compareTo(currentWorstValue) < 0) {
                            continue;
                        } else {
                            break;
                        }
                    }
                }
            }
            if (resultSet.size() > maxSize) {
                resultSet.remove(resultSet.last());
            }

            completeSelectionResponse = new PublishedProductSearchResponse();
            completeSelectionResponse.setProducts(resultSet.toArray(new PublishedProduct[resultSet.size()]));
            completeSelectionResponse.setResultsNumber(resultSet.size());
        }

        // resort products by DM criteria
        if (isExistingDmCriteria(completeSelectionResponse)) {
            completeSelectionResponse = sortProductsByDmCriteria(completeSelectionResponse);
        }

        if (completeSelectionResponse != null && completeSelectionResponse.getProductsCount() > 0) {
            context.write(Constants.Context.PREFERRED_PRODUCT_SEARCH_RESPONSE, completeSelectionResponse);
        }

        return completeSelectionResponse;
    }

    /**
     *
     * Method to verify whether at least one product has DM criteria.
     *
     * @param completeSelectionResponse list of products
     * @return boolean
     */
    private boolean isExistingDmCriteria(PublishedProductSearchResponse completeSelectionResponse) {
        if (completeSelectionResponse == null || completeSelectionResponse.getProducts() == null) {
            return false;
        }
        for (PublishedProduct publishedProduct : completeSelectionResponse.getProducts()) {
            CriterionValue[] criterionValues = Util.getCriterionValues(
                publishedProduct, Constants.CriterionConstants.SELECTION_CRITERION_CODE);
            if (criterionValues != null) {
                for (CriterionValue criterionValue : criterionValues) {
                    String code = criterionValue.getCode();
                    if (code != null && code.indexOf(".") >= 0) {
                        String criterionCode = code.substring(0, code.indexOf("."));
                        if (criterionCode.equals(Constants.CriterionConstants.DM_CRITERION_CODE)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     *
     * Method to verify whether the DM criteria can be applied (mises en avant).
     *
     * @param product the current product
     * @param rqDate request date
     * @param rqFlex request flexibility
     * @return boolean
     */
    private boolean isValideDmCriteria(PublishedProduct product, Calendar rqDate, int rqFlex) {
        if (product == null) {
            return false;
        }

        // get pushing parameters
        PushingServicesLocal pushingServices;
        Pushing[] pus = null;
        try {
            pushingServices = ServicesManager.getPushingServices();
            pus = pushingServices.getProductPushings(product.getCode());
        } catch (RemoteException e) {
            LOGGER.error("error: " + e.getMessage());
        } catch (NamingException e) {
            LOGGER.error("error: " + e.getMessage());
        } catch (CreateException e) {
            LOGGER.error("error: " + e.getMessage());
        } catch (TechnicalException e) {
            LOGGER.error("error: " + e.getMessage());
        }


        Pushing flashSalePushing = null;
        if (pus != null) {
            for (Pushing pushing : pus) {
                if (pushing.getCategoryCodeLabel() != null
                                && pushing.getCategoryCodeLabel().getCode() != null
                                && Constants.Common.LAST_MINUTE_SELECTION_EXPERT_CODE.equals(
                                    pushing.getCategoryCodeLabel().getCode())) {
                    flashSalePushing = pushing;
                    break;
                }
            }
        }

        if (flashSalePushing != null) {
            String departDate = null;
            String endDate = null;
            String flexibility = null;

            for (PushingField field : flashSalePushing.getField()) {
                if (field.getCode().equals("LMN_DEPARTURE_DATE")) {
                    departDate = field.getValue();
                } else if (field.getCode().equals("LMN_END_DATE")) {
                    endDate = field.getValue();
                } else if (field.getCode().equals("LMN_DATE_FLEXIBILITY")) {
                    flexibility = field.getValue();
                }
            }

            if (departDate != null && endDate != null) {
                Calendar calendarDepart = getCalendar(departDate);
                Calendar calendarEnd = getCalendar(endDate);
                if (calendarDepart != null && calendarEnd != null) {
                    if (rqDate.after(calendarDepart) && rqDate.before(calendarEnd)) {
                        return true;
                    }
                }
            } else if (flexibility != null) {
                Calendar calendarToday = Calendar.getInstance();
                rqDate.add(Calendar.DAY_OF_MONTH, (-1) * rqFlex);
                int flex = 0;
                try {
                    flex = Integer.parseInt(flexibility);
                } catch (NumberFormatException e) {
                    LOGGER.error("error: " + e.getMessage());
                }
                calendarToday.add(Calendar.DAY_OF_MONTH, flex);
                if (rqDate.before(calendarToday)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     * Method to get calendar from a date string.
     *
     * @param date the given date
     * @return calendar
     */
    private Calendar getCalendar(String date) {
        if (date.split("/").length == 3) {
            try {
                int day = Integer.parseInt(date.split("/")[0]);
                int month = Integer.parseInt(date.split("/")[1]);
                int year = Integer.parseInt(date.split("/")[2]);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month - 1);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                calendar.get(0);
                return calendar;
            } catch (NumberFormatException e) {
                LOGGER.error("error: " + e.getMessage());
                return null;
            }
        }
        return null;
    }
    /**
     *
     * Method to get user selected departure date.
     *
     * @return date
     */
    private Calendar getDepartDate() {

        HttpServletRequest request = this.getEnvironment().getRequest();
        String rqDay = request.getParameter(Constants.CriterionConstants.CRITERION_DAYS);
        String rqMonthYear = request.getParameter(Constants.CriterionConstants.CRITERION_MONTH_YEAR);

        if (rqDay == null || rqMonthYear == null) {
            return null;
        }
        try {
            int date = Integer.parseInt(rqDay);
            int month = Integer.parseInt(rqMonthYear.split("/")[0]);
            int year = Integer.parseInt(rqMonthYear.split("/")[1]);
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month - 1, date);
            calendar.get(0);
            return calendar;
        } catch (NumberFormatException e) {
            LOGGER.error("error: " + e.getMessage());
        }

        return null;
    }
    /**
     *
     * Method to sort products by DM criteria.
     *
     * @param completeSelectionResponse list of published products
     * @return new sorted products
     */
    private PublishedProductSearchResponse sortProductsByDmCriteria(
        PublishedProductSearchResponse completeSelectionResponse) {

        if (completeSelectionResponse.getProducts() == null || completeSelectionResponse.getProducts().length <= 0) {
            return null;
        }
        PublishedProductSearchResponse newResults = (PublishedProductSearchResponse) completeSelectionResponse.clone();

        // Verify whether user selected a date
        Calendar departDate = getDepartDate();
        if (departDate == null) {
            return completeSelectionResponse;
        }
        HttpServletRequest request = this.getEnvironment().getRequest();
        String f = (String) request.getParameter(Constants.CriterionConstants.CRITERION_FLEXIBILITY);
        int flexibility = 0;
        if (f != null) {
            flexibility = Integer.parseInt(f);
        }

        boolean isProductDm[] = new boolean[completeSelectionResponse.getProducts().length];
        for (int i = 0; i < completeSelectionResponse.getProducts().length; i ++) {
            isProductDm[i] = false;
        }

        for (PublishedProduct publishedProduct : completeSelectionResponse.getProducts()) {
            CriterionValue[] criterionValues = Util.getCriterionValues(
                publishedProduct, Constants.CriterionConstants.SELECTION_CRITERION_CODE);
            if (criterionValues != null) {
                for (CriterionValue criterionValue : criterionValues) {
                    String code = criterionValue.getCode();
                    if (code != null && code.indexOf(".") >= 0 && code.indexOf(".") < code.length()) {
                        String criterionCode = code.substring(0, code.indexOf("."));
                        if (criterionCode.equals(Constants.CriterionConstants.DM_CRITERION_CODE)) {
                            if (isValideDmCriteria(publishedProduct, departDate, flexibility)) {
                                String rank = code.substring(code.indexOf(".") + 1);
                                try {
                                    int index = Integer.parseInt(rank);
                                    PublishedProduct p = (PublishedProduct) publishedProduct.clone();
                                    newResults.removeProducts(p);
                                    if (index - 1 <= newResults.getProducts().length) {
                                        newResults.addProducts(index - 1, p);
                                        isProductDm[index - 1] = true;
                                        for (int i = index; i < completeSelectionResponse.getProducts().length - 1; i++) {
                                            if (isProductDm[i]) {
                                                PublishedProduct p1 = (PublishedProduct) newResults.getProducts(i).clone();
                                                newResults.removeProducts(p1);
                                                newResults.addProducts(i + 1, p1);
                                            }
                                        }

                                    }
                                } catch (NumberFormatException e) {
                                    LOGGER.error("Error : " + e.getMessage());
                                }
                            }
                        }
                    }
                }
            }
        }
        return newResults;
    }

    /**
     * @param countryProduct product
     * @return interger
     */
    private Integer calculatePrecedence(PublishedProduct countryProduct) {
        if (countryProduct == null) {
            return null;
        }
        int sortInfoCount = countryProduct.getSortInfoCount();
        if (sortInfoCount != 1) {
            return null;
        }
        Object sortInfo = countryProduct.getSortInfo(0);
        if (sortInfo instanceof Integer) {
            return (Integer) sortInfo;
        }
        return null;
    }

    /**
     * @param i
     * @param resultCount
     * @param selection
     * @param completeCriteria
     * @return
     */
    private PublishedProduct peek(int i, PublishedProductSearchResponse selection, String criteriaCode,
            PublishedProductSearchCriteria completeCriteria) {
        if (selection == null) {
            return null;
        }
        if (i >= selection.getProductInternalCodeCount()) {
            return null;
        }
        String productInternalCode = selection.getProductInternalCode(i);
        PublishedProduct product = getProduct(productInternalCode, completeCriteria);
        if (product != null) {
            TechnicalInfo technicalInfo = product.getTechnicalInfo();
            if (technicalInfo != null) {
                int criterionCount = technicalInfo.getCriterionCount();
                for (int j = 0; j < criterionCount; j++) {
                    Criterion criterion = technicalInfo.getCriterion(j);
                    if (!Constants.CriterionConstants.SELECTION_CRITERION_CODE.equals(criterion.getCode())) {
                        continue;
                    }
                    int valueCount = criterion.getValueCount();
                    for (int k = 0; k < valueCount; k++) {
                        CriterionValue value = criterion.getValue(k);
                        String code = value.getCode();
                        if (code.startsWith(criteriaCode)) {
                            String[] values = code.split("\\.");
                            if (values != null && values.length > 1) {
                                Integer intValue = Integer.parseInt(values[1]);
                                product.removeAllSortInfo();
                                product.addSortInfo(intValue);
                            }
                            break;
                        }
                    }
                }
            }
        }
        return product;
    }

    /**
     * @param productInternalCode
     * @param completeCriteria
     * @return
     */
    private PublishedProduct getProduct(String productInternalCode, PublishedProductSearchCriteria completeCriteria) {
        PublishedProductSearchServicesInterface searchServices = ServicesFactory.getPublishedProductSearchServices();
        PublishedProductSearchCriteria criteria = (PublishedProductSearchCriteria) completeCriteria.clone();
        criteria.setResultWithCriterion(true);
        criteria.setResultWithDocuments(true);
        criteria.setResultWithAvails(true);
        PublishedProduct product = null;
        try {
            product = searchServices
                    .getPublishedProduct(productInternalCode, criteria, Constants.CAMELEO_CACHE_MANAGER);
        } catch (TechnicalException e) {
            LOGGER.warn("Failed to retrieve product " + productInternalCode, e);
        }
        return product;
    }

    /**
     * @param productInternalCode
     * @param completeCriteria
     * @return
     */
    private PublishedProduct[] getProducts(String[] productInternalCodes,
            PublishedProductSearchCriteria completeCriteria) {
        PublishedProductSearchServicesInterface searchServices = ServicesFactory.getPublishedProductSearchServices();
        PublishedProductSearchCriteria criteria = (PublishedProductSearchCriteria) completeCriteria.clone();
        criteria.setResultWithCriterion(true);
        criteria.setResultWithDocuments(true);
        criteria.setResultWithAvails(true);
        try {
            PublishedProduct[] products = searchServices.getPublishedProducts(productInternalCodes, criteria,
                    Constants.CAMELEO_CACHE_MANAGER);
            return products;
        } catch (TechnicalException e) {
            LOGGER.warn("Failed to retrieve product ids  " + StringUtils.join(productInternalCodes, ','), e);
        }
        return new PublishedProduct[0];
    }

    /**
     * This attempts to build an efficient product ID dictionary in O(n).
     * @param userSearchResponse
     * @return
     */
    private Set<String> buildProductIdSet(PublishedProductSearchResponse userSearchResponse) {
        Set<String> productSet = null;
        int codeCount = userSearchResponse.getProductInternalCodeCount();
        if (codeCount > 0) {
            productSet = new HashSet<String>((int) Math.ceil(codeCount / 0.75));
            for (int i = 0; i < codeCount; i++) {
                productSet.add(userSearchResponse.getProductInternalCode(i));
            }
        }
        return productSet;
    }

    /**
     * Keeps product codes of the responseToTrim that are found in the given productSet,
     * in O(responseToTrim.getProductInternalCodeCount()), keeping the order from the responseToTrim.
     * @param responseToTrim
     * @param productSet
     * @return
     */
    private PublishedProductSearchResponse calculateSetOperationProductIds(
            PublishedProductSearchResponse responseToTrim, Set<String> productSet, boolean intersect, int maxSize) {
        if (responseToTrim == null || productSet == null) {
            return null;
        }
        PublishedProductSearchResponse intersectionResult = (PublishedProductSearchResponse) responseToTrim.clone();
        intersectionResult.removeAllProductInternalCode();
        int codeCount = responseToTrim.getProductInternalCodeCount();
        for (int i = 0; i < codeCount; i++) {
            String productInternalCode = responseToTrim.getProductInternalCode(i);
            boolean inclusion = productSet.contains(productInternalCode);
            if (!intersect) {
                inclusion = !inclusion;
            }
            if (inclusion) {
                intersectionResult.addProductInternalCode(productInternalCode);
                if (maxSize != -1 && intersectionResult.getProductInternalCodeCount() >= maxSize) {
                    break;
                }
            }
        }
        intersectionResult.setResultsNumber(intersectionResult.getProductInternalCodeCount());
        return intersectionResult;
    }

    /**
     * @param response
     * @param completeCriteria
     * @return
     */
    private PublishedProductSearchResponse completeProductsInResponse(PublishedProductSearchResponse response, int fin,
            int lin, PublishedProductSearchCriteria completeCriteria) {
        PublishedProductSearchResponse completeSelectionResponse;
        completeSelectionResponse = (PublishedProductSearchResponse) response.clone();
        int productCount = response.getProductInternalCodeCount();

        int firstIndex = fin;
        int lastIndex = Math.min(lin, productCount);

        if (firstIndex >= lastIndex) {
            firstIndex = 0;
            lastIndex = 0;
        }

        completeSelectionResponse.removeAllProducts();
        String productIds[] = new String[lastIndex - firstIndex];
        for (int i = firstIndex; i < lastIndex; i++) {
            String internalCode = response.getProductInternalCode(i);
            productIds[i - firstIndex] = internalCode;

        }
        PublishedProduct[] products = this.getProducts(productIds, completeCriteria);
        for (PublishedProduct publishedProduct : products) {
            completeSelectionResponse.addProducts(publishedProduct);
        }

        completeSelectionResponse.setCurrPageResultsNumber(completeSelectionResponse.getProductsCount());
        return completeSelectionResponse;
    }

    /**
     * Retrieves the preferred products for a given criterion code.
     * @param finalSearchCriteria The final search criteria
     * @param context The context
     * @param criterionCode The criterion code
     * @return
     */
    private PublishedProductSearchResponse retrievePreferredProductsByDestinationCriteria(
            PublishedProductSearchCriteria finalSearchCriteria, IComponentContext<PageLayoutComponent> context,
            String criterionCode) {
        PublishedProductSearchResponse response = null;
        try {
            PublishedProductSearchCriteria preferredCountryCriteria = (PublishedProductSearchCriteria) finalSearchCriteria
                    .clone();
            this.addPreferredProductsCriteria(preferredCountryCriteria, context, criterionCode);
            response = this.getPublishedProductSearchResponse(preferredCountryCriteria);
        } catch (Exception e) {
            LOGGER.error("Unable to retrieve the preferred products for the criterion code : " + criterionCode, e);
        }
        return response;
    }

    /**
     * Adds preferred products criterion value code to the search criteria.
     * @param finalProductSearchCriteria The final search criteria
     * @param context The context
     * @param criteriaValueCode The criteria value code
     */
    private void addPreferredProductsCriteria(PublishedProductSearchCriteria finalProductSearchCriteria,
            IComponentContext<PageLayoutComponent> context, String criteriaValueCode) {
        Criterion criterion = new Criterion();
        criterion.setCode(Constants.CriterionConstants.SELECTION_CRITERION_CODE);
        CriterionValue criterionValue = new CriterionValue();
        criterionValue.setCode(criteriaValueCode);
        criterion.addValue(criterionValue);
        finalProductSearchCriteria.addCustomCriteria(criterion);
        finalProductSearchCriteria.setFirstResultIndex(0);
        finalProductSearchCriteria.setLastResultIndex(this.getMaxPreferredProducts(context));
        finalProductSearchCriteria.setSortBy(Constants.CriterionConstants.CRITERION_CUSTOMER_FIELD
                + Constants.CriterionConstants.SELECTION_CRITERION_CODE_WITH_DOT + criteriaValueCode);

    }

    /**
     * Checks if the country, city or no destination are requested by the user.
     * @param finalProductSearchCriteria The criteria
     * @param criterionType The criterion type. Country, city or no destination
     * @return true if the country, city or no destination are requested by the user
     */
    private boolean isQueryWithDestinationOrDestCityCriteria(PublishedProductSearchCriteria finalProductSearchCriteria,
            String criterionType) {
        if (finalProductSearchCriteria.getUser() != null) {
            Criterion[] customCriteria = finalProductSearchCriteria.getCustomCriteria();
            for (Criterion criterion : customCriteria) {
                if ("de".equals(criterion.getCode())) {
                    CriterionValue[] criterionValues = criterion.getValue();
                    for (CriterionValue criterionValue : criterionValues) {
                        String code = criterionValue.getCode();
                        if (code != null) {
                            String[] destinationCodes = code.split("\\.");
                            if (destinationCodes.length == 1 && criterionType.equals("country")) {
                                return true;
                            } else if (destinationCodes.length > 1 && criterionType.equals("city")) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Retrieves the current search criteria (The criteria from the search engine).
     * @param context The page layout context
     * @param queryString The query string
     * @return The current criteria
     */
    private PublishedProductSearchCriteria getCurrentCriteria(IComponentContext<PageLayoutComponent> context,
            String queryString) {

        PublishedProductSearchCriteria currentSearchCriteria = Util.retrieveSearchCriteria(context,
                Constants.Config.DEFAULT_CRITERIA_KEY);

        context.write("defaultLastIndex", currentSearchCriteria.getLastResultIndex());

        String criteriaQueryString = SearchProductUtil.removePrefixedQueryString(queryString,
                Constants.Common.SEARCH_ENGINE_PREFIX);

        currentSearchCriteria = Utils.completeProductSearchCriteria(criteriaQueryString, Constant.CRITERIA_SEPARATOR,
                Constant.CRITERION_SEPARATOR, Constant.OPTIONS_SEPARATOR, currentSearchCriteria,
                Constants.CAMELEO_CACHE_MANAGER);

        User user = this.retrieveUser(context, currentSearchCriteria);
        if (user != null) {
            currentSearchCriteria.setUser(user);
        }

        context.write(Constants.Context.CURRENT_CRITERIA_CONSTRAINTS, currentSearchCriteria);
        context.write("queryString", queryString);

        addDataInContext(context);

        return currentSearchCriteria;
    }

    /**
     * Method retrieves the final criteria.
     *
     * @param context The current context
     * @param currentProductSearchCriteria The search criteria
     * @param queryString The query string
     * @return The final criteria
     */
    private PublishedProductSearchCriteria getFinalCriteria(IComponentContext<PageLayoutComponent> context,
            PublishedProductSearchCriteria currentProductSearchCriteria, String queryString) {

        String finalQueryString = SearchProductUtil.removePrefixedQueryString(queryString,
                Constants.Common.REFINEMENT_SEARCH_ENGINE_PREFIX);

        PublishedProductSearchCriteria finalProductSearchCriteria = (PublishedProductSearchCriteria) currentProductSearchCriteria
                .clone();

        if (finalQueryString.length() > 0) {
            finalProductSearchCriteria = Utils.completeProductSearchCriteria(finalQueryString,
                    Constant.CRITERIA_SEPARATOR, Constant.CRITERION_SEPARATOR, Constant.OPTIONS_SEPARATOR,
                    finalProductSearchCriteria, Constants.CAMELEO_CACHE_MANAGER);

            context.write(Constants.Context.FINAL_CRITERIA_CONSTRAINTS, finalProductSearchCriteria);

        } else {
            finalProductSearchCriteria = currentProductSearchCriteria;
            context.write(Constants.Context.FINAL_CRITERIA_CONSTRAINTS, currentProductSearchCriteria);
        }
        context.write(Constants.Context.CURRENT_PAGE, Constants.Context.SERP_PAGE);
        return finalProductSearchCriteria;
    }

    /**
     * Method retrieves the user from a user code retrieved from the page
     * description page.
     *
     * @param context The current context
     * @param currentSearchCriteria The current search criteria
     * @return the user
     */
    private User retrieveUser(IComponentContext<PageLayoutComponent> context,
            PublishedProductSearchCriteria currentSearchCriteria) {
        String userCode = null;
        try {
            if (com.travelsoft.cameleo.catalog.interfaces.Constants.SortConstants.SORT_BASE_PRICE_COEFF.equals(currentSearchCriteria.getSortBy())
                    || com.travelsoft.cameleo.catalog.interfaces.Constants.SortConstants.SORT_DEPARTURE_DATE_ASC.equals(currentSearchCriteria.getSortBy())) {

                userCode = Util.retrieveConfigFromPageDescription(context, Constants.Config.USER_CODE);

                if (userCode != null) {
                    UserServicesInterface userServices = ServicesFactory.getUserServicesInterface();
                    return userServices.findUserByCode(userCode, false, Constants.CAMELEO_CACHE_MANAGER);
                }
            }
        } catch (TechnicalException e) {
            LOGGER.error("Unable to retrieve the user with code " + userCode + " .", e);
        }
        return null;
    }

    /**
     * Retrieves the maximum of preferred products to retrieve.
     * @param context The context
     * @return the maximum of preferred products to retrieve
     */
    private int getMaxPreferredProducts(IComponentContext<PageLayoutComponent> context) {
        try {
            String maxPrefProducts = Util.retrieveConfigFromPageDescription(context,
                    Constants.Config.MAX_SELECTION_PRODUCTS);
            if (maxPrefProducts != null) {
                return Integer.valueOf(maxPrefProducts);
            }
        } catch (Exception e) {
            LOGGER.error("Error while trying to retrieve the maximum of preferred products from the configuration.", e);
        }
        return 0;
    }


    /**
     * Added data in context.
     *
     * @param context The context
     */
    private void addDataInContext(IComponentContext<PageLayoutComponent> context) {
        SeoData seoData = new SeoData();
        context.write("seoData", seoData);
        String b2bParameterValue = this.getEnvironment().getRequestParameter("user");
        String partnerId = this.getEnvironment().getRequestParameter("partnerId");
        Boolean b2bRequest = (Boolean) this.getEnvironment().getRequest().getSession().getAttribute("b2bRequest");
        BrandData brandData = Util.addBrandContext(this.getEnvironment().getRequest(), context);
        if (b2bRequest != null && b2bRequest
            || Constants.Common.SELECTOUR_BRAND_NAME.equals(brandData.getBrandName())
            || b2bParameterValue != null && "b2b".equals(b2bParameterValue)
            || partnerId != null && "21469".equals(partnerId)) {
            context.write("displayb2bBlock", true);
            this.getEnvironment().getRequest().getSession().setAttribute("b2bRequest", true);
        }
        TrackingData trackingData = new TrackingData();
        SerpTrackingData serpTrackingData = new SerpTrackingData();
        trackingData.setSerp(serpTrackingData);
        context.write(Constants.Context.TRACKING_DATA, trackingData);

        MediaTagTrackingStats mediaTagTrackingData =
                        TrackingUtil.addDepMonthFromRqToCookie(this.getEnvironment(), trackingData);
        context.write("mediaTagStats", mediaTagTrackingData);

        String catIdValue = this.getEnvironment().getRequestParameter("s_c.catid");
        if (catIdValue != null) {
            context.write("catIdValue", catIdValue);
        }

    }
}
