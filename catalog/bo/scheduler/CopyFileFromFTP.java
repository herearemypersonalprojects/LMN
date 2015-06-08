/**
 * 
 */
package com.travelsoft.lastminute.catalog.bo.scheduler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

import com.travelsoft.cameleo.catalog.interfaces.ServicesFactory;
import com.travelsoft.cameleo.catalog.interfaces.exceptions.TechnicalException;
import com.travelsoft.cameleo.catalog.interfaces.setup.SetupServicesInterface;

/**
 * Scheduler task intented with copying a file 
 * from an FTP server to a local destination. 
 * @author jerome.ruillier@travelsoft.fr
 */
public class CopyFileFromFTP implements StatefulJob  {
	
	/** Logger for this class. */
	private static final Logger LOG = Logger.getLogger(CopyFileFromFTP.class);

	/** Config Key. */
	static final String COPYFILEFROMFTP_FTPHOSTNAME = 
			"COPYFILEFROMFTP_FTPHOSTNAME";

	/** Config Key. */
	static final String COPYFILEFROMFTP_FTPPORT = 
			"COPYFILEFROMFTP_FTPPORT";

	/** Config Key. */
	static final String COPYFILEFROMFTP_FTPUSERNAME = 
			"COPYFILEFROMFTP_FTPUSERNAME";

	/** Config Key. */
	static final String COPYFILEFROMFTP_FTPPASSWORD = 
			"COPYFILEFROMFTP_FTPPASSWORD";

	/** Config Key. */
	static final String COPYFILEFROMFTP_FTPSOURCEFILE = 
			"COPYFILEFROMFTP_FTPSOURCEFILE";
	
	/** Config Key. */
	static final String COPYFILEFROMFTP_LOCALTARGETPATH = 
			"COPYFILEFROMFTP_LOCALTARGETPATH";

	/** Used to access setup parameters. */
	SetupServicesInterface setupServices;
	
	/** Used to establish FTP connection. */
	FTPClient ftpClient;

	/* (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		LOG.info("Starting scheduled job: " + this.getClass().getSimpleName());
		
		this.setupServices = ServicesFactory.getSetupServices();
		this.ftpClient = new FTPClient();
		
		doExecute();
	}    
	
	/**
	 * Extracts config values and retrieve FTP file.
	 * @throws JobExecutionException on error
	 */	
	void doExecute() throws JobExecutionException {

		final String ftpHostName = 
				this.getConfigValue(COPYFILEFROMFTP_FTPHOSTNAME);
		
		final String ftpPortStr = 
				this.getConfigValue(COPYFILEFROMFTP_FTPPORT);
		final Integer ftpPort = 
				ftpPortStr == null ? null : new Integer(ftpPortStr);
		
		final String ftpUserName =
				this.getConfigValue(COPYFILEFROMFTP_FTPUSERNAME);
		final String ftpPassword = 
				this.getConfigValue(COPYFILEFROMFTP_FTPPASSWORD);
		
		final String ftpSourceFile = 
				this.getConfigValue(COPYFILEFROMFTP_FTPSOURCEFILE);
		final String locatTargetFile = 
				this.getConfigValue(COPYFILEFROMFTP_LOCALTARGETPATH);
		
		connectAndLogin(ftpHostName, ftpPort, ftpUserName, ftpPassword);
		ftpClient.enterLocalPassiveMode();
		downloadFile(ftpSourceFile, locatTargetFile);
		disconnect();
		
	}

	/**
	 * Gets a config value for given key.
	 * @param configKey to look for
	 * @return matching config value
	 * @throws JobExecutionException on error
	 */
	private String getConfigValue(final String configKey) 
			throws JobExecutionException {
		try {
			final String configValue = 
					setupServices.getConfigValue(configKey, null);
			
			if (configValue.length() == 0) {
				final String errorMsg = 
						"Missing configuration key " + configKey;
				LOG.error(errorMsg);
				throw new JobExecutionException(
						errorMsg);
			}
			
			return configValue;
		} catch (TechnicalException e) {
			final String errorMsg = 
					"Error while getting value for config key " + configKey;
			LOG.error(errorMsg);
			throw new JobExecutionException(
					errorMsg, 
					e, false);
		}
	}

	/**
     * A convenience method for connecting and logging in.
     * @param host FTP host name
     * @param port FTP host connection port
     * @param userName FTP user name (login)
     * @param password FTP password
     * @return success true if sucess, false otherwise
     * @throws JobExecutionException on error
     */
	private boolean connectAndLogin(
    		final String host, final Integer port, 
    		final String userName, final String password) 
    				throws JobExecutionException {
        boolean success = false;
        try {
			ftpClient.connect(host, port);
	        int reply = ftpClient.getReplyCode();
	        if (FTPReply.isPositiveCompletion(reply)) {
	            success = ftpClient.login(userName, password);
	        }
	        if (!success) {
	        	ftpClient.disconnect();
	        }
		} catch (IOException e) {
			final String errorMsg = 
					"Error while connecting or authenticating on FTP server..." +
					"\n- HOST: " + ftpClient.getRemoteAddress() +
			        "\n- USERNAME: " + userName +
			        "\n- PASSWORD: " + password;
			LOG.error(errorMsg);
			throw new JobExecutionException(
					errorMsg,
					e, false);
		}
        return success;
    }

    /** 
     * Download a file from the server, and save it to the specified local file.
     * @param serverFile serverFile
     * @param localFileStr localFile
     * @return success
     * @throws JobExecutionException on error
     */
	private boolean downloadFile(
    		final String serverFile, final String localFileStr) 
    				throws JobExecutionException {
		try {
			final File localFile = new File(localFileStr);
			localFile.delete(); // remove existing file if any
	        FileOutputStream out = new FileOutputStream(localFile);
	        boolean result = ftpClient.retrieveFile(serverFile, out);
	        out.close();
	        return result;
		} catch (IOException e) {
			final String errorMsg = 
					"Error while downloading or writing file..." +
					"\n- HOST: " + ftpClient.getRemoteAddress() +
					"\n- FTPSOURCEFILE: " + serverFile +
					"\n- LOCALTARGETFILE: " + localFileStr;
			LOG.error(errorMsg);
			throw new JobExecutionException(
					errorMsg,
					e, false);
		}
    }

	/**
	 * Disconnects from FTP server.
	 * @throws JobExecutionException on error
	 */
	private void disconnect() throws JobExecutionException {
		try {
			ftpClient.disconnect();
		} catch (IOException e) {
			final String errorMsg = 
					"Error while disconnecting from FTP server " + 
					ftpClient.getRemoteAddress();
			LOG.error(errorMsg);
			throw new JobExecutionException(
					errorMsg, 
				    e, false);
		}
	}

}
