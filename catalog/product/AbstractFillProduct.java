/*
 *
 */
package com.travelsoft.lastminute.catalog.product;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.exolab.castor.types.Date;

import com.travelsoft.cameleo.catalog.data.City;
import com.travelsoft.cameleo.catalog.data.CodeLabel;
import com.travelsoft.cameleo.catalog.data.Criterion;
import com.travelsoft.cameleo.catalog.data.CriterionValue;
import com.travelsoft.cameleo.catalog.data.Disponibility;
import com.travelsoft.cameleo.catalog.data.Document;
import com.travelsoft.cameleo.catalog.data.MainZone;
import com.travelsoft.cameleo.catalog.data.MealPlan;
import com.travelsoft.cameleo.catalog.data.PageLayoutComponent;
import com.travelsoft.cameleo.catalog.data.PublishedProduct;
import com.travelsoft.cameleo.catalog.data.TechnicalInfo;
import com.travelsoft.cameleo.catalog.data.TransverseContents;
import com.travelsoft.cameleo.catalog.data.Zone;
import com.travelsoft.cameleo.catalog.data.Zones;
import com.travelsoft.cameleo.catalog.taglib.constant.Constant;
import com.travelsoft.cameleo.cms.processor.controller.AbstractStructuredController;
import com.travelsoft.cameleo.cms.processor.controller.WebProcessEnvironment;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.lastminute.catalog.seo.SeoTool;
import com.travelsoft.lastminute.catalog.serp.DurationComparator;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.ProductMapper;
import com.travelsoft.lastminute.catalog.util.TrackingUtil;
import com.travelsoft.lastminute.catalog.util.Util;
import com.travelsoft.lastminute.data.Duration;
import com.travelsoft.lastminute.data.SmallProductDisplayable;

/**
 * <p>Titre : AbstractFillProduct.</p>
 * Description :
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Company : Travelsoft</p>
 * @author MengMeng
 * @version
 *
 */
public abstract class AbstractFillProduct extends
    AbstractStructuredController<PageLayoutComponent, WebProcessEnvironment> {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(AbstractFillProduct.class);

    /** One hundred value constant. */
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    /**
     * Construct displayable product by PublishedProduct.
     * @param product the PublishedProduct
     * @param context The context
     * @return productDisplayable the SmallProductDisplayable
     */
    @SuppressWarnings("unchecked")
    protected SmallProductDisplayable constructProductDisplayable(PublishedProduct product,
            IComponentContext<PageLayoutComponent> context) {
        SmallProductDisplayable productDisplayable = new SmallProductDisplayable();
        Document edito = product.getEdito();
        String pid = product.getCode();
        fillDisplayableProductFile(productDisplayable, edito);
        fillDisplayableProductCiterion(
                productDisplayable, product, context,
                this.getEnvironment().getRequest().getParameterMap());
        productDisplayable.setId(pid);
        fillDisplayableProductByDispo(productDisplayable, product);
        fillDisplayableProductByBaseDispo(productDisplayable, product);
        fillStayStype(productDisplayable, product);
        fillProductPreferedParams(productDisplayable, context);
        productDisplayable.setReference(product.getToProductCode());
        if (product.getTechnicalInfo().getTourOperator() != null
                && product.getTechnicalInfo().getTourOperator().getCode() != null) {
            productDisplayable.setToCode(product.getTechnicalInfo().getTourOperator().getCode());
        }
        productDisplayable.setSeoUrl(this.getSeoUrl(productDisplayable, product));
        TransverseContents[] transContents = product.getTransverseContents();
        if (transContents != null && transContents.length > 0) {
            for (TransverseContents transverseContent : transContents) {
                String typeCode = transverseContent.getCategoryCodeLabel()
                        .getCode();
                if ("pubProduct".equals(typeCode)) {
                    Document[] document = transverseContent.getDocument();
                    if (document != null && document.length > 0) {
                        MainZone imgPubSrcMainZone = Util.getEditoMainZone(
                                document[0], "image");
                        String imgPubSrc = Util
                                .getZoneContent(imgPubSrcMainZone);
                        if (imgPubSrc != null
                                && imgPubSrc.indexOf("blankimg.gif") == -1) {
                             productDisplayable.setImagePub(imgPubSrc);
                        }
                        MainZone linkPubMainZone = Util.getEditoMainZone(
                                document[0], "link");
                        String linkPub = Util.getZoneContent(linkPubMainZone);
                        if (linkPub != null && !"".equals(linkPub)) {
                             productDisplayable.setUriPub(linkPub);
                        }
                        MainZone contentPubMainZone = Util.getEditoMainZone(
                                document[0], "content");
                        String contentPub = Util
                                .getZoneContent(contentPubMainZone);
                        if (contentPub != null && !"".equals(contentPub)) {
                             productDisplayable.setContentPub(contentPub);
                        }
                    }
                    break;
                }
            }
        }
        return productDisplayable;
    }

    /**
     *
     * @param productDisplayable The product info to display
     * @param product The product
     * @return The seo url
     */
    private String getSeoUrl(SmallProductDisplayable productDisplayable, PublishedProduct product) {
        StringBuffer seoUrl = new StringBuffer();

        Boolean b2bRequest = (Boolean) this.getEnvironment().getRequest().getSession().getAttribute("b2bRequest");
        if (b2bRequest != null && b2bRequest) {
              seoUrl = seoUrl.append("b2b/");
        }

        String productSeoUrl = SeoTool.getSeoProductUrl(product);
        if (!"".equals(productSeoUrl)) {
            seoUrl = seoUrl.append(productSeoUrl).append('/').append(productDisplayable.getId());
        } else {
            seoUrl = seoUrl.append(productDisplayable.getId());
        }
        if (!"".equals(productDisplayable.getParams())) {
            seoUrl = seoUrl.append('/').append(productDisplayable.getParams());
        }
        return seoUrl.toString();
    }

    /**
     *
     * @param productDisplayable the small product displayer
     * @param product The product
     */
    private void fillStayStype(SmallProductDisplayable productDisplayable, PublishedProduct product) {
        Criterion[] criterionArray = product.getTechnicalInfo().getCriterion();
        if (criterionArray != null) {
            for (Criterion criterion : criterionArray) {
                if (Constants.CriterionConstants.STAY_TYPE_CODE.equals(criterion.getCode())) {
                    int valueCount = criterion.getValueCount();
                    if (valueCount == 1) {
                        productDisplayable.setStayType(criterion.getValue(0).getLabel());
                    }
                }
            }
        }

    }

    /**
     * Fill the small product displayer by product's criterion.
     * @param productDisplayable the small product displayer
     * @param product the product
     * @param context The context
     */
    protected void fillDisplayableProductCiterion(
            SmallProductDisplayable productDisplayable, PublishedProduct product,
            IComponentContext<PageLayoutComponent> context,
            Map<String, String[]> requestParameters) {

        String destinationTitle = SeoTool.getUniqueDestination(product, "country");
        productDisplayable.setDestinationTitle(destinationTitle);

        // add destination city labels
        List<String> destCityLabels = TrackingUtil.getCriterionDestCityLabelList(product,
            Constants.CriterionConstants.CRITERION_DESTIONATION_CODE);
        productDisplayable.setDestinationCities(StringUtils.join(destCityLabels, Constants.Common.SEPARATOR_COMMA));

        String pictoCode = Util.getCriterionValue(product, Constants.CriterionConstants.CRITERION_PICTO_CODE, true);
        if (pictoCode != null && !"".equals(pictoCode)) {
            String pictoImageUrl = Util.getMedialibraryPathByFileName(Constants.Config.PRODUCT_PICTO_FILE_NAME)
                    + pictoCode + Constants.Common.IMAGE_FORMAT_GIF;
            String pictoLable = Util.getCriterionValue(product, Constants.CriterionConstants.CRITERION_PICTO_CODE,
                    false);
            CodeLabel picto = new CodeLabel();
            picto.setCode(pictoImageUrl);
            picto.setLabel(pictoLable);
            productDisplayable.setPicto(picto);
        }


        String category = Util.getCriterionValue(product, Constants.CriterionConstants.CRITERION_CATEGORY_CODE, true);
        if (category != null) {
            productDisplayable.setStar(category.replace("category", ""));
        }


        ProductMapper.retrieveDestinationCityAndCountry(productDisplayable, product);
    }

    /**
     * Fill small displayable product price by base disponibility.
     *
     * @param productDisplayable the small product displayer
     * @param product the published product
     */
    protected void fillDisplayableProductByBaseDispo(SmallProductDisplayable productDisplayable,
        PublishedProduct product) {

        Disponibility baseDispo = product.getBasePriceDisponibility();
        if (baseDispo != null) {
            BigDecimal promoReduc = baseDispo.getPromoReduc();
            if (promoReduc != null && baseDispo.getInPromotion() != null && BigDecimal.ZERO.compareTo(promoReduc) != 0
                && "Oui".equals(baseDispo.getInPromotion())) {
                BigDecimal ttcPricePromo = baseDispo.getTtcPrice();
                BigDecimal promoPercentage = promoReduc.multiply(ONE_HUNDRED);
                BigDecimal brochurePrice = baseDispo.getBrochurePrice();
                BigDecimal differenceTTC = null;
                BigDecimal toTtcPrice = baseDispo.getToTtcPrice();
                BigDecimal price = null;
                if (promoReduc.compareTo(BigDecimal.ZERO) == -1) {
                    productDisplayable.setBasePrice(baseDispo.getTtcPrice());
                    promoPercentage = BigDecimal.ZERO;
                    productDisplayable.setPromoPercentage(promoPercentage);
                } else {
                    if (brochurePrice != null) {
                        differenceTTC = brochurePrice.subtract(ttcPricePromo);
                        price = brochurePrice;
                    } else if (toTtcPrice != null) {
                        differenceTTC = toTtcPrice.subtract(ttcPricePromo);
                        price = toTtcPrice;
                    }
                    productDisplayable.setBasePrice(price);
                    productDisplayable.setPromoPrice(ttcPricePromo);
                    productDisplayable.setDiffencePrice(differenceTTC);
                    productDisplayable.setPromoPercentage(promoPercentage);
                }
            } else {
                BigDecimal ttcPrice = baseDispo.getTtcPrice();
                productDisplayable.setBasePrice(ttcPrice);
            }

            City departureCity = baseDispo.getDepartureCity();
            if (departureCity != null) {
                productDisplayable.setBaseDepartureCity(departureCity.getLabel());
            }

            Date departureDate = baseDispo.getDepartureDate();
            if (departureDate != null) {
                SimpleDateFormat sdf = new SimpleDateFormat(Constants.Common.DATE_FORMAT, Locale.FRANCE);
                productDisplayable.setBaseDepartureDate(sdf.format(departureDate.toDate())) ;
            }
        }
    }

    /**
     * Fill small displayable product by disponibilities and preferred date, city, duration.
     * @param productDisplayable the small product displayer
     * @param product the published product
     */
    protected void fillDisplayableProductByDispo(SmallProductDisplayable productDisplayable, PublishedProduct product) {
        long beginTime = System.currentTimeMillis();
        String pid = product.getCode();

        int disponibilityCount = 0;
        TechnicalInfo technicalInfo = product.getTechnicalInfo();
        if (technicalInfo != null) {
            disponibilityCount = technicalInfo.getDisponibilityCount();
            if (disponibilityCount == 0) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("the product has not diponibilites, the pid is:" + pid);
                }
                return;
            }
        }

        Collection<City> allCities = new HashSet<City>();
        Collection<Duration> durationList = new HashSet<Duration>();
        Collection<String> pensionList = new HashSet<String>();
        Collection<Date> depDateList = new HashSet<Date>();
        for (int i = 0; i < disponibilityCount; i++) {
            Disponibility dispo = technicalInfo.getDisponibility(i);
            allCities.add(dispo.getDepartureCity());

            int days = dispo.getDurationInDays();
            int nights = dispo.getDurationInNights();
            Duration durationLocal = new Duration();
            durationLocal.setDays(days);
            durationLocal.setNights(nights);
            durationList.add(durationLocal);

            MealPlan mealPlan = dispo.getMealPlan();
            if (mealPlan != null) {
                String code = mealPlan.getCode();
                if (code != null) {
                    pensionList.add(code);
                }
            }

            depDateList.add(dispo.getDepartureDate());
        }

        // add city list
        if (allCities != null && allCities.size() > 0) {
            int citySize = allCities.size();
            String cityLabels = "";
            int cityCount = 0;
            for (City city : allCities) {
                String cityLabel = city.getLabel();
                cityLabels = Util.appendString(cityLabels, cityLabel);
                cityCount++;
                if (cityCount != citySize) {
                    cityLabels = Util.appendString(cityLabels, Constants.Common.CHARACTER_SLASH
                            + Constants.Common.CHARACTER_SPACE);
                }
            }
            productDisplayable.setDepartureCities(cityLabels);
        }

        // add duration list
        if (durationList.size() > 0) {
            Duration[] durationsTable = new Duration[durationList.size()];
            int durationCount = 0;
            for (Duration duration : durationList) {
                durationsTable[durationCount] = duration;
                durationCount++;
            }
            DurationComparator durationComparator = new DurationComparator();
            Arrays.sort(durationsTable, durationComparator);
            productDisplayable.setDurations(durationsTable);
        }

        // add departure dates
        if (depDateList.size() > 0) {
            Date[] depDates = depDateList.toArray(new Date[depDateList.size()]);
            productDisplayable.setDepDates(depDates);
        }

        MealPlan mealPlan = product.getBasePriceDisponibility().getMealPlan();
        if (mealPlan != null) {
            productDisplayable.setPensions(mealPlan.getCode());
        }
        LOGGER.info("Spent " + (System.currentTimeMillis() - beginTime) + " ms. in fillDisplayableProductByDispo");
    }

    /**
     * Fill the small product displayer by product's editorial.
     * @param productDisplayable the small product displayer
     * @param edito the product's editorial
     */
    protected void fillDisplayableProductFile(SmallProductDisplayable productDisplayable, Document edito) {
        MainZone picMainZone = Util.getEditoMainZone(edito, "mainPicture");
        String picUrl = Util.getZoneContent(picMainZone);
        productDisplayable.setImageUrl(picUrl);

        MainZone titleZone = Util.getEditoMainZone(edito, "title");
        String title = Util.getZoneContent(titleZone);
        productDisplayable.setTitle(title);

        MainZone descriptionButtomZone = Util.getEditoMainZone(edito, "miniDescriptiveButtom");
        String descriptionButtom = Util.getZoneContent(descriptionButtomZone);
        productDisplayable.setDescriptionButtom(descriptionButtom);

        MainZone descriptionsMainZone = Util.getEditoMainZone(edito, "miniDescriptive");

        if (descriptionsMainZone != null) {
            Zones zones = descriptionsMainZone.getZones();
            if (zones != null) {
                int zoneCount = zones.getZoneCount();
                String[] descriptions = new String[zoneCount];
                for (int i = 0; i < zoneCount; i++) {
                    Zone descriptionZone = zones.getZone(i);
                    Zones descriptionContentZones = descriptionZone.getZones();
                    Zone descriptionContentZone = descriptionContentZones.getZone(0);
                    String description = Util.getZoneContent(descriptionContentZone);
                    descriptions[i] = description;
                }
                boolean hasContent = Util.hasContentInStringTable(descriptions);
                if (hasContent) {
                    productDisplayable.setDescriptions(descriptions);
                }
            }
        }
    }

    /**
     * Fill the small product displayer prefered parameters by web environment.
     * @param productDisplayable the small product displayer
     * @param context The context
     */
    protected void fillProductPreferedParams(SmallProductDisplayable productDisplayable,
            IComponentContext<PageLayoutComponent> context) {
        // TODO Refactor this !!
        WebProcessEnvironment webEvironment = this.getEnvironment();
        String query = "";
        String preferedDate = "";
        if (webEvironment != null) {
            String preferredMonthYear = webEvironment.getRequestParameter(Constants.Common.SEARCH_ENGINE_PREFIX
                    + Constant.DEPARTURE_MONTH_YEAR);
            if (preferredMonthYear != null && !"".equals(preferredMonthYear)) {
                String preferredDay = webEvironment.getRequestParameter(Constants.Common.SEARCH_ENGINE_PREFIX
                        + Constant.DEPARTURE_DAY);
                if (preferredDay != null && !"".equals(preferredDay)) {
                    preferedDate = Util.appendString(preferredDay, "-", preferredMonthYear.replace("/", "-"));
                    String preferedAj = webEvironment.getRequestParameter(Constants.Common.SEARCH_ENGINE_PREFIX
                            + Constant.DATE_ADJUST);
                    if (preferedAj != null && !"".equals(preferedAj)) {
                        query = Util.appendString(Constants.Common.PREFERED_DATE_ADJUST,
                                Constants.CriterionConstants.CRITERION_EQUAL, preferedAj);
                    }
                } else {
                    preferedDate = preferredMonthYear.replace("/", "-");
                }
                if (!Constants.Common.SEPARATOR_EMPTY.equals(query)) {
                    query = Util.appendString(query, Constants.CriterionConstants.CRITERION_SEPARATOR,
                            Constants.Common.PREFERED_DATE, Constants.CriterionConstants.CRITERION_EQUAL, preferedDate);
                } else {
                    query = Util.appendString(Constants.Common.PREFERED_DATE,
                            Constants.CriterionConstants.CRITERION_EQUAL, preferedDate);
                }
            }

            String preferedCity = webEvironment.getRequestParameter(Constants.Common.SEARCH_ENGINE_PREFIX
                    + Constant.DEP_CITIES);
            if (preferedCity != null && !"".equals(preferedCity)) {
                if (!Constants.Common.SEPARATOR_EMPTY.equals(query)) {
                    query = Util.appendString(query, Constants.CriterionConstants.CRITERION_SEPARATOR,
                            Constants.Common.PREFERED_CITY, Constants.CriterionConstants.CRITERION_EQUAL, preferedCity);
                } else {
                    query = Util.appendString(Constants.Common.PREFERED_CITY,
                            Constants.CriterionConstants.CRITERION_EQUAL, preferedCity);
                }
            }

            String preferedDuration = webEvironment.getRequestParameter(Constants.Common.SEARCH_ENGINE_PREFIX
                    + Constant.MIN_MAX_NIGHTS);
            if (preferedDuration != null && !"".equals(preferedDuration)) {
                if (!Constants.Common.SEPARATOR_EMPTY.equals(query)) {
                    query = Util.appendString(query, Constants.CriterionConstants.CRITERION_SEPARATOR,
                            Constants.Common.PREFERED_DURATION, Constants.CriterionConstants.CRITERION_EQUAL,
                            preferedDuration);
                } else {
                    query = Util.appendString(Constants.Common.PREFERED_DURATION,
                            Constants.CriterionConstants.CRITERION_EQUAL, preferedDuration);
                }
            }
            Boolean displayAlternatifProducts = (Boolean) context.lookup("displayAlternatifProducts");
            if (displayAlternatifProducts != null) {
                query = "";
            }
            productDisplayable.setParams(query);
        }
    }

    /**
     * Check if the product contains a video
     * @param product The published product
     * @return true if video exists
     */
    protected String isProductWithVideo(PublishedProduct product) {
        Document edito = product.getEdito();
        if (edito != null)  {
            String contentID = Util.getZoneContent(Util.getEditoMainZone(edito, Constants.Common.VIDEO_CONTENT_ID));
            String playerID = Util.getZoneContent(Util.getEditoMainZone(edito, Constants.Common.VIDEO_PLAYER_ID));
            if (contentID != null && playerID != null && !contentID.equals("") && !playerID.equals("")) {
                return playerID.concat("###").concat(contentID);
            }
        }
        return null;
    }
}
