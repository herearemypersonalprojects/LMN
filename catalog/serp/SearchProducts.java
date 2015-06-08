/*
 * Created on 5 sept. 2011
 *
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.serp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.data.Criterion;
import com.travelsoft.cameleo.catalog.data.CriterionValue;
import com.travelsoft.cameleo.catalog.data.PageLayoutComponent;
import com.travelsoft.cameleo.catalog.data.PublishedProduct;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchCriteria;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchResponse;
import com.travelsoft.cameleo.catalog.data.User;
import com.travelsoft.cameleo.catalog.interfaces.ServicesFactory;
import com.travelsoft.cameleo.catalog.interfaces.exceptions.TechnicalException;
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
public class SearchProducts extends AbstractSearchProducts {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(SearchProducts.class);

    /** The preferred products. */
    private List<PublishedProduct> preferredProducts = new ArrayList<PublishedProduct>();

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

                    alternatifResultsNumber = this.processSearchProducts(context, alternatifQueryString, uniqueDestination);

                } else {

                    alternatifQueryString = Util.retrieveConfigFromPageDescription(context, "alternatifQueryString");

                    alternatifResultsNumber = this.processSearchProducts(context, alternatifQueryString, uniqueDestination);
                }

                if (alternatifResultsNumber != 0) {
                    context.write("displayAlternatifProducts", true);
                    context.write("queryString", alternatifQueryString);
                    context.write("firstQueryString", queryString);
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
     * @return the results number
     * @throws TechnicalException if errors occurs
     */
    private int processSearchProducts(IComponentContext<PageLayoutComponent> context, String queryString, String destination)
            throws TechnicalException {

        int resultsNumber = 0;

        // Retrieve the current search criteria - Query search from the search engine
        PublishedProductSearchCriteria currentSearchCriteria = this.getCurrentCriteria(context, queryString);

        // Retrieve the final search criteria - Query search from the
        // refinement search engine
        PublishedProductSearchCriteria finalSearchCriteria = this.getFinalCriteria(context, currentSearchCriteria,
                queryString);

        // Retrieve the preferred products
        PublishedProductSearchCriteria finalCriteria = (PublishedProductSearchCriteria) context
            .lookup(Constants.Context.FINAL_CRITERIA_CONSTRAINTS);
        if (finalCriteria.getUser() != null) {
            this.retrievePreferredProducts(finalSearchCriteria, context);
        }

        // Get excluded products codes from the search products
        String[] excludedProductsCodes = this.getExcludedProductsCodes(preferredProducts);

        PublishedProductSearchResponse publishedProductSearchResponse = this.getPublishedProductSearchResponse(
                finalSearchCriteria, excludedProductsCodes);

        if (publishedProductSearchResponse == null) {
            throw new TechnicalException("The publishedProductSearchResponse object must be not null.");
        }

        if (!preferredProducts.isEmpty()) {
            resultsNumber = publishedProductSearchResponse.getResultsNumber() + preferredProducts.size();
            context.write(Constants.Context.RESULTS_NUMBER, resultsNumber);
        } else {
            resultsNumber = publishedProductSearchResponse.getResultsNumber();
            context.write(Constants.Context.RESULTS_NUMBER, resultsNumber);
        }


        context.write(Constants.Context.DESTINATION, destination);
        context.write(Constants.Context.PRODUCT_SEARCH_RESPONSE, publishedProductSearchResponse);

        return resultsNumber;
    }

    /**
     * Checks if the preferred products list contains the given product.
     * @param publishedProduct The published product
     * @return true if the given product is in the preferred product list
     */
    private boolean isPreferredProductListContainsProduct(PublishedProduct publishedProduct) {
        if (publishedProduct != null) {
            for (PublishedProduct preferredProduct : preferredProducts) {
                if (preferredProduct.getCode() != null && preferredProduct.getCode().equals(publishedProduct.getCode())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Retrieves preferred products when the countries or cities are requested.
     * @param finalSearchCriteria The final search criteria
     * @param context The context
     */
    @SuppressWarnings("unchecked")
    private void retrievePreferredProducts(PublishedProductSearchCriteria finalSearchCriteria,
            IComponentContext<PageLayoutComponent> context) {

        boolean isQueryWithoutDestination = true;

        if (isQueryWithDestinationOrDestCityCriteria(finalSearchCriteria,
                Constants.CriterionConstants.COUNTRY_CRITERION_CODE)) {
            isQueryWithoutDestination = false;
            this.retrievePreferredProductsByDestinationCriteria(finalSearchCriteria, context,
                    Constants.CriterionConstants.COUNTRY_CRITERION_CODE);
        }

        if (isQueryWithDestinationOrDestCityCriteria(finalSearchCriteria,
                Constants.CriterionConstants.CITY_CRITERION_CODE)) {
            isQueryWithoutDestination = false;
            this.retrievePreferredProductsByDestinationCriteria(finalSearchCriteria, context,
                    Constants.CriterionConstants.CITY_CRITERION_CODE);
        }

        if (isQueryWithoutDestination) {
            this.retrievePreferredProductsByDestinationCriteria(finalSearchCriteria, context,
                    Constants.CriterionConstants.NODESTI_CRITERION_CODE);
        }

        PublishedProductSearchResponse preferredFinalProducts = new PublishedProductSearchResponse();
        if (!preferredProducts.isEmpty()) {

            // Compare products
            PublishedProduct[] productBaseArray = (PublishedProduct[]) preferredProducts
                    .toArray(new PublishedProduct[preferredProducts.size()]);
            Arrays.sort(productBaseArray, new ProductCriterionValueCodeComparator(null,
                    Constants.CriterionConstants.SELECTION_CRITERION_CODE));

            int j = 0;
            int maxPrefProducts = this.getMaxPreferredProducts(context);
            for (int i = 0; i < preferredProducts.size(); i++) {
                if (j < maxPrefProducts) {
                    preferredFinalProducts.addProducts(productBaseArray[i]);
                }
                j++;
            }
            context.write(Constants.Context.PREFERRED_PRODUCT_SEARCH_RESPONSE, preferredFinalProducts);
        }
    }

    /**
     * Retrieves the preferred products for a given criterion code.
     * @param finalSearchCriteria The final search criteria
     * @param context The context
     * @param criterionCode The criterion code
     */
    private void retrievePreferredProductsByDestinationCriteria(PublishedProductSearchCriteria finalSearchCriteria,
            IComponentContext<PageLayoutComponent> context, String criterionCode) {
        try {
            PublishedProductSearchCriteria preferredCountryCriteria = (PublishedProductSearchCriteria) finalSearchCriteria
                    .clone();
            addPreferredProductsCriteria(preferredCountryCriteria, context, criterionCode);
            PublishedProductSearchResponse countriesSearchResponse = this
                    .getPublishedProductSearchResponse(preferredCountryCriteria);
            if (countriesSearchResponse != null) {
                int productsCount = countriesSearchResponse.getProductsCount();
                for (int i = 0; i < productsCount; i++) {
                    PublishedProduct publishedProduct = countriesSearchResponse.getProducts(i);
                    if (!isPreferredProductListContainsProduct(publishedProduct)) {
                        preferredProducts.add(publishedProduct);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Unable to retrieve the preferred products for the criterion code : " + criterionCode, e);
        }
    }

    /**
     * Gets the products codes.
     * @param productList The preferred products list
     * @return The product codes
     */
    private String[] getExcludedProductsCodes(List<PublishedProduct> productList) {
        List<String> prefProductCodeList = new ArrayList<String>();
        for (PublishedProduct publishedProduct : productList) {
            prefProductCodeList.add("!" + publishedProduct.getCode());
        }
        return (String[]) prefProductCodeList.toArray(new String[prefProductCodeList.size()]);
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
        criterion.setValue(new CriterionValue[] {criterionValue});
        finalProductSearchCriteria.addCustomCriteria(criterion);
        finalProductSearchCriteria.setFirstResultIndex(0);
        finalProductSearchCriteria.setLastResultIndex(this.getMaxPreferredProducts(context));
        finalProductSearchCriteria.setSortBy(Constants.CriterionConstants.CRITERION_CUSTOMER_FIELD
                + Constants.CriterionConstants.SELECTION_CRITERION_CODE);
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
        return currentSearchCriteria;
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
        Boolean b2bRequest = (Boolean) this.getEnvironment().getRequest().getSession().getAttribute("b2bRequest");
        BrandData brandData = Util.addBrandContext(this.getEnvironment().getRequest(), context);
        String partnerId = this.getEnvironment().getRequestParameter("partnerId");

        if ((b2bRequest != null && b2bRequest)
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

        MediaTagTrackingStats mediaTagTrackingData = TrackingUtil.addDepMonthFromRqToCookie(this.getEnvironment(), trackingData);
        context.write("mediaTagStats", mediaTagTrackingData);
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
            if (com.travelsoft.cameleo.catalog.interfaces.Constants.SortConstants.SORT_BASE_PRICE_COEFF
                    .equals(currentSearchCriteria.getSortBy())) {

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
}
