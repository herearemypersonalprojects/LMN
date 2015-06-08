/*
 * Copyright Travelsoft, 2000-2011.
 */
package com.travelsoft.lastminute.catalog.bo.scheduler;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.ejb.EJBException;
import javax.naming.NamingException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

import com.travelsoft.cameleo.catalog.data.BookingFileElement;
import com.travelsoft.cameleo.catalog.data.ClientFile;
import com.travelsoft.cameleo.catalog.data.ClientFileElement;
import com.travelsoft.cameleo.catalog.data.ClientFileSearchCriteria;
import com.travelsoft.cameleo.catalog.data.ClientFileSearchResponse;
import com.travelsoft.cameleo.catalog.data.Result;
import com.travelsoft.cameleo.catalog.data.Traveller;
import com.travelsoft.cameleo.catalog.data.types.ClientFileTypeDef;
import com.travelsoft.cameleo.catalog.data.types.ProductStructureDef;
import com.travelsoft.cameleo.catalog.interfaces.Constants.SortConstants;
import com.travelsoft.cameleo.catalog.interfaces.ServicesFactory;
import com.travelsoft.cameleo.catalog.interfaces.clientfile.ClientFileMainServicesInterface;
import com.travelsoft.cameleo.catalog.interfaces.exceptions.TechnicalException;
import com.travelsoft.cameleo.catalog.interfaces.search.BackSearchServicesInterface;
import com.travelsoft.lastminute.catalog.bo.util.SchedulerUtils;
import com.travelsoft.multitos.enterprise.search.sql.Constants;
import com.travelsoft.multitos.enterprise.search.sql.SQLQuery;
import com.travelsoft.nucleus.email.JndiSessionMail;
import com.travelsoft.nucleus.email.attachment.FileAttachment;
import com.travelsoft.nucleus.email.content.AlternativeContent;
import com.travelsoft.nucleus.email.exception.MailException;
import com.travelsoft.nucleus.productfaring.enterprise.searching.SQLConstants;


/**
 * The goal of this job is to generate email with .txt file attached containing
 * subscriptions info's for the Mondial Insurance (done for last minute).
 */
public class SendMondialSubscriptionsJob implements StatefulJob {


    /** The logger . */
    private static final Logger LOGGER = Logger.getLogger(SendMondialSubscriptionsJob.class);

    /** Available countries for the mondial cancel insurance. */
    private static final String MONDIAL_CANCEL_COUNTRY_CODES = "MONDIAL_CANCEL_COUNTRY_CODES";

    /** Product code for the mondial cancel insurance. */
    private static final String MONDIAL_CANCEL_PROD_CODE = "MONDIAL_CANCEL_PRODUCT_CODE";

    /** Product code for the mondial cancel insurance for the other countries. */
    private static final String MONDIAL_CANCEL_PROD_CODE_OTHER = "MONDIAL_CANCEL_PRODUCT_CODE_OTHER";

    /** Mondial cancel insurance cost price. */
    private static final String MONDIAL_CANCEL_COST_PRICE = "MONDIAL_CANCEL_COST_PRICE";

    /** Mondial cancel insurance cost price for other countries. */
    private static final String MONDIAL_CANCEL_COST_PRICE_OTHER = "MONDIAL_CANCEL_COST_PRICE_OTHER";

    /** Available countries for the mondial multirisk insurance. */
    private static final String MONDIAL_MULTIRISK_COUNTRY_CODE = "MONDIAL_MULTIRISK_COUNTRY_CODES";

    /** Product code for the mondial multirisk insurance. */
    private static final String MONDIAL_MULTIRISK_PROD_CODE = "MONDIAL_MULTIRISK_PRODUCT_CODE";

    /** Product code for the mondial multirisk insurance for the other countries. */
    private static final String MONDIAL_MULTIRISK_PROD_CODE_OTHER = "MONDIAL_MULTIRISK_PRODUCT_CODE_OTHER";

    /** Mondial multirisk insurance cost price. */
    private static final String MONDIAL_MULTIRISK_COST_PRICE = "MONDIAL_MULTIRISK_COST_PRICE";

    /** Mondial multirisk insurance cost price for the other countries. */
    private static final String MONDIAL_MULTIRISK_COST_PRICE_OTHER = "MONDIAL_MULTIRISK_COST_PRICE_OTHER";

    /** Mondial email last sent date. */
    private static final String MONDIAL_EMAIL_LAST_SENT_DATE = "MONDIAL_EMAIL_LAST_SENT_DATE";

    /** Mondial clientFile instance. */
    private static final String MONDIAL_LAST_SENT_CF_INSTANCE = "MONDIAL_LAST_SENT_CF_INSTANCE";

    /** Mondial attachment instance. */
    private static final String MONDIAL_LAST_SENT_ATTACHMENT_INSTANCE = "MONDIAL_LAST_SENT_ATTACHMENT_INSTANCE";

    /** Separator used for subscribers stats. */
    private static final String SEPARATOR = "|";

    /** Mondial cancel codes. */
    private List<String> mondialCancelCodes;

    /** Mondial multirisk codes. */
    private List<String> mondialMultiriskCodes;

    /** Formatter used for writing stats in a mondial file. */
    private SimpleDateFormat mondialFormatter = new SimpleDateFormat("yyyyMMdd");




    /**
     * Job execution method.
     *
     * @param context : the Job context
     * @throws JobExecutionException : job exception
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    public void execute(JobExecutionContext context) throws JobExecutionException {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SendMondialSubscriptionsJob started.");
        }
        long beginChannelTreatmentMethod = System.currentTimeMillis();

        Date lastSentEmailDate = getLastSentEmailDate();
        ClientFile[] clientFiles = getClientFileResultIds(fillClientFileSearchCriteria(lastSentEmailDate));

        Integer lastSentCfInstanceNmb
            = Integer.parseInt(SchedulerUtils.getConfigValue(MONDIAL_LAST_SENT_CF_INSTANCE, false));

        Integer lastSentAttachmentInstance
            = Integer.parseInt(SchedulerUtils.getConfigValue(MONDIAL_LAST_SENT_ATTACHMENT_INSTANCE, false));
        lastSentAttachmentInstance = lastSentAttachmentInstance + 1;

        StringBuffer resultSb = new StringBuffer();
        boolean emptyStringBuffer = Boolean.TRUE;

        for (int i = 0; i < clientFiles.length; i++) {

            ClientFile clientFileResult = clientFiles[i];
            ClientFileMainServicesInterface cfServices = ServicesFactory.getClientFileMainServices();
            ClientFile cf = cfServices.getClientFile(clientFileResult.getId());

            // check if cf was created after 16h : this cannot be checked by sql
            if (!SchedulerUtils.cfCreatedAfterSpecificTime(cf, lastSentEmailDate)) {
                continue;
            }

            mondialCancelCodes = getMondialCodes("MONDIAL_CANCEL_CODES");
            mondialMultiriskCodes = getMondialCodes("MONDIAL_MULTIRISK_CODES");

            // check if cf has Mondial insurance
            ClientFileElement cfeTd = cf.getClientFileElementTd();
            BookingFileElement mondialInsBfe = null;
            try {
                mondialInsBfe = getMondialBfe(Integer.toString(cfeTd.getId()));
            } catch (TechnicalException te) {
                LOGGER.error("TechnicalException in getMondialBfe for cfeTdId: "
                                + cfeTd.getId() + ":" + te.getMessage(), te);
            }
            if (mondialInsBfe == null) {
                // cf does not have Mondial insurance
                continue;
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Generating Mondial stats for cfId:" + cf.getId());
            }

            // separate this cf from the previous one
            if (i > 0 && !emptyStringBuffer) {
                resultSb.append(System.lineSeparator());
            }

            lastSentCfInstanceNmb = lastSentCfInstanceNmb + 1;

            writeCfToSb(cf, lastSentCfInstanceNmb, mondialInsBfe, resultSb);
            if (emptyStringBuffer) {
                // atleast one cf was written into the string buffer
                emptyStringBuffer = Boolean.FALSE;
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Mondial subscribers file to send: " + resultSb.toString());
        }

        //Set stringBuffer to a txt file
        File mondialFile = fillFile(resultSb, lastSentAttachmentInstance);

        // Send email with attachment
        Properties properties
            = SchedulerUtils.loadPropertiesFile("mails/clientfile/mondialSubscriptionsMail.properties");

        try {
            AlternativeContent content = new AlternativeContent("");
            JndiSessionMail mailToSend = new JndiSessionMail("java:/MailSession", content);

            SchedulerUtils.fillMailWithAttributes(mailToSend, properties);

            FileAttachment attachment = new FileAttachment(mondialFile);
            if (attachment != null) {
                mailToSend.addAttachment(attachment);
            }

            mailToSend.send();

            // Update current cf sequence number in config table
            if (!"".equals(resultSb.toString())) {
                try {
                    SchedulerUtils.updateConfigTable(MONDIAL_LAST_SENT_CF_INSTANCE,
                        Integer.toString(lastSentCfInstanceNmb));
                } catch (TechnicalException te) {
                    LOGGER.error("TechnicalException in updateConfigTable : " + te.getMessage(), te);
                }
            }

            SchedulerUtils.updateLastSentTime(MONDIAL_EMAIL_LAST_SENT_DATE);
            updateAttachmentInstance(lastSentAttachmentInstance.toString());

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Mondial email sent.");
            }
        } catch (NamingException ne) {
            LOGGER.error("Naming Exception occured." + ne);
        } catch (MailException ne) {
            LOGGER.error("Naming Exception occured." + ne);
        }

        long durationTreatment = System.currentTimeMillis() - beginChannelTreatmentMethod;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SendMondialSubscriptionsJob ended in (" + durationTreatment + "ms)");
        }
    }

    /**
     * Update attachmentInstance.
     * @param value the new value.
     */
    private void updateAttachmentInstance(String value) {
        try {
            SchedulerUtils.updateConfigTable(MONDIAL_LAST_SENT_ATTACHMENT_INSTANCE, value);
        } catch (TechnicalException te) {
            LOGGER.error("TechnicalException in updateConfigTable : " + te.getMessage(), te);
        }
    }

    /**
     * Create and fill the File from StringBuffer.
     *
     * @param sb the StringBuffer
     * @param attachmentInstance the attachment instance
     * @return {@link File}
     */
    private File fillFile(StringBuffer sb, int attachmentInstance) {
        String fileName = "LASTMINUTE_ORC_" + intToString(attachmentInstance, 9) + ".txt";
        File file = new File(fileName);
        try {
            FileUtils.writeStringToFile(file, sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * This method search for clientFiles with specific criteria. In the result we will have departure date.
     *
     * @param criteria : the criteria
     * @return ClientFile[] the client files
     */
    private ClientFile[] getClientFileResultIds(ClientFileSearchCriteria criteria) {

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
     * This method prepares criteria for search. .
     *
     * @param lastSentEmailDate the last sent email date
     * @return ClientFileSearchCriteria
     */
    private ClientFileSearchCriteria fillClientFileSearchCriteria(Date lastSentEmailDate) {

        ClientFileSearchCriteria criteria = new ClientFileSearchCriteria();
        criteria.setClientFileType(ClientFileTypeDef.DISTRIBUTOR);
        criteria.setSortBy(SortConstants.SORT_DEPARTURE_DATE_ASC);
        criteria.setMinDepDate(Calendar.getInstance().getTime());

        // send infos only for the CF created after the last time stats were sent
        if (lastSentEmailDate != null) {
            criteria.setBookDateStart(lastSentEmailDate);
        }

        // with insurance (later we will check if it was mondial insurance that was not added after)
        criteria.setBookType("INS");

        return criteria;
    }


    /**
     * Get last sent emial date from config table.
     * @return {@link Date}
     */
    private Date getLastSentEmailDate() {
        String lastSentTime = SchedulerUtils.getConfigValue(MONDIAL_EMAIL_LAST_SENT_DATE, false);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            return formatter.parse(lastSentTime);
        } catch (ParseException pe) {
            LOGGER.error("Parse exception occured while trying to "
                    + "parse config value [MONDIAL_EMAIL_LAST_SENT_DATE]", pe);
        }
        return null;
    }



    /**
     * Retrieve mondial subsribers attached file instance number.
     * We will update this number after each sent email.
     * Don't cache the value.
     *
     * @param key the config key
     * @return {@link List<String>}
     */
    private List<String> getMondialCodes(String key) {
        List<String> codesList = new ArrayList<String>();
        String value = SchedulerUtils.getConfigValue(key, true);
        String[] codes = StringUtils.split(value, ",");
        codesList = Arrays.asList(codes);
        return codesList;
    }


    /**
     * Write Cf infos to the mondial txt file that will be sent as attachment in the email.
     *
     * @param cf the ClientFile
     * @param sequence the sequence that corresponds to the current cf
     * @param mondialBfe the mondial Bfe
     * @param sb the StringBuffer
     */
    private void writeCfToSb(ClientFile cf, Integer sequence,
        BookingFileElement mondialBfe, StringBuffer sb) {

        // sequence
        String sequenceString = intToString(sequence, 9);
        sb.append(sequenceString);
        sb.append(SEPARATOR);

        // order nmb
        if (cf.getExternOrderCode() != null) {
            if (cf.getChannel() != null
                    && cf.getChannel().getChannel() != null
                    && cf.getChannel().getChannel().contains("DEGRIFTOUR")) {
                sb.append("DGT");
            } else {
                sb.append("LMN");
            }
            sb.append(cf.getExternOrderCode());
            sb.append(SEPARATOR);
        }

        sb.append("C");
        sb.append(SEPARATOR);

        // card holder
        try {
            String cardHolder = getCardHolder(Integer.toString(cf.getId()));
            sb.append(cardHolder);
            sb.append(SEPARATOR);
        } catch (TechnicalException te) {
            LOGGER.error("TechnicalException occured while trying to get cardHoderName for the cfId:" + cf.getId(), te);
        }

        // nmb of passengers with mondial insurance
        List<String> passengers = new ArrayList<String>();
        Traveller[] travellers = cf.getTraveller();
        int nmbOfTravellers = travellers.length;
        for (Traveller traveller : travellers) {
            passengers.add(traveller.getSurname() + "," + traveller.getName());
        }
        sb.append(nmbOfTravellers);
        sb.append(SEPARATOR);

        // find out if current mondial insurance is cancel or multirisk type
        String mondialCode = mondialBfe.getLabel();
        boolean isMultirisk = false;
        if (mondialMultiriskCodes.contains(mondialCode)) {
            isMultirisk = true;
        }

        //destination code
        String mondialCountryConfigKey = MONDIAL_CANCEL_COUNTRY_CODES;
        if (isMultirisk) {
            mondialCountryConfigKey = MONDIAL_MULTIRISK_COUNTRY_CODE;
        }
        List<String> countryCodes = getMondialCodes(mondialCountryConfigKey);
        boolean isOtherCountryCode = true;
        String cfDest = cf.getDestinationCountry();
        if (countryCodes.contains(cfDest)) {
            isOtherCountryCode = false;
        }
        if (isOtherCountryCode) {
            sb.append("OTHER");
        } else {
            sb.append(cfDest);
        }
        sb.append(SEPARATOR);

        // booking date
        Date createDate = cf.getDate();
        String creationDate = mondialFormatter.format(createDate);
        sb.append(creationDate);
        sb.append(SEPARATOR);

        // insurance date = booking date
        sb.append(creationDate);
        sb.append(SEPARATOR);

        String depDate = mondialFormatter.format(cf.getDepartureDate());
        sb.append(depDate);
        sb.append(SEPARATOR);

        String retDate = mondialFormatter.format(cf.getReturnDate());
        sb.append(retDate);
        sb.append(SEPARATOR);

        // policy start date = depDate
        sb.append(depDate);
        sb.append(SEPARATOR);

        // mondial product code
        String productCode = null;
        if (isMultirisk) {
            if (isOtherCountryCode) {
                productCode = SchedulerUtils.getConfigValue(MONDIAL_MULTIRISK_PROD_CODE_OTHER, true);
            } else {
                productCode = SchedulerUtils.getConfigValue(MONDIAL_MULTIRISK_PROD_CODE, true);
            }
        } else {
            if (isOtherCountryCode) {
                productCode = SchedulerUtils.getConfigValue(MONDIAL_CANCEL_PROD_CODE_OTHER, true);
            } else {
                productCode = SchedulerUtils.getConfigValue(MONDIAL_CANCEL_PROD_CODE, true);
            }
        }
        sb.append(productCode);
        sb.append(SEPARATOR);

        // cf value
        sb.append(cf.getPriceTotal());
        sb.append(SEPARATOR);

        // Insurance value
        String costPrice = null;
        if (isMultirisk) {
            if (isOtherCountryCode) {
                costPrice = SchedulerUtils.getConfigValue(MONDIAL_MULTIRISK_COST_PRICE_OTHER, true);
            } else {
                costPrice = SchedulerUtils.getConfigValue(MONDIAL_MULTIRISK_COST_PRICE, true);
            }
        } else {
            if (isOtherCountryCode) {
                costPrice = SchedulerUtils.getConfigValue(MONDIAL_CANCEL_COST_PRICE_OTHER, true);
            } else {
                costPrice = SchedulerUtils.getConfigValue(MONDIAL_CANCEL_COST_PRICE, true);
            }
        }
        BigDecimal costPriceDecimal = new BigDecimal(costPrice);
        BigDecimal nmbOfTravellersDecimal = new BigDecimal(nmbOfTravellers);
        BigDecimal total = costPriceDecimal.multiply(nmbOfTravellersDecimal);
        sb.append(total);
        sb.append(SEPARATOR);

        sb.append(StringUtils.join(passengers, ";"));
    }

    /**
     * Create String with leading zeros.
     * Example: intoToString(1, 3) will return 0001
     *
     * @param num the value infront of which zeros will be added.
     * @param digits the number of zeros.
     * @return {@link String}
     */
    static String intToString(int num, int digits) {

        // create variable length array of zeros
        char[] zeros = new char[digits];
        Arrays.fill(zeros, '0');
        // format number as String
        DecimalFormat df = new DecimalFormat(String.valueOf(zeros));
        return df.format(num);
    }


    /*
     *
     * SQL MANIPULATION METHODS START
     *
     */

    /**
     * Get name of the payment cart holder.
     *
     * @param cfId The clientFileId.
     * @throws TechnicalException If a technical error occurs
     * @return The Sources object containing single agencyConfig.
     */
    private String getCardHolder(String cfId) throws TechnicalException {

        SQLQuery query = new SQLQuery();
        query.addSelect(Constants.TABLE_PAYMENT, "CARD_MEMBER_NAME");

        query.addWhere(Constants.TABLE_PAYMENT, Constants.PAYMENT_CLIENT_FILE_ID, SQLConstants.SQL_EQUALS, cfId);

        String queryStr = query.getQuery();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("executed query:" + queryStr);
        }

        StringBuffer sb = new StringBuffer();

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            // execute SQL query.
            con = SchedulerUtils.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(queryStr);
            while (rs.next()) {
                sb.append(rs.getString("CARD_MEMBER_NAME"));
            }
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
            SchedulerUtils.closeConnection(con);
        }
        return sb.toString();
    }

    /**
     * Get Mondial booking file element (the one that was adedd before, has type INS and is Mondial).
     *
     * @param cfeId The client file element id.
     * @throws TechnicalException If a technical error occurs
     * @return The Sources object containing single agencyConfig.
     */
    private BookingFileElement getMondialBfe(String cfeId) throws TechnicalException {

        List<String> allMondialCodes = new ArrayList<String>();
        allMondialCodes.addAll(mondialCancelCodes);
        allMondialCodes.addAll(mondialMultiriskCodes);

        SQLQuery query = new SQLQuery();
        query.addSelect(Constants.TABLE_BOOKING_FILE_ELEMENT, "ID");
        query.addSelect(Constants.TABLE_BOOKING_FILE_ELEMENT, Constants.BOOKING_FILE_ELEMENT_TYPE);
        query.addSelect(Constants.TABLE_BOOKING_FILE_ELEMENT, "ADDED_AFTER");
        query.addSelect(Constants.TABLE_BOOKING_FILE_ELEMENT, "EXTERNAL_REF");
        query.addSelect(Constants.TABLE_BOOKING_FILE_ELEMENT, "TOTAL_AMOUNT");

        query.addWhere(Constants.TABLE_BOOKING_FILE_ELEMENT, Constants.BOOKING_FILE_ELEMENT_CLIENT_FILE_ELEMENT_ID,
            SQLConstants.SQL_EQUALS, cfeId);
        query.addWhere(Constants.TABLE_BOOKING_FILE_ELEMENT, Constants.BOOKING_FILE_ELEMENT_TYPE,
            SQLConstants.SQL_EQUALS, "INS");
        query.addWhereIn(Constants.TABLE_BOOKING_FILE_ELEMENT, "EXTERNAL_REF", allMondialCodes);

        String queryStr = query.getQuery();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("executed query:" + queryStr);
        }

        BookingFileElement mondialBfe = null;

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            // execute SQL query.
            con = SchedulerUtils.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(queryStr);
            while (rs.next()) {
                // one mondial insurance par cf
                mondialBfe = new BookingFileElement();

                // in Label we will set external_Ref
                mondialBfe.setLabel(rs.getString("EXTERNAL_REF"));
                String bfeId = rs.getString("ID");
                mondialBfe.setId(Integer.valueOf(bfeId));
                mondialBfe.setAmount(rs.getString("TOTAL_AMOUNT"));
            }
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
            SchedulerUtils.closeConnection(con);
        }
        return mondialBfe;
    }

}
