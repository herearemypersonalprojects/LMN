/*
 * Created on 2 sept. 2011 Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.util;

import com.travelsoft.nucleus.cache.generic.CacheManager;
import com.travelsoft.nucleus.cache.jboss.implementations.JbossCacheManagerFactory;

/**
 * <p>
 * Titre : Constants.
 * </p>
 * <p>
 * Description : .
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Travelsoft
 * </p>
 *
 * @author zouhair.mechbal
 */
public class Constants {


    /** The cameleo cache manager. */
    public static final CacheManager CAMELEO_CACHE_MANAGER = JbossCacheManagerFactory
        .getCacheManager(Constants.Common.CAMELEO_CACHE);


    /**
     * <p>
     * Titre : Common.
     * </p>
     * <p>
     * Description : .
     * </p>
     * <p>
     * Copyright: Copyright (c) 2011
     * </p>
     * <p>
     * Company: Travelsoft
     * </p>
     *
     * @author zouhair.mechbal
     */
    public class Common {


        /** The cameleo cache. */
        public static final String CAMELEO_CACHE = "CameleoTreeCache";

        /** The request parameters from the search engine. */
        public static final String SEARCH_ENGINE_PREFIX = "s_";

        /** The part results page url. */
        public static final String SERP_PAGE_PART_URL = "/serp.cms?";

        /** The request parameters from the refinement search engine. */
        public static final String REFINEMENT_SEARCH_ENGINE_PREFIX = "aff_";

        /** The product id. */
        public static final String PID = "pid";

        /** The preferred departure date. */
        public static final String PREFERED_DATE = "date";

        /** The preferred duration. */
        public static final String PREFERED_DURATION = "duration";

        /** The preferred duration day. */
        public static final String PREFERED_DURATION_DAY = "preferredDurationDay";

        /** The preferred date adjust. */
        public static final String PREFERED_DATE_ADJUST = "aj";

        /** The preferred city. */
        public static final String PREFERED_CITY = "city";

        /** The disponibility date format. */
        public static final String DISPO_DATE_FORMAT = "EEE dd MMM";

        /** The disponibility date format. */
        public static final String ADJUST_DISPO_DATE_FORMAT = "EEE dd MMM yyyy";

        /** The select option date label format. */
        public static final String SELECT_OPTION_LABEL_DATE_FORMAT = "MMMMM yyyy";

        /** The select option date code format. */
        public static final String SELECT_OPTION_CODE_DATE_FORMAT = "MM-yyyy";

        /** The preferred date format. */
        public static final String PREFERED_DATE_FORMAT = "dd-MM-yyyy";

        /** The date format. */
        public static final String DATE_FORMAT = "dd/MM/yyyy";

        /** The character slash. */
        public static final String CHARACTER_SLASH = "/";

        /** The character slash. */
        public static final String CHARACTER_BAR = "-";

        /** The character space. */
        public static final String CHARACTER_SPACE = " ";

        /** The calendar first day. */
        public static final String CALENDAR_FIRST_DAY = "01";

        /** The separator comma. */
        public static final String SEPARATOR_COMMA = ",";

        /** The separator underscore. */
        public static final String SEPARATOR_UNDERSCORE = "_";

        /** The separator ampersand. */
        public static final String SEPARATOR_AMP = "&";

        /** The separator empty. */
        public static final String SEPARATOR_EMPTY = "";

        /** The currency euro. */
        public static final String CURRENCY_EURO = "&euro;";

        /** Destination query parameter. */
        public static final String DESTINATION_QUERY_PARAMETER = "s_c.de";

        /** The destination city request parameter from the refinement search engine. */
        public static final String REFINEMENT_DESTINATION_CITY_PARAMETER = "aff_c.de";

        /** The stay type request parameter from the refinement search engine. */
        public static final String REFINEMENT_STAY_TYPE_PARAMETER = "aff_c.tvoyages";

        /** The criterion separator. */
        public static final String CRITERION_SEPARATOR = "\\.";

        /** Should we use print.css instead of the common.css. */
        public static final String USE_PRINT_CSS = "usePrintCss";

        /** Destination page opened from booking page. */
        public static final String FROM_BOOKING = "fromBooking";

        /** The user parameter. */
        public static final String USER_PARAMETER = "user";

        /** The image png format. */
        public static final String IMAGE_FORMAT_GIF = ".gif";

        /** Utf-8 encoding. */
        public static final String ENCODING_UTF8 = "UTF-8";

        /** Departure month cookie name. */
        public static final String DEP_MONTH_COOKIE = "depMonth";

        /** Trip type cookie name. */
        public static final String TRIP_TYPE_COOKIE = "tripType";

        /** Hotel stars cookie name. */
        public static final String HOTEL_STARS_COOKIE = "hotelStars";

        /** Name in the application scope for the resources cache. */
        public static final String CACHE_RESOURCES = "resourcesCache";

        /** The default brand name. */
        public static final String DEFAULT_BRAND_NAME = "lastminute";

        /** Selectour brand name. */
        public static final String SELECTOUR_BRAND_NAME = "SELECTOUR";

        /** Last minute search: departure city request parameter. */
        public static final String DEPARTURE_CITY = "depCity";

        /** Last minute search: prefix for pushing codes. */
        public static final String LAST_MINUTE_SEARCH_PUSHING_CODE_PREFIX = "LMN_SEARCH_";

        /** Last minute search: product description code. */
        public static final String LAST_MINUTE_SEARCH_PUSHING_DESCRIPTION_CODE = "LMN_SEARCH_DESC";

        /** Last minute search: code for number of nights. */
        public static final String LAST_MINUTE_SEARCH_PUSHING_NB_NIGHTS_CODE = "LMN_NIGHTS";

        /** Player id main zone. */
        public static final String VIDEO_PLAYER_ID = "playerID";

        /** Content id main zone. */
        public static final String VIDEO_CONTENT_ID = "contentID";

        /** Last minute flash sale. */
        public static final String LAST_MINUTE_FLASH_SALE_PUSHING_CODE = "LMN_VF";

        /** Last minute selection expert. */
        public static final String LAST_MINUTE_SELECTION_EXPERT_CODE = "SE";
    }


    /**
     * <p>
     * Titre : Config.
     * </p>
     * <p>
     * Description : .
     * </p>
     * <p>
     * Copyright: Copyright (c) 2011
     * </p>
     * <p>
     * Company: Travelsoft
     * </p>
     *
     * @author zouhair.mechbal
     */
    public class Config {


        /** The key from them the default criteria is retrieved from the page description. */
        public static final String DEFAULT_CRITERIA_KEY = "services.product.defaultSearchCriteria";

        /** The key from them the user code from the page description is retrieved. */
        public static final String USER_CODE = "specific.product.userCode";

        /** The key from them the maximum of selection products is retrieved. */
        public static final String MAX_SELECTION_PRODUCTS = "maxSelectionProducts";

        /** The key for retrieve the background transContents criteria. */
        public static final String BACKGROUND_SEARCH_CRITERIA = "serp.backgroundTransContent";

        /** The key for retrieve the product background active config. */
        public static final String PRODUCT_BACKGROUND_ACTIVE = "productActive";

        /** The key for retrieve the results background active config. */
        public static final String RESULTS_BACKGROUND_ACTIVE = "resultsActive";

        /** The STATIC_GLOBAL_ROOT config key. */
        public static final String STATIC_GLOBAL_ROOT = "STATIC_GLOBAL_ROOT";

        /** The media library key. */
        public static final String MEDIA_LIBRARY_KEY = "MEDIATHEQUE_URL";

        /** The product picto file name. */
        public static final String PRODUCT_PICTO_FILE_NAME = "Picto_Valeur_ajoutee";

        /** The equipement file name. */
        public static final String EQUIPEMENT_FILE_NAME = "picto_equipement";
    }


    /**
     * <p>
     * Titre : Containers.
     * </p>
     * <p>
     * Description : .
     * </p>
     * <p>
     * Copyright: Copyright (c) 2011
     * </p>
     * <p>
     * Company: Travelsoft
     * </p>
     *
     * @author zouhair.mechbal
     */
    public class Containers {


        /** The search engine container. */
        public static final String SEARCH_ENGINE_CONTAINER = "searchEngineContainer";

        /** The main content container. */
        public static final String MAIN_CONTENT_CONTAINER = "mainContentContainer";

        /** The main content container. */
        public static final String MAIN_CONTAINER = "mainContainer";

        /** The search with results container. */
        public static final String SEARCH_WITHRESULTS_CONTAINER = "withResultsContainer";

        /** The search without results container. */
        public static final String SEARCH_WITHOUTRESULTS_CONTAINER = "withoutResultsContainer";

        /** The product item container. */
        public static final String CARTOUCHE_CONTAINER = "cartoucheContainer";

        /** The related product item container. */
        public static final String RELATED_CARTOUCHE_CONTAINER = "relatedCartoucheContainer";

        /** The pagination item container. */
        public static final String PAGINATION_CONTAINER = "paginationContainer";

        /** The product resume container. */
        public static final String PRODUCT_RESUME_CONTAINER = "productResumeContainer";

        /** The destination Compare Container. */
        public static final String COMPARE_DESTINATION_CONTAINER = "destinationCompareContainer";

        /** The delete Compare Container. */
        public static final String COMPARE_DELETE_CONTAINER = "deleteCompareContainer";

        /** The hotel Compare Container. */
        public static final String COMPARE_HOTEL_CONTAINER = "hotelCompareContainer";

        /** The type Compare Container. */
        public static final String COMPARE_TYPE_CONTAINER = "typeCompareContainer";

        /** The main Picture Compare Container. */
        public static final String COMPARE_MAINPICTURE_CONTAINER = "mainPictureCompareContainer";

        /** The prix Compare Container. */
        public static final String COMPARE_PRIX_CONTAINER = "prixCompareContainer";

        /** The reference Compare Container. */
        public static final String COMPARE_REF_CONTAINER = "refCompareContainer";

        /** The departure date Compare Container. */
        public static final String COMPARE_DATEDEP_CONTAINER = "dateDepCompareContainer";

        /** The arrive city Compare Container. */
        public static final String COMPARE_CITYARR_CONTAINER = "cityArriveCompareContainer";

        /** The duration Compare Container. */
        public static final String COMPARE_DURATION_CONTAINER = "durationCompareContainer";

        /** The prestation Compare Container. */
        public static final String COMPARE_PRESTATION_CONTAINER = "prestationsCompareContainer";

        /** The product linked button Compare Container. */
        public static final String COMPARE_BTNCONTI_CONTAINER = "btnContinueCompareContainer";

        /** The bottom button linked to the product Compare Container. */
        public static final String COMPARE_BTNBOTTOM_CONTAINER = "btnBottomCompareContainer";

        /** The main Picture Compare Container. */
        public static final String COMPARE_LUX_CONTAINER = "luxCompareContainer";

        /** The main Picture Compare Container. */
        public static final String COMPARE_NOCES_CONTAINER = "nocesCompareContainer";

        /** The main Picture Compare Container. */
        public static final String COMPARE_RELAX_CONTAINER = "relaxCompareContainer";

        /** The main Picture Compare Container. */
        public static final String COMPARE_CHARM_CONTAINER = "charmCompareContainer";

        /** The main Picture Compare Container. */
        public static final String COMPARE_DIVE_CONTAINER = "diveCompareContainer";

        /** The main Picture Compare Container. */
        public static final String COMPARE_WITHOUANI_CONTAINER = "withOutAnimationCompareContainer";

        /** The main Picture Compare Container. */
        public static final String COMPARE_STUDENTS_CONTAINER = "studentsCompareContainer";

        /** The main Picture Compare Container. */
        public static final String COMPARE_CULTURAL_CONTAINER = "culturalCompareContainer";

        /** The main Picture Compare Container. */
        public static final String COMPARE_CLUB_CONTAINER = "clubCompareContainer";

        /** The main Picture Compare Container. */
        public static final String COMPARE_BRIDGEWK_CONTAINER = "bridgeWKCompareContainer";

        /** The main Picture Compare Container. */
        public static final String COMPARE_OFFRE_CONTAINER = "offreCompareContainer";

        /** The disponibility Compare Container. */
        public static final String COMPARE_DISPONIBILITY_CONTAINER = "disponibilityCompareContainer";

        /** The opinion Compare Container. */
        public static final String COMPARE_OPINION_CONTAINER = "opinionCompareContainer";

        /** The description Compare Container. */
        public static final String COMPARE_DESCRIPTION_CONTAINER = "descriptionCompareContainer";

        /** The include Compare Container. */
        public static final String COMPARE_INCLUDE_CONTAINER = "includeCompareContainer";

        /** The noInclude Compare Container. */
        public static final String COMPARE_NOINCLUDE_CONTAINER = "noIncludeCompareContainer";

        /** The continue Compare Container. */
        public static final String COMPARE_CONTINUE_CONTAINER = "continueCompareContainer";

        /** The continue Compare Container. */
        public static final String COMPARE_CRITERE_SELECT_CONTAINER = "critereSelectContainer";

        /** The critere Container. */
        public static final String COMPARE_CRITERE_CONTAINER = "critereContainer";

        /** The critere main Container. */
        public static final String COMPARE_CRITERE_MAIN_CONTAINER = "critereMainContainer";

        /** The critere main Container. */
        public static final String LMN_SEARCH_RESULT_ITEM_CONTAINER = "resultItemContainer";
    }


    /**
     * <p>
     * Titre : Context.
     * </p>
     * Description :
     * <p>
     * Copyright : Copyright (c) 2011
     * </p>
     * <p>
     * Company : Travelsoft
     * </p>
     *
     * @author MengMeng
     * @version
     */
    public final class Context {


        /** The published Product search response. */
        public static final String PRODUCT_SEARCH_RESPONSE = "publishedProductSearchResponse";

        /** The published Product search response. */
        public static final String RELATED_PRODUCT_SEARCH_RESPONSE = "relatedProductSearchResponse";

        /** The preferred published Product search response. */
        public static final String PREFERRED_PRODUCT_SEARCH_RESPONSE = "preferredPublishedProductSearchResponse";

        /** The current criteria constraints. */
        public static final String CURRENT_CRITERIA_CONSTRAINTS = "currentCriteriasConstraints";

        /** The final criteria constraints. */
        public static final String FINAL_CRITERIA_CONSTRAINTS = "finalCriteriasConstraints";

        /** The published Product. */
        public static final String PUBLISHED_PRODUCT = "publishedProduct";

        /** The published Product. */
        public static final String RELATED_PUBLISHED_PRODUCT = "relatedPublishedProduct";

        /** The published Product index. */
        public static final String PUBLISHED_PRODUCT_INDEX = "publishedProductIndex";

        /** The fly and drive. */
        public static final String FLY_AND_DRIVE = "flyAndDrive";

        /** The published Product index. */
        public static final String RELATED_PUBLISHED_PRODUCT_INDEX = "relatedPublishedProductIndex";

        /** The public index. */
        public static final String ADS_INDEX = "adsIndex";

        /** The public index. */
        public static final String NB_EXPERT_LIST = "nbExpertList";

        /** The published Product index. */
        public static final String LANDING_PAGE_PRODUCT_SELECTED_AVAILABLE = "landingPageSelectedAvailable";

        /** The published Product index. */
        public static final String LANDING_PAGE_PRODUCT_SELECTED_AVAILABLE_DAYS_OFFSET = "landingPageSelectedAvailableDaysOffset";

        /** The published Product index. */
        public static final String LANDING_PAGE_PRODUCT_SEARCH_RESPONSE = "landingPageProductSearchResponse";

        /** The product is selected. */
        public static final String IS_PRODUCT_SELECTED = "isProductSelected";

        /** The partitioner. */
        public static final String PARTITIONER = "partitioner";

        /** Current page number. */
        public static final String DESTINATION = "destination";

        /** Current page number. */
        public static final String CURRENT_PAGE_NMB = "currentPageNmb";

        /** The small displayable product. */
        public static final String SMALL_PRODUCT_DISPLAYABLE = "smallProductDisplayable";

        /** The small displayable product. */
        public static final String RELATED_SMALL_PRODUCT_DISPLAYABLE = "relatedSmallProductDisplayable";

        /** The compare displayable product. */
        public static final String COMPARE_PRODUCT_DISPLAYABLE = "compareProductDisplayable";

        /** The supplement displayable product. */
        public static final String PRODUCT_SUPPLEMENT_DISPLAYABLE = "productSupplementDisplayable";

        /** The disponibilties. */
        public static final String DISPONIBILITIES = "disponibilties";

        /** The select option list. */
        public static final String SELECT_OPTION_LIST = "selectOptionList";

        /** The availabilities map key constructed by base availability. */
        public static final String BASE_DISPO_MAP_KEY = "baseDispoMapKey";

        /** The published product list. */
        public static final String PRODUCT_DISPLAYABLE_LIST = "productDisplayableList";

        /** The compare product list. */
        public static final String COMPARE_DISPLAYABLE_LIST = "compareDisplayableList";

        /** The criterion value displayable list. */
        public static final String CRITERE_VALUE_LIST = "criterionValueDisplayableList";

        /** The criterion displayable value. */
        public static final String CRITERE_VALUE = "criterionValueDisplayable";

        /** The compare show light. */
        public static final String COMPARE_SHOW_LIGHT = "showLight";

        /** The compare show checked image. */
        public static final String COMPARE_SHOW_CHECKED_IMAGE = "showCheckedImage";

        /** The no results search engine. */
        public static final String SEARCH_ENGINE_NO_RESULTS = "searchEngineNoResults";

        /** The attribute to show economy column in a availability. */
        public static final String SHOW_DISPO_ECONOMY_COLUMN = "showDispoEconomyColumn";

        /** The attribute to show economy column in a availability. */
        public static final String DISPLAY_EXPERT_PRODUCT_BLOCK = "showExpertBlock";

        /** The search products result number. */
        public static final String RESULTS_NUMBER = "resultsNumber";

        /** The search products result number. */
        public static final String TOTAL_RESULTS_NUMBER = "totalResultsNumber";

        /** The current page. */
        public static final String CURRENT_PAGE = "currentPage";

        /** The serp page. */
        public static final String SERP_PAGE = "serp";

        /** The product page. */
        public static final String PRODUCT_PAGE = "product";

        /** The compare page. */
        public static final String COMPARE_PAGE = "compare";

        /** The send product email popup page. */
        public static final String PRODUCT_EMAIL_PAGE = "productEmail";

        /** Tracking data . */
        public static final String TRACKING_DATA = "trackingData";

        /** Brand data . */
        public static final String BRAND_DATA = "brandData";

        /** The name for the cache used for static files contents. */
        public static final String STATIC_FILE_CACHE_NAME = "staticFileCache";

        /** B2C channel. */
        public static final String B2C_CHANNEL = "B2C";

        /** B2C_INT channel. */
        public static final String INT_B2C_CHANNEL = "B2C_INT";

        /** B2B channel. */
        public static final String B2B_CHANNEL = "B2B";

        /** B2B_INT channel. */
        public static final String INT_B2B_CHANNEL = "B2B_INT";

        /** The source product. */
        public static final String SOURCE_PRODUCT = "sourceProduct";

        /** The product search criteria. */
        public static final String PRODUCT_SEARCH_CRITERIA = "productSearchCriteria";

        /** An item index. */
        public static final String ITEM_INDEX = "idx";

        /** A last minute departure. */
        public static final String LAST_MINUTE_DEPARTURE = "departure";

        /** The last minute departures cities list. */
        public static final String LAST_MINUTE_DEPARTURES_CITIES = "departureCities";

        /** The player id. */
        public static final String PLAYER_ID = "playerID";

        /** The content id. */
        public static final String CONTENT_ID = "contentID";

        /** The source product. */
        public static final String FLASE_SALE_INITIAL_PRICE = "flashSaleInitialPrice";

        /** The source product. */
        public static final String FLASH_SALE_DISPO = "flashSaleDispo";
    }


    /**
     * <p>
     * Titre : SortConstants.
     * </p>
     * Description :
     * <p>
     * Copyright : Copyright (c) 2011
     * </p>
     * <p>
     * Company : Travelsoft
     * </p>
     *
     * @author MengMeng
     * @version
     */
    public final class SortConstants {


        /** BasePrice sort type : sort by basePriceDesc, label. */
        public static final String SORT_BASE_PRICE_DESC = "base_price_desc";

        /** BasePrice sort type : sort by basePrice, label. */
        public static final String SORT_BASE_PRICE = "base_price";

    }


    /**
     * <p>
     * Titre : CriterionConstants.
     * </p>
     * Description :
     * <p>
     * Copyright : Copyright (c) 2011
     * </p>
     * <p>
     * Company : Travelsoft
     * </p>
     *
     * @author MengMeng
     * @version
     */
    public final class CriterionConstants {


        /** Criterion select code. */
        public static final String CRITERION_SELECT_CODE = "selec";

        /** Criterion select value. */
        public static final String CRITERION_SELECT_VALUE = "true";

        /** Criterion destination code. */
        public static final String CRITERION_DESTIONATION_CODE = "de";

        /** The stay type code. */
        public static final String STAY_TYPE_CODE = "tvoyages";

        /** Criterion sort code. */
        public static final String CRITERION_SORT_CODE = "s_st";

        /** Criterion customer field. */
        public static final String CRITERION_CUSTOMER_FIELD = "c.";

        /** Criterion level separator. */
        public static final String CRITERION_LEVEL_SEPARATOR = "\\.";

        /** The criterion separator. */
        public static final String CRITERION_SEPARATOR = "&";

        /** The criterion equal. */
        public static final String CRITERION_EQUAL = "=";

        /** Criterion category code. */
        public static final String CRITERION_CATEGORY_CODE = "cat";

        /** Criterion picto code. */
        public static final String CRITERION_PICTO_CODE = "picto";

        /** Criterion voyage type code. */
        public static final String CRITERION_TYPE_VOYAGE_CODE = "tvoyages";

        /** Criterion sport code. */
        public static final String CRITERION_SPORT_CODE = "as";

        /** Criterion thematic code. */
        public static final String CRITERION_THEMATIC_CODE = "th";

        /** Meal plan code. */
        public static final String MEAL_PLAN_CODE = "meal";

        /** Meal plan code. */
        public static final String PROMO_CODE = "pro";

        /** Max price code. */
        public static final String MAX_PRICE_CODE = "mxp";

        /** The special cases (dm) criterion code. */
        public static final String DM_CRITERION_CODE = "dm";

        /** The city criterion code. */
        public static final String CITY_CRITERION_CODE = "city";

        /** The country criterion code. */
        public static final String COUNTRY_CRITERION_CODE = "country";

        /** The selection criterion code. */
        public static final String SELECTION_CRITERION_CODE = "selec";

        /** The fly and drive criterion code. */
        public static final String FLY_CRITERION_CODE = "FLY";

        /** The selection criterion code. */
        public static final String SELECTION_CRITERION_CODE_WITH_DOT = SELECTION_CRITERION_CODE + ".";

        /** The selection criterion code. */
        public static final String LANDING_PAGE_CRITERION_CODE = "lp";

        /** The no destination criterion code. */
        public static final String NODESTI_CRITERION_CODE = "noDesti";

        /** Criterion departure day. */
        public static final String CRITERION_DAYS = "s_dd";

        /** Criterion departure month and year. */
        public static final String CRITERION_MONTH_YEAR = "s_dmy";

        /** Criterion flexibility days. */
        public static final String CRITERION_FLEXIBILITY = "s_aj";

        /** Criterion destination city. */
        public static final String DESTINATION_CITY = "s_dpci";

        /** Criterion equipement picto. */
        public static final String EQUIPEMENT_PICTO = "PICTOEQ";
    }


    /**
     * <p>
     * Titre : PartitionerConstants.
     * </p>
     * Description :
     * <p>
     * Copyright : Copyright (c) 2011
     * </p>
     * <p>
     * Company : Travelsoft
     * </p>
     *
     * @author MengMeng
     * @version
     */
    public final class PartitionerConstants {


        /** The next page label. */
        public static final int NEXT_PAGE_NUMBER = 0;

        /** The last page label. */
        public static final int LAST_PAGE_NUMBER = -1;

        /** The max partition pages to show. */
        public static final int SHOW_MAX_PAGES = 6;

        /** The end index. */
        public static final String LIN = "s_lin";

        /** The start index. */
        public static final String FIN = "s_fin";

        /** The maximun products per page. */
        public static final String PRODUCTS_MAX_PAGE = "size";

    }


    /**
     * <p>
     * Titre : TransContentsConstants.
     * </p>
     * Description :
     * <p>
     * Copyright : Copyright (c) 2011
     * </p>
     * <p>
     * Company : Travelsoft
     * </p>
     *
     * @author MengMeng
     * @version
     */
    public final class TransContentsConstants {


        /** The transverse content's category code. */
        public static final String CATEGORY_CODE = "cat";

        /** The destination info category code. */
        public static final String DESTINATION_INFO_CATEGORY_CODE = "destination";

        /** The background category code. */
        public static final String BACKGROUND_CATEGORY_CODE = "background";

        /** The passenger info category code. */
        public static final String PASSENGER_INFO_CATEGORY_CODE = "passengerInfo";

        /** The formality category code. */
        public static final String FORMALITY_CATEGORY_CODE = "formalite";

        /** The to category code. */
        public static final String TO_CATEGORY_CODE = "to";

        /** The flights category code. */
        public static final String FLIGHTS_CATEGORY_CODE = "flights";

        /** The criteria to name code. */
        public static final String CRITERIA_TO_NAME_CODE = "to";

        /** The criteria city code. */
        public static final String CRITERIA_CITY_CODE = "dtci";

        /** The DP code. */
        public static final String CRITERIA_DP_CODE = "dp";

        /** The meteo code. */
        public static final String CRITERIA_METEO_CODE = "meteo";

    }


    /**
     * <p>
     * Titre : Comparator.
     * </p>
     * Description :
     * <p>
     * Copyright : Copyright (c) 2011
     * </p>
     * <p>
     * Company : Travelsoft
     * </p>
     *
     * @author MengMeng
     * @version
     */
    public final class Comparator {


        /** The comparator cookie name. */
        public static final String COOKIE_NAME = "comparator";

        /** The comparator cookie separator. */
        public static final String COOKIE_SEPARATOR = "%26%26%26";

    }


    /**
     * <p>
     * Titre : Directive.
     * </p>
     * Description :
     * <p>
     * Copyright : Copyright (c) 2011
     * </p>
     * <p>
     * Company : Travelsoft
     * </p>
     *
     * @author MengMeng
     * @version
     */
    public final class Directive {


        /** The pension code. */
        public static final String PENSION_CODE = "pensionCode";

        /** The pension configuration file name. */
        public static final String PENSION_CONFIG_NAME = "pension.properties";

    }


    /**
     * <p>
     * Titre : Product.
     * </p>
     * Description :
     * <p>
     * Copyright : Copyright (c) 2011
     * </p>
     * <p>
     * Company : Travelsoft
     * </p>
     *
     * @author MengMeng
     * @version
     */
    public final class Product {


        /** The product availabilities date. */
        public static final String DISPO_DATE = "date";

        /** The product availabilities duration. */
        public static final String DISPO_DURATION = "duration";
    }


    /**
     * <p>
     * Titre : Mail.
     * </p>
     * <p>
     * Description : .
     * </p>
     * <p>
     * Copyright: Copyright (c) 2011
     * </p>
     * <p>
     * Company: Travelsoft
     * </p>
     *
     * @author dusan.spaic
     */
    public class Mail {


        /** The product name param used for sending product email. */
        public static final String PRODUCT_NAME = "pName";

        /** The email additional text that client can insert. */
        public static final String EMAIL_TEXT = "emailText";

        /** The name of a person that sent email. */
        public static final String EMAIL_FROM_NAME = "fromName";

        /** The email address of a person that sent email. */
        public static final String EMAIL_FROM_ADDRESS = "fromAddress";

        /** The email destination address. */
        public static final String EMAIL_TO_ADDRESS = "toAddress";

    }


    /**
     * <p>
     * Titre : OmnitureConstants.
     * </p>
     * Description : Constants used for omniture tag
     * <p>
     * Copyright : Copyright (c) 2011
     * </p>
     * <p>
     * Company : Travelsoft
     * </p>
     *
     * @author dusan.spaic
     * @version
     */
    public final class OmnitureConstants {


        /** Omniture label matching base price coeff sort. */
        public static final String SORT_BASE_PRICE_COEFF = "Holidays sort by :: FA";

        /** Omniture label matching base price sort. */
        public static final String SORT_BASE_PRICE = "Holidays sort by :: PriceA";

        /** Omniture label matching base price desc sort. */
        public static final String SORT_BASE_PRICE_DESC = "Holidays sort by :: PriceD";

        /** Omniture label matching star sort. */
        public static final String SORT_STARS = "Holidays sort by :: Star Rate";

        /** The omniture stats object. */
        public static final String OMNITURE_STATS = "omnitureStats";

        /** The partner id. */
        public static final String PARTNER_ID = "partnerId";

        /** The source. */
        public static final String OMNITURE_SOURCE = "omniture_source";
    }

}
