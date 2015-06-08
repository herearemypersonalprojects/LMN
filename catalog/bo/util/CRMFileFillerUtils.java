/*
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.bo.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import javax.ejb.EJBException;
import javax.naming.NamingException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.exolab.castor.xml.Unmarshaller;

import com.travelsoft.cameleo.catalog.data.ClientFile;
import com.travelsoft.cameleo.catalog.data.Newsletter;
import com.travelsoft.cameleo.catalog.data.Person;
import com.travelsoft.cameleo.catalog.data.Traveller;
import com.travelsoft.cameleo.catalog.interfaces.ServicesFactory;
import com.travelsoft.cameleo.catalog.interfaces.exceptions.TechnicalException;
import com.travelsoft.cameleo.catalog.interfaces.label.LabelServicesInterface;
import com.travelsoft.multitos.enterprise.search.sql.Constants;
import com.travelsoft.multitos.enterprise.search.sql.SQLQuery;
import com.travelsoft.nucleus.cache.generic.CacheManager;
import com.travelsoft.nucleus.cache.jboss.implementations.JbossCacheManagerFactory;
import com.travelsoft.nucleus.productfaring.enterprise.searching.SQLConstants;
import com.travelsoft.order.lastminute.data.CDE;

/**
 * Util.
 */
public class CRMFileFillerUtils {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(CRMFileFillerUtils.class);

    /** Separator used for subscribers stats. */
    private static final String SEPARATOR = "|";

    /** Semicolon Separator used for subscribers stats. */
    private static final String SEMICOLON_SEPARATOR = ";";

    /** Date formatter. */
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

    /** Hour formatter. */
    private static SimpleDateFormat hourFormatter = new SimpleDateFormat("HH");

    /** Date formatter full. */
    private static SimpleDateFormat dateFormatterFull = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /** The export for LMN. */
    public static String LMN_BRAND = "LMN";

    /** The export for DS. */
    public static String DS_BRAND = "DS";

    /**
     * Build headers row in crm file.
     * @param sb the StringBuffer
     */
    public static void buildOrderDataFeedHeaders(StringBuffer sb) {

        sb.append("Order_Number");
        sb.append(SEPARATOR);
        sb.append("Order_Dt");
        sb.append(SEPARATOR);
        sb.append("Booking_Id");
        sb.append(SEPARATOR);
        sb.append("Lmn_Hour_Of_Purchase");
        sb.append(SEPARATOR);
        sb.append("Account_Id");
        sb.append(SEPARATOR);
        sb.append("City_Code");
        sb.append(SEPARATOR);
        sb.append("City_Name");
        sb.append(SEPARATOR);
        sb.append("City_Country_Code");
        sb.append(SEPARATOR);
        sb.append("City_Country_Name");
        sb.append(SEPARATOR);
        sb.append("Lmn_Dep_Terminal_Cd");
        sb.append(SEPARATOR);
        sb.append("LMN_Dep_Terminal_Name");
        sb.append(SEPARATOR);
        sb.append("LMN_Dep_Term_City_Code");
        sb.append(SEPARATOR);
        sb.append("LMN_Dep_Term_City_Name");
        sb.append(SEPARATOR);
        sb.append("LMN_Dep_Term_Country_Cd");
        sb.append(SEPARATOR);
        sb.append("LMN_Dep_Term_Country_Name");
        sb.append(SEPARATOR);
        sb.append("Lmn_Arr_Terminal_Cd");
        sb.append(SEPARATOR);
        sb.append("LMN_Arr_Terminal_Name");
        sb.append(SEPARATOR);
        sb.append("LMN_Arr_Term_City_Code");
        sb.append(SEPARATOR);
        sb.append("LMN_Arr_Term_City_Name");
        sb.append(SEPARATOR);
        sb.append("LMN_Arr_Term_Country_Cd");
        sb.append(SEPARATOR);
        sb.append("LMN_Arr_Term_Country_Name");
        sb.append(SEPARATOR);
        sb.append("Product_Id");
        sb.append(SEPARATOR);
        sb.append("Supplier_Id");
        sb.append(SEPARATOR);
        sb.append("Transtype_Name");
        sb.append(SEPARATOR);
        sb.append("Website_Code");
        sb.append(SEPARATOR);
        sb.append("Partner_Id");
        sb.append(SEPARATOR);
        sb.append("Partner_Name");
        sb.append(SEPARATOR);
        sb.append("Partner_Brand");
        sb.append(SEPARATOR);
        sb.append("Category_Id");
        sb.append(SEPARATOR);
        sb.append("Category_Name");
        sb.append(SEPARATOR);
        sb.append("Cancelled_Dt");
        sb.append(SEPARATOR);
        sb.append("Use_Dt");
        sb.append(SEPARATOR);
        sb.append("Return_Dt");
        sb.append(SEPARATOR);
        sb.append("Lmn_Credit_Card_Type");
        sb.append(SEPARATOR);
        sb.append("Lmn_Website_Currency_Code");
        sb.append(SEPARATOR);
        sb.append("UK_TOV");
        sb.append(SEPARATOR);
        sb.append("Website_TOV");
        sb.append(SEPARATOR);
        sb.append("UK_TTV");
        sb.append(SEPARATOR);
        sb.append("Website_TTV");
        sb.append(SEPARATOR);
        sb.append("Units_Sold");
        sb.append(SEPARATOR);
        sb.append("Rooms_Booked");
        sb.append(SEPARATOR);
        sb.append("Uk_Unit_Price");
        sb.append(SEPARATOR);
        sb.append("Website_Unit_Price");
        sb.append(SEPARATOR);
        sb.append("Update_Ts");
        sb.append(SEPARATOR);
        sb.append("Email_Address");
    }


    /**
     * Build headers row in crm file.
     * @param sb the StringBuffer
     */
    public static void buildToProductDataFeedHeaders(StringBuffer sb) {
        sb.append("Product_Id");
        sb.append(SEPARATOR);
        sb.append("Date_Modified");
        sb.append(SEPARATOR);
        sb.append("Product_Name_Local");
        sb.append(SEPARATOR);
        sb.append("Product_Type_Name");
        sb.append(SEPARATOR);
        sb.append("SEC_PRODUCT_TYPE_NAME");
    }

    /**
     * Build headers row in crm file.
     * @param sb the StringBuffer
     */
    public static void buildToSupplierDataFeedHeaders(StringBuffer sb) {
        sb.append("Supplier_Id");
        sb.append(SEPARATOR);
        sb.append("Date_Modified");
        sb.append(SEPARATOR);
        sb.append("Supplier_Name");
        sb.append(SEPARATOR);
        sb.append("Supplier_Signing Office");
    }

    /**
     * Build headers row in crm file.
     * @param sb the StringBuffer
     */
    public static void buildToCustomerDataFeedHeaders(StringBuffer sb) {
        sb.append("Account_Id");
        sb.append(SEPARATOR);
        sb.append("Email_Address");
        sb.append(SEPARATOR);
        sb.append("Bounce_Ind");
        sb.append(SEPARATOR);
        sb.append("Email_Status_Ind");
        sb.append(SEPARATOR);
        sb.append("Email_Domain_Nm");
        sb.append(SEPARATOR);
        sb.append("Title_Nm");
        sb.append(SEPARATOR);
        sb.append("First_Nm");
        sb.append(SEPARATOR);
        sb.append("Middle_Nm");
        sb.append(SEPARATOR);
        sb.append("Last_Nm");
        sb.append(SEPARATOR);
        sb.append("Partner_Owner_id");
        sb.append(SEPARATOR);
        sb.append("Gender");
        sb.append(SEPARATOR);
        sb.append("Birth_Dt");
        sb.append(SEPARATOR);
        sb.append("Effective_Ts");
        sb.append(SEPARATOR);
        sb.append("Discontinue_Ts");
    }

    /**
     * Build headers row in crm file.
     * @param sb the StringBuffer
     */
    public static void buildToCustomerAddressDataFeedHeaders(StringBuffer sb) {
        sb.append("Account_Id");
        sb.append(SEPARATOR);
        sb.append("Address_Id");
        sb.append(SEPARATOR);
        sb.append("Address_Line_1");
        sb.append(SEPARATOR);
        sb.append("Address_Line_2");
        sb.append(SEPARATOR);
        sb.append("Address_Line_3");
        sb.append(SEPARATOR);
        sb.append("City_Nm");
        sb.append(SEPARATOR);
        sb.append("State_Nm");
        sb.append(SEPARATOR);
        sb.append("Postal_Cd");
        sb.append(SEPARATOR);
        sb.append("Country_Cd");
        sb.append(SEPARATOR);
        sb.append("Address_Type");
        sb.append(SEPARATOR);
        sb.append("Effective_Ts");
        sb.append(SEPARATOR);
        sb.append("Discontinue_Ts");
    }

    /**
     * Build headers row in crm file.
     * @param sb the StringBuffer
     */
    public static void buildToCustomerSubscriptionDataFeedHeaders(StringBuffer sb) {
        sb.append("Account_Id");
        sb.append(SEPARATOR);
        sb.append("Communication_subscription_Id");
        sb.append(SEPARATOR);
        sb.append("Partner_Owner_Id");
        sb.append(SEPARATOR);
        sb.append("Communication_Id");
        sb.append(SEPARATOR);
        sb.append("Communication_Nm");
        sb.append(SEPARATOR);
        sb.append("Campaign_Newsletter_Nm");
        sb.append(SEPARATOR);
        sb.append("Comm_Country_Cd");
        sb.append(SEPARATOR);
        sb.append("Newsletter_Type_nm");
        sb.append(SEPARATOR);
        sb.append("Brand");
        sb.append(SEPARATOR);
        sb.append("Comm_Event_Type");
        sb.append(SEPARATOR);
        sb.append("Communication_Format_Cd");
        sb.append(SEPARATOR);
        sb.append("Subscription_TS");
        sb.append(SEPARATOR);
        sb.append("Effective_Ts");
        sb.append(SEPARATOR);
        sb.append("Discontinue_Ts");
    }


    /**
     * Write Cf infos to the orderDataFeed txt file.
     *
     * @param cf the ClientFile
     * @param sb the StringBuffer
     * @param crmProp the crm properties
     * @param toId the tour operator id
     * @param accountId The account identifier (used for DS export)
     */
    public static void writeToOrderDataFeed(ClientFile cf, StringBuffer sb, Properties crmProp, String toId, String accountId) {

        if (cf.getExternOrderCode() != null) {
            sb.append(StringUtils.substring(cf.getExternOrderCode(), 0, 18));
        }
        sb.append(SEPARATOR);

        sb.append(dateFormatter.format(cf.getDate()));
        sb.append(SEPARATOR);

        sb.append(cf.getId());
        sb.append(SEPARATOR);

        sb.append(hourFormatter.format(cf.getDate()));
        sb.append(SEPARATOR);

        String userIdentifier = cf.getClientEmail();

        if (accountId  != null) {
        	userIdentifier = accountId;
        }

        sb.append(userIdentifier);

        sb.append(SEPARATOR);

        String destCityCode = cf.getDestinationCity();
        sb.append(StringUtils.substring(destCityCode, 0, 50));
        sb.append(SEPARATOR);

        String destCityLabel = getCityLabel(destCityCode);
        sb.append(StringUtils.substring(destCityLabel, 0, 50));
        sb.append(SEPARATOR);

        String destCountryCode = cf.getDestinationCountry();
        sb.append(StringUtils.substring(destCountryCode, 0, 50));
        sb.append(SEPARATOR);

        String destCountryLabel = getCountryLabel(destCountryCode);
        sb.append(StringUtils.substring(destCountryLabel, 0, 50));
        sb.append(SEPARATOR);

        String depCityCode = cf.getDepartureCity();
        sb.append(StringUtils.substring(depCityCode, 0, 5));
        sb.append(SEPARATOR);

        String depCityLabel = getCityLabel(depCityCode);
        sb.append(StringUtils.substring(depCityLabel, 0, 50));
        sb.append(SEPARATOR);

        sb.append(StringUtils.substring(depCityCode, 0, 5));
        sb.append(SEPARATOR);

        sb.append(StringUtils.substring(depCityLabel, 0, 50));
        sb.append(SEPARATOR);

        // LMN_Dep_Term_Country_Cd
        sb.append("FR");
        sb.append(SEPARATOR);

        sb.append("FRANCE");
        sb.append(SEPARATOR);

        sb.append(StringUtils.substring(destCityCode, 0, 5));
        sb.append(SEPARATOR);

        sb.append(StringUtils.substring(destCityLabel, 0, 50));
        sb.append(SEPARATOR);

        sb.append(StringUtils.substring(destCityCode, 0, 5));
        sb.append(SEPARATOR);

        sb.append(StringUtils.substring(destCityLabel, 0, 50));
        sb.append(SEPARATOR);

        sb.append(StringUtils.substring(destCountryCode, 0, 5));
        sb.append(SEPARATOR);

        sb.append(StringUtils.substring(destCountryLabel, 0, 50));
        sb.append(SEPARATOR);

        String productRefTO = cf.getProductRefTO();
        sb.append(StringUtils.substring(productRefTO, 0, 25));
        sb.append(SEPARATOR);

        // supplierId : TO id from db
        sb.append(StringUtils.substring(toId, 0, 30));
        sb.append(SEPARATOR);

        sb.append("BOOK");
        sb.append(SEPARATOR);

        sb.append("FR");
        sb.append(SEPARATOR);

        if (accountId != null) {
        	sb.append("NULL");
        	sb.append(SEPARATOR);
        	sb.append("NULL");
        	sb.append(SEPARATOR);
        	sb.append("Degriftour Selection ");
        	sb.append(SEPARATOR);
        } else {
        	Unmarshaller unmar = new Unmarshaller(CDE.class);
            String diskAccess = SchedulerUtils.getConfigValue("DISKACCESS_URL", true);
            String fileName = diskAccess + "/csvlocal/externalorder/orders/" + cf.getReference() + ".xml";

            try {
                CDE order = (CDE) (unmar.unmarshal(new StringReader(getExternalOrderStream(fileName))));
                String partnerId = order.getPARTNER_ID();

                if ("0".equals(partnerId) || accountId != null) {
                    sb.append("NULL");
                } else {
                    sb.append(partnerId);
                }
                sb.append(SEPARATOR);

                if (accountId == null) {
                	String partnerName = crmProp.getProperty("partnerName." + partnerId);
                    if (partnerName != null) {
                        sb.append(partnerName);
                    } else {
                        // default value
                        sb.append("lastminute.com");
                    }
                } else {
                	sb.append("NULL");
                }

                sb.append(SEPARATOR);

                if (accountId == null) {
                	String partnerBrand = crmProp.getProperty("partnerBrand." + partnerId);
                    if (partnerBrand != null) {
                        sb.append(partnerBrand);
                    } else {
                        // default value
                        sb.append("lastminute.com");
                    }
                } else {
                	sb.append("Degriftour Selection");
                }


                sb.append(SEPARATOR);

            } catch (Exception e) {
                LOGGER.error("Error while tring to read order for the file name " + fileName
                    + ". Default values will be used for crm partner infos");
                // default values
                sb.append("NULL");
                sb.append(SEPARATOR);
                sb.append("lastminute.com");
                sb.append(SEPARATOR);
                sb.append("lastminute.com");
                sb.append(SEPARATOR);
            }
        }


        sb.append("66");
        sb.append(SEPARATOR);

        sb.append("Holidays");
        sb.append(SEPARATOR);

        sb.append("01/01/2099");
        sb.append(SEPARATOR);

        // use_dt
        Date depDate = cf.getDepartureDate();
        String depDateString = dateFormatter.format(depDate);
        sb.append(depDateString);
        sb.append(SEPARATOR);

        Date returnDate = cf.getReturnDate();
        String returnDateString = dateFormatter.format(returnDate);
        sb.append(returnDateString);
        sb.append(SEPARATOR);

        // card type
        if (cf.getPayment() != null && cf.getPayment().length > 0) {
        	String cardTypeFromDb = cf.getPayment()[0].getCardType();
            if (cardTypeFromDb != null) {
                String mappedCardType = crmProp.getProperty(cardTypeFromDb);
                sb.append(mappedCardType);
            }
        } else {
        	sb.append("NULL");
        }

        sb.append(SEPARATOR);

        sb.append("EUR");
        sb.append(SEPARATOR);

        // total order value in UK pounds
        String priceTotalString = cf.getPriceTotal();
        BigDecimal priceTotal = new BigDecimal(priceTotalString);
        BigDecimal priceTotalScaled = priceTotal.setScale(2);

        String poundsRateString = crmProp.getProperty("currency.rate.UK");
        BigDecimal poundsRate = new BigDecimal(poundsRateString);

        BigDecimal priceUK = priceTotalScaled.multiply(poundsRate);
        sb.append(priceUK.toString());
        sb.append(SEPARATOR);

        BigDecimal priceTotalScaled4 = priceTotalScaled.setScale(4);
        sb.append(priceTotalScaled4.toString());
        sb.append(SEPARATOR);

        sb.append(priceUK.toString());
        sb.append(SEPARATOR);

        sb.append(priceTotalScaled4.toString());
        sb.append(SEPARATOR);

        int nmbOfTravellers = 0;
        Traveller[] travellers = cf.getTraveller();
        for (Traveller traveller : travellers) {
            if ("A".equals(traveller.getType()) || "C".equals(traveller.getType())) {
                nmbOfTravellers = nmbOfTravellers + 1;
            }
        }
        sb.append(nmbOfTravellers);
        sb.append(SEPARATOR);

        sb.append("0");
        sb.append(SEPARATOR);

        BigDecimal nmbOfTravellersDec = new BigDecimal(nmbOfTravellers);
        BigDecimal unitPrice = priceTotalScaled.divide(nmbOfTravellersDec, 2, RoundingMode.HALF_UP);
        BigDecimal unitPriceUK = unitPrice.multiply(poundsRate);

        sb.append(unitPriceUK);
        sb.append(SEPARATOR);

        BigDecimal unitPriceScaled = unitPrice.setScale(4);
        sb.append(unitPriceScaled);
        sb.append(SEPARATOR);

        Date modifyDate = cf.getModifyDate();
        String modifyDateString = dateFormatterFull.format(modifyDate);
        sb.append(modifyDateString);
        sb.append(SEPARATOR);

        sb.append(StringUtils.substring(cf.getClientEmail(), 0, 255));
    }



    /**
     * Write Cf infos to the productDataFeed txt file.
     *
     * @param cf the ClientFile
     * @param sb the StringBuffer
     */
    public static void writeToProductDataFeed(ClientFile cf, StringBuffer sb) {

    	String productId = cf.getProductId();
        sb.append(StringUtils.substring(productId, 0, 25));
        sb.append(SEPARATOR);

        // product modify date
        if (productId != null) {
            try {
                Date productModifyDate = getProductModifyDate(productId);
                if (productModifyDate != null) {
                    sb.append(dateFormatter.format(productModifyDate));
                }
            } catch (TechnicalException te) {
                LOGGER.debug("Technical exception occured while trying to retrieve product modify date for productId: "
                    + cf.getProductId(), te);
            }
        }
        sb.append(SEPARATOR);

        String productName = cf.getProductName();
        if (productName != null) {
            sb.append(StringUtils.substring(productName, 0, 255));
        }
        sb.append(SEPARATOR);

        sb.append("Holidays");
        sb.append(SEPARATOR);

        // sec_product_type : empty value
    }



    /**
     * Write Cf infos to the toSupplierDataFeed txt file.
     *
     * @param cf the ClientFile
     * @param sb the StringBuffer
     * @param toId the tour operator id
     */
    public static void writeToSupplierDataFeed(ClientFile cf, StringBuffer sb, String toId) {

        sb.append(StringUtils.substring(toId, 0, 30));
        sb.append(SEPARATOR);

        sb.append("UNKNOW");
        sb.append(SEPARATOR);

        sb.append(StringUtils.substring(cf.getTourOperator(), 0, 50));
        sb.append(SEPARATOR);

        sb.append("LMN FR");
    }

    /**
     * Write Cf infos to the toCustomerDataFeed txt file.
     *
     * @param cf the ClientFile
     * @param sb the StringBuffer
     * @param crmProp the crm properties
     * @param accountId the customer id
     * @param the brand
     */
    public static void writeToCustomerDataFeed(ClientFile cf, StringBuffer sb, Properties crmProp, String accountId, String brand) {
    	String clientEmail = cf.getClientEmail();
    	if (accountId != null) {
            sb.append(accountId);
    	} else {
    		sb.append(clientEmail);
    	}

        sb.append(SEPARATOR);

        sb.append(StringUtils.substring(cf.getClientEmail(), 0, 255));
        sb.append(SEPARATOR);

        sb.append("N");
        sb.append(SEPARATOR);

        sb.append("V");
        sb.append(SEPARATOR);

        // email domain
        clientEmail = cf.getClientEmail();
        if (cf.getClientEmail().contains("@")) {
            String domain = clientEmail.split("@")[1];
            sb.append(StringUtils.substring(domain, 0, 40));
            sb.append(SEPARATOR);
        }

        // personal title
        String clientPersonalTitle = "";
        try {
            clientPersonalTitle = getCustomerPersonalTitle(accountId);
        } catch (TechnicalException te) {
            LOGGER.debug("Technical exception occured while trying to retrieve customer personal title for cf id: "
                + cf.getId(), te);
        }
        String propToSearch = "client.details.socialTitle." + clientPersonalTitle;
        if (crmProp.getProperty(propToSearch) != null) {
            String mappedPersonalTitle = crmProp.getProperty(propToSearch);
            sb.append(mappedPersonalTitle);
        }
        sb.append(SEPARATOR);

        sb.append(StringUtils.substring(cf.getClientName(), 0, 100));
        sb.append(SEPARATOR);

        //Middle name: empty
        sb.append(SEPARATOR);


        sb.append(StringUtils.substring(cf.getClientSurname(), 0, 100));
        sb.append(SEPARATOR);

        if (brand.equals(LMN_BRAND)) {
        	sb.append("-99");
        } else {
        	sb.append("NULL");
        }

        sb.append(SEPARATOR);

        // gender
        if ("Mr".equals(clientPersonalTitle)) {
            sb.append("M");
        } else {
            sb.append("F");
        }
        sb.append(SEPARATOR);

        // birth date
        sb.append("01/01/2099");
        sb.append(SEPARATOR);

        String cfCreationDate = dateFormatterFull.format(cf.getDate());
        sb.append(cfCreationDate);
        sb.append(SEPARATOR);

        // accoutn closed - empty value
    }

    /**
     * Write Cf infos to the toCustomerSubscriptionDataFeed txt file.
     *
     * @param cf the ClientFile
     * @param sb the StringBuffer
     */
    public static void writeToCustomerSubscriptionDataFeed(ClientFile cf, StringBuffer sb) {

        /*
         * We will have 2 lines per clientFile :
         * 1st line for lastminute.com with fixed values for
         * columns Communication_Nm, Campaign_Newsletter_Nm, Comm_Country_Cd, Newsletter_Type_nm, Brand,
         * Comm_Event_Type
         * 2nd line for partners
         *
         * More details: check mail from Mouna on 18/01/2012 16:52
         */

        buildFirstSubscriptionLine(cf, sb);
        sb.append("\n");
        buildSecondSubscriptionLine(cf, sb);
    }

    /**
     * Build 1st clientFile subscription line.
     *
     * @param cf the ClientFile
     * @param sb the stringBuffer
     * @return {@link StringBuffer}
     */
    private static StringBuffer buildFirstSubscriptionLine(ClientFile cf, StringBuffer sb) {

        String clientEmail = cf.getClientEmail();
        sb.append(clientEmail);
        sb.append(SEPARATOR);

        sb.append(SEPARATOR);
        sb.append(SEPARATOR);
        sb.append(SEPARATOR);

        // communication_Nm
        sb.append("Lastminute Infos Hot Tip");
        sb.append(SEPARATOR);
        // Campaign_Newsletter_Nm
        sb.append("LMN FR Tactical");
        sb.append(SEPARATOR);
        sb.append("FR");
        sb.append(SEPARATOR);
        // Newsletter_Type_nm
        sb.append("Tactical");
        sb.append(SEPARATOR);
        sb.append("lastminute.com");
        sb.append(SEPARATOR);
        sb.append("Subscribed");
        sb.append(SEPARATOR);

        // communication_format_cd
        sb.append("H");
        sb.append(SEPARATOR);

        sb.append(dateFormatterFull.format(cf.getDate()));
        sb.append(SEPARATOR);

        sb.append(dateFormatterFull.format(cf.getDate()));
        sb.append(SEPARATOR);

        sb.append("31/12/9999 00:00:00");
        return sb;
    }

    /**
     * Build 2nd clientFile subscription line.
     *
     * @param cf the ClientFile
     * @param sb the stringBuffer
     * @return {@link StringBuffer}
     */
    private static StringBuffer buildSecondSubscriptionLine(ClientFile cf, StringBuffer sb) {
        String clientEmail = cf.getClientEmail();
        sb.append(clientEmail);
        sb.append(SEPARATOR);

        sb.append(SEPARATOR);
        sb.append(SEPARATOR);
        sb.append(SEPARATOR);

        // communication_Nm
        String personId = cf.getPersonId();
        boolean isWithNewsletter = false;
        try {
            isWithNewsletter = isWithNewsletter(personId);
        } catch (TechnicalException te) {
            LOGGER.debug("Technical exception occured while trying to check if person Id: "
                + personId + " has newsletter or not.", te);
        }
        sb.append("Lastminute Infos Partner");
        sb.append(SEPARATOR);

        sb.append("LMN FR Partner");
        sb.append(SEPARATOR);
        sb.append("FR");
        sb.append(SEPARATOR);
        sb.append("lastminute.com");
        sb.append(SEPARATOR);

        // newsletter or not
        if (isWithNewsletter) {
            sb.append("Subscribed");
        } else {
            sb.append("Unsubscribed");
        }
        sb.append(SEPARATOR);

        // communication_format_cd
        sb.append("H");
        sb.append(SEPARATOR);

        sb.append(dateFormatterFull.format(cf.getDate()));
        sb.append(SEPARATOR);

        sb.append(dateFormatterFull.format(cf.getDate()));
        sb.append(SEPARATOR);

        sb.append("31/12/9999 00:00:00");

        return sb;
    }

    /**
     * Write Cf infos to the toCustomerAddressDataFeed txt file.
     *
     * @param cf the ClientFile
     * @param sb the StringBuffer
     * @param customerId the customer id
     * @param accountId
     */
    public static void writeToCustomerAddressDataFeed(ClientFile cf, StringBuffer sb, String customerId, String accountId) {

    	if (accountId != null) {
    		sb.append(accountId);
    	} else {
    		String clientEmail = cf.getClientEmail();
            sb.append(clientEmail);
    	}

        sb.append(SEPARATOR);

        // we dont have address id in db - empty value
        sb.append(SEPARATOR);

        SQLQuery query = new SQLQuery();
        query.addSelect("ADDRESS", "STREET");
        query.addSelect("ADDRESS", "POSTAL_CODE");
        query.addSelect("ADDRESS", "L");
        query.addSelect("ADDRESS", "COUNTRY");
        query.addSelect("ADDRESS", "POSTAL_CODE");
        query.addSelect("ADDRESS", "ADDRESS_TYPE");

        query.addWhere("ADDRESS", "CUSTOMER_ID", SQLConstants.SQL_EQUALS, customerId);

        String queryStr = query.getQuery();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("executed query:" + queryStr);
        }

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            // execute SQL query.
            con = SchedulerUtils.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(queryStr);

            // take infos only from delivery address (address_type='D')
            while (rs.next()) {
                String addressType = rs.getString("ADDRESS_TYPE");
                if ("D".equals(addressType)) {

                    String street = rs.getString("STREET");
                    if (street != null) {
                        sb.append(street);
                    }
                    sb.append(SEPARATOR);

                    // line 2 and line3 are empty
                    sb.append(SEPARATOR);
                    sb.append(SEPARATOR);

                    String city = rs.getString("L");
                    if (city != null) {
                        sb.append(city);
                    }
                    sb.append(SEPARATOR);

                    // state: empty
                    sb.append(SEPARATOR);

                    String postalCode = rs.getString("POSTAL_CODE");
                    if (postalCode != null) {
                        sb.append(postalCode);
                    }
                    sb.append(SEPARATOR);

                    String country = rs.getString("COUNTRY");
                    if (country != null) {
                        sb.append(country);
                    }
                    sb.append(SEPARATOR);

                    // address type
                    sb.append("Billing");
                }
            }
        } catch (NamingException e) {
            throw new EJBException(e);
        } catch (SQLException se) {
            throw new EJBException("Problem occurs while executing " + queryStr, se);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException se) {
                LOGGER.error("error trying to close resultset", se);
            }
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se) {
                LOGGER.error("error trying to close statement", se);
            }
            SchedulerUtils.closeConnection(con);
        }

    }

    /**
     * Method <code>getCityLabel</code>. This method returns label that corresponds to cityCode.
     *
     * @param cityCode the cityCode.
     * @return String cityLabel
     */
    private static String getCityLabel(String cityCode) {
        String cityLabel = cityCode;
        CacheManager cacheMgr = JbossCacheManagerFactory.getCacheManager("CameleoTreeCache");
        LabelServicesInterface lsInterface = ServicesFactory.getLabelServices();
        try {
            cityLabel = lsInterface.getLabel("city", cityCode, Locale.FRANCE, cacheMgr);
        } catch (TechnicalException te) {
            LOGGER.debug("Cannot find label for the city with code " + cityCode + " and type: city. "
                    + "We will use code as a label instead.", te);
        }
        return cityLabel;
    }

    /**
     * Method <code>getCountryLabel</code>. This method returns label that corresponds to the country code.
     *
     * @param countryCode the cityCode.
     * @return String cityLabel
     */
    private static String getCountryLabel(String countryCode) {
        String countryLabel = countryCode;
        CacheManager cacheMgr = JbossCacheManagerFactory.getCacheManager("CameleoTreeCache");
        LabelServicesInterface lsInterface = ServicesFactory.getLabelServices();
        try {
            countryLabel = lsInterface.getCountryLabel(countryCode, Locale.FRANCE, cacheMgr);
        } catch (TechnicalException te) {
            LOGGER.debug("Cannot find label for the country with code " + countryCode, te);
        }
        return countryLabel;
    }

    /**
     * Get tour operator id.
     *
     * @param toName The tour operator name.
     * @throws TechnicalException If a technical error occurs
     * @return String toId.
     */
    public static String getToId(String toName) throws TechnicalException {

        SQLQuery query = new SQLQuery();
        query.addSelect(Constants.TABLE_TOUR_OPERATOR, Constants.TO_PK);

        query.addWhere(Constants.TABLE_TOUR_OPERATOR, Constants.TO_NAME, SQLConstants.SQL_EQUALS, toName);

        String queryStr = query.getQuery();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("executed query:" + queryStr);
        }

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            // execute SQL query.
            con = SchedulerUtils.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(queryStr);
            while (rs.next()) {
                return Integer.toString(rs.getInt(Constants.TO_PK));
            }
        } catch (NamingException e) {
            throw new EJBException(e);
        } catch (SQLException se) {
            throw new EJBException("Problem occurs while executing " + queryStr, se);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException se) {
                LOGGER.error("error trying to close resultset", se);
            }
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se) {
                LOGGER.error("error trying to close statement", se);
            }
            SchedulerUtils.closeConnection(con);
        }
        return null;
    }



    /**
     * Get product modify date.
     *
     * @param productId The product id.
     * @throws TechnicalException If a technical error occurs
     * @return Date the product modify date.
     */
    private static Date getProductModifyDate(String productId) throws TechnicalException {

        SQLQuery query = new SQLQuery();
        query.addSelect(Constants.TABLE_PRODUCT, Constants.PRODUCT_MODIFY_TIMESTAMP);
        query.addWhere(Constants.TABLE_PRODUCT, Constants.PRODUCT_PK, SQLConstants.SQL_EQUALS, productId);

        String queryStr = query.getQuery();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("executed query:" + queryStr);
        }

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            // execute SQL query.
            con = SchedulerUtils.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(queryStr);
            while (rs.next()) {
                return rs.getDate(Constants.PRODUCT_MODIFY_TIMESTAMP);
            }
        } catch (NamingException e) {
            throw new EJBException(e);
        } catch (SQLException se) {
            throw new EJBException("Problem occurs while executing " + queryStr, se);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException se) {
                LOGGER.error("error trying to close resultset", se);
            }
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se) {
                LOGGER.error("error trying to close statement", se);
            }
            SchedulerUtils.closeConnection(con);
        }
        return null;
    }

    /**
     * Get customer personal title.
     *
     * @param customerId The cf id.
     * @throws TechnicalException If a technical error occurs
     * @return String the customer personal title.
     */
    private static String getCustomerPersonalTitle(String customerId) throws TechnicalException {

        SQLQuery query = new SQLQuery();
        query.addSelect(Constants.TABLE_CUSTOMER, "PERSONAL_TITLE");
        query.addWhere(Constants.TABLE_CUSTOMER, Constants.CUSTOMER_PK, SQLConstants.SQL_EQUALS, customerId);

        String queryStr = query.getQuery();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("executed query:" + queryStr);
        }

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            // execute SQL query.
            con = SchedulerUtils.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(queryStr);
            while (rs.next()) {
                return rs.getString("PERSONAL_TITLE");
            }
        } catch (NamingException e) {
            throw new EJBException(e);
        } catch (SQLException se) {
            throw new EJBException("Problem occurs while executing " + queryStr, se);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException se) {
                LOGGER.error("error trying to close resultset", se);
            }
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se) {
                LOGGER.error("error trying to close statement", se);
            }
            SchedulerUtils.closeConnection(con);
        }
        return null;
    }

    /**
     * Get customer id.
     *
     * @param cfId The cf id.
     * @throws TechnicalException If a technical error occurs
     * @return String the customer id.
     */
    public static String getCustomerId(String cfId) throws TechnicalException {

        SQLQuery query = new SQLQuery();
        query.addSelect(Constants.TABLE_CLIENT_FILE, Constants.CLIENT_FILE_CUSTOMER_ID);
        query.addWhere(Constants.TABLE_CLIENT_FILE, Constants.CLIENT_FILE_PK, SQLConstants.SQL_EQUALS, cfId);

        String queryStr = query.getQuery();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("executed query:" + queryStr);
        }

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            // execute SQL query.
            con = SchedulerUtils.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(queryStr);
            while (rs.next()) {
                return rs.getString(Constants.CLIENT_FILE_CUSTOMER_ID);
            }
        } catch (NamingException e) {
            throw new EJBException(e);
        } catch (SQLException se) {
            throw new EJBException("Problem occurs while executing " + queryStr, se);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException se) {
                LOGGER.error("error trying to close resultset", se);
            }
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se) {
                LOGGER.error("error trying to close statement", se);
            }
            SchedulerUtils.closeConnection(con);
        }
        return null;
    }

    /**
     * Get if person is with newsletter or not.
     *
     * @param personId The person id.
     * @throws TechnicalException If a technical error occurs
     * @return {@link boolean}}.
     */
    public static boolean isWithNewsletter(String personId) throws TechnicalException {

        SQLQuery query = new SQLQuery();
        query.addSelect(Constants.TABLE_PERSON, Constants.PERSON_NEWSLETTER_COUNTER);
        query.addWhere(Constants.TABLE_PERSON, Constants.PERSON_PK, SQLConstants.SQL_EQUALS, personId);

        String queryStr = query.getQuery();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("executed query:" + queryStr);
        }

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            // execute SQL query.
            con = SchedulerUtils.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(queryStr);
            while (rs.next()) {
                int newsletterCounter = rs.getInt(Constants.PERSON_NEWSLETTER_COUNTER);
                return newsletterCounter >= 1;
            }
        } catch (NamingException e) {
            throw new EJBException(e);
        } catch (SQLException se) {
            throw new EJBException("Problem occurs while executing " + queryStr, se);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException se) {
                LOGGER.error("error trying to close resultset", se);
            }
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se) {
                LOGGER.error("error trying to close statement", se);
            }
            SchedulerUtils.closeConnection(con);
        }
        return false;
    }

    /**
     * @param fileName The complete file name of the external order stream that must be returned.
     * @return the externalOrderStream saved in the given complete file name.
     * @throws IOException The general external order exception.
     */
    private static String getExternalOrderStream(String fileName) throws IOException  {

        File extOrderFile = new File(fileName);
        InputStreamReader inputReader = new InputStreamReader(new FileInputStream(extOrderFile));
        BufferedReader reader = new BufferedReader(inputReader);
        String externalOrderStream = "";
        String ligne = "";
        while ((ligne = reader.readLine()) != null) {
            externalOrderStream += ligne;
        }
        reader.close();
        return externalOrderStream;
    }

    /**
     *
     * @param cfId The client file value object id.
	 * @param sb The string buffer to append
     * @param person The person object
     * @param accountId The account id
     */
	public static void writeToDSCustomerSubscriptionDataFeed(ClientFile cf, StringBuffer sb, Person person, String accountId) {
		LOGGER.debug("Enter in writeToDSCustomerSubscriptionDataFeed");
		Newsletter[] newsletters = person.getNewsletter();
		for (Newsletter newsletter : newsletters) {
			if (newsletter != null && ("DGF_Mail".equals(newsletter.getCode())
					|| "DGF_Sms".equals(newsletter.getCode())
					|| "DGF_Partners".equals(newsletter.getCode()))) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Newsletter code : " + newsletter.getCode());
				}
				addSubsriptionLine(cf, sb, newsletter.getCode(), accountId);
			    sb.append("\n");

			}
		}
	}

	/**
	 *
	 * @param cfId The client file value object id.
	 * @param sb The string buffer to append
	 * @param newsletterCode The news letter code
	 * @param accountId The account id
	 */
	private static void addSubsriptionLine(ClientFile cf, StringBuffer sb, String newsletterCode, String accountId) {
		sb.append(accountId);
		sb.append(SEPARATOR);
		sb.append("NULL");
		sb.append(SEPARATOR);
		sb.append("NULL");
		sb.append(SEPARATOR);
		sb.append("NULL");
		sb.append(SEPARATOR);

		// communication_Nm
		String personId = cf.getPersonId();
		boolean isWithNewsletter = false;
		try {
			isWithNewsletter = isWithNewsletter(personId);
		} catch (TechnicalException te) {
			LOGGER.debug(
					"Technical exception occured while trying to check if person Id: "
							+ personId + " has newsletter or not.", te);
		}

		if ("DGF_Mail".equals(newsletterCode)) {
			sb.append("Optin_Email");
		} else if ("DGF_Sms".equals(newsletterCode)) {
			sb.append("Opt_Mobile");
		} else if ("DGF_Partners".equals(newsletterCode)) {
			sb.append("Opt_Partner");
		}

		sb.append(SEPARATOR);

		sb.append("NULL");
		sb.append(SEPARATOR);
		sb.append("FR");
		sb.append(SEPARATOR);

		sb.append("NULL");
		sb.append(SEPARATOR);

		sb.append("Degriftour Selection");
		sb.append(SEPARATOR);

		// newsletter or not
		if (isWithNewsletter) {
			sb.append("Subscribed");
		} else {
			sb.append("Unsubscribed");
		}
		sb.append(SEPARATOR);

		// communication_format_cd
		sb.append("H");
		sb.append(SEPARATOR);

		sb.append(dateFormatterFull.format(cf.getDate()));
		sb.append(SEPARATOR);

		sb.append(dateFormatterFull.format(cf.getDate()));
		sb.append(SEPARATOR);

		sb.append("31/12/9999 00:00:00");

	}

	 /**
     * Build headers row in crm file.
     * @param sb the StringBuffer
     */
	public static void buildCGOSOrderDataFeedHeaders(StringBuffer sb) {
		 sb.append("AgentNum");
	     sb.append(SEMICOLON_SEPARATOR);
	     sb.append("orderId");
	     sb.append(SEMICOLON_SEPARATOR);
	     sb.append("autreAgenNum");
	}
}
