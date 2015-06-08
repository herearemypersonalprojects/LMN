/*
 * Copyright Travelsoft, 2000-2013.
 */
package com.travelsoft.lastminute.catalog.bo.scheduler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.travelsoft.cameleo.catalog.data.ClientFile;
import com.travelsoft.cameleo.catalog.interfaces.ServicesFactory;
import com.travelsoft.cameleo.catalog.interfaces.clientfile.ClientFileMainServicesInterface;
import com.travelsoft.lastminute.catalog.bo.util.CRMFileFillerUtils;
import com.travelsoft.lastminute.catalog.bo.util.SchedulerUtils;

/**
 * <p>Titre : ProductDataRetriever.</p>
 * <p>Description : .</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: Travelsoft</p>
 * @author zouhair.mechbal
 */
public class ExportCGOSOrders extends ExportCRMDataFeedJob {

    /** The logger . */
    private static final Logger LOGGER = Logger.getLogger(ExportCGOSOrders.class);

    /** The CGOS last export date. */
    private static final String CGOS_LAST_EXPORT_DATE = "CGOS_LAST_EXPORT_DATE";

    /** Crm ftp address. */
    private static final String CGOS_FTP_ADDRESS = "CGOS_FTP_ADDRESS";

    /** Crm ftp login. */
    private static final String CGOS_FTP_LOGIN = "CGOS_FTP_LOGIN";

    /** Crm ftp password. */
    private static final String CGOS_FTP_PASSWORD = "CGOS_FTP_PASSWORD";

    /**
     * Job execution method.
     *
     * @param context : the Job context
     * @throws JobExecutionException : job exception
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
	public void execute(JobExecutionContext context) throws JobExecutionException {

		long beginChannelTreatmentMethod = System.currentTimeMillis();

		Date lastExportedDate = getLastExportedDate();

		ClientFile[] clientFiles = getClientFileResultIds(fillClientFileSearchCriteria(lastExportedDate));

		StringBuffer orderDataFeedSb = new StringBuffer();

		 // build headers
        CRMFileFillerUtils.buildCGOSOrderDataFeedHeaders(orderDataFeedSb);

		for (int i = 0; i < clientFiles.length; i++) {
			try {
				ClientFile clientFileResult = clientFiles[i];
				ClientFileMainServicesInterface cfServices = ServicesFactory.getClientFileMainServices();
				ClientFile cf = cfServices.getClientFile(clientFileResult.getId());

				// check if cf was created after the last export time : this
				// cannot be checked by sql
				// for example if last export was yesterday at 16h, client files
				// created before 16h should not
				// be used for crm export.
				if (!SchedulerUtils.cfCreatedAfterSpecificTime(cf, lastExportedDate)) {
					continue;
				}

				if (cf != null && cf.getChannel() != null && cf.getChannel().getChannel() != null) {
					String channel = cf.getChannel().getChannel();
					if (channel != null && channel.indexOf("CGOS") != -1) {
						orderDataFeedSb.append("\n");
						orderDataFeedSb.append(this.buildRestultOuput(cf));
					}
				}
			} catch (Exception e) {
				LOGGER.error("Error while trying to retrieve order information. " + e);
			}
		}

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(">>>>> orderDataFeedSb : " + orderDataFeedSb.toString());
		}

		  File fileToExport = createFile(orderDataFeedSb, "cgosOrder");
	      exportFlux(fileToExport, CGOS_LAST_EXPORT_DATE);

	      long durationTreatment = System.currentTimeMillis() - beginChannelTreatmentMethod;
	      if (LOGGER.isInfoEnabled()) {
	    	  LOGGER.info("ExportCRMDataFeedJob ended in (" + durationTreatment + "ms)");
	      }
	}

	  /**
     * Export flux files to ftp server.
     *
     * @param fileToExport the list of files to export
     */
    protected void exportFlux(File fileToExport, String lastExportDateByType) {

        String address = SchedulerUtils.getConfigValue(CGOS_FTP_ADDRESS, true);
        String login = SchedulerUtils.getConfigValue(CGOS_FTP_LOGIN, true);
        String pwd = SchedulerUtils.getConfigValue(CGOS_FTP_PASSWORD, true);

        FTPClient client = new FTPClient();

        FileInputStream fis = null;

        try {
            client.connect(address);
            client.login(login, pwd);

            // Create an InputStream of the file to be uploaded
            fis = new FileInputStream(fileToExport);

            // Store file to server
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Trying to upload file to ftp server...");
            }

            boolean result = client.storeFile(fileToExport.getName(), fis);

            if (LOGGER.isInfoEnabled()) {
            	LOGGER.info("File " + fileToExport.getName() + "stored to ftp server? " + result);
            }

            SchedulerUtils.updateLastSentTime(lastExportDateByType);

            if (client != null && client.isConnected()) {
                client.logout();
            }
        } catch (IOException e) {
            LOGGER.error(e.toString(), e);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (client != null && client.isConnected()) {
                    client.disconnect();
                }
            } catch (IOException e) {
            	 LOGGER.error(e.toString(), e);
            }
        }
    }

	/**
	 * Build the result ouput.
	 * @param cf The client file object
	 * @return the result
	 */
    private String buildRestultOuput(ClientFile cf) {
    	String result = null;
    	String externOrderCode = cf.getExternOrderCode();
    	String customerAffiliationNumber = cf.getAffiliation();
    	if (customerAffiliationNumber == null) {
			result = ";".concat(externOrderCode).concat(";");
    	} else {
    		if (customerAffiliationNumber.indexOf(";") == -1) {
    			result = customerAffiliationNumber.concat(";").concat(externOrderCode).concat(";");
    		} else {
    			String firstAffilationNumber = customerAffiliationNumber.split(";")[0];
    			int index = customerAffiliationNumber.indexOf(";");
    			String lastCustomerAffiliationNumber = customerAffiliationNumber.substring(index + 1);
				result = firstAffilationNumber
						.concat(";")
						.concat(externOrderCode)
						.concat(";")
						.concat(lastCustomerAffiliationNumber.replaceAll(";", "-"));
    		}
    	}
		return result;
	}

	/**
     * Get last export date from config table.
     * @return {@link Date}
     */
    private Date getLastExportedDate() {
        String lastSentTime = SchedulerUtils.getConfigValue(CGOS_LAST_EXPORT_DATE, false);
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
