/*
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.product;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.travelsoft.cameleo.cms.processor.CmsProcessorServlet;
import com.travelsoft.cameleo.cms.processor.Processor;
import com.travelsoft.cameleo.cms.processor.controller.WebProcessEnvironment;
import com.travelsoft.cameleo.cms.processor.exception.PageNotFoundException;
import com.travelsoft.cameleo.cms.processor.messages.MessageRetriever;
import com.travelsoft.cameleo.cms.processor.model.PageIdentifier;
import com.travelsoft.lastminute.catalog.util.AjaxUtil;
import com.travelsoft.lastminute.catalog.util.Constants;
import com.travelsoft.lastminute.catalog.util.Util;
import com.travelsoft.lastminute.data.BrandData;
import com.travelsoft.nucleus.email.JndiSessionMail;
import com.travelsoft.nucleus.email.attachment.PdfFromStringAttachment;
import com.travelsoft.nucleus.email.attachment.renderer.FSaucerRenderer;
import com.travelsoft.nucleus.email.content.AlternativeContent;
import com.travelsoft.nucleus.utilities.text.Normalizer;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * The servlet that will send product email.
 */
public class SendProductEmailServlet extends CmsProcessorServlet {


    /** serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(SendProductEmailServlet.class);


    /**
     * The doGet method of the servlet. <br>
     * This method is called when a form has its tag value method equals to get.
     *
     * @param request the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException if an error occurred
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }


    /**
     * The doPost method of the servlet. <br>
     * This method is called when a form has its tag value method equals to post.
     *
     * @param request the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException if an error occurred
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            sendEmail(request, response);
        } catch (Exception e) {
            LOGGER.error("Error while trying to send product details by mail." + e);
        }
        return;
    }


    /**
     *
     * @param rq the request
     * @param rp the response
     * @throws Exception the Exception
     */
    private void sendEmail(HttpServletRequest rq, HttpServletResponse rp) throws Exception {

        try {
            String pid = rq.getParameter(Constants.Common.PID);
            String productName = rq.getParameter(Constants.Mail.PRODUCT_NAME);
            productName = Util.decodeURL(productName);

            Configuration ftlConfiguration = this.getFtlConfiguration(rq);
            Template template = ftlConfiguration.getTemplate("product/productMail.ftl");

            Map<String, String> rootMap = new HashMap<String, String>();
            String emailText = rq.getParameter(Constants.Mail.EMAIL_TEXT);
            if (emailText != null && !"".equals(emailText)) {
                emailText = emailText.replace("\r\n", "<br />").replace("\n", "<br />");
                rootMap.put(Constants.Mail.EMAIL_TEXT, emailText);
            }
            rootMap.put(Constants.Mail.EMAIL_FROM_NAME, rq.getParameter(Constants.Mail.EMAIL_FROM_NAME));

            BrandData brandData = Util.addBrandContext(rq, null);
            if (brandData != null) {
                 rootMap.put("brandName", brandData.getBrandName());
            }

            StringWriter writer = new StringWriter(1024);
            template.process(rootMap, writer);

            AlternativeContent content = new AlternativeContent(writer.toString());
            JndiSessionMail mailToSend = new JndiSessionMail("java:/Mail", content);
            MessageRetriever retriever = new MessageRetriever(getPageIdentifierInstance(rq, "sentProductEmail"),
                "emailMessages.properties");
            String subject = retriever.createMessage("product.mail.subject", null) + " " + productName;
            String from = rq.getParameter(Constants.Mail.EMAIL_FROM_ADDRESS);
            String to = rq.getParameter(Constants.Mail.EMAIL_TO_ADDRESS);

            rq.setAttribute("usePrintCss", "true");
            rq.setAttribute("fromBooking", "true");
            if (subject != null && !"".equals(subject)) {
                mailToSend.setSubject(subject);
            }
            if (from != null && !"".equals(from)) {
                mailToSend.putFrom(from);
            }
            mailToSend.addTo(to);

            PdfFromStringAttachment pdfFile = getProductPdfFile(rq, rp, productName);
            if (pdfFile != null) {
                mailToSend.addAttachment(pdfFile);
            }

            mailToSend.send();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Email for productId: " + pid + " successfully sent.");
            }

            AjaxUtil.printMessage(rp, "success");

        } catch (IOException io) {
            LOGGER.error(io.toString(), io);
        } catch (Exception e) {
            LOGGER.error(e.toString(), e);
        }
    }


    /**
     * Method retrieves freemarker configuration.
     *
     * @param request The request servlet
     * @return freemarker configuration
     */
    private Configuration getFtlConfiguration(HttpServletRequest request) {
        PageIdentifier pageIdentifier = this.getPageIdentifierInstance(request, "sentProductEmail");
        Configuration configuration = new Configuration();
        String templateRootPath = pageIdentifier.buildTemplateRootPath().toString();
        try {
            configuration.setDirectoryForTemplateLoading(new File(templateRootPath));
        } catch (IOException e) {
            throw new IllegalArgumentException("Impossible to access template folder root '" + templateRootPath + "'",
                e);
        }
        return configuration;
    }


    /**
     * Method builds PageIdentifier object.
     *
     * @param request The request servlet
     * @param page The page
     * @return PageIdentifier object
     */
    private PageIdentifier getPageIdentifierInstance(HttpServletRequest request, String page) {
        String staticRoot = this.getStaticRoot(request);
        String applicationCode = this.getApplicationCode(request);
        String siteCode = this.getSiteCode(request);
        Locale locale = this.getLocale(request);

        return new PageIdentifier(staticRoot, applicationCode, page, siteCode, locale);
    }


    /**
     * Get product pdf file to be used as an attachment.
     *
     * @param rq the request
     * @param rp the response
     * @param productName the name of a product
     * @return {@link PdfFromStringAttachment}
     */
    private PdfFromStringAttachment getProductPdfFile(HttpServletRequest rq,
        HttpServletResponse rp, String productName) {

        String pdfContent = "";
        try {

            String server = rq.getRequestURL().toString();
            String productUrl = server.substring(0, server.lastIndexOf("/")) + "/product.cms?pid="
                            + rq.getParameter(Constants.Common.PID)
                            + Util.getConfigValue("PRODUCT_PDF_URL");
            URL url = new URL(productUrl);
            URLConnection conn = url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                pdfContent = pdfContent + inputLine;
            }
            br.close();
        } catch (MalformedURLException e) {
            LOGGER.error("The product's url is not correct: " + e.getMessage());
        } catch (IOException e) {
            LOGGER.error("Failed to get the product's content: " + e.getMessage());
        }

        String pdfFileName = Util.normalize(productName + ".pdf")
            .replaceAll("[^\\p{ASCII}]", "");
        if (pdfContent == null) {
            return null;
        }
        return new PdfFromStringAttachment(pdfContent, pdfFileName, new FSaucerRenderer());
    }


    /**
     * Get product.cms page as a string that will be used for .pdf generation.
     *
     * @param rq the request
     * @param rp the response
     * @return {@link String}}
     */
    private String getProductPageString(HttpServletRequest rq, HttpServletResponse rp) {
        StringWriter writer = new StringWriter(2048);
        WebProcessEnvironment environment = new WebProcessEnvironment(rq, rp);
        try {
            Processor.render(getPageIdentifierInstance(rq, Constants.Context.PRODUCT_PAGE), environment, writer);
        } catch (PageNotFoundException pnfe) {
            LOGGER.error("Page not found exception occured: " + pnfe);
        }
        return writer.toString();
    }

}
