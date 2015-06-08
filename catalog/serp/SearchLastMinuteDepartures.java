/*
 * Created on 17 oct. 2012
 *
 * Copyright Travelsoft, 2012.
 */
package com.travelsoft.lastminute.catalog.serp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.data.Disponibility;
import com.travelsoft.cameleo.catalog.data.Document;
import com.travelsoft.cameleo.catalog.data.MainZone;
import com.travelsoft.cameleo.catalog.data.PageLayoutComponent;
import com.travelsoft.cameleo.catalog.data.PublishedProduct;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchCriteria;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchResponse;
import com.travelsoft.cameleo.catalog.data.Pushing;
import com.travelsoft.cameleo.catalog.data.PushingCategory;
import com.travelsoft.cameleo.catalog.data.PushingField;
import com.travelsoft.cameleo.catalog.interfaces.ServicesFactory;
import com.travelsoft.cameleo.catalog.interfaces.pushing.PushingServicesInterface;
import com.travelsoft.cameleo.catalog.interfaces.search.PublishedProductSearchServicesInterface;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.lastminute.catalog.comparator.LastMinuteDepartureComparator;
import com.travelsoft.lastminute.catalog.seo.SeoTool;
import com.travelsoft.lastminute.catalog.util.CollectionUtil;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.ProductMapper;
import com.travelsoft.lastminute.catalog.util.Util;
import com.travelsoft.lastminute.data.LastMinuteDeparture;
import com.travelsoft.lastminute.data.LastMinuteDeparturesCities;
import com.travelsoft.lastminute.data.SmallProductDisplayable;

/**
 * <p>Title: SearchLastMinuteDepartures.java.</p>
 * <p>Description: .</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Travelsoft</p>
 *
 * @author thiago.bohme
 */
public final class SearchLastMinuteDepartures {

    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(SearchLastMinuteDepartures.class);

    /** No constructor for utility class. */
    private SearchLastMinuteDepartures() {
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @return
     */
    public static LastMinuteDeparturesCities searchLastMinutesDeparturesCities() {
        LastMinuteDeparturesCities depCities = null;

        try {
            PushingServicesInterface service = ServicesFactory.getPushingServices();
            PushingCategory[] categories = service.getPushingCategories();

            if (CollectionUtil.isNotBlank(categories)) {
                depCities = new LastMinuteDeparturesCities();
                int prefixLength = Constants.Common.LAST_MINUTE_SEARCH_PUSHING_CODE_PREFIX.length();
                for (PushingCategory cat : categories) {
                    if (StringUtils.isNotBlank(cat.getCode())
                            && cat.getCode().startsWith(Constants.Common.LAST_MINUTE_SEARCH_PUSHING_CODE_PREFIX)) {
                        depCities.addCities(cat.getCode().substring(prefixLength));
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.warn("Last minutes seraches links building has failed.", e);
        }

        return depCities;
    }

    /**
     *
     * @param context
     * @param departureCity
     * @return
     */
    public static List<LastMinuteDeparture> searchLastMinutesDepartures(
            IComponentContext<PageLayoutComponent> context, String departureCity) {

        PublishedProductSearchCriteria searchCriteria = buildSearchProductsCriteria(context, departureCity);

        PublishedProductSearchServicesInterface service = ServicesFactory.getPublishedProductSearchServices();
        PublishedProductSearchResponse searchProductsResponse = null;
        try {
            searchProductsResponse = service.searchProducts(searchCriteria, null);
        } catch (Exception e) {
            LOGGER.warn("Products search failed. Process aborted.", e);
        }

        if (searchProductsResponse != null && CollectionUtil.isNotBlank(searchProductsResponse.getProducts())) {
            Map<String, PublishedProduct> productsByCode =
                    new HashMap<String, PublishedProduct>(searchProductsResponse.getProducts().length + 1);

            PublishedProduct product;

            String[] productCodes = new String[searchProductsResponse.getProducts().length];
            for (int i = 0; i < searchProductsResponse.getProducts().length; i++) {
                product = searchProductsResponse.getProducts()[i];
                productCodes[i] = product.getCode();
                productsByCode.put(product.getCode(), product);
            }

            updateCriteriaForDisposSearch(searchCriteria, productCodes);

            PublishedProductSearchResponse searchDisposResponse = null;
            try {
                searchDisposResponse = service.searchProducts(searchCriteria, null);
            } catch (Exception e) {
                 LOGGER.warn("Dispos search failed. Process aborted.", e);
            }

            return SearchLastMinuteDepartures.buildAndSortDepartures(searchDisposResponse, productsByCode);
        }

        return null;
    }

    /**
     *
     * @param searchDisposResponse
     * @param productsByCode
     * @return
     */
    private static List<LastMinuteDeparture> buildAndSortDepartures(
            PublishedProductSearchResponse searchDisposResponse, Map<String, PublishedProduct> productsByCode) {

        if (searchDisposResponse != null && CollectionUtil.isNotBlank(searchDisposResponse.getProducts())) {
            List<LastMinuteDeparture> departures =
                    new ArrayList<LastMinuteDeparture>(productsByCode.size() * 2);
            Map<String, List<LastMinuteDeparture>> departuresByProductCode =
                    new HashMap<String, List<LastMinuteDeparture>>(productsByCode.size() + 1);
            Map<String, SmallProductDisplayable> dProductsByCode =
                    new HashMap<String, SmallProductDisplayable>(productsByCode.size() + 1);
            PublishedProduct product;
            String productCode;
            Disponibility[] dispos;
            SmallProductDisplayable dProduct;

            for (int i = 0; i < searchDisposResponse.getProducts().length; i++) {
                if (searchDisposResponse.getProducts()[i].getTechnicalInfo() == null
                        || CollectionUtil.isBlank(searchDisposResponse.getProducts()[i].getTechnicalInfo().getDisponibility())) {
                    continue;
                }

                dispos = searchDisposResponse.getProducts()[i].getTechnicalInfo().getDisponibility();

                productCode = searchDisposResponse.getProducts()[i].getCode();
                product = productsByCode.get(productCode);
                LastMinuteDeparture departure;

                if (product == null) {
                    continue;
                }

                for (Disponibility dispo : dispos) {
                    Pushing currentPushing = respectsDepartureDateConstraints(
                            dispo, productsByCode.get(searchDisposResponse.getProducts()[i].getCode()).getPushing());
                    if (currentPushing == null) {
                        continue;
                    }

                    if (!departuresByProductCode.containsKey(productCode)) {
                        departuresByProductCode.put(productCode, new ArrayList<LastMinuteDeparture>(15));
                    }

                    departure = null;
                    for (LastMinuteDeparture existingDeparture : departuresByProductCode.get(productCode)) {
                        if (existingDeparture.getDate().equals(dispo.getDepartureDate())) {
                            departure = existingDeparture;
                            if (dispo.getTtcPrice().compareTo(departure.getAvail().getTtcPrice()) < 0) {
                                departure.setAvail(dispo);
                            }
                            break;
                        }
                    }

                    for (LastMinuteDeparture existingDeparture : departuresByProductCode.get(productCode)) {
                        if (existingDeparture.getDate().equals(dispo.getDepartureDate())) {
                            departure = existingDeparture;
                            String nbNight = retrievePushNbNights(currentPushing);
                            if (nbNight != null && !nbNight.isEmpty()) {
                                try {
                                    if (Integer.parseInt(nbNight) == dispo.getDurationInNights()) {
                                        dProduct = dProductsByCode.get(productCode);
                                        if (dProduct == null) {
                                            dProduct = ProductMapper.marshallForLastMinuteDeparture(product);
                                            dProductsByCode.put(productCode, dProduct);
                                        }
                                        dProduct = (SmallProductDisplayable) dProduct.clone();
                                        ProductMapper.addInfosFromDisponibility(dProduct, dispo);
                                        fillPrices(dProduct, dispo);
                                        departure.setDProduct(dProduct);
                                    }
                                } catch (NumberFormatException e) {
                                    if (LOGGER.isDebugEnabled()) {
                                        LOGGER.debug("The format of input number is not correct! " + e);
                                    }
                                }
                            }
                            break;
                        }
                    }

                    if (departure == null) {
                        departure = new LastMinuteDeparture();
                        departure.setAvail(dispo);
                        departure.setDate(dispo.getDepartureDate());

                        dProduct = dProductsByCode.get(productCode);
                        if (dProduct == null) {
                            dProduct = ProductMapper.marshallForLastMinuteDeparture(product);
                            dProductsByCode.put(productCode, dProduct);
                        }
                        dProduct = (SmallProductDisplayable) dProduct.clone();
                        ProductMapper.addInfosFromDisponibility(dProduct, dispo);
                        fillPrices(dProduct, dispo);
                        departure.setDProduct(dProduct);

                        String productName = "";
                        Document edito = product.getEdito();
                        if (edito != null) {
                            MainZone titleZone = Util.getEditoMainZone(edito, "title");
                            productName = Util.getZoneContent(titleZone);
                        }

                        departure.setSeoUrl(SeoTool.getProductNameSeoUrl(productName));
                        String desc = retrievePushDescription(currentPushing);
                        if (desc != null) {
                            departure.getDProduct().setTitle(desc);
                        }

                        departuresByProductCode.get(productCode).add(departure);
                        departures.add(departure);
                    }
                }
            }

            Collections.sort(departures, new LastMinuteDepartureComparator());
            return departures;
        }
        return null;
    }

    /**
     * Retrieves the pushing description.
     * @param pushing The pushing
     * @return  the pushing description
     */
    private static String retrievePushDescription(Pushing pushing) {
          for (PushingField field : pushing.getField()) {
              if (Constants.Common.LAST_MINUTE_SEARCH_PUSHING_DESCRIPTION_CODE.equals(field.getCode())) {
                  if (StringUtils.isNotBlank(field.getValue())) {
                      return field.getValue();
                  }
              }
          }
        return null;
    }

    /*
    * Retrieves the pushing number of nights.
    * @param pushing The pushing
    * @return  the pushing number of nights
    */
   private static String retrievePushNbNights(Pushing pushing) {
       for (PushingField field : pushing.getField()) {
           if (Constants.Common.LAST_MINUTE_SEARCH_PUSHING_NB_NIGHTS_CODE.equals(field.getCode())) {
               if (StringUtils.isNotBlank(field.getValue())) {
                   return field.getValue();
               }
           }
       }
       return null;
   }
    /**
     * Retrieves the current search criteria (The criteria from the search engine).
     * @param context The page layout context
     * @param queryString The query string
     * @return The current criteria
     */
    private static PublishedProductSearchCriteria buildSearchProductsCriteria(
            IComponentContext<PageLayoutComponent> context, String departureCity) {

        PublishedProductSearchCriteria searchCriteria = Util.retrieveSearchCriteria(context,
                Constants.Config.DEFAULT_CRITERIA_KEY);

        searchCriteria.setResultWithPushings(true);
        searchCriteria.setResultWithCriterion(true);
        searchCriteria.setResultWithDocuments(true);
        searchCriteria.setResultWithBaseAvail(false);
        searchCriteria.setResultWithAvails(false);

        searchCriteria.setDepartureCityCode(new String[] {departureCity});
        searchCriteria.setPushingCategory(Constants.Common.LAST_MINUTE_SEARCH_PUSHING_CODE_PREFIX + departureCity);

        String configValue = Util.retrieveConfigFromPageDescription(context, "departuresWithin");
        int departuresWithin = 14;
        if (StringUtils.isNotBlank(configValue)) {
            try {
                departuresWithin = Integer.valueOf(configValue);
            } catch (NumberFormatException e) {
                departuresWithin = 14;
            }
        }
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, departuresWithin);
        searchCriteria.setMaxDepartureDate(new org.exolab.castor.types.Date(cal.getTime()));
        searchCriteria.setMinDepartureDate(new org.exolab.castor.types.Date());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Search products criteria: " + searchCriteria.convertToString());
        }

        return searchCriteria;
    }

    /**
     *
     * @param dProduct
     * @param dispo
     */
    private static void fillPrices(SmallProductDisplayable dProduct, Disponibility dispo) {
        if (dProduct != null && dispo != null) {
            dProduct.setBasePrice(dispo.getTtcPrice());

            if ("Oui".equals(dispo.getInPromotion())) {
                BigDecimal oldPrice = BigDecimal.ZERO;
                if (dispo.getBrochurePrice() != null) {
                    oldPrice = dispo.getBrochurePrice();

                } else if (dispo.getToTtcPrice() != null) {
                    oldPrice = dispo.getToTtcPrice();

                } else {
                    return;
                }

                BigDecimal perc = oldPrice.subtract(dispo.getTtcPrice()).divide(oldPrice, 2, RoundingMode.HALF_UP);
                if (perc.compareTo(new BigDecimal(0.2)) > 0) {
                    dProduct.setBasePrice(oldPrice);
                    dProduct.setPromoPrice(dispo.getTtcPrice());
                    dProduct.setPromoPercentage(perc.multiply(new BigDecimal(100)));
                }
            }
        }
    }

    /**
     *
     * @param criteria
     * @param productCodes
     */
    private static void updateCriteriaForDisposSearch(PublishedProductSearchCriteria criteria, String[] productCodes) {
        criteria.setResultWithPushings(false);
        criteria.setResultWithCriterion(false);
        criteria.setResultWithDocuments(false);
        criteria.setResultWithAvails(true);
        criteria.setCode(productCodes);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Search dispos criteria: " + criteria.convertToString());
        }
    }

    /**
     *
     * @param dispo
     * @param pushings
     * @return
     */
    private static Pushing respectsDepartureDateConstraints(Disponibility dispo, Pushing[] pushings) {
        if (dispo.getDepartureDate() != null) {
            for (Pushing pushing : pushings) {
                if (pushing.getProductDatePeriod() != null) {
                    if (pushing.getProductDatePeriod().getFrom() != null
                            && pushing.getProductDatePeriod().getFrom().compareTo(dispo.getDepartureDate()) - 1 > 0) {
                        continue;
                    }
                    if (pushing.getProductDatePeriod().getTo() != null
                            && pushing.getProductDatePeriod().getTo().compareTo(dispo.getDepartureDate()) - 1 < 0) {
                        continue;
                    }
                }
                return pushing;
            }
        }
        return null;
    }
}
