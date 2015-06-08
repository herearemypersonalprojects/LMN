/*
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.product;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.exolab.castor.types.Date;
import org.exolab.castor.types.DateTimeBase;

import com.travelsoft.cameleo.catalog.data.Disponibility;
import com.travelsoft.cameleo.catalog.data.Document;
import com.travelsoft.cameleo.catalog.data.MainZone;
import com.travelsoft.cameleo.catalog.data.PageLayoutComponent;
import com.travelsoft.cameleo.catalog.data.PublishedProduct;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchCriteria;
import com.travelsoft.cameleo.catalog.data.TechnicalInfo;
import com.travelsoft.cameleo.catalog.data.User;
import com.travelsoft.cameleo.catalog.data.Zone;
import com.travelsoft.cameleo.catalog.data.Zones;
import com.travelsoft.cameleo.catalog.ejb.util.CacheUtils;
import com.travelsoft.cameleo.cms.processor.controller.WebProcessEnvironment;
import com.travelsoft.cameleo.cms.processor.exception.MovedTemporarilyStatusException;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.InjectionData;
import com.travelsoft.lastminute.catalog.seo.SeoTool;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.Util;
import com.travelsoft.lastminute.data.BrandData;
import com.travelsoft.lastminute.data.MediaTagTrackingStats;
import com.travelsoft.lastminute.data.SeoData;
import com.travelsoft.lastminute.data.SmallProductDisplayable;
import com.travelsoft.nucleus.cache.generic.CacheException;

/**
 * <p>Title:  ProductPageController.</p>
 * Description: The product page controller
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Travelsoft</p>
 * @author MengMeng
 */
public class ProductMainController extends AbstractFillProduct {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(ProductMainController.class);

    /** The procedure for the product retrieval in auto cache. */
    private static final LastminuteProductRetrievalProcedure PRODUCT_RETRIEVAL_PROCEDURE
        = new LastminuteProductRetrievalProcedure();

    /** The code of preferred departure city. */
    private String preferredCity;

    /** The preferred duration night (format min,max). */
    private String preferredDuration;

    /** The preferred duration day (format min,max). */
    private String preferredDurationDay;

    /** The preferred date (format dd/mm/yyyy). */
    private String preferredDepartureDate;

    /** The preferred adjust day. */
    private String preferredAj;

    /** The search product criteria. */
    private PublishedProductSearchCriteria searchCriteria;

    /**
     * Process business logic and produce the data model to get the product.
     * @param context the component's context
     * @throws PageNotFoundException if the page can not be found
     */
    @Override
    public void fillComponentContext(IComponentContext<PageLayoutComponent> context) throws PageNotFoundException  {
        searchCriteria = Util.retrieveSearchCriteria(context, "services.product.searchCriteriaById");
        WebProcessEnvironment webEnvironment = this.getEnvironment();
        if (webEnvironment != null) {

            MyEnvironment myEnvironment = new MyEnvironment(webEnvironment);
            context.write("myEnvironment", myEnvironment);
            context.write("vd", webEnvironment.getRequest().getRequestURL().toString() + webEnvironment.getRequest().getQueryString());

            // set new mediaTagStats object that will be filled only once (in the header part)
            MediaTagTrackingStats mediaTagTrackingData = new MediaTagTrackingStats();
            context.write("mediaTagStats", mediaTagTrackingData);

            // check if we should only use print.css instead of other css files (case of email .pdf generation)
            writeRqParamIntoContext(webEnvironment, context, Constants.Common.USE_PRINT_CSS);

            // check if fromBooking parameter was used: if yes, hide info center and resa table in css.
            writeRqParamIntoContext(webEnvironment, context, Constants.Common.FROM_BOOKING);

            String pid = webEnvironment.getRequestParameter(Constants.Common.PID);
            preferredDepartureDate = webEnvironment.getRequestParameter(Constants.Common.PREFERED_DATE);
            preferredCity = webEnvironment.getRequestParameter(Constants.Common.PREFERED_CITY);
            preferredDuration = webEnvironment.getRequestParameter(Constants.Common.PREFERED_DURATION);
            preferredAj = webEnvironment.getRequestParameter(Constants.Common.PREFERED_DATE_ADJUST);
            preferredDurationDay = webEnvironment.getRequestParameter(Constants.Common.PREFERED_DURATION_DAY);


            if (preferredCity != null && !"".equals(preferredCity)) {
                searchCriteria.setDepartureCityCode(new String[] {preferredCity,});
            }
            constructPreferredDurationForCriteria();
            constructPreferredDurationDayForCriteria();
            constructPreferredDateForCriteria();

            context.write(Constants.Context.PRODUCT_SEARCH_CRITERIA, searchCriteria);

            try {
                PublishedProduct publishedProduct = this.retrievePublishedProduct(pid, context);

                String from = webEnvironment.getRequestParameter("from");
                if (from == null && publishedProduct != null && publishedProduct.getBasePrice() == null) {
                    throw new Exception("The base price is null");
                }

                SmallProductDisplayable productDisplayable = constructProductDisplayable(publishedProduct, context);
                context.write(Constants.Context.SMALL_PRODUCT_DISPLAYABLE, productDisplayable);
                context.write(Constants.Context.PUBLISHED_PRODUCT, publishedProduct);
                if (preferredAj != null || preferredCity != null || preferredDepartureDate != null
                        || preferredDuration != null || preferredAj != null) {
                    context.write("queryWithDynamicParameters", true);
                } else {
                    context.write("queryWithDynamicParameters", false);
                }
                addDataInContext(context, webEnvironment, publishedProduct);
            } catch (Exception ce) {
            	LOGGER.error("Error while searching product ", ce);
                String seoParameter = webEnvironment.getRequestParameter("seo");
                String url = null;
                if (seoParameter != null && seoParameter.indexOf("voyage-") != -1) {
                    if (seoParameter.startsWith("/")) {
                        seoParameter = seoParameter.substring(1);
                    }
                    String[] seoParameterSplit = seoParameter.split("/");
                    if (seoParameterSplit != null && seoParameterSplit.length > 0) {
                        url = "/" + seoParameterSplit[0];
                    }
                } else {
                    url = "/serp.cms?s_aj=2";
                }
                if (url != null) {
                    throw new MovedTemporarilyStatusException(url, "Redirect to " + url);
                } else {
                    throw new PageNotFoundException("can not get the product by id :" + pid);
                }
            }
        }
    }


    /**
     * @param pid The product identifier
     * @param context The context
     * @return The product
     * @throws CacheException The cache exception
     */
    private PublishedProduct retrievePublishedProduct(String pid, IComponentContext<PageLayoutComponent> context) throws CacheException {
        PublishedProduct sourceProduct = CacheUtils.autoRetrieveCachedValue(Constants.CAMELEO_CACHE_MANAGER,
                "productDisponibilities", buildProductSearchKey(pid), PRODUCT_RETRIEVAL_PROCEDURE);
        if (sourceProduct != null) {
            context.write(Constants.Context.SOURCE_PRODUCT, sourceProduct);
        }

        PublishedProduct outputProduct = new PublishedProduct();
        Disponibility[] disponibilities = sourceProduct.getTechnicalInfo().getDisponibility();

        String chosenCityCode = this.preferredCity;
        Date minCal = this.searchCriteria.getMinDepartureDate();
        Date maxCal = this.searchCriteria.getMaxDepartureDate();

        boolean hasMinDurationInDays = this.searchCriteria.hasMinDurationInDays();
        boolean hasMaxDurationInDays = this.searchCriteria.hasMaxDurationInDays();
        boolean hasMinDurationInNights = this.searchCriteria.hasMinDurationInNights();
        boolean hasMaxDurationInNights = this.searchCriteria.hasMaxDurationInNights();
        int minDurationInDays = this.searchCriteria.getMinDurationInDays();
        int maxDurationInDays = this.searchCriteria.getMaxDurationInDays();
        int minDurationInNights = this.searchCriteria.getMinDurationInNights();
        int maxDurationInNights = this.searchCriteria.getMaxDurationInNights();

        outputProduct.setBeginSaleDate(sourceProduct.getBeginSaleDate());
        outputProduct.setBookingInformation(sourceProduct.getBookingInformation());
        outputProduct.setBookingProcessStatus(sourceProduct.getBookingProcessStatus());
        outputProduct.setCatalogPricesStatus(sourceProduct.getCatalogPricesStatus());
        outputProduct.setCode(sourceProduct.getCode());
        outputProduct.setCommented(sourceProduct.getCommented());
        outputProduct.setCommentedOpinionsNumber(sourceProduct.getCommentedOpinionsNumber());
        outputProduct.setCommentedWithMessage(sourceProduct.getCommentedWithMessage());
        outputProduct.setCreateDate(sourceProduct.getCreateDate());
        outputProduct.setEdito(sourceProduct.getEdito());
        outputProduct.setFaringState(sourceProduct.getFaringState());
        outputProduct.setHotel(sourceProduct.getHotel());
        outputProduct.setId(sourceProduct.getId());
        outputProduct.setLabel(sourceProduct.getLabel());
        outputProduct.setMargin(sourceProduct.getMargin());
        outputProduct.setMarginType(sourceProduct.getMarginType());
        outputProduct.setMean(sourceProduct.getMean());
        outputProduct.setModifyDate(sourceProduct.getModifyDate());
        outputProduct.setNightType(sourceProduct.getNightType());
        outputProduct.setOpinionsNumber(sourceProduct.getOpinionsNumber());
        outputProduct.setPreferred(sourceProduct.getPreferred());
        outputProduct.setPublishDate(sourceProduct.getPublishDate());
        outputProduct.setPubStatusTime(sourceProduct.getPubStatusTime());
        outputProduct.setPushing(sourceProduct.getPushing());
        outputProduct.setPushingsNumber(sourceProduct.getPushingsNumber());
        outputProduct.setPushingState(sourceProduct.getPushingState());
        outputProduct.setResult(sourceProduct.getResult());
        outputProduct.setResultType(sourceProduct.getResultType());
        outputProduct.setSortInfo(sourceProduct.getSortInfo());
        outputProduct.setStructureType(sourceProduct.getStructureType());
        outputProduct.setToProductCode(sourceProduct.getToProductCode());
        outputProduct.setTransverseContents(sourceProduct.getTransverseContents());
        outputProduct.setType(sourceProduct.getType());

        TechnicalInfo outputTechnicalInfo = new TechnicalInfo();
        TechnicalInfo sourceTechnicalInfo = sourceProduct.getTechnicalInfo();
        outputProduct.setTechnicalInfo(outputTechnicalInfo);
        outputTechnicalInfo.setCountry(sourceTechnicalInfo.getCountry());
        outputTechnicalInfo.setCriterion(sourceTechnicalInfo.getCriterion());
        outputTechnicalInfo.setForbiddenBaby(sourceTechnicalInfo.getForbiddenBaby());
        outputTechnicalInfo.setForbiddenChild(sourceTechnicalInfo.getForbiddenChild());
        outputTechnicalInfo.setFormula(sourceTechnicalInfo.getFormula());
        outputTechnicalInfo.setHotelAvailability(sourceTechnicalInfo.getHotelAvailability());
        outputTechnicalInfo.setTourOperator(sourceTechnicalInfo.getTourOperator());

        Disponibility basePriceDispo = null;
        for (Disponibility disponibility : disponibilities) {
            // Selectivity filters
            String cityCode = disponibility.getDepartureCity().getCode();
            if (chosenCityCode != null && !chosenCityCode.equals(cityCode)) {
                continue;
            }
            Date dispoCal = disponibility.getDepartureDate();
            if (minCal != null && dispoCal.compareTo(minCal) == DateTimeBase.LESS_THAN) {
                continue;
            }
            if (maxCal != null && dispoCal.compareTo(maxCal) == DateTimeBase.GREATER_THAN) {
                continue;
            }
            int durationInDays = disponibility.getDurationInDays();
            if ((hasMinDurationInDays && durationInDays < minDurationInDays)
                    || (hasMaxDurationInDays && durationInDays > maxDurationInDays)) {
                continue;
            }
            int durationInNights = disponibility.getDurationInNights();
            if ((hasMinDurationInNights && durationInNights < minDurationInNights)
                    || (hasMaxDurationInNights && durationInNights > maxDurationInNights)) {
                continue;
            }
            if (basePriceDispo == null) {
                basePriceDispo = disponibility;
            } else {
                BigDecimal ttcPrice = basePriceDispo.getTtcPrice();
                BigDecimal currentTtcPrice = disponibility.getTtcPrice();
                if (ttcPrice != null && currentTtcPrice != null && ttcPrice.compareTo(currentTtcPrice) > 0) {
                    basePriceDispo = disponibility;
                }
            }
            outputTechnicalInfo.addDisponibility(disponibility);
        }
        if (basePriceDispo != null) {
            outputProduct.setBasePriceDisponibility(basePriceDispo);
            outputProduct.setBasePrice(basePriceDispo.getTtcPrice());
        }

        return outputProduct;
    }

    /**
     * @return
     */
    private LastminuteProductKey buildProductSearchKey(String pid) {
        PublishedProductSearchCriteria clonedCriteria = (PublishedProductSearchCriteria) searchCriteria.clone();
        clonedCriteria.removeAllDepartureCityCode();
        clonedCriteria.setMaxDurationInDays(0);
        clonedCriteria.deleteMaxDurationInDays();
        clonedCriteria.setMinDurationInDays(0);
        clonedCriteria.deleteMinDurationInDays();
        clonedCriteria.setMaxDurationInNights(0);
        clonedCriteria.deleteMaxDurationInNights();
        clonedCriteria.setMinDurationInNights(0);
        clonedCriteria.deleteMinDurationInNights();
        clonedCriteria.setMinDepartureDate(null);
        clonedCriteria.setMaxDepartureDate(null);
        return new LastminuteProductKey(pid, clonedCriteria);
    }

    /**
     * Adds the data information in context.
     * @param context The context
     * @param webEnvironment The environment
     * @param product The product
     */
    private void addDataInContext(IComponentContext<PageLayoutComponent> context,
            WebProcessEnvironment webEnvironment, PublishedProduct product) {

        HttpServletRequest request = this.getEnvironment().getRequest();
        String productUrl =  request.getScheme() + "://" + request.getServerName() + "/" + SeoTool.getSeoProductUrl(product) ;
        context.write("productUrl", productUrl);

        BrandData brandData = Util.addBrandContext(this.getEnvironment().getRequest(), context);

        String userParameter = webEnvironment.getRequestParameter(Constants.Common.USER_PARAMETER);

        String partnerId = this.getEnvironment().getRequestParameter("partnerId");

        if ("b2b".equals(userParameter) || Constants.Common.SELECTOUR_BRAND_NAME.equals(brandData.getBrandName())
            || (partnerId != null && "21469".equals(partnerId))) {
            context.write("displayb2bBlock", true);
            String currentPageUrl = webEnvironment.getRequest().getRequestURL().toString() + "?"
                            + webEnvironment.getRequest().getQueryString().toString();
            context.write("currentPageUrl", currentPageUrl);
            HttpSession session = webEnvironment.getRequest().getSession();
            User user = (User) session.getAttribute("user");
            if (user != null && user.getProfile() != null) {
                context.write("userProfile", user.getProfile());
                context.write("user", user);
            } else {
                String errorMessage = (String) session.getAttribute("errorMessage");
                context.write("errorMessage", errorMessage);
            }
        }
        context.write("currentPage", "product");
        SeoData seoData = new SeoData();
        context.write("seoData", seoData);
        boolean flyAndDrive = Util.isExistingFlyAndDriveCriteria(product);
        context.write(Constants.Context.FLY_AND_DRIVE, flyAndDrive);
        addChannelInContext(product, context);
        retrievePromoData(request, product, context);
        addSpecificPrePackageParametersInContext(product, context);
    }


    /**
     * Add specific data into the context for the prepackage process.
     * @param product The product
     * @param context The context
     */
    private void addSpecificPrePackageParametersInContext(PublishedProduct product, IComponentContext<PageLayoutComponent> context) {
    	if (product != null)  {
    		Document edito = product.getEdito();
    		if (edito != null) {
    			 // Receptif name
    			 MainZone receptifNameZone = Util.getEditoMainZone(edito, "receptifName");
    			 if (receptifNameZone != null) {
        		     String receptifName = Util.getZoneContent(receptifNameZone);
        		     if (!receptifName.equals("") && receptifName != null) {
        		    	 context.write("receptifName", receptifName);
        		     }
    			 }

    		     // Receptif tel
    			 MainZone receptifTelZone = Util.getEditoMainZone(edito, "receptifTel");
    			 if (receptifTelZone != null) {
    				 String receptifTel = Util.getZoneContent(receptifTelZone);
        		     if (!receptifTel.equals("") && receptifTel != null) {
        		    	 context.write("receptifTel", receptifTel);
        		     }
    			 }


    		     // Receptif Email
    			 MainZone receptifEmailZone = Util.getEditoMainZone(edito, "receptifEmail");
    			 if (receptifEmailZone != null) {
    				 String receptifEmail = Util.getZoneContent(receptifEmailZone);
        		     if (!receptifEmail.equals("") && receptifEmail != null) {
        		    	 context.write("receptifEmail", receptifEmail);
        		     }
    			 }


    		    // Receptif address
    			 MainZone receptifAddressZone = Util.getEditoMainZone(edito, "receptifAddress");
    			 if (receptifAddressZone != null) {
    				 String receptifAddress = Util.getZoneContent(receptifAddressZone);
        		     if (!receptifAddress.equals("") && receptifAddress != null) {
        		    	 context.write("receptifAddress", receptifAddress);
        		     }
    			 }


    		     // Transfer Label
    			 MainZone transferLabelZone = Util.getEditoMainZone(edito, "transferLabel");
    			 if (transferLabelZone != null) {
    				 String transferLabel = Util.getZoneContent(transferLabelZone);
        		     if (!transferLabel.equals("") && transferLabel != null) {
        		    	 context.write("transferLabel", transferLabel);
        		     }
    			 }


    		     // Transfer Label
    			 MainZone transferPriceZone = Util.getEditoMainZone(edito, "transferPrice");
    			 if (transferPriceZone != null) {
    				 String transferPrice = Util.getZoneContent(transferPriceZone);
        		     if (!transferPrice.equals("") && transferPrice != null) {
        		    	 context.write("transferPrice", transferPrice);
        		     }
    			 }


    		     // Transfer Label
    			 MainZone mealPlanLabelZone = Util.getEditoMainZone(edito, "mealPlanLabel");
    			 if (mealPlanLabelZone != null) {
    				 String mealPlanLabel = Util.getZoneContent(mealPlanLabelZone);
        		     if (!mealPlanLabel.equals("") && mealPlanLabel != null) {
        		    	 context.write("mealPlanLabel", mealPlanLabel);
        		     }
    			 }


    		     // Transfer Label
    			 MainZone mealPlanPriceZone = Util.getEditoMainZone(edito, "mealPlanPrice");
    			 if (mealPlanPriceZone != null) {
    				 String mealPlanPrice = Util.getZoneContent(mealPlanPriceZone);
        		     if (!mealPlanPrice.equals("") && mealPlanPrice != null) {
        		    	 context.write("mealPlanPrice", mealPlanPrice);
        		     }
    			 }
    		}
    	}
	}


	/**
     * Retrieves the promotion data.
     * @param request The request
     * @param product The product
     * @param context The context
     */
    private void retrievePromoData(HttpServletRequest request, PublishedProduct product, IComponentContext<PageLayoutComponent> context) {
        Document edito = product.getEdito();
        if (edito != null) {
            MainZone promoZone = Util.getEditoMainZone(edito, "promo");
            if (promoZone != null) {
                Zones zones = promoZone.getZones();
                if (zones != null) {
                    Zone[] zoneArray = zones.getZone();
                    if (zoneArray != null && zoneArray.length > 0) {
                        Zone paraZone = zoneArray[0];
                        if (paraZone != null) {
                            Zones zones2 = paraZone.getZones();
                            if (zones2 != null) {
                                Zone[] zoneArray2 = zones2.getZone();
                                if (zoneArray2 != null) {
                                    for (Zone zone : zoneArray2) {
                                        if (zone != null && "pid".equals(zone.getCode())) {
                                            String content = zone.getValue().getContent();
                                            if (content != null && !"".equals(content)) {
                                                 String productUrl =  request.getScheme() + "://" + request.getServerName() + "/" + content;
                                                 context.write("productPromoUrl", productUrl);
                                            }
                                        }
                                        if (zone != null && "label".equals(zone.getCode())) {
                                            String promoLabel = zone.getValue().getContent();
                                            if (promoLabel != null && !"".equals(promoLabel)) {
                                                context.write("promoLabel", promoLabel);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }


        String criterionValue = Util.getCriterionValue(product, "7plus1", true);
        if (!criterionValue.equals("")) {
            context.write("7plus1", true);
        }
    }

    /**
     * Adds the channel in the context

     * @param product The product
     * @param context The context
     */
    private void addChannelInContext(PublishedProduct product, IComponentContext<PageLayoutComponent> context) {
        String channel = null;
        String criterionValue = Util.getCriterionValue(product, "INT", true);
        BrandData brandData = (BrandData) context.lookup(Constants.Context.BRAND_DATA);
        User user = (User) this.getEnvironment().getRequest().getSession().getAttribute("user");
        if (user != null) {
            if ("".equals(criterionValue)) {
                channel = Constants.Context.B2B_CHANNEL;
            } else {
                channel = Constants.Context.INT_B2B_CHANNEL;
            }
        } else {
            if ("".equals(criterionValue)) {
                channel = Constants.Context.B2C_CHANNEL;
            } else {
                channel = Constants.Context.INT_B2C_CHANNEL;
            }
        }

        if (brandData != null && !Constants.Common.DEFAULT_BRAND_NAME.equals(brandData.getBrandName())) {
            channel = brandData.getBrandName().concat(channel);
        }

        if (brandData != null && Constants.Common.SELECTOUR_BRAND_NAME.equals(brandData.getBrandName())) {
            channel = Constants.Common.SELECTOUR_BRAND_NAME;
            if (!"".equals(criterionValue)) {
                context.write("defaultResConfigCode", "SELECTOUR_INT");
            }
        }

        context.write("channel", channel);
    }

    /**
     * Construct the preferred departure day for search product criteria.
     */
    private void constructPreferredDateForCriteria() {
        if (preferredDepartureDate != null && !"".equals(preferredDepartureDate)) {
            String[] preferredDateTable = preferredDepartureDate.split(Constants.Common.CHARACTER_BAR);
            int preferedDateTableLenght = preferredDateTable.length;
            org.exolab.castor.types.Date minDepartureDate = null;
            org.exolab.castor.types.Date maxDepartureDate = null;
            Calendar calendar = null;
            if (preferedDateTableLenght == 2) {
                calendar = Util.parseTimestamp(Constants.Common.CALENDAR_FIRST_DAY + Constants.Common.CHARACTER_BAR
                        + preferredDepartureDate);
                minDepartureDate = new Date(calendar.getTime());
                calendar.add(Calendar.MONTH, 1);
                calendar.add(Calendar.DAY_OF_YEAR, -1);
                maxDepartureDate = new Date(calendar.getTime());
            } else if (preferedDateTableLenght == 3) {
                String monthYear = Util.appendString(preferredDateTable[1], Constants.Common.CHARACTER_BAR,
                        preferredDateTable[2]);
                calendar = Util.parseTimestamp(Constants.Common.CALENDAR_FIRST_DAY + Constants.Common.CHARACTER_BAR
                        + monthYear);
                Calendar calendarWithDay = Util.parseTimestamp(preferredDepartureDate);
                int aj = Integer.valueOf(preferredAj);
                calendarWithDay.add(Calendar.DAY_OF_MONTH, aj);
                int month = calendar.get(Calendar.MONTH);
                int monthAj = calendarWithDay.get(Calendar.MONTH);
                int year = calendarWithDay.get(Calendar.YEAR);
                if (month != monthAj) {
                    monthAj = monthAj + 1;
                    calendarWithDay = Util.parseTimestamp(Constants.Common.CALENDAR_FIRST_DAY
                            + Constants.Common.CHARACTER_BAR + monthAj + Constants.Common.CHARACTER_BAR + year);
                    calendarWithDay.add(Calendar.MONTH, 1);
                    calendarWithDay.add(Calendar.DAY_OF_YEAR, -1);
                    minDepartureDate = new Date(calendar.getTime());
                    maxDepartureDate = new Date(calendarWithDay.getTime());
                } else {
                    calendarWithDay.add(Calendar.DAY_OF_MONTH, -(aj * 2));
                    monthAj = calendarWithDay.get(Calendar.MONTH);
                    if (month != monthAj) {
                        monthAj = monthAj + 1;
                        if (monthAj == 12) {
                            year -= 1;
                        }
                        calendarWithDay = Util.parseTimestamp(Constants.Common.CALENDAR_FIRST_DAY
                                + Constants.Common.CHARACTER_BAR + monthAj + Constants.Common.CHARACTER_BAR + year);
                        calendar.add(Calendar.MONTH, 1);
                        calendar.add(Calendar.DAY_OF_YEAR, -1);
                        minDepartureDate = new Date(calendarWithDay.getTime());
                        maxDepartureDate = new Date(calendar.getTime());
                    } else {
                        minDepartureDate = new Date(calendar.getTime());
                        calendar.add(Calendar.MONTH, 1);
                        calendar.add(Calendar.DAY_OF_YEAR, -1);
                        maxDepartureDate = new Date(calendar.getTime());
                    }
                }

            }
            searchCriteria.setMinDepartureDate(minDepartureDate);
            searchCriteria.setMaxDepartureDate(maxDepartureDate);
        }
    }

    /**
     * Construct the preferred duration for search product criteria.
     */
    private void constructPreferredDurationForCriteria() {
        if (preferredDuration != null && !"".equals(preferredDuration)) {
            String[] durationMinMaxNights = preferredDuration.split(",");
            int durationMinNights = Integer.valueOf(durationMinMaxNights[0]);
            int durationMaxNights = Integer.valueOf(durationMinMaxNights[1]);
            searchCriteria.setMaxDurationInNights(durationMaxNights);
            searchCriteria.setMinDurationInNights(durationMinNights);
        }
    }

    /**
     * Construct the preferred duration day for search product criteria.
     */
    private void constructPreferredDurationDayForCriteria() {
        if (preferredDurationDay != null && !"".equals(preferredDurationDay)) {
            int durationDays = Integer.valueOf(preferredDurationDay);
            searchCriteria.setMinDurationInDays(durationDays);
            searchCriteria.setMaxDurationInDays(durationDays);
        }
    }

    /**
     * Put request parameter from rq into the context.
     * @param webEnvironment the webEnvironment
     * @param context the webEnvironment
     * @param paramName the name of the parameter
     */
    private void writeRqParamIntoContext(WebProcessEnvironment webEnvironment,
        IComponentContext<PageLayoutComponent> context, String paramName) {
        String rqParam = webEnvironment.getRequestParameter(paramName);
        if (Boolean.valueOf(rqParam)) {
            context.write(paramName, Boolean.TRUE.toString());
        } else {
            context.write(paramName, Boolean.FALSE.toString());
        }
        webEnvironment.getRequest().setAttribute(paramName, Boolean.FALSE.toString());

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
