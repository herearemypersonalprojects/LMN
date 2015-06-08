package com.travelsoft.lastminute.catalog.review;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.apache.log4j.Logger;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;


/**
 * ReviewUtils.
 * .
 *
 */
public class ReviewUtils {
    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(ReviewUtils.class);
    /**
     * Returns the integer from a string.
     * @param num the input string
     * @return {@link String}
     */
    public static int getInt(String num) {
        try {
            int n = Integer.parseInt(num);
            return n;
        } catch (NumberFormatException  e) {
            return 0;
        }
    }

    /**
     *
     * Method to convert special characters.
     *
     * @param input sd
     * @return String
     */
    public static String encode(String input) {
        String output = input;
        output = output.replaceAll("À", "&Agrave;");
        output = output.replaceAll("Â", "&Acirc;");
        output = output.replaceAll("Ç", "&Ccedil;");
        output = output.replaceAll("È", "&Egrave;");
        output = output.replaceAll("É", "&Eacute;");
        output = output.replaceAll("Ê", "&Ecirc;");
        output = output.replaceAll("Ë", "&Euml;");
        output = output.replaceAll("Î", "&Icirc;");
        output = output.replaceAll("Ï", "&Iuml;");
        output = output.replaceAll("Ô", "&Ocirc;");
        output = output.replaceAll("Œ", "&OElig;");
        output = output.replaceAll("Ù", "&Ugrave;");
        output = output.replaceAll("Û", "&Ucirc;");
        output = output.replaceAll("Ÿ", "&#376;");
        output = output.replaceAll("à", "&agrave;");
        output = output.replaceAll("â", "&acirc;");
        output = output.replaceAll("ç", "&ccedil;");
        output = output.replaceAll("è", "&egrave;");
        output = output.replaceAll("é", "&eacute;");
        output = output.replaceAll("ê", "&ecirc;");
        output = output.replaceAll("ë", "&euml;");
        output = output.replaceAll("î", "&icirc;");
        output = output.replaceAll("ï", "&iuml;");
        output = output.replaceAll("ô", "&ocirc;");
        output = output.replaceAll("œ", "&oelig;");
        output = output.replaceAll("ù", "&ugrave;");
        output = output.replaceAll("û", "&ucirc;");
        output = output.replaceAll("ü", "&uuml;");
        output = output.replaceAll("ÿ", "&yuml;");

        return output;
    }

    /**
     * Returns the float from a string.
     * @param num the input string
     * @return {@link String}
     */
    public static float getFloat(String num) {
        if (num == null) {
            return 0;
        }

        try {
            int endId = num.indexOf('.') + 2;
            if (endId > num.length()) {
                endId = num.length();
            }
            float n = Float.parseFloat(num.substring(0, endId));
            return n;
        } catch (NumberFormatException  e) {
            return 0;
        }
    }

    /**
     *
     * Method to convert a string to date value.
     *
     * @param strDate the string of date format.
     * @return Date.
     */
    public static Date getReviewDate(String strDate) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-d", Locale.FRENCH).parse(strDate);
        } catch (ParseException e) {
            return null;
        }
        return date;
    }

    /**
     *
     * Method to remove all space characters from a string.
     *
     * @param input the input.
     * @return String.
     */
    public static String removeSpace(String input) {
        String inputString = input;
        inputString = inputString.replaceAll("\t", "");
        inputString = inputString.replaceAll("\n", "");
        inputString = inputString.replaceAll(" ", "");
        inputString = inputString.replaceAll("\\s", "");
        return inputString;
    }

    /**
    *
    * Method ..
    *
    * @param xmlFileName sd
    * @param xslFileName sd
    * @param htmlFileName sd
    * @param objectName sd
    * @param object sd
    */
    public static void xmlToHtml(String xmlFileName,
                                   String xslFileName,
                                   String htmlFileName,
                                   String objectName, Object object) {
        try {
            Source xmlFile =  new javax.xml.transform.stream.StreamSource(xmlFileName);
            Source xslFile = new javax.xml.transform.stream.StreamSource(xslFileName);
            Result htmlFile = new javax.xml.transform.stream.StreamResult(new FileOutputStream(htmlFileName));

            TransformerFactory tFactory = TransformerFactory.newInstance();

            Transformer transformer = tFactory.newTransformer(xslFile);
            transformer.setParameter(objectName, object);
            transformer.transform(xmlFile, htmlFile);
        } catch (Exception e) {
            LOGGER.error("Failed to transform the file: " + xmlFileName + " using the XSL file: " + xslFileName);
        }
    }

    /**
     *
     * Method to create an html file from a freemarker template.
     *
     * @param ftlFolder the repertory
     * @param ftlFile the freemarker file
     * @param htmlFile the html output file
     * @param data the java data class
     */
    public static void ftlToHtml(String ftlFolder,
                                    String ftlFile,
                                    String htmlFile,
                                    Object data) {
        /** Freemarker configuration object */
        Configuration cfg = new Configuration();
        try {
            cfg.setDirectoryForTemplateLoading(new File(ftlFolder));
            /** Load template from source folder */
            Template template = cfg.getTemplate(ftlFile);

            /** Html file output */
            Writer file = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(htmlFile), "UTF-8"));
            template.process(data, file);
            file.flush();
            file.close();
        } catch (IOException e) {
            LOGGER.error("Failed to find the folder " + e);
        } catch (TemplateException e) {
            LOGGER.error("Failed to process the FTL file " + ftlFile + " " + e);
        }
    }
}
