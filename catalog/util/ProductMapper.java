/*
 * Created on 25 sept. 2012
 *
 * Copyright Travelsoft, 2012.
 */
package com.travelsoft.lastminute.catalog.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.exolab.castor.types.Date;

import com.travelsoft.cameleo.catalog.data.City;
import com.travelsoft.cameleo.catalog.data.Criterion;
import com.travelsoft.cameleo.catalog.data.CriterionValue;
import com.travelsoft.cameleo.catalog.data.Disponibility;
import com.travelsoft.cameleo.catalog.data.MainZone;
import com.travelsoft.cameleo.catalog.data.PublishedProduct;
import com.travelsoft.lastminute.data.SmallProductDisplayable;

/**
 * <p>Title: ProductMapper.java.</p>
 * <p>Description: .</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Travelsoft</p>
 *
 * @author thiago.bohme
 */
public final class ProductMapper {

    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(ProductMapper.class);


    /** No constructor for utility class. */
    private ProductMapper() {

    }

    /**
     *
     * @param product
     * @return
     */
    public static SmallProductDisplayable marshallForLastMinuteDeparture(PublishedProduct product) {
        if (product == null) {
            return null;
        }
        SmallProductDisplayable dProduct = new SmallProductDisplayable();
        dProduct.setId(product.getCode());

        retrieveDestinationCityAndCountry(dProduct, product);

        if (product.getEdito() != null) {
            MainZone titleZone = Util.getEditoMainZone(product.getEdito(), "title");
            String title = Util.getZoneContent(titleZone);
            dProduct.setTitle(title);
        }

        return dProduct;
    }

    /**
     *
     * @param dProduct
     * @param dispo
     */
    public static void addInfosFromDisponibility(SmallProductDisplayable dProduct, Disponibility dispo) {
        if (dProduct == null || dispo == null) {
            return;
        }

        if (dispo.getMealPlan()!= null && StringUtils.isNotBlank(dispo.getMealPlan().getCode())) {
            dProduct.setPensions(dispo.getMealPlan().getCode());
        }


    }

    /**
     * Fill small displayable product price by base disponibility.
     *
     * @param dProduct the small product displayer
     * @param product the published product
     */
    /*public static void fillDispoPrices(SmallProductDisplayable dProduct, Disponibility dispo) {
        if (dProduct != null && dispo != null) {
            BigDecimal promoReduc = dispo.getPromoReduc();
            if (promoReduc != null && dispo.getInPromotion() != null && BigDecimal.ZERO.compareTo(promoReduc) != 0
                    && "Oui".equals(dispo.getInPromotion())) {

                BigDecimal ttcPricePromo = dispo.getTtcPrice();
                BigDecimal promoPercentage = promoReduc.multiply(ONE_HUNDRED);
                BigDecimal brochurePrice = dispo.getBrochurePrice();
                BigDecimal differenceTTC = null;
                BigDecimal toTtcPrice = dispo.getToTtcPrice();
                BigDecimal price = null;
                if (promoReduc.compareTo(BigDecimal.ZERO) == -1) {
                    dProduct.setBasePrice(dispo.getTtcPrice());
                    promoPercentage = BigDecimal.ZERO;
                    dProduct.setPromoPercentage(promoPercentage);
                } else {
                    if (brochurePrice != null) {
                        differenceTTC = brochurePrice.subtract(ttcPricePromo);
                        price = brochurePrice;
                    } else if (toTtcPrice != null) {
                        differenceTTC = toTtcPrice.subtract(ttcPricePromo);
                        price = toTtcPrice;
                    }
                    dProduct.setBasePrice(price);
                    dProduct.setPromoPrice(ttcPricePromo);
                    dProduct.setDiffencePrice(differenceTTC);
                    dProduct.setPromoPercentage(promoPercentage);
                }
            } else {
                BigDecimal ttcPrice = dispo.getTtcPrice();
                dProduct.setBasePrice(ttcPrice);
            }
        }
    }*/

    /**
     * Retrieves the destination city and country.
     *
     * @param productDisplayable
     *            the value object for product displaying
     * @param product
     *            the published product
     */
    public static void retrieveDestinationCityAndCountry(SmallProductDisplayable productDisplayable, PublishedProduct product) {
        try {
            Criterion[] criterionArray =
                    product.getTechnicalInfo().getCriterion();

            final Map<String, CriterionValue> countriesMap =
                    new LinkedHashMap<String, CriterionValue>();
            final Map<String, CriterionValue> citiesMap =
                    new LinkedHashMap<String, CriterionValue>();

            if (criterionArray != null) {
                for (Criterion criterion : criterionArray) {

                    // pour chaque criterion 'destination'
                    if (criterion != null
                        && Constants.CriterionConstants.CRITERION_DESTIONATION_CODE.equals(
                                criterion.getCode())) {

                        CriterionValue[] criterionValues = criterion.getValue();

                        // pour chaque criterionValue associee pour ce produit
                        if (criterionValues != null) {
                            for (CriterionValue criterionValue : criterionValues) {

                                if (criterion != null && criterionValue.getCode() != null) {

                                    if (criterionValue.getCode().indexOf('.') == -1) {
                                        // s'il s'agit d'un pays, on l'ajoute à la map des pays
                                        countriesMap.put(
                                                criterionValue.getCode(),
                                                criterionValue);
                                    } else {
                                        // s'il s'agit d'une ville, on l'ajoute à la map des villes
                                        citiesMap.put(
                                                criterionValue.getCode(),
                                                criterionValue);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (!citiesMap.isEmpty()) {
                // on set en priorité la ville criterisee
                // et sont parent en tant de pays
                final CriterionValue anyCity =
                        citiesMap.values().iterator().next();
                productDisplayable.setDestinationCity(
                        anyCity.getLabel().toLowerCase());
                productDisplayable.setDestinationTitle(
                        getParentCode(anyCity));

            } else if (!countriesMap.isEmpty()) {
                // si pas de ville, mais pays critérisé
                // on set le pays critérisé uniquement
                final CriterionValue anyCountry =
                        countriesMap.values().iterator().next();
                productDisplayable.setDestinationTitle(
                        anyCountry.getLabel().toLowerCase());
            }

        } catch (Exception e) {
            LOGGER.error(e.toString(), e);
        }
    }

    /**
     * Gets the parent criterionValue code.
     * @param criterionValue
     *            the child
     * @return parent criterionValue code if any, or null
     */
    private static String getParentCode(final CriterionValue criterionValue) {
        final String code = criterionValue.getCode();
        final int lastIndexOfDot = code.lastIndexOf('.');
        if (lastIndexOfDot == -1) {
            return null;
        } else {
            return code.substring(0, lastIndexOfDot);
        }
    }
}
