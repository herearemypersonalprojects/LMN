/*
 * Copyright Travelsoft, 2000-2011.
 */
package com.travelsoft.lastminute.catalog.bo.scheduler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

import com.travelsoft.cameleo.catalog.data.ClientFile;
import com.travelsoft.cameleo.catalog.data.ClientFileSearchCriteria;
import com.travelsoft.cameleo.catalog.data.ClientFileSearchResponse;
import com.travelsoft.cameleo.catalog.data.Result;
import com.travelsoft.cameleo.catalog.data.types.ClientFileTypeDef;
import com.travelsoft.cameleo.catalog.data.types.ProductStructureDef;
import com.travelsoft.cameleo.catalog.interfaces.Constants.SortConstants;
import com.travelsoft.cameleo.catalog.interfaces.ServicesFactory;
import com.travelsoft.cameleo.catalog.interfaces.clientfile.ClientFileMainServicesInterface;
import com.travelsoft.cameleo.catalog.interfaces.exceptions.TechnicalException;
import com.travelsoft.cameleo.catalog.interfaces.search.BackSearchServicesInterface;
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
public class ExportCRMDataFeedJob implements StatefulJob {


    /** The logger . */
    private static final Logger LOGGER = Logger.getLogger(ExportCRMDataFeedJob.class);

    /** Crm email last sent date. */
    private static final String CRM_LAST_EXPORT_DATE = "CRM_LAST_EXPORT_DATE";

    /** Crm ftp address. */
    private static final String CRM_FTP_ADDRESS = "CRM_FTP_ADDRESS";

    /** Crm ftp login. */
    private static final String CRM_FTP_LOGIN = "CRM_FTP_LOGIN";

    /** Crm ftp password. */
    private static final String CRM_FTP_PASSWORD = "CRM_FTP_PASSWORD";

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

            //check if cf was created after the last export time : this cannot be checked by sql
            // for example if last export was yesterday at 16h, client files created before 16h should not
            // be used for crm export.
            if (!SchedulerUtils.cfCreatedAfterSpecificTime(cf, lastExportedDate)) {
                continue;
            }

            if (cf != null && cf.getChannel() != null && cf.getChannel().getChannel() != null) {
				 String channel = cf.getChannel().getChannel();
                 if (LOGGER.isDebugEnabled()) {
                	 LOGGER.debug("The channel value : " + channel);
                 }

                if (channel.equals("DEGRIFTOUR")|| channel.equals("DEGRIFTOUR-B2B")) {
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
                LOGGER.debug("Technical exception occured while trying to retrieve to_id for toName: " + cf.getTourOperator(), te);
            }

            // get customerId only once
            String customerId = getCustomerId(cf);

            CRMFileFillerUtils.writeToOrderDataFeed(cf, orderDataFeedSb, crmProperties, toId, null);
            CRMFileFillerUtils.writeToProductDataFeed(cf, productDataFeedSb);
            CRMFileFillerUtils.writeToSupplierDataFeed(cf, supplierDataFeedSb, toId);
            CRMFileFillerUtils.writeToCustomerDataFeed(cf, customerDataFeedSb, crmProperties, customerId, CRMFileFillerUtils.LMN_BRAND);
            CRMFileFillerUtils.writeToCustomerAddressDataFeed(cf, customerAddressFeedSb, customerId, null);
            CRMFileFillerUtils.writeToCustomerSubscriptionDataFeed(cf, customerSubscriptionFeedSb);
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
        filesToExport.add(createFile(orderDataFeedSb, "orderDataFeed"));
        filesToExport.add(createFile(productDataFeedSb, "productDataFeed"));
        filesToExport.add(createFile(supplierDataFeedSb, "supplierDataFeed"));
        filesToExport.add(createFile(customerDataFeedSb, "customerDataFeed"));
        filesToExport.add(createFile(customerAddressFeedSb, "customerAddressDataFeed"));
        filesToExport.add(createFile(customerSubscriptionFeedSb, "customerSubscriptionDataFeed"));

        // upload files to FTP server
        exportFlux(filesToExport, CRM_LAST_EXPORT_DATE);

        long durationTreatment = System.currentTimeMillis() - beginChannelTreatmentMethod;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("ExportCRMDataFeedJob ended in (" + durationTreatment + "ms)");
        }
    }



	protected String getCustomerId(ClientFile cf) {
		String customerId = "";
		try {
		    customerId = CRMFileFillerUtils.getCustomerId(Integer.toString(cf.getId()));
		} catch (TechnicalException te) {
		    LOGGER.debug("Technical exception occured while trying to retrieve customer id for cf id: "
		                    + cf.getId(), te);
		}
		return customerId;
	}



    /**
     * Create and fill the File from StringBuffer.
     *
     * @param sb the StringBuffer
     * @param fileName the fileName
     * @return {@link File}
     */
    protected File createFile(StringBuffer sb, String fileName) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy_HH-mm");
        Calendar cal = Calendar.getInstance();
        String currTime = formatter.format(cal.getTime());


        File file = new File(fileName + currTime + ".txt");
        try {
            FileUtils.writeStringToFile(file, sb.toString());
        } catch (IOException e) {
            LOGGER.error(e.toString(), e);
        }
        return file;
    }

    /**
     * This method search for clientFiles with specific criteria. In the result we will have departure date.
     *
     * @param criteria : the criteria
     * @return ClientFile[] the client files
     */
    protected ClientFile[] getClientFileResultIds(ClientFileSearchCriteria criteria) {

        ClientFileSearchResponse cfsResponse = new ClientFileSearchResponse();
        BackSearchServicesInterface bssInterface = ServicesFactory.getBackSearchServices();

        try {
            cfsResponse = bssInterface.searchLightClientFiles(criteria);
        } catch (TechnicalException te) {
            LOGGER.error("TechnicalException in searchLightClientFiles : " + te.getMessage(), te);
            return new ClientFile[0];
        }

        Result[] results = cfsResponse.getResult();
        ClientFile[] clientFiles = new ClientFile[results.length];
        for (int i = 0; i < results.length; i++) {
            clientFiles[i] = (ClientFile) results[i];
        }
        return clientFiles;
    }


    /**
     * This method prepares criteria for search.
     *
     * @param lastExportedDate the time of the last successful export
     * @return ClientFileSearchCriteria
     */
    protected ClientFileSearchCriteria fillClientFileSearchCriteria(Date lastExportedDate) {

        ClientFileSearchCriteria criteria = new ClientFileSearchCriteria();
        criteria.setClientFileType(ClientFileTypeDef.DISTRIBUTOR);
        criteria.setSortBy(SortConstants.SORT_DEPARTURE_DATE_ASC);
        //criteria.setMinDepDate(Calendar.getInstance().getTime());

        // Export info's only for the CF created after the last export was executed.
        // By default job will be executed every 3 days at midnight.
//        if (lastExportedDate != null) {
//            criteria.setBookDateStart(lastExportedDate);
//        }

        // not for PD
        criteria.setStructureType(ProductStructureDef.PRODUCT.toString());
        return criteria;
    }


    /**
     * Export flux files to ftp server.
     *
     * @param filesToExport the list of files to export
     */
    protected void exportFlux(List<File> filesToExport, String lastExportDateByType) {
        String address = SchedulerUtils.getConfigValue(CRM_FTP_ADDRESS, true);
        String login = SchedulerUtils.getConfigValue(CRM_FTP_LOGIN, true);
        String pwd = SchedulerUtils.getConfigValue(CRM_FTP_PASSWORD, true);

        FTPClient client = new FTPClient();
        FileInputStream fis = null;

        try {
            client.connect(address);
            client.login(login, pwd);

            // export all files
            for (File file : filesToExport) {

                // Create an InputStream of the file to be uploaded
                fis = new FileInputStream(file);

                // Store file to server
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Trying to upload file to ftp server...");
                }
                boolean result = client.storeFile(file.getName(), fis);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("File " + file.getName() + "stored to ftp server? " + result);
                }
            }

            SchedulerUtils.updateLastSentTime(lastExportDateByType);

            if (client != null && client.isConnected()) {
                client.logout();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (client != null && client.isConnected()) {
                    client.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Get last export date from config table.
     * @return {@link Date}
     */
    private Date getLastExportedDate() {
        String lastSentTime = SchedulerUtils.getConfigValue(CRM_LAST_EXPORT_DATE, false);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            return formatter.parse(lastSentTime);
        } catch (ParseException pe) {
            LOGGER.error("Parse exception occured while trying to "
                    + "parse config value [CRM_EMAIL_LAST_SENT_DATE]", pe);
        }
        return null;
    }
}
