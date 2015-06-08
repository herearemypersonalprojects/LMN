/*
 * Copyright Travelsoft, 2000-2012.
 */
package com.travelsoft.lastminute.catalog.bo.scheduler;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.travelsoft.cameleo.catalog.data.Account;
import com.travelsoft.cameleo.catalog.data.ClientFile;
import com.travelsoft.cameleo.catalog.data.ClientSearchCriteria;
import com.travelsoft.cameleo.catalog.data.Person;
import com.travelsoft.cameleo.catalog.interfaces.ServicesFactory;
import com.travelsoft.cameleo.catalog.interfaces.clientfile.ClientFileMainServicesInterface;
import com.travelsoft.cameleo.catalog.interfaces.exceptions.TechnicalException;
import com.travelsoft.cameleo.catalog.interfaces.person.PersonServicesInterface;
import com.travelsoft.lastminute.catalog.bo.util.CRMFileFillerUtils;
import com.travelsoft.lastminute.catalog.bo.util.SchedulerUtils;

/**
 * The goal of this job is to export to ftp six .txt files containing
 * CRM info's (done for last minute).
 *
 * Job will be executed every 3 days at midnight and all the informations for the clientFiles
 * created in last 3 days will be sent.
 *
 */
public class ExportCRMDegrifDataFeedJob extends ExportCRMDataFeedJob  {


    /** The logger . */
    private static final Logger LOGGER = Logger.getLogger(ExportCRMDegrifDataFeedJob.class);

    /** Crm email last sent date. */
    private static final String CRM_DEGRIF_LAST_EXPORT_DATE = "CRM_DEGRIF_LAST_EXPORT_DATE";

     /**
     * Job execution method.
     *
     * @param context : the Job context
     * @throws JobExecutionException : job exception
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    public void execute(JobExecutionContext context) throws JobExecutionException {

          if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("ExportCRMDataFeedJob started.");
            }
            long beginChannelTreatmentMethod = System.currentTimeMillis();

            Date lastExportedDate = getLastExportedDate();

            ClientFile[] clientFiles = getClientFileResultIds(fillClientFileSearchCriteria(lastExportedDate));

            Properties crmProperties = SchedulerUtils.loadPropertiesFile("mails/clientfile/crmDataFeed.properties");

            StringBuffer orderDataFeedSb = new StringBuffer();
            StringBuffer productDataFeedSb = new StringBuffer();
            StringBuffer supplierDataFeedSb = new StringBuffer();
            StringBuffer customerDataFeedSb = new StringBuffer();
            StringBuffer customerAddressFeedSb = new StringBuffer();
            StringBuffer customerSubscriptionFeedSb = new StringBuffer();

            // build headers
            CRMFileFillerUtils.buildOrderDataFeedHeaders(orderDataFeedSb);
            CRMFileFillerUtils.buildToProductDataFeedHeaders(productDataFeedSb);
            CRMFileFillerUtils.buildToSupplierDataFeedHeaders(supplierDataFeedSb);
            CRMFileFillerUtils.buildToCustomerDataFeedHeaders(customerDataFeedSb);
            CRMFileFillerUtils.buildToCustomerAddressDataFeedHeaders(customerAddressFeedSb);
            CRMFileFillerUtils.buildToCustomerSubscriptionDataFeedHeaders(customerSubscriptionFeedSb);

            for (int i = 0; i < clientFiles.length; i++) {
                ClientFile clientFileResult = clientFiles[i];
                ClientFileMainServicesInterface cfServices = ServicesFactory.getClientFileMainServices();
                ClientFile cf = cfServices.getClientFile(clientFileResult.getId());

                if (!SchedulerUtils.cfCreatedAfterSpecificTime(cf, lastExportedDate)) {
                    continue;
                }

				if (cf != null && cf.getChannel() != null && cf.getChannel().getChannel() != null) {
					 String channel = cf.getChannel().getChannel();
                     if (LOGGER.isDebugEnabled()) {
                    	 LOGGER.debug("The channel value : " + channel);
                     }

                     if (!channel.equals("DEGRIFTOUR") && !channel.equals("DEGRIFTOUR-B2B")) {
                    	 continue;
                     }
                }

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Generating CRM dataFeed files for cfId: " + cf.getId());
                }

                // separate this cf from the previous line
                orderDataFeedSb.append("\n");
                productDataFeedSb.append("\n");
                supplierDataFeedSb.append("\n");
                customerDataFeedSb.append("\n");
                customerAddressFeedSb.append("\n");
                customerSubscriptionFeedSb.append("\n");


                // get toId only once
                String toId = "";
                try {
                    toId = CRMFileFillerUtils.getToId(cf.getTourOperator());
                } catch (TechnicalException te) {
                    LOGGER.debug("Technical exception occured while trying to retrieve to_id for toName: "
                                    + cf.getTourOperator(), te);
                }

                String accountId = "";
                Person person = null;
                try {
                    PersonServicesInterface personServicesInterface = ServicesFactory.getPersonServices();
                    if (personServicesInterface == null) {
                        LOGGER.error("Failed to retrieve person service.");
                        return;
                    }
                    ClientSearchCriteria searchCriteria = new ClientSearchCriteria();
                    searchCriteria.setWithAccounts(true);
                    searchCriteria.setWithNewsletters(true);
                    person = personServicesInterface.getPerson(Integer.valueOf(cf.getPersonId()), searchCriteria);
                    Account[] accounts = person.getAccount();

                    if (accounts != null && accounts.length > 0) {
                    	accountId = String.valueOf(accounts[0].getId());
                    }

                } catch (Exception te) {
                    LOGGER.debug("Technical exception occured while trying to retrieve customer id for cf id: " + cf.getId(), te);
                }

                CRMFileFillerUtils.writeToOrderDataFeed(cf, orderDataFeedSb, crmProperties, toId, accountId);
                CRMFileFillerUtils.writeToProductDataFeed(cf, productDataFeedSb);
                CRMFileFillerUtils.writeToSupplierDataFeed(cf, supplierDataFeedSb, toId);
                CRMFileFillerUtils.writeToCustomerDataFeed(cf, customerDataFeedSb, crmProperties, accountId, CRMFileFillerUtils.DS_BRAND);
                CRMFileFillerUtils.writeToCustomerAddressDataFeed(cf, customerAddressFeedSb, getCustomerId(cf), accountId);
                CRMFileFillerUtils.writeToDSCustomerSubscriptionDataFeed(cf, customerSubscriptionFeedSb, person, accountId);
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("CRM orderDataFeedSb file to export: " + orderDataFeedSb.toString());
                LOGGER.debug("CRM productDataFeedSb file to export: " + productDataFeedSb.toString());
                LOGGER.debug("CRM supplierDataFeedSb file to export: " + supplierDataFeedSb.toString());
                LOGGER.debug("CRM customerDataFeedSb file to export: " + customerDataFeedSb.toString());
                LOGGER.debug("CRM customerAddressFeedSb file to export: " + customerAddressFeedSb.toString());
                LOGGER.debug("CRM customerSubscriptionFeedSb file to export: " + customerSubscriptionFeedSb.toString());
            }


            List<File> filesToExport = new ArrayList<File>();
            filesToExport.add(createFile(orderDataFeedSb, "DS_orderDataFeed"));
            filesToExport.add(createFile(productDataFeedSb, "DS_productDataFeed"));
            filesToExport.add(createFile(supplierDataFeedSb, "DS_supplierDataFeed"));
            filesToExport.add(createFile(customerDataFeedSb, "DS_customerDataFeed"));
            filesToExport.add(createFile(customerAddressFeedSb, "DS_customerAddressDataFeed"));
            filesToExport.add(createFile(customerSubscriptionFeedSb, "DS_customerSubscriptionDataFeed"));


            // upload files to FTP server
            exportFlux(filesToExport, CRM_DEGRIF_LAST_EXPORT_DATE);

            long durationTreatment = System.currentTimeMillis() - beginChannelTreatmentMethod;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("ExportCRMDataFeedJob ended in (" + durationTreatment + "ms)");
            }



    }
      /**
     * Get last export date from config table.
     * @return {@link Date}
     */
    private Date getLastExportedDate() {
        String lastSentTime = SchedulerUtils.getConfigValue(CRM_DEGRIF_LAST_EXPORT_DATE, false);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            return formatter.parse(lastSentTime);
        } catch (ParseException pe) {
            LOGGER.error("Parse exception occured while trying to parse config value [CRM_DEGRIF_LAST_EXPORT_DATE]", pe);
        }
        return null;
    }
}
