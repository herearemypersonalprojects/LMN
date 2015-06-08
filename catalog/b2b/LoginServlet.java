/*
 * Created on 24 nov. 2011
 *
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.b2b;

import java.io.IOException;

import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.data.User;
import com.travelsoft.cameleo.catalog.interfaces.ServicesFactory;
import com.travelsoft.cameleo.catalog.interfaces.exceptions.AuthenticationException;
import com.travelsoft.cameleo.catalog.interfaces.exceptions.TechnicalException;
import com.travelsoft.cameleo.catalog.interfaces.users.UserServicesInterface;
import com.travelsoft.cameleo.cms.processor.CmsProcessorServlet;
import com.travelsoft.cameleo.cms.processor.messages.MessageRetriever;
import com.travelsoft.cameleo.cms.processor.model.PageIdentifier;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.Util;
import com.travelsoft.lastminute.data.BrandData;



/**
 * <p>Titre : LoginServlet.</p>
 * <p>Description : .</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Travelsoft</p>
 * @author zouhair.mechbal
 */
public class LoginServlet extends CmsProcessorServlet {
    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class);


    /** Default serial version uid. */
    private static final long serialVersionUID = 1L;

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
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        String currentPageUrl = request.getParameter("currentPageUrl");
        String action = request.getParameter("action");
        MessageRetriever retriever = new MessageRetriever(this.getPageIdentifierInstance(request), "b2b.properties");
        if (login != null && password != null && "connect".equals(action)) {
            try {
                UserServicesInterface userServicesInterface = ServicesFactory.getUserServicesInterface();
                BrandData brandData = Util.addBrandContext(request, null);
                User user = null;
                if (brandData != null && brandData.getBrandName() != null) {
                	if (Constants.Common.SELECTOUR_BRAND_NAME.equals(brandData.getBrandName())) {
                        user = userServicesInterface.authenticateUserByEmail(login, password);
                	} else {
                		user = userServicesInterface.authenticateUser(login, password);
                	}
                }

                if (user != null && user.getProfile() != null) {
                    request.getSession().setAttribute("user", user);
                }
            } catch (TechnicalException e) {
                LOGGER.error("Error during authenticating user with login " + login + ".", e);
                String errorMessage = retriever.createMessage("login.technical.error", null);
                request.getSession().setAttribute("errorMessage", errorMessage);
            } catch (AuthenticationException e) {
                LOGGER.error("Unable to authenticate the user with login " + login  + ".", e);
                String errorMessage = retriever.createMessage("login.authentication.error", null);
                request.getSession().setAttribute("errorMessage", errorMessage);
            }
        }
        if ("deconnect".equals(action)) {
            request.getSession().invalidate();
        }
        response.sendRedirect(response.encodeRedirectURL(currentPageUrl));
    }

    /**
     * Method builds PageIdentifier object.
     * @param request The request servlet
     * @return PageIdentifier object
     */
    private PageIdentifier getPageIdentifierInstance(HttpServletRequest request) {
        String staticRoot = this.getStaticRoot(request);
        String applicationCode = this.getApplicationCode(request);
        String siteCode = this.getSiteCode(request);
        Locale locale = this.getLocale(request);
        return new PageIdentifier(staticRoot, applicationCode, null, siteCode, locale);
    }
}
