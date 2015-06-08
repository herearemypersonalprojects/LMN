package com.travelsoft.lastminute.catalog.product;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.exolab.castor.types.Date;

import com.travelsoft.cameleo.catalog.data.City;
import com.travelsoft.cameleo.catalog.data.Disponibility;
import com.travelsoft.cameleo.catalog.data.PageLayoutComponent;
import com.travelsoft.cameleo.catalog.data.PublishedProduct;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchCriteria;
import com.travelsoft.cameleo.catalog.data.TechnicalInfo;
import com.travelsoft.cameleo.catalog.taglib.resform.DispoTool;
import com.travelsoft.cameleo.catalog.taglib.resform.ProductDisponibilities;
import com.travelsoft.cameleo.cms.processor.controller.AbstractStructuredController;
import com.travelsoft.cameleo.cms.processor.controller.WebProcessEnvironment;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.serp.DurationComparator;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.Util;
import com.travelsoft.lastminute.data.BookingInfo;
import com.travelsoft.lastminute.data.Disponibilities;
import com.travelsoft.lastminute.data.DisponibilityDisplayable;
import com.travelsoft.lastminute.data.DisponibilityMapKey;
import com.travelsoft.lastminute.data.Duration;
import com.travelsoft.lastminute.data.SelectOption;
import com.travelsoft.lastminute.data.SelectOptionList;
import com.travelsoft.lastminute.data.SmallProductDisplayable;

/**
 * <p>
 * Title: FillProductResaform.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Travelsoft
 * </p>
 *
 * @author MengMeng
 */
public class FillProductResaform extends AbstractStructuredController<PageLayoutComponent, WebProcessEnvironment> {

	/**
	 * Default period in months, for disponibilities display.
	 */
    static final int DEFAULT_DISPO_MONTHS_COUNT = 2;

	/** Logger. */
    private static final Logger LOGGER = Logger.getLogger(FillProductResaform.class);

    /** The preferred date (format dd/mm/yyyy). */
    private String preferredDepartureDate;

    /** Map containing all availabilities by map key. */
    Map<DisponibilityMapKey, Collection<Disponibility>> dispoListByKey;

    /** The resa data. */
    private BookingInfo bookingInfo;

    /** The locale. */
    private Locale locale;

    /** The select option list. */
    protected SelectOptionList optionList;

    /** The availabilities map key constructed by base availability. */
    DisponibilityMapKey baseMapKey;

    /** The attribute to show economy column. */
    protected boolean showDispoEconomyColumn;


    /**
     * Process business logic and produce the data model to fill the product resaform.
     *
     * @param context the component's context
     */
    @Override
    public void fillComponentContext(IComponentContext<PageLayoutComponent> context) {
        PublishedProduct product = (PublishedProduct) context.lookup(Constants.Context.PUBLISHED_PRODUCT);
        if (product != null) {
            WebProcessEnvironment webEnvironment = this.getEnvironment();
            if (webEnvironment != null) {
                preferredDepartureDate = webEnvironment.getRequestParameter(Constants.Common.PREFERED_DATE);
            }
            Disponibilities disponiblities = this.getDisponibilies(product, true, context);
            if (LOGGER.isDebugEnabled() && disponiblities != null) {
                LOGGER.debug("There is the disponibilities : " + disponiblities.convertToString());
            }
            context.write(Constants.Context.SHOW_DISPO_ECONOMY_COLUMN, showDispoEconomyColumn);
            context.write(Constants.Context.DISPONIBILITIES, disponiblities);
            context.write(Constants.Context.SELECT_OPTION_LIST, optionList);
            constructResaData(product);
            context.write("bookingInfo", bookingInfo);

            String partnerId = getPartnerId(webEnvironment);
            if (partnerId != null) {
                context.write(Constants.OmnitureConstants.PARTNER_ID, partnerId);
            }

            String source = getSource(webEnvironment);
            if (source != null) {
                context.write(Constants.OmnitureConstants.OMNITURE_SOURCE, source);
            }

            // This variable will be used in mediaTag on resa pages
            Cookie tripTypeCookie = Util.getCookie(this.getEnvironment(),
                Constants.Common.TRIP_TYPE_COOKIE);
            if (tripTypeCookie != null && tripTypeCookie.getValue() != null) {
                String tripType = tripTypeCookie.getValue();
                try {
                    tripType = URLDecoder.decode(tripType, Constants.Common.ENCODING_UTF8);
                    context.write(Constants.Common.TRIP_TYPE_COOKIE, tripType);
                } catch (UnsupportedEncodingException uee) {
                    // error will not be thrown with utf8 encoding
                }
            }

            // This variable will be used in mediaTag on resa pages
            Cookie hotelStarsCookie = Util.getCookie(this.getEnvironment(),
                Constants.Common.HOTEL_STARS_COOKIE);
            if (hotelStarsCookie != null && hotelStarsCookie.getValue() != null) {
                context.write(Constants.Common.HOTEL_STARS_COOKIE, hotelStarsCookie.getValue());
            }

            // This variable will be used in mediaTag on resa pages
            Cookie depMonthCookie = Util.getCookie(this.getEnvironment(),
                Constants.Common.DEP_MONTH_COOKIE);
            if (depMonthCookie != null && depMonthCookie.getValue() != null) {
                context.write(Constants.Common.DEP_MONTH_COOKIE, depMonthCookie.getValue());
            }

            // get city label from small displayable product : label is with accent.
            // This variable will be used in mediaTag on resa pages
            SmallProductDisplayable productDisplayable = (SmallProductDisplayable) context
              .lookup(Constants.Context.SMALL_PRODUCT_DISPLAYABLE);
            String destCities = productDisplayable.getDestinationCities();
            if (destCities.indexOf(Constants.Common.SEPARATOR_COMMA) == -1) {
                // there were no multiple cities
                String destCityLabel = destCities.replaceAll(" ", Constants.Common.SEPARATOR_UNDERSCORE).toLowerCase();
                context.write("destCityLabel", destCityLabel);
            }
        }
    }


    /**
     * Construct the select option list for departure date, departure city and duration.
     * @param product The product
     *
     * @param baseDispo the base dispo
     * @param context
     */
    private void constructSelectionOptionList(PublishedProduct product, Disponibility baseDispo,
        IComponentContext<PageLayoutComponent> context) {

        PublishedProduct sourceProduct = (PublishedProduct) context.lookup(Constants.Context.SOURCE_PRODUCT);

        Collection<Disponibility> allDispos = Arrays.asList(sourceProduct.getTechnicalInfo().getDisponibility());

        ProductDisponibilities productDisponibilities = new ProductDisponibilities(locale, allDispos);

        optionList = new SelectOptionList();
        Collection<City> citiesList = productDisponibilities.getDepCitiesList();
        int count = 0;
        int citiesListSize = citiesList.size();
        baseMapKey = retrieveDispoMapKey(baseDispo);
        String keyCityCode = baseMapKey.getDepartureCity();
        if (citiesList != null && citiesListSize > 0) {
            SelectOption[] cityOptions = new SelectOption[citiesListSize];
            for (City city : citiesList) {
                String cityCode = city.getCode();
                String cityLabel = city.getLabel();
                SelectOption cityOption = new SelectOption();
                if (cityCode.equals(keyCityCode)) {
                    cityOption.setSelected(true);
                }
                cityOption.setCode(cityCode);
                cityOption.setLabel(cityLabel);
                cityOptions[count] = cityOption;
                count++;
            }
            optionList.setDepartureCities(cityOptions);
        }
        SelectOption[] dateOptions = getSelectOptionList(sourceProduct, Constants.Product.DISPO_DATE, baseMapKey, context);
        SelectOption[] durationOptions = getSelectOptionList(sourceProduct, Constants.Product.DISPO_DURATION, baseMapKey, context);
        optionList.setDepartureDates(dateOptions);
        optionList.setDepartureDuration(durationOptions);

    }


    /**
     * Get the select option list by dispoListByKey.
     * @param product The product
     *
     * @param listName the list name(date or duration)
     * @param key the dispo map key
     * @param context The context
     * @return selectionOptionListByName
     */
    private SelectOption[] getSelectOptionList(PublishedProduct product, String listName, DisponibilityMapKey key,
        IComponentContext<PageLayoutComponent> context) {
        List<SelectOption> selectionOptionListByName = new ArrayList<SelectOption>();

        Collection<Disponibility> allDispos = Arrays.asList(product.getTechnicalInfo().getDisponibility());

        Set<DisponibilityMapKey> dispoKeySet = retrieveDispoMayBykey(allDispos).keySet();
        List<Duration> durationList = new ArrayList<Duration>();
        List<Calendar> dateList = new ArrayList<Calendar>();
        if (dispoKeySet != null) {
            for (DisponibilityMapKey disponibilityMapKey : dispoKeySet) {
                if (Constants.Product.DISPO_DATE.equals(listName)) {
                    Calendar dispoCal = disponibilityMapKey.getDepartureMonthYear().toCalendar();
                    if (!dateList.contains(dispoCal)) {
                        dateList.add(dispoCal);
                    }
                } else if (Constants.Product.DISPO_DURATION.equals(listName)) {
                    Duration duration = (Duration) disponibilityMapKey.getDuration();
                    if (!durationList.contains(duration)) {
                        durationList.add(duration);
                    }
                }
            }
        }
        if (Constants.Product.DISPO_DURATION.equals(listName)) {
            if (durationList.size() > 0) {
                Duration[] durationsTable = new Duration[durationList.size()];
                int durationCount = 0;
                for (Duration duration : durationList) {
                    durationsTable[durationCount] = duration;
                    durationCount++;
                }
                DurationComparator durationComparator = new DurationComparator();
                Arrays.sort(durationsTable, durationComparator);
                for (int i = 0; i < durationsTable.length; i++) {
                    SelectOption selectOption = new SelectOption();
                    Duration duration = durationsTable[i];
                    Duration baseKeyDuration = (Duration) key.getDuration();
                    if (baseKeyDuration.equals(duration)) {
                        selectOption.setSelected(true);
                    }

                    String durationLabel = Util.appendString(String.valueOf(duration.getDays()
                        + this.getDayAdjust(product, null)),
                        " jours", " / ", String.valueOf(duration.getNights()), " nuits");
                    String durationCode = Util.appendString(String.valueOf(duration.getDays()), ",", String
                        .valueOf(duration.getNights()));
                    selectOption.setCode(durationCode);
                    selectOption.setLabel(durationLabel);
                    selectionOptionListByName.add(selectOption);
                }
            }
        } else if (Constants.Product.DISPO_DATE.equals(listName)) {
            if (dateList.size() > 0) {
                Calendar[] datesTable = new Calendar[dateList.size()];
                int dateCount = 0;
                for (Calendar date : dateList) {
                    datesTable[dateCount] = date;
                    dateCount++;
                }
                Arrays.sort(datesTable);


                SimpleDateFormat sdfCode = new SimpleDateFormat(Constants.Common.SELECT_OPTION_CODE_DATE_FORMAT,
                    Locale.FRANCE);
                SimpleDateFormat sdfLabel = new SimpleDateFormat(Constants.Common.SELECT_OPTION_LABEL_DATE_FORMAT,
                    Locale.FRANCE);

                PublishedProductSearchCriteria searchCriteria = (PublishedProductSearchCriteria) context
                    .lookup(Constants.Context.PRODUCT_SEARCH_CRITERIA);
                Date minDepartureDate = searchCriteria.getMinDepartureDate();

                String defaultSelectOptionDepartureDate = null;
                if (minDepartureDate != null) {
                    defaultSelectOptionDepartureDate = sdfCode.format(minDepartureDate.toDate());
                }


                for (int i = 0; i < datesTable.length; i++) {
                    SelectOption selectOption = new SelectOption();
                    Calendar calendar = datesTable[i];
                    String dateCode = sdfCode.format(calendar.getTime());
                    String dateLabel = sdfLabel.format(calendar.getTime());
                    if (defaultSelectOptionDepartureDate != null && defaultSelectOptionDepartureDate.equals(dateCode)) {
                        selectOption.setSelected(true);
                    }
                    selectOption.setCode(dateCode);
                    selectOption.setLabel(dateLabel);

                    if (!selectionOptionListByName.contains(selectOption)) {
                        selectionOptionListByName.add(selectOption);
                    }
                }
            }
        }
        int size = selectionOptionListByName.size();
        int count = 0;
        SelectOption[] optionTable = new SelectOption[size];
        for (SelectOption option : selectionOptionListByName) {
            optionTable[count] = option;
            count++;
        }
        return optionTable;
    }


    /**
     * Construct the bookingInfo data.
     *
     * @param product the published product
     */
    private void constructResaData(PublishedProduct product) {
        bookingInfo = new BookingInfo();
        TechnicalInfo techInfo = product.getTechnicalInfo();
        if (techInfo != null && techInfo.getTourOperator() != null) {
            String providerCode = techInfo.getTourOperator().getCode();
            bookingInfo.setProvider(providerCode);
            boolean forbiddenBaby = techInfo.getForbiddenBaby();
            bookingInfo.setForbiddenBaby(forbiddenBaby);
            boolean forbiddenChild = techInfo.getForbiddenChild();
            bookingInfo.setForbiddenChild(forbiddenChild);
        }
        String productCode = product.getCode();
        bookingInfo.setCatalogCode(productCode);
        String toCode = product.getToProductCode();
        bookingInfo.setToCode(toCode);
    }


    /**
     * Get the availabilities's by the key which is retrieved
     * by base availability.
     *
     * @return dispoList the availability list
     */
    Collection<Disponibility> getDisponibilityListByKey() {
        Collection<Disponibility> dispoList = new ArrayList<Disponibility>();
        Calendar calendar = Calendar.getInstance();
        if (dispoListByKey != null) {
            if (preferredDepartureDate == null ||
            		"".equals(preferredDepartureDate)) {
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                Date date = new Date(calendar.getTime());
                baseMapKey.setDepartureMonthYear(date);
                dispoList = dispoListByKey.get(baseMapKey);

                // on ajoute les dispos des mois suivants si aucune date saisie
                for (int i = 1 ; i <= DEFAULT_DISPO_MONTHS_COUNT ; ++i ) {
	                calendar.add(Calendar.MONTH, 1);
	                date = new Date(calendar.getTime());
	                baseMapKey.setDepartureMonthYear(date);
	                Collection<Disponibility> secondeDispoList =
	                		dispoListByKey.get(baseMapKey);

	                if (secondeDispoList != null) {
	                    if (dispoList != null) {
	                        dispoList.addAll(dispoListByKey.get(baseMapKey));
	                    } else {
	                        dispoList = dispoListByKey.get(baseMapKey);
	                    }
	                }
                }

                if (dispoList == null) {
                    for (int i = 0; i < 12; i++) {
                        calendar.add(Calendar.MONTH, 1);
                        date = new Date(calendar.getTime());
                        baseMapKey.setDepartureMonthYear(date);
                        Collection<Disponibility> disponibilityList =
                        		dispoListByKey.get(baseMapKey);
                        if (disponibilityList != null) {
                            dispoList = disponibilityList;
                            break;
                        }
                    }
                }
            } else {
                List<DisponibilityMapKey> keyList =
                		new ArrayList<DisponibilityMapKey>();
                for (DisponibilityMapKey mapKey : dispoListByKey.keySet()) {
                    String cityCode = mapKey.getDepartureCity();
                    Duration duration = (Duration) mapKey.getDuration();
                    if (baseMapKey.getDuration().equals(duration) &&
                    		baseMapKey.getDepartureCity().equals(cityCode)) {
                        keyList.add(mapKey);
                    }
                }
                if (keyList != null && keyList.size() > 0) {
                    for (int i = keyList.size() - 1; i >= 0; i--) {
                        DisponibilityMapKey mapKey = keyList.get(i);
                        Collection<Disponibility> selectDispoList =
                        		dispoListByKey.get(mapKey);
                        if (selectDispoList != null) {
                            dispoList.addAll(selectDispoList);
                        }
                    }
                }
            }
        }
        return dispoList;
    }

    protected Collection<Disponibility> filtreDispos(Collection<Disponibility> allDispos,IComponentContext<PageLayoutComponent> context) {
        return getDisponibilityListByKey();
    }

    protected Disponibility getBaseDispo(PublishedProduct product, IComponentContext<PageLayoutComponent> context) {
        return product.getBasePriceDisponibility();
    }

    /**
     * Get the disponibility list.
     *
     * @param product PublishedProduct
     * @param context The current context
     * @return disponibilities Disponibilities
     */
    protected Disponibilities getDisponibilies(PublishedProduct product, boolean checkSignature,
        IComponentContext<PageLayoutComponent> context) {
        Disponibilities disponibilities = new Disponibilities();
        locale = new Locale("FR");
        Collection<Disponibility> allDispos = Arrays.asList(product.getTechnicalInfo().getDisponibility());
        constructDispoMayBykey(allDispos);
        if (product.getBasePriceDisponibility() != null) {
            Disponibility baseDispo = getBaseDispo(product, context);
            constructSelectionOptionList(product, baseDispo, context);
            allDispos = filtreDispos(allDispos, context);
            if (allDispos != null) {
                DisponibilityDisplayable[] disponibilityDisplayableTable = new DisponibilityDisplayable[allDispos
                    .size()];
                int count = 0;
                Set<String> signatureSet = new HashSet<String>((int) (allDispos.size() / 0.7));
                int dayAdjust = this.getDayAdjust(product, context);
                for (Disponibility disponibility : allDispos) {

                    DisponibilityDisplayable dispoDisplayable = new DisponibilityDisplayable();
                    String signature = DispoTool.getDispoSignature(disponibility, locale);
                    if (checkSignature) {
                        if (signatureSet.contains(signature)) {
                            continue;
                        }
                        signatureSet.add(signature);
                    }
                    dispoDisplayable.setSignature(signature);

                    String departureCity = disponibility.getDepartureCity().getLabel();
                    String departureCityCode = disponibility.getDepartureCity().getCode();
                    dispoDisplayable.setDepartureCity(departureCity);
                    dispoDisplayable.setDepartureCityCode(departureCityCode);
                    int durationNights = disponibility.getDurationInNights();
                    int durationDays = disponibility.getDurationInDays();

                    String duration = Util.appendString(String.valueOf(durationDays + dayAdjust), "j/", String
                        .valueOf(durationNights), "n");
                    dispoDisplayable.setDuration(duration);
                    disponibilityDisplayableTable[count] = dispoDisplayable;
                    BigDecimal tdTtcPrice = disponibility.getTtcPrice();
                    if (disponibility.getPromoReduc() != null && disponibility.getInPromotion() != null
                                    && (disponibility.getPromoReduc().compareTo(BigDecimal.ZERO) != 0)
                                    && "Oui".equals(disponibility.getInPromotion())) {
                        BigDecimal brochureTTC = disponibility.getBrochurePrice();
                        BigDecimal toTtcPrice = baseDispo.getToTtcPrice();
                        BigDecimal economyPrice = null;
                        BigDecimal promoReduc = disponibility.getPromoReduc();
                        if (brochureTTC != null) {
                            economyPrice = brochureTTC.subtract(tdTtcPrice);
                        } else if (toTtcPrice != null) {
                            economyPrice = toTtcPrice.subtract(tdTtcPrice);

                            if (promoReduc != null && promoReduc.compareTo(BigDecimal.ZERO) != 0) {
                                dispoDisplayable.setPercentage(promoReduc.multiply(BigDecimal.valueOf(100)));
                            }
                        }
                        if (economyPrice != null && (economyPrice.compareTo(BigDecimal.ZERO) > 0)) {
                            showDispoEconomyColumn = true;
                            dispoDisplayable.setEconomyPrice(economyPrice);
                            if (promoReduc != null && promoReduc.compareTo(BigDecimal.ZERO) != 0) {
                                dispoDisplayable.setPercentage(promoReduc.multiply(BigDecimal.valueOf(100)));
                            }
                        }
                    }
                    dispoDisplayable.setPrice(tdTtcPrice);
                    org.exolab.castor.types.Date departureDate = disponibility.getDepartureDate();
                    Calendar returnCalendar = departureDate.toCalendar();
                    returnCalendar.add(Calendar.DAY_OF_MONTH, durationDays - 1 + dayAdjust);
                    Date returnDate = new Date(returnCalendar.getTime());
                    String returnDateString = Util.retrieveDateMessage(returnDate);
                    dispoDisplayable.setReturnDate(returnDateString);
                    this.retrieveAdjustDate(dispoDisplayable, returnDate, durationDays + dayAdjust);
                    String departureDateString = Util.retrieveDateMessage(departureDate);
                    dispoDisplayable.setDepartureDate(departureDateString);
                    String supplementDescription = disponibility.getComments();
                    dispoDisplayable.setSupplementDescription(supplementDescription);
                    count++;
                }
                disponibilities = new Disponibilities();
                searchLowestPrice(disponibilityDisplayableTable);
                for (DisponibilityDisplayable disponibilityDisplayable : disponibilityDisplayableTable) {
                    if (disponibilityDisplayable != null) {
                        disponibilities.addDisponibilityDisplayable(disponibilityDisplayable);
                    }
                }
            }
        }
        return disponibilities;
    }

    /**
     * @param dispoDisplayable The product to display
     * @param returnDate The return date
     * @param adjustedDays The adjusted duration in days
     */
    private void retrieveAdjustDate(DisponibilityDisplayable dispoDisplayable, Date returnDate, int adjustedDays) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.Common.ADJUST_DISPO_DATE_FORMAT, Locale.FRANCE);
        String formatedReturnDate = sdf.format(returnDate.toDate());
        dispoDisplayable.setAdjustDate(formatedReturnDate.concat("|").concat("" + adjustedDays));
    }


    /**
     * Return the day adjust to add to the number of days.
     * @param product The product
     * @param context The context
     * @return the day adjust
     */
    private int getDayAdjust(PublishedProduct product, IComponentContext<PageLayoutComponent> context) {
        int dayAdjust = 0;
        String criterionValue = Util.getCriterionValue(product, "dayAdjust", true);
        if (!"".equals(criterionValue)) {
            dayAdjust = Integer.valueOf(criterionValue);
        }
        if (context != null) {
            context.write("dayAdjust", dayAdjust);
        }
        return dayAdjust;
    }


    /**
     * Search the lowest price in the dispo display table.
     *
     * @param dispoDisplayTable DisponibilityDisplayable[]
     */
    private void searchLowestPrice(DisponibilityDisplayable[] dispoDisplayTable) {
        DisponibilityDisplayable[] dispoDisplayTableClone = dispoDisplayTable.clone();
        DisponibilityDisplayableComparator comparator = new DisponibilityDisplayableComparator();
        Arrays.sort(dispoDisplayTableClone, comparator);
        DisponibilityDisplayable disponibilityDisplayableFirstElement = dispoDisplayTableClone[0];
        String lowestId = disponibilityDisplayableFirstElement.getSignature();
        BigDecimal lowestPrice = disponibilityDisplayableFirstElement.getPrice();
        for (DisponibilityDisplayable disponibilityDisplayable : dispoDisplayTableClone) {
            if (disponibilityDisplayable == null) {
                continue;
            }
            if (lowestId.equals(disponibilityDisplayable.getSignature())
                && lowestPrice.equals(disponibilityDisplayable.getPrice())
                && ("".equals(disponibilityDisplayable.getSupplementDescription()) || disponibilityDisplayable
                    .getSupplementDescription() == null)) {
                disponibilityDisplayable.setSupplementDescription("lowest");
            }
        }
    }


    /**
     * Construct the availabilities's map by key contains departureMonthYear, city, duration.
     *
     * @param allDispos All availabilities
     */
    private void constructDispoMayBykey(Collection<Disponibility> allDispos) {
        dispoListByKey = new HashMap<DisponibilityMapKey, Collection<Disponibility>>();
        for (Disponibility disponibility : allDispos) {
            DisponibilityMapKey key = retrieveDispoMapKey(disponibility);
            if (dispoListByKey.containsKey(key)) {
                dispoListByKey.get(key).add(disponibility);
            } else {
                dispoListByKey.put(key, new ArrayList<Disponibility>());
                dispoListByKey.get(key).add(disponibility);
            }
        }
    }

    /**
     * Construct the availabilities's map by key contains departureMonthYear, city, duration.
     *
     * @param allDispos All availabilities
     */
    private Map<DisponibilityMapKey, Collection<Disponibility>> retrieveDispoMayBykey(Collection<Disponibility> allDispos) {
        Map<DisponibilityMapKey, Collection<Disponibility>> dispoListByKey = new HashMap<DisponibilityMapKey, Collection<Disponibility>>();
        for (Disponibility disponibility : allDispos) {
            DisponibilityMapKey key = retrieveDispoMapKey(disponibility);
            if (dispoListByKey.containsKey(key)) {
                dispoListByKey.get(key).add(disponibility);
            } else {
                dispoListByKey.put(key, new ArrayList<Disponibility>());
                dispoListByKey.get(key).add(disponibility);
            }
        }

        return dispoListByKey;
    }


    /**
     * Retrieve the availabilities's map by availability info.
     *
     * @param dispo the availability
     * @return key the map key
     */
    private DisponibilityMapKey retrieveDispoMapKey(Disponibility dispo) {
        DisponibilityMapKey key = new DisponibilityMapKey();
        if (dispo != null) {
            Date departDate = dispo.getDepartureDate();
            Date departDateClone = new Date();
            String date = departDate.toString();
            try {
                departDateClone = Date.parseDate(date);
            } catch (ParseException e) {
                LOGGER.error("parse date error" + date, e);
            }
            departDateClone.setDay((short) 1);
            key.setDepartureMonthYear(departDateClone);
            int durationDays = dispo.getDurationInDays();
            int durationNights = dispo.getDurationInNights();
            Duration duration = new Duration();
            duration.setDays(durationDays);
            duration.setNights(durationNights);
            key.setDuration(duration);
            City city = dispo.getDepartureCity();
            String cityCode = city.getCode();
            key.setDepartureCity(cityCode);
        }
        return key;
    }


    /**
     * Get partnerId value from the request or from the cookie.
     *
     * @param webEnvironment the webEnvironment
     * @return {@link String}
     */
    private String getPartnerId(WebProcessEnvironment webEnvironment) {

        String partnerId = null;
        HttpServletRequest rq = this.getEnvironment().getRequest();

        if (rq.getParameter(Constants.OmnitureConstants.PARTNER_ID) != null) {
            // partnerId found in rq. Update cookie value.
            partnerId = rq.getParameter(Constants.OmnitureConstants.PARTNER_ID);
        } else {
            // check if partnerId exists in cookie.
            Cookie partnerIdCookie = Util.getCookie(this.getEnvironment(), Constants.OmnitureConstants.PARTNER_ID);
            if (partnerIdCookie != null && partnerIdCookie.getValue() != null) {
                partnerId = partnerIdCookie.getValue();
            }
        }
        return partnerId;
    }

    /**
     * Get source value from the request or from the cookie.
     *
     * @param webEnvironment the webEnvironment
     * @return {@link String}
     */
    private String getSource(WebProcessEnvironment webEnvironment) {

        String source = null;
        HttpServletRequest rq = this.getEnvironment().getRequest();

        if (rq.getParameter("source") != null) {
            // partnerId found in rq. Update cookie value.
            source = rq.getParameter("source");
        } else {
            // check if source exists in cookie.
            Cookie sourceCookie = Util.getCookie(this.getEnvironment(),
                Constants.OmnitureConstants.OMNITURE_SOURCE);
            if (sourceCookie != null && sourceCookie.getValue() != null) {
                source = sourceCookie.getValue();
            }
        }
        return source;
    }


    /**
     * Produces a data model mock.
     *
     * @param context the component's context
     * @param injectionData the injection data
     * @throws PageNotFoundException If the page is not found
     */
    public void preview(IComponentContext<PageLayoutComponent> context, InjectionData injectionData)
        throws PageNotFoundException {

    }

}
