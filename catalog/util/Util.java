/*
 * Created on 2 sept. 2011
 *
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.travelsoft.cameleo.catalog.data.BaseZone;
import com.travelsoft.cameleo.catalog.data.Criterion;
import com.travelsoft.cameleo.catalog.data.CriterionDefinition;
import com.travelsoft.cameleo.catalog.data.CriterionValue;
import com.travelsoft.cameleo.catalog.data.Document;
import com.travelsoft.cameleo.catalog.data.LayoutCompConfig;
import com.travelsoft.cameleo.catalog.data.LayoutCompConfigItem;
import com.travelsoft.cameleo.catalog.data.LayoutCompContextObject;
import com.travelsoft.cameleo.catalog.data.LayoutComponent;
import com.travelsoft.cameleo.catalog.data.MainZone;
import com.travelsoft.cameleo.catalog.data.PublishedProduct;
import com.travelsoft.cameleo.catalog.data.PublishedProductSearchCriteria;
import com.travelsoft.cameleo.catalog.data.PushingCategory;
import com.travelsoft.cameleo.catalog.data.TechnicalInfo;
import com.travelsoft.cameleo.catalog.data.Zone;
import com.travelsoft.cameleo.catalog.data.ZoneValue;
import com.travelsoft.cameleo.catalog.data.Zones;
import com.travelsoft.cameleo.catalog.interfaces.ServicesFactory;
import com.travelsoft.cameleo.catalog.interfaces.criteria.CriteriaServicesInterface;
import com.travelsoft.cameleo.catalog.interfaces.exceptions.TechnicalException;
import com.travelsoft.cameleo.catalog.interfaces.pushing.PushingServicesInterface;
import com.travelsoft.cameleo.catalog.interfaces.setup.SetupServicesInterface;
import com.travelsoft.cameleo.cms.processor.controller.WebProcessEnvironment;
import com.travelsoft.cameleo.cms.processor.model.IComponentContext;
import com.travelsoft.cameleo.cms.processor.model.PageIdentifier;
import com.travelsoft.lastminute.catalog.tripadvisor.ReviewsRetriever;
import com.travelsoft.lastminute.data.BrandData;
import com.travelsoft.lastminute.data.LastMinuteDeparturesCities;
import com.travelsoft.lastminute.data.Paragraph;

/**
 * <p>Titre : Util.</p>
 * <p>Description : .</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Travelsoft</p>
 * @author zouhair.mechbal
 */
public class Util {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(Util.class);

    /**
    * Mirror of the unicode table from 00c0 to 017f without diacritics.
    */
    private static final String TAB00C0 = "AAAAAAACEEEEIIII"
                   + "DNOOOOO\u00d7\u00d8UUUUYI\u00df"
                   + "aaaaaaaceeeeiiii"
                   + "\u00f0nooooo\u00f7\u00f8uuuuy\u00fey"
                   + "AaAaAaCcCcCcCcDd"
                   + "DdEeEeEeEeEeGgGg"
                   + "GgGgHhHhIiIiIiIi"
                   + "IiJjJjKkkLlLlLlL"
                   + "lLlNnNnNnnNnOoOo"
                   + "OoOoRrRrRrSsSsSs"
                   + "SsTtTtTtUuUuUuUu"
                   + "UuUuWwYyYZzZzZzF";


    /**
    *
    * Method ..
    *
    * @param reviewsUrl source
    * @param reviewsJson destination
    */
   public static void getJson(String reviewsUrl, String reviewsJson) {
       ReviewsRetriever reviewsRetriever = new ReviewsRetriever();
       File reviewsFile = new File(reviewsJson);
       if (!reviewsFile.exists()) {
           JSONObject json = reviewsRetriever.getJsonData(reviewsUrl);
           try {
               FileWriter file = new FileWriter(reviewsJson);
               file.write(json.toString());
               file.flush();
               file.close();
           } catch (IOException e) {
               LOGGER.error("Failed to save reviews json: " + e.getMessage());
           }
       }
   }

   /**
    *
    * Method ..
    *
    * @param path full path
    * @return json object
    */
   public static JSONObject getJson(String path) {
    JSONObject json = null;
    try {
        InputStream is = new FileInputStream(path);
        String jsonTxt = IOUtils.toString(is);
        json = new JSONObject(jsonTxt);
    } catch (FileNotFoundException e) {
        LOGGER.error("Failed to load json object: " + e.getMessage());
    } catch (IOException e) {
        LOGGER.error("Failed to string of inputstream: " + e.getMessage());
    }
    return json;
   }
    /**
     *
     * Method to check whether existing fly & drive criteria.
     *
     * @param publishedProduct the product to be checked
     * @return boolean
     */
    public static boolean isExistingFlyAndDriveCriteria(PublishedProduct publishedProduct) {
        CriterionValue[] criterionValues = Util.getCriterionValues(
            publishedProduct, Constants.CriterionConstants.FLY_CRITERION_CODE);
        if (criterionValues != null) {
            for (CriterionValue criterionValue : criterionValues) {
                String code = criterionValue.getCode();
                if (code != null && code.equals("OUI")) {
                    return true;
                }

            }
        }
        return false;
    }
    /*

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
    */

    /**
     *
     * Retrieves the configuration from the XML page description file.
     *
     * @param context the component's context
     * @param configurationKey The key to retrieve in the configuration file
     * @return The configuration values
     */
    public static PublishedProductSearchCriteria retrieveSearchCriteria(
            IComponentContext<? extends LayoutComponent> context, String configurationKey) {
        LayoutComponent associatedLayout = context.getAssociatedLayout();
        if (associatedLayout != null) {
            LayoutCompConfig config = associatedLayout.getConfig();
            if (config != null) {
                int itemCount = config.getItemCount();
                for (int i = 0; i < itemCount; i++) {
                    LayoutCompConfigItem item = config.getItem(i);
                    if (item != null && configurationKey.equals(item.getModelCodeRef())) {
                        return (PublishedProductSearchCriteria) item.getPublishedProductSearchCriteria().clone();
                    }
                }
            }
        }
        return null;
    }

    /**
     * Retrieves the configuration from the page description file.
     * @param context the component's context
     * @param label The key to retrieve in the configuration file
     * @return The configuration values
     */
    public static String retrieveConfigFromPageDescription(IComponentContext<? extends LayoutComponent> context,
            String label) {
        LayoutComponent associatedLayout = context.getAssociatedLayout();
        if (associatedLayout != null) {
            LayoutCompConfig config = associatedLayout.getConfig();
            if (config != null) {
                LayoutCompConfigItem[] items = config.getItem();
                if (items != null) {
                    for (LayoutCompConfigItem item : items) {
                        LayoutCompContextObject contextObject = item.getContextObject();
                        if (contextObject != null && label.equals(contextObject.getLabel())) {
                            return contextObject.getCode();

                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Removes the prefix from each request parameters.
     *
     * @param queryString
     *            The query string parameters
     * @param prefix
     *            The prefix in the query parameters
     * @return The search product criteria
     */
    public static String removePrefixedQueryString(String queryString, String prefix) {
        StringBuffer criteriaQueryString = new StringBuffer();
        if (queryString != null) {
            for (String param : queryString.split(Constants.Common.SEPARATOR_AMP)) {
                if (param != null && param.startsWith(prefix)) {
                    criteriaQueryString = criteriaQueryString.append(Constants.Common.SEPARATOR_AMP).append(
                            param.substring(prefix.length()));
                }
            }
        }
        return criteriaQueryString.toString();

    }

    /**
     * Get the given MainZone from the given Document.
     *
     * @param edito
     *            the Document.
     * @param mainZoneCode
     *            the code of the MainZone to get.
     * @return The main zone
     */
    public static MainZone getEditoMainZone(Document edito, String mainZoneCode) {
        if (edito != null) {
            int mainZoneCount = edito.getMainZoneCount();
            for (int i = 0; i < mainZoneCount; i++) {
                MainZone mainZone = edito.getMainZone(i);
                if (mainZone != null && mainZone.getCode().equals(mainZoneCode)) {
                    return mainZone;
                }
            }
        }
        return null;
    }

    /**
     * Get a zone content.
     *
     * @param zone
     *            the given zone
     * @return the zone content
     */
    public static String getZoneContent(BaseZone zone) {
        String content = "";
        if (zone != null) {
            ZoneValue value = zone.getValue();
            if (value != null) {
                content = value.getContent();
            }
        }
        return content;
    }

    /**
     * @param parent
     *            the zone parent
     * @param subZoneCode
     *            the sub zone code
     * @return the subZone with the given code
     */
    public static Zone getSubZone(BaseZone parent, String subZoneCode) {
        if (parent != null && subZoneCode != null) {
            Zones subZones = parent.getZones();
            if (subZones.getZoneCount() > 0) {
                for (Zone zone : subZones.getZone()) {
                    if (zone != null && subZoneCode.equals(zone.getCode())) {
                        return zone;
                    }
                }
            }
        }
        return null;
    }

    /**
     * @param parent
     *            the zone parent
     * @param subZoneCode
     *            the sub zone code
     * @return the content of the subZone with the given code
     */
    public static String getSubZoneContent(BaseZone parent, String subZoneCode) {
        return getZoneContent(getSubZone(parent, subZoneCode));
    }

    /**
     * Get product criterion value label list.
     * @param product the published product
     * @param critCode the criterion code
     * @param isCode the return value is code or label
     * @return codeList the value label list
     */
    public static List<String> getCriterionValueLabelList(PublishedProduct product, String critCode, boolean isCode) {
        List<String> codeList = new ArrayList<String>();
        TechnicalInfo technicalInfo = product.getTechnicalInfo();
        if (technicalInfo != null) {
            Criterion[] criterion = technicalInfo.getCriterion();
            if (criterion != null) {
                for (Criterion crit : criterion) {
                    if (crit != null && critCode.equals(crit.getCode())) {
                        int valueCount = crit.getValueCount();
                        for (int i = 0; i < valueCount; i++) {
                            CriterionValue criterionValue = crit.getValue(i);
                            String result;
                            if (isCode) {
                                result = criterionValue.getCode();
                            } else {
                                result = criterionValue.getLabel();
                            }
                            codeList.add(result);
                        }
                        return codeList;
                    }
                }
            }
        }
        return codeList;
    }


    /**
     * Get product criterion values for a given criterion code.
     * @param product the published product
     * @param critionCode the criterion code
     * @return codeList the value label list
     */
    public static CriterionValue[] getCriterionValues(PublishedProduct product, String  critionCode) {
        TechnicalInfo technicalInfo = product.getTechnicalInfo();
        if (technicalInfo != null) {
            Criterion[] criterions = technicalInfo.getCriterion();
            if (criterions != null) {
                for (Criterion criterion : criterions) {
                    if (criterion != null && critionCode.equals(criterion.getCode())) {
                        return criterion.getValue();
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get product criterion value.
     * @param product the published product
     * @param critCode the criterion code
     * @param isCode the return value is code or label
     * @return criterionValueLabel the value
     */
    public static String getCriterionValue(PublishedProduct product, String critCode, boolean isCode) {
        String criterionValue = "";
        TechnicalInfo technicalInfo = product.getTechnicalInfo();
        if (technicalInfo != null) {
            int criterionCount = technicalInfo.getCriterionCount();
            if (criterionCount != 0) {
                for (int i = 0; i < criterionCount; i++) {
                    Criterion crit = technicalInfo.getCriterion(i);
                    if (crit != null && critCode.equals(crit.getCode())) {
                        int criterionValueCount = crit.getValueCount();
                        if (criterionValueCount > 0) {
                            CriterionValue value = crit.getValue(0);
                            if (isCode) {
                                criterionValue = value.getCode();
                            } else {
                                criterionValue = value.getLabel();
                            }
                            return criterionValue;
                        }
                    }
                }
            }
        }
        return criterionValue;
    }

    /**
     * Retrieve the criterion value label.
     * @param criterionCode the criterion code
     * @param criterionValueCode the criterion value code
     * @return valueLabel the criterion value label
     */
    public static String retrieveCriterionValueLabel(String criterionCode, String criterionValueCode) {
        String valueLabel = "";
        CriteriaServicesInterface criteriaServices = ServicesFactory.getCriteriaServices();
        if (criteriaServices != null) {
            CriterionDefinition criteria = null;
            try {
                criteria = criteriaServices.getCriteria(criterionCode);
                if (criteria != null) {
                    int valueCount = criteria.getValueCount();
                    if (valueCount > 0) {
                        for (int i = 0; i < valueCount; i++) {
                            CriterionValue cv = criteria.getValue(i);
                            String code = cv.getCode();
                            if (code.equals(criterionValueCode)) {
                                valueLabel = cv.getLabel();
                                return valueLabel;
                            }
                        }
                    }
                }
            } catch (TechnicalException e) {
                LOGGER.error("Could not get criteria values.", e);
            }
        }
        return valueLabel;
    }

    /**
     * Get criterion all values list.
     * @param criterionCode the criterion code
     * @return criterionValues criterion values
     */
    public static CriterionValue[] getCriterionAllValueList(String criterionCode) {
        CriterionValue[] criterionValues = null;
        CriteriaServicesInterface criteriaServices = ServicesFactory.getCriteriaServices();
        if (criteriaServices != null) {
            CriterionDefinition criteria = null;
            try {
                criteria = criteriaServices.getCriteria(criterionCode, Constants.CAMELEO_CACHE_MANAGER);
                if (criteria != null) {
                    criterionValues = criteria.getValue();
                }
            } catch (TechnicalException e) {
                LOGGER.error("Could not get all criteria values.", e);
            }
        }
        return criterionValues;
    }

    /**
     * Get the value's level by given character.
     * @param value the string value
     * @param character the given character to split the value
     * @return level the value level
     */
    public static int getValueLevel(String value, String character) {
        int level = 1;
        String[] valueSplitTable = value.split(character);
        level = valueSplitTable.length;
        return level;
    }

    /**
     * Get the value's level by given character.
     * @param value the string value
     * @param character the given character to split the value
     * @param level the value level
     * @return valueLabel the value in this level
     */
    public static String getValueByLevel(String value, String character, int level) {
        String valueLabel = "";
        String[] valueSplitTable = value.split(character);
        int length = valueSplitTable.length;
        if (level <= length) {
            valueLabel = valueSplitTable[level];
        }
        return valueLabel;
    }

    /**
     * Append the dynamic parameters.
     * @param params the dynamic parameters
     * @return sb string buffer to string
     */
    public static String appendString(String... params) {
        StringBuilder sb = new StringBuilder();
        for (String param : params) {
            sb.append(param);
        }
        return sb.toString();
    }

    /**
     * Convert the list to array, in the java lib there is already the function,
     * but when i used it, it's always the class casting exception.
     * @param objectList object list
     * @return objectTable object array
     */
    public static String[] convertListToArray(List<String> objectList) {
        int lenght = objectList.size();
        String[] objectTable = new String[lenght];
        int count = 0;
        for (String object : objectList) {
            objectTable[count] = object;
            count++;
        }
        return objectTable;
    }

    /**
     * Parse the timestamp by the given date format.
     * @param timestamp the timestamp string
     * @return cal the Calendar
     */
    public static Calendar parseTimestamp(String timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.Common.PREFERED_DATE_FORMAT, Locale.FRANCE);
        Calendar cal = Calendar.getInstance();
        try {
            Date d = sdf.parse(timestamp);
            cal.setTime(d);
        } catch (ParseException e) {
            LOGGER.error("found the exception when parsing the date : ", e);
        }
        return cal;
    }

    /**
     * To check there has content in string the table or not.
     * @param table the string table
     * @return hasContent
     */
    public static boolean hasContentInStringTable(String[] table) {
        boolean hasContent = false;
        if (table != null && table.length > 0) {
            for (String content : table) {
                if (content != null && !"".equals(content)) {
                    hasContent = true;
                    break;
                }
            }
        }
        return hasContent;
    }

    /**
     * To check there has content in the paragraph table or not.
     * @param table the paragraph table
     * @return hasContent
     */
    public static boolean hasContentInParagraphTable(Paragraph[] table) {
        boolean hasContent = false;
        if (table != null && table.length > 0) {
            for (Paragraph content : table) {
                if (content != null && (!"".equals(content.getContent()) || !"".equals(content.getTitle()))) {
                    hasContent = true;
                    break;
                }
            }
        }
        return hasContent;
    }

    /**
     * Get the cookie from the web container.
     * @param webEnvironment the web container
     * @param cookieName the cookie name
     * @return cookie the cookie
     */
    public static Cookie getCookie(WebProcessEnvironment webEnvironment, String cookieName) {
        Cookie value = null;
        HttpServletRequest request = webEnvironment.getRequest();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie != null && cookieName.equals(cookie.getName())) {
                    value = cookie;
                    break;
                }
            }
        }
        return value;
    }

    /**
     * Deletes a cookie.
     *
     * @param cookieName : the cookie name.
     * @param webEnvironment the web container
     */
    public static void deleteCookie(WebProcessEnvironment webEnvironment, String cookieName) {
        Cookie cookie = getCookie(webEnvironment, cookieName);
        if (cookie != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Deleting cookie " + cookieName);
            }
            cookie.setMaxAge(0);
            HttpServletResponse response = webEnvironment.getResponse();
            response.addCookie(cookie);
        }
    }

    /**
     * Retrieve the date message.
     * @param departureDate org.exolab.castor.types.Date
     * @return departureString departure date string
     */
    public static String retrieveDateMessage(org.exolab.castor.types.Date departureDate) {
        String departureString = "";
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.Common.DISPO_DATE_FORMAT, Locale.FRANCE);
        departureString = sdf.format(departureDate.toDate());
        return departureString;
    }

    /**
     * Get the paragraph table by main zone.
     * @param mainZone the main zone
     * @return paragraphs the paragraph table
     */
    public static Paragraph[] getParagraphs(MainZone mainZone) {
        Paragraph[] paragraphs = null;
        if (mainZone != null && mainZone.getZones() != null) {
            Zone[] zones = mainZone.getZones().getZone();
            if (zones != null && zones.length > 0) {
                paragraphs = new Paragraph[zones.length];
                int count = 0;
                for (Zone zone : zones) {
                    Zones subZones = zone.getZones();
                    Zone subTitleZones = subZones.getZone(0);
                    String title = Util.getZoneContent(subTitleZones);
                    Zone subContentZones = subZones.getZone(1);
                    String content = Util.getZoneContent(subContentZones);
                    Paragraph paragraph = new Paragraph();
                    paragraph.setTitle(title);
                    paragraph.setContent(content);
                    if (count == 0) {
                        paragraph.setAddContent(true);
                    }
                    paragraphs[count] = paragraph;
                    count++;
                }
            }
        }
        return paragraphs;
    }

    /**
     * To check the product is in cookie or not.
     * @param webEnvironment the web environment
     * @param cookieName the cookieName
     * @param pid the product code
     * @return isInCookie is in cookie or not
     */
    public static boolean isInCookie(WebProcessEnvironment webEnvironment, String cookieName, String pid) {
        boolean isInCookie = false;
        Cookie productCookie = getCookie(webEnvironment, cookieName);
        if (productCookie != null) {
            String products = productCookie.getValue();
            if (products != null && !"".equals(products)) {
                String[] productsTable = products.split(Constants.Comparator.COOKIE_SEPARATOR);
                for (String productId : productsTable) {
                    if (productId.equals(pid)) {
                        isInCookie = true;
                        return isInCookie;
                    }
                }
            }
        }
        return isInCookie;
    }

    /**
     * Method enables to remove all the specific characters in a string.
     * @param input the input string
     * @return String the input string without the specific character
     */
    public static String removeAccent(String input) {
        String temp = "";
        if (input != null) {
            temp = normalize(input);
            if (temp.indexOf("'") != -1) {
                temp = temp.replaceAll("'", " ");
            }
        }
        return temp.trim();
    }

   /**
    * Returns string without diacritics - 7 bit approximation.
    *
    * @param source string to convert
    * @return corresponding string without diacritics
    */
    public static String removeDiacritic(String source) {
        char[] vysl = new char[source.length()];
        char one;
        for (int i = 0; i < source.length(); i++) {
            one = source.charAt(i);
            if (one >= '\u00c0' && one <= '\u017f') {
                one = TAB00C0.charAt((int) one - '\u00c0');
            }
            vysl[i] = one;
        }
        return new String(vysl);
    }
    /**
     * Normalizes a string removing all accented characters.
     * @param string the string to normalize
     * @return the normalize string
     */
    public static String normalize(String string) {
        if (string != null) {
            return removeDiacritic(string);
        }
        return string;
    }

    /**
     * Decodes an URL using UTF-8 encoding.
     *
     * @param url The URL.
     * @return {@link String}
     */
    public static String decodeURL(String url) {
        if (url != null) {
            try {
                return URLDecoder.decode(url, Constants.Common.ENCODING_UTF8);
            } catch (UnsupportedEncodingException uee) {
                LOGGER.error("Could not decode URL=[" + url + " ] : " + uee.getMessage(), uee);
            }
        }
        return url;
    }

    /**
     * Method getting configuration value in CONFIGURATION table for a key given.
     *
     * @param configKey the key for define the returned value.
     * @return String of configuration value.
     */
    public static String getConfigValue(String configKey) {
        String value = "";
        if (configKey != null) {
            SetupServicesInterface productService = ServicesFactory.getSetupServices();
            try {
                value = productService.getConfigValue(configKey, Constants.CAMELEO_CACHE_MANAGER);
            } catch (TechnicalException e) {
                LOGGER.error("Technical Exception calling getConfigValue() :" + e, e);
            }
        }
        return value;
    }

    /**
     * Method builds PageIdentifier object.
     * @return PageIdentifier object
     */
    public static PageIdentifier getPageIdentifierInstance() {
        String staticRoot = "/var/data/static/lastminute";
        String applicationCode = "lastminute";
        String siteCode = "lastminute";
        Locale locale = Locale.getDefault();
        return new PageIdentifier(staticRoot, applicationCode, null, siteCode, locale);
    }

    /**
     * Get the media library path.
     * @param fileName the file name
     * @return path the media library path
     */
    public static String getMedialibraryPathByFileName(String fileName) {
        String path = "";
        String mediaLibraryPath = getConfigValue(Constants.Config.MEDIA_LIBRARY_KEY);
        path = mediaLibraryPath + Constants.Common.CHARACTER_SLASH + fileName + Constants.Common.CHARACTER_SLASH;
        return path;
    }

    /**
     * Adds brand data in the context.
     * @param request The servlet request
     * @param context The context
     * @return brandData;
     */
    public static BrandData addBrandContext(HttpServletRequest request,
            IComponentContext<? extends LayoutComponent> context) {
        BrandData brandData = new BrandData();
        String brandName = Util.getConfigValue(request.getServerName());
        if (brandName == null || "".equals(brandName)) {
            brandName = Constants.Common.DEFAULT_BRAND_NAME;
        }
        brandData.setBrandName(brandName);
        brandData.setBrandServerName(request.getServerName());
        if (context != null) {
            context.write(Constants.Context.BRAND_DATA, brandData);
        }
        return brandData;
    }

    /**
     *
     * @return departure cities
     */
    public static LastMinuteDeparturesCities searchLastMinuteDeparturesCities() {
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
            LOGGER.warn("Last minute departures cities search has failed.", e);
        }
        return depCities;
    }
}
