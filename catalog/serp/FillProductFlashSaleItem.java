/*
 * Created on 2 sept. 2011
 *
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.serp;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.data.Disponibility;
import com.travelsoft.cameleo.catalog.data.PageLayoutComponent;
import com.travelsoft.cameleo.catalog.data.PublishedProduct;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchCriteria;
import com.travelsoft.cameleo.catalog.data.Pushing;
import com.travelsoft.cameleo.catalog.data.PushingField;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.product.FillProductResaform;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.data.Disponibilities;

/**
 * <p>Titre : FillProductItem.</p>
 * Description : Fill the product item.
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Company : Travelsoft</p>
 * @author MengMeng
 * @version
 *
 */
public class FillProductFlashSaleItem extends FillProductResaform {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(FillProductFlashSaleItem.class);

    /**
     * Process business logic and produce the data model to fill the product item.
     * @param context the component's context
     */
    @Override
    public void fillComponentContext(IComponentContext<PageLayoutComponent> context) {

        PublishedProduct product = (PublishedProduct) context.lookup(Constants.Context.PUBLISHED_PRODUCT);
        context.write(Constants.Context.SOURCE_PRODUCT, product);

        Disponibility[] dispoArray = product.getTechnicalInfo().getDisponibility();
        Arrays.sort(dispoArray, new FlashSaleDispoComparator());
        product.getTechnicalInfo().setDisponibility(dispoArray);
        Disponibilities disponiblities = this.getDisponibilies(product, false, context);
        if (LOGGER.isDebugEnabled() && disponiblities != null) {
            LOGGER.debug("There is the disponibilities : " + disponiblities.convertToString());
        }
        context.write(Constants.Context.SHOW_DISPO_ECONOMY_COLUMN, showDispoEconomyColumn);
        context.write(Constants.Context.DISPONIBILITIES, disponiblities);
        context.write(Constants.Context.SELECT_OPTION_LIST, optionList);



    }

    public class FlashSaleDispoComparator implements Comparator<Disponibility> {

        public int compare(Disponibility o1, Disponibility o2) {

            int res = 0;

            if (o1.getDepartureCity() == null || o1.getDepartureCity().getCode() == null) {
                res = -1;
            } else if (o2.getDepartureCity() == null || o2.getDepartureCity().getCode() == null) {
                res = 1;
            } else {
                res = o1.getDepartureCity().getCode().compareTo(o2.getDepartureCity().getCode());
            }

            if (res == 0) {
                if (o1.getDurationInNights() < o2.getDurationInNights()) {
                    res = -1;
                } else if (o1.getDurationInNights() > o2.getDurationInNights()) {
                    res = 1;
                }
            }

            if (res == 0) {
                res = o1.getDepartureDate().compareTo(o2.getDepartureDate());
            }

            return res;
        }

    }

    protected Collection<Disponibility> filtreDispos(Collection<Disponibility> allDispos,IComponentContext<PageLayoutComponent> context) {
        //don't filtre
        return allDispos;
    }

    /**
     * Get the dispo which is indicated by pushing
     * @param product
     * @param context
     * @return
     */
    protected Disponibility getBaseDispo(PublishedProduct product, IComponentContext<PageLayoutComponent> context) {

        Pushing flashSalePushing = null;
        Disponibility baseDispo = product.getBasePriceDisponibility();

        for (Pushing pushing : product.getPushing()) {
            if (Constants.Common.LAST_MINUTE_FLASH_SALE_PUSHING_CODE.equals(pushing.getCategoryCodeLabel().getCode())) {
                flashSalePushing = pushing;
                break;
            }
        }

        if (flashSalePushing != null) {
            String departDate = null;
            String departCity = null;
            String nbNight = null;
            String nbDays = null;
            String initialPrice = null;

            for (PushingField field : flashSalePushing.getField()) {
                if (field.getCode().equals("DEPART_DATE")) {
                    departDate = field.getValue();
                } else if (field.getCode().equals("DEPART_CITY")) {
                    departCity = field.getValue();
                } else if (field.getCode().equals("NB_NIGHT")) {
                    nbNight = field.getValue();
                } else if (field.getCode().equals("NB_DAYS")) {
                    nbDays = field.getValue();
                } else if (field.getCode().equals("NB_DAYS")) {
                    nbDays = field.getValue();
                } else if (field.getCode().equals("PRIX_INIT")) {
                    initialPrice = field.getValue();
                }
            }

            context.write(Constants.Context.FLASE_SALE_INITIAL_PRICE, new BigDecimal(initialPrice));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            for (Disponibility dispo : product.getTechnicalInfo().getDisponibility()) {
                if (!StringUtils.isBlank(departCity) && !dispo.getDepartureCity().getCode().equals(departCity)) {
                    continue;
                }
                if (!StringUtils.isBlank(nbNight) && !String.valueOf(dispo.getDurationInNights()).equals(nbNight)) {
                    continue;
                }
                if (!StringUtils.isBlank(nbDays) && !String.valueOf(dispo.getDurationInDays()).equals(nbDays)) {
                    continue;
                }
                if (!StringUtils.isBlank(departDate) && !sdf.format(dispo.getDepartureDate().toDate()).equals(departDate)) {
                    continue;
                }
                baseDispo = dispo;
                break;
            }
        }

        context.write(Constants.Context.FLASH_SALE_DISPO, baseDispo);

        PublishedProductSearchCriteria c = new PublishedProductSearchCriteria();
        c.setMinDepartureDate(baseDispo.getDepartureDate());
        context.write(Constants.Context.PRODUCT_SEARCH_CRITERIA, c);

        return baseDispo;
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
