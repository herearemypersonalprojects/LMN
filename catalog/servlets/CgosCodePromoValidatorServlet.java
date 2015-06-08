/**
 *
 */
package com.travelsoft.lastminute.catalog.servlets;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.interfaces.ServicesFactory;
import com.travelsoft.cameleo.catalog.interfaces.setup.SetupServicesInterface;
import com.travelsoft.cameleo.cms.processor.CmsProcessorServlet;
import com.travelsoft.cameleo.cms.processor.messages.MessageRetriever;
import com.travelsoft.cameleo.cms.processor.model.PageIdentifier;
import com.travelsoft.lastminute.catalog.comparator.CompareCritereController;
import com.travelsoft.lastminute.catalog.util.Constants;

/**
 * @author zouhair
 *
 */
public class CgosCodePromoValidatorServlet extends HttpServlet  {

     /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(CompareCritereController.class);

    /** Default serial version uid. */
    private static final long serialVersionUID = 1L;

    /** Default connection time-out: 90 seconds. */
    private static final Integer DEFAULT_CONNECTION_TIMEOUT = 10 * 1000;

    /** Default socket response time-out: 45 seconds. */
    private static final Integer DEFAULT_SOCKET_TIMEOUT = 10 * 1000;

    /** The promotion code request parameter. */
    private String PROMO_CODE_PARAMETER = "promoCode";

    /** The url key in configuration in data base table. */
    private String CGOS_CODE_PROMO_VALDATOR_URL_KEY = "CGOS_CODE_PROMO_VALDATOR_URL_KEY";

    /** The date formater. */
    private SimpleDateFormat dateFormater = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);

    /** The date formater. */
    private SimpleDateFormat cgosDateFormater = new SimpleDateFormat("yyyyMMdd", Locale.FRANCE);

    /** The connection timeout. */
    private Integer connectionTimeout;

    /** The socket timeout. */
    private Integer socketTimeout;

    /** The messages file properties*/
    private String properties_file = "/var/data/static/lastminute/shared/ts/local/cms/lastminute/main/messages/cgos.properties";


    /**
     * Called by the server (via the service method) to allow a servlet to handle a GET request.
     *
     * @param request object that contains the request the client has made of the servlet
     * @param response object that contains the response the servlet sends to the client
     * @throws ServletException if an input or output error is detected when the servlet handles the GET request
     * @throws IOException if the request for the GET could not be handled
     *
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        this.doPost(request, response);
    }

    /**
     * Called by the server (via the service method) to allow a servlet to handle a POST request.
     * @param request object that contains the request the client has made of the servlet
     * @param response object that contains the response the servlet sends to the client
     * @throws ServletException if an input or output error is detected when the servlet handles the POST request
     * @throws IOException if the request for the POST could not be handled
     *
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        StringBuffer responseMessage = new StringBuffer();
        responseMessage.append("");
        String promoCodes = request.getParameter(PROMO_CODE_PARAMETER);
        if (promoCodes != null) {
            String valideMsg = "";
            String invalideMsg = "";
            String expiredMsg = "";
            String techErrMsg = "";
            String[] promoCodesAsArray = promoCodes.split(";");
            Properties prop = new Properties();
            prop.load(new FileInputStream(properties_file));
            if (promoCodesAsArray != null) {
                for (String promoCode : promoCodesAsArray) {
                     String cgosResponse = this.sendRequest(promoCode);
                     if (cgosResponse != null && !cgosResponse.equals("")) {
                         Date codePromoValidity = this.retrieveCodeDateValidity(cgosResponse);
                         if (codePromoValidity != null) {
                             Calendar currentDate = Calendar.getInstance();
                             Calendar cgosCalendar = Calendar.getInstance();
                             cgosCalendar.setTime(codePromoValidity);
                             if (currentDate.before(cgosCalendar)) {
                                 valideMsg = valideMsg.concat(";").concat(promoCode);
                             } else {
                                expiredMsg = expiredMsg.concat(";").concat(promoCode);
                             }
                         }
                     } else if (cgosResponse != null && cgosResponse.equals("")) {
                         invalideMsg = invalideMsg.concat(";").concat(promoCode);
                      } else {
                         techErrMsg = techErrMsg.concat(";").concat(promoCode);
                     }
                }
            }
            /********process the results*******/
            // At least one code is valide
            if (!valideMsg.isEmpty()) {
                buildResponseMessage(valideMsg, responseMessage, prop.getProperty("cgos.code.ok", null));
            } else
            // One or many codes cannot be verified because of a technical error
            if (!techErrMsg.isEmpty()) {
                buildResponseMessage(techErrMsg, responseMessage, prop.getProperty("cgos.code.technicalerror", null));
            } else
            // One or many codes are invalid, no other cases
            if (!invalideMsg.isEmpty() && expiredMsg.isEmpty() && techErrMsg.isEmpty()) {
                buildResponseMessage(invalideMsg, responseMessage, prop.getProperty("cgos.code.ko", null));
            } else
            // One or many codes are expired, no other case
            if (invalideMsg.isEmpty() && !expiredMsg.isEmpty() && techErrMsg.isEmpty()) {
                String errorMessage = prop.getProperty("cgos.code.date.ko", null);
                buildResponseMessage(expiredMsg, responseMessage, errorMessage);
            } else
            // One or many codes are invalid or expired
            if (!invalideMsg.isEmpty() && !expiredMsg.isEmpty()){
                String inputCodes = invalideMsg.concat(expiredMsg);
                buildResponseMessage(inputCodes, responseMessage, prop.getProperty("cgos.code.error", null));
            }
        }
        response.getWriter().write(responseMessage.toString().substring(1));
    }

    /**
     * Builds the response message to display to client.
     * @param promoCode The promotion code
     * @param responseMessage The message to display
     * @param message The message to display
     * @return The message
     */
    private StringBuffer buildResponseMessage(String promoCode, StringBuffer responseMessage, String message) {
        if (responseMessage.length() != 0) {
            responseMessage.append("%");
        }
        return responseMessage.append(promoCode).append("#").append(message);
    }
    /**
     * Method sends request to the cgos promo code validator system.
     * @param promoCode The promotion code filled by a user
     * @return the promo code status if valid or not
     */
    private String sendRequest(String promoCode) {
        String cgosResponse = null;
        HttpClient httpClient = new HttpClient();
        setTimeouts(httpClient);
        HttpMethod getMethod = null;
        try {
            SetupServicesInterface productService = ServicesFactory.getSetupServices();
            String cgosUrl = productService.getConfigValue(CGOS_CODE_PROMO_VALDATOR_URL_KEY, Constants.CAMELEO_CACHE_MANAGER);
            //getMethod = new GetMethod("http://www2.cgos.mezcalito.fr/infos_membre.php?membre=" + promoCode);
            getMethod = new GetMethod(cgosUrl.concat(promoCode));
            httpClient.executeMethod(getMethod);
            if (getMethod.getStatusCode() == HttpStatus.SC_OK) {
                 return getMethod.getResponseBodyAsString();
            }
        } catch (Exception e) {
            LOGGER.error(e.toString(), e);
        } finally {
            getMethod.releaseConnection();
        }
        return cgosResponse;
    }

    /**
     * Retrieves the code promo validity.
     * @param cgosResponse The cgos response
     * @return The promo validity
     */
    private Date retrieveCodeDateValidity(String cgosResponse) {
        try {
            if (cgosResponse.indexOf("validite") != -1) {
                String validityDate = cgosResponse.substring(cgosResponse.indexOf("validite") + 10, cgosResponse.indexOf("validite") + 18);
                if (validityDate != null && validityDate.length() == 8) {
                    return cgosDateFormater.parse(validityDate);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString(), e);
        }
        return null;
    }

    /** @param httpClient HTTP client of which timeout values have to be set */
    private void setTimeouts(HttpClient httpClient) {
        HttpConnectionManagerParams httpParams = httpClient.getHttpConnectionManager().getParams();
        if (connectionTimeout != null) {
            httpParams.setConnectionTimeout(connectionTimeout);
        } else {
            httpParams.setConnectionTimeout(DEFAULT_CONNECTION_TIMEOUT);
        }
        if (socketTimeout != null) {
            httpParams.setSoTimeout(socketTimeout);
        } else {
            httpParams.setSoTimeout(DEFAULT_SOCKET_TIMEOUT);
        }
    }
}
