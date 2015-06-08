/*
 * Copyright Travelsoft, 2011.
 */
package com.travelsoft.lastminute.catalog.bo.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;

import javax.ejb.EJBException;
import javax.mail.internet.AddressException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.travelsoft.cameleo.catalog.data.ClientFile;
import com.travelsoft.cameleo.catalog.interfaces.ServicesFactory;
import com.travelsoft.cameleo.catalog.interfaces.Constants.SetupConstants;
import com.travelsoft.cameleo.catalog.interfaces.exceptions.TechnicalException;
import com.travelsoft.cameleo.catalog.interfaces.setup.SetupServicesInterface;
import com.travelsoft.lastminute.catalog.util.Util;
import com.travelsoft.multitos.enterprise.search.SearchEngineGeneral;
import com.travelsoft.multitos.enterprise.search.sql.SQLQuery;
import com.travelsoft.nucleus.cache.generic.CacheManager;
import com.travelsoft.nucleus.cache.jboss.implementations.JbossCacheManagerFactory;
import com.travelsoft.nucleus.email.JndiSessionMail;
import com.travelsoft.nucleus.productfaring.enterprise.searching.SQLConstants;

/**
 * Util.
 */
public class SchedulerUtils {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(SchedulerUtils.class);


    /** Configuration table. */
    private static final String CONFIGURATION_TABLE = "CONFIGURATION";


    /**
     * Update last email sent time in config table.
     * @param configKey the configKey
     */
    public static void updateLastSentTime(String configKey) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Calendar cal = Calendar.getInstance();
            String lastSent = formatter.format(cal.getTime());
            updateConfigTable(configKey, lastSent);
        } catch (TechnicalException te) {
            LOGGER.error("TechnicalException in updateConfigTable : " + te.getMessage(), te);
        }
    }


    /**
     * Update config table.
     *
     * @param key The config key.
     * @param value The new config value.
     * @throws TechnicalException If a technical error occurs
     */
    public static void updateConfigTable(String key, String value) throws TechnicalException {

        SQLQuery query = new SQLQuery();

        // Construction of the Update Part
        query.setUpdateMap(new Hashtable());
        query.getUpdateMap().put("VALUE", "'" + value + "'");

        query.addWhere(CONFIGURATION_TABLE, "KEY", SQLConstants.SQL_EQUALS, key);

        StringBuilder sb = new StringBuilder();
        sb.append(query.getUpdateQuery(CONFIGURATION_TABLE));
        sb.append(" ");
        sb.append(query.getWherePart());

        String queryStr = sb.toString();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("executed query:" + queryStr);
        }

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(queryStr);
        } catch (NamingException e) {
            throw new EJBException(e);
        } catch (SQLException se) {
            throw new EJBException("Problem occurs while executing " + queryStr, se);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException se) {
                LOGGER.error("error trying to close resultset", se);
            }
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se) {
                LOGGER.error("error trying to close statement", se);
            }
            closeConnection(con);
        }
    }


    /**
     * Get the connection to the database.
     *
     * @return a Connection to the data source.
     * @throws NamingException if a JNDI exception occurs.
     * @throws SQLException if an error occurs while opening the connection
     *         factory.
     */
    public static Connection getConnection() throws NamingException, SQLException {
        Context jndiCntx = new InitialContext();
        DataSource ds = (DataSource) jndiCntx.lookup(SearchEngineGeneral.BD_POSTGRESQL_JNDI);
        return ds.getConnection();
    }


    /**
     * Close a database connection.
     * An error will be logged if the operation fails.
     *
     * @param connection the connection to be closed (may be null).
     */
    public static void closeConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            LOGGER.error("Error trying to close connection.", ex);
        }
    }


    /**
     * Load properties file that will be used for sending email.
     *
     * @param filePath the path to properties file
     * @return {@link Properties}
     */
    public static Properties loadPropertiesFile(String filePath) {
        Properties properties = new Properties();
        String propertiesPath = SchedulerUtils.getOverridePath(filePath);
        File mailPropFile = new File(propertiesPath);
        if (mailPropFile.exists()) {
            try {
                properties.load(new FileInputStream(mailPropFile));
            } catch (IOException ioe) {
                LOGGER.error("IOException while accessing properties file : " + mailPropFile, ioe);
            }
        }
        return properties;
    }


    /**
     * Returns the path to an email file. If the file exists in the client folder (CS/), this one will be preferred.
     * Otherwise, return the default (TS/) file.
     *
     * @param fileName The email file name.
     * @return {@link String}
     */
    public static String getOverridePath(String fileName) {
        String overridePath = null;

        String customMailPath = Util.getConfigValue(SetupConstants.CS_PATH);
        String defaultMailPath = Util.getConfigValue(SetupConstants.TS_PATH);

        if ((new File(customMailPath, fileName)).exists()) {
            overridePath = customMailPath + fileName;
        } else {
            overridePath = defaultMailPath + fileName;
        }
        return overridePath;
    }


    /**
     * Fill mail with properties attributes.
     *
     * @param mailToSend the mail to fill
     * @param properties the properties containing the attributes
     */
    public static void fillMailWithAttributes(JndiSessionMail mailToSend, Properties properties) {
        try {
            String from = properties.getProperty("from");
            if (from != null && !"".equals(from)) {
                mailToSend.putFrom(from);
            }
            String to = properties.getProperty("to");
            mailToSend.addTo(to);

            String subject = properties.getProperty("subject");
            mailToSend.setSubject(subject);

            String cc = properties.getProperty("cc");
            if (cc != null && !"".equals(cc)) {
                mailToSend.addCc(cc);
            }

            String bcc = properties.getProperty("bcc");
            if (bcc != null && !"".equals(bcc)) {
                mailToSend.addCc(bcc);
            }
        } catch (AddressException ae) {
            LOGGER.error("Address Exception occured." + ae);
        }
    }


    /**
     * Retrieve config value.
     *
     * @param key the config key
     * @param withCache : should we use cache or not
     * @return {@link List<String>}
     */
    public static String getConfigValue(String key, boolean withCache) {
        SetupServicesInterface setupServices = ServicesFactory.getSetupServices();
        CacheManager cacheMgr = null;
        if (withCache) {
            cacheMgr = JbossCacheManagerFactory.getCacheManager("CameleoTreeCache");
        }
        try {
            return setupServices.getConfigValue(key, cacheMgr);
        } catch (TechnicalException te) {
            LOGGER.error("TechnicalException while finding the config value for key=[" + key + "]", te);
        }
        return null;
    }

    /**
     * Check if cf creation time was after or before lastSentEmailTime.
     * @param cf the clientFile
     * @param specificTime the last sent or last exported time
     * @return {@link Boolean}
     */
    public static boolean cfCreatedAfterSpecificTime(ClientFile cf, Date specificTime) {
        boolean result = false;

        Date creationDate = cf.getDate();
        Calendar creationCal = Calendar.getInstance();
        creationCal.setTime(creationDate);

        Calendar specificCal = Calendar.getInstance();
        specificCal.setTime(specificTime);

        if (creationCal.after(specificCal)) {
            result = true;
        }

        return result;
    }
}
