/*
² * Created on 18 nov. 2011
 *
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.seo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;

import com.travelsoft.cameleo.catalog.data.CriterionValue;
import com.travelsoft.cameleo.catalog.data.Document;
import com.travelsoft.cameleo.catalog.data.MainZone;
import com.travelsoft.cameleo.catalog.data.PublishedProduct;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.Util;
import com.travelsoft.nucleus.utilities.text.Normalizer;

/**
 * <p>Titre : SeoTool.</p>
 * <p>Description : .</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Travelsoft</p>
 * @author zouhair.mechbal
 */
public class SeoTool {

    /**
     * Retrieves the SEO product Url. This url is used in product result block.
     * @param product The product
     * @return The seo url
     */
    public static String getSeoProductUrl(PublishedProduct product) {

        String destinationCountryCode = null;
        String destinationCityCode = null;
        String productName = null;
        StringBuffer seoUrl = new StringBuffer();

        List<String> destinationResults = Util.getCriterionValueLabelList(product,
            Constants.CriterionConstants.CRITERION_DESTIONATION_CODE, true);

        if (destinationResults != null && !destinationResults.isEmpty()) {
            for (String destinationResult : destinationResults) {
                if (destinationResult != null && destinationResult.indexOf('.') != -1) {
                    destinationCityCode = destinationResult.split("\\.")[1];
                    destinationCountryCode = destinationResult.split("\\.")[0];
                } else if (destinationResult != null && destinationResult.indexOf('.') == -1) {
                    if (destinationCountryCode == null) {
                        destinationCountryCode = destinationResult;
                    }
                }
            }
        }

//        if (destinationCountryCode != null) {
//            seoUrl = seoUrl.append("voyage-").append(destinationCountryCode);
//        }
//
//        if (destinationCityCode != null) {
//            seoUrl = seoUrl.append('/').append(destinationCityCode);
//        }

        Document edito = product.getEdito();
        if (edito != null) {
            MainZone titleZone = Util.getEditoMainZone(edito, "title");
            productName = Util.getZoneContent(titleZone);
        }

        seoUrl = seoUrl.append(getProductNameSeoUrl(productName));

        return seoUrl.toString();
    }

    /**
     * Retrieves the only requested destination from the request.
     * @param request The servlet request
     * @return the only requested destination
     */
    public static String getUniqueDestinationCountry(HttpServletRequest request) {
        List<String> result = new ArrayList<String>();
        String[] paramValueCodes = getParamValueCodes(request, Constants.Common.DESTINATION_QUERY_PARAMETER);
        if (paramValueCodes != null) {
             for (String valueArray : paramValueCodes) {
                 String[] resultArray = valueArray.split("\\.");
                 if (resultArray != null) {
                     if (!result.contains(resultArray[0])) {
                         result.add(resultArray[0]);
                     }
                 }
             }
             if (result.size() == 1) {
                 return result.get(0);
             }
        }
        return null;
    }

    /**
     * Retrieves the only requested destination from the product.
     * @param product The product
     * @return the only requested destination
     */
    public static String getUniqueDestination(PublishedProduct product, String destType) {

        String result = null;

        List<String> destinationResults = Util.getCriterionValueLabelList(product,
            Constants.CriterionConstants.CRITERION_DESTIONATION_CODE, true);

        if (destinationResults != null && !destinationResults.isEmpty()) {
            for (String destination : destinationResults) {
                if (destination.indexOf('.') != -1) {
                    if ("country".equals(destType)) {
                        result = destination.split("\\.")[0];
                    }
                    if ("city".equals(destType)) {
                        result = destination.split("\\.")[1];
                    }
                } else if ("country".equals(destType) && destination.indexOf('.') == -1) {
                    result = destination;
                }
            }
        }
        return result;
    }

    /**
     * Retrieves the only requested destination from the criterion values.
     * @param criterionValue The criterion value array
     * @return the only requested destination
     */
    public static String getUniqueDestination(CriterionValue[] criterionValues) {

        List<String> countryList = new ArrayList<String>();
        List<String> cityList = new ArrayList<String>();

        if (criterionValues != null) {
            for (CriterionValue criterionValue : criterionValues) {
                if (criterionValue != null && criterionValue.getCode() != null) {
                    if (criterionValue.getCode().indexOf('.') == -1) {
                        countryList.add(criterionValue.getCode());
                    } else {
                        cityList.add(criterionValue.getCode());
                    }
                }
            }

            if (countryList.size() == 1 && cityList.size() == 1) {
                String country = countryList.get(0);
                String city = cityList.get(0);
                if (city != null && city.indexOf('.') != -1 && city.split("\\.")[0].equals(country)) {
                    return city;
                }
            } else if (countryList.size() == 1 &&  cityList.size() == 0) {
               return countryList.get(0);
            } else if  (countryList.size() == 0 &&  cityList.size() == 1) {
                return cityList.get(0);
            }
        }
        return null;
    }

    /**
     * Retrieves the only requested destination.
     * @param request The servlet request
     * @return the only requested destination
     */
    public static String getUniqueDestinationCity(HttpServletRequest request) {
        String destinationCode = null;

        String[] paramValueCodes = getParamValueCodes(request, Constants.Common.DESTINATION_QUERY_PARAMETER);
        if (paramValueCodes != null) {
            destinationCode = getDestinationCode(paramValueCodes);
            if (destinationCode != null) {
                return destinationCode;
            }
        }

        paramValueCodes = getParamValueCodes(request, Constants.Common.REFINEMENT_DESTINATION_CITY_PARAMETER);
        if (paramValueCodes != null) {
            destinationCode = getDestinationCode(paramValueCodes);
            if (destinationCode != null) {
                return destinationCode;
            }
        }

        return null;
    }

    /**
     * Retrieves the destination codes.
     * @param paramValueCodes The query parameters
     * @return The destination code
     */
    private static String getDestinationCode(String[] paramValueCodes) {
        List<String> result = new ArrayList<String>();
        if (paramValueCodes != null) {
             for (String valueArray : paramValueCodes) {
                 String[] resultArray = valueArray.split("\\.");
                 if (resultArray != null && resultArray.length == 2) {
                     if (!result.contains(resultArray[1])) {
                         result.add(resultArray[1]);
                     }
                 }
             }
             if (result.size() == 1) {
                 return result.get(0);
             }
        }
        return null;
    }


    /**
     * Retrieves the only requested destination.
     * @param request The servlet request
     * @return the only requested destination
     */
    public static String getUniqueStayType(HttpServletRequest request) {
        String[] paramValueCodes = getParamValueCodes(request, Constants.Common.REFINEMENT_STAY_TYPE_PARAMETER);
        if (paramValueCodes != null && paramValueCodes.length == 1) {
            return paramValueCodes[0];
        }
        return null;
    }

    /**
     * Retrieves the only stay type from the product.
     * @param product The product
     * @return the only requested destination
     */
    public static String getUniqueStayType(PublishedProduct product) {
        List<String> destinationList = Util.getCriterionValueLabelList(product, Constants.CriterionConstants.STAY_TYPE_CODE, true);
        if (destinationList != null && destinationList.size() == 1) {
            return destinationList.get(0);
        }
        return null;
    }

    /**
     * @param request The servlet request
     * @param queryParameter The query parameter
     * @return The parameter query and the value
     */
    private static String[] getParamValueCodes(HttpServletRequest request, String queryParameter) {
         Map requestParameterAsMap = request.getParameterMap();
         if (requestParameterAsMap != null) {
             Iterator iterator = requestParameterAsMap.keySet().iterator();
             while (iterator.hasNext()) {
                 String key = (String) iterator.next();
                 if (queryParameter.equals(key)) {
                     String[] destinationParameterValue = (String[]) requestParameterAsMap.get(key);
                     if (destinationParameterValue != null && destinationParameterValue.length > 0) {
                         String destinationValues = destinationParameterValue[0];
                         return destinationValues.split(",");
                     }
                 }
             }
         }
         return null;
    }

    /**
     * @param url the given url String.
     * @return a new url containing valid characters only.
     */
    public static String replaceInvalidUrlCharacters(String url) {
        url = url.replaceAll("\\W", "-");
        url = url.replaceAll("--+", "-");
        if (url.length() >= 2 && url.lastIndexOf('-') == url.length() - 1) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }

    /**
     * @param url the given url String.
     * @return a new url containing valid characters only and no accents.
     */
    public static String getProductNameSeoUrl(String productName) {
        productName = StringEscapeUtils.unescapeHtml(productName);
        productName = StringEscapeUtils.unescapeXml(productName);
        productName = Util.normalize(productName).toUpperCase();
        //productName = java.text.Normalizer.normalize(productName, java.text.Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toUpperCase();;
        productName = replaceInvalidUrlCharacters(productName);
        productName = productName.toLowerCase();
        return productName;
    }
}
