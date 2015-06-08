/*
 * Copyright Travelsoft, 2014.
 */
package com.travelsoft.lastminute.catalog.review;

import java.io.File;

import org.apache.commons.net.ftp.FTP;
import org.apache.log4j.Logger;

import net.lingala.zip4j.core.ZipFile;





/**
 * Start here.
 *
 */
public class GenerateReviews extends FTP {
    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(GenerateReviews.class);
    /** Place to get XML inputs. */
    private static final String INPUTSOURCE =
                    "C://var//data//static//lastminute//shared//cs//web//lastminute-catalog//reviews";
    /** Place to save created reviews. */
    private static final String OUTPUTSOURCE =
                    "C://var//data//static//lastminute//shared//cs//web//lastminute-catalog//reviews";


    /**
     * Method con gi..
     *
     * @param args sd
     * @throws ReviewServiceException ex
     */
    public static void main(String[] args) throws ReviewServiceException {
        String server = "ftp.meteor.orchestra-platform.com";
        String username = "ftp_lastminute";
        String password = "st4k9y99";

        String srcDir = "avis";
        String src = "avis.zip";
        String output = "C://var//data//static//lastminute//shared//cs//web//lastminute-catalog//avis.zip";
        String destPath = "C://var//data//static//lastminute//shared//cs//web//lastminute-catalog//reviews";

        long startTime = System.currentTimeMillis();

        /** 1. get zip file from FTP server */
        FTPClientModel.getFile(server, username, password, srcDir, src, output);


        /** 2. unzip the file */
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(output);
        } catch (net.lingala.zip4j.exception.ZipException e) {
            LOGGER.error("Failed to unzip the file " + output + " :" + e.getMessage());
        }
        try {
            zipFile.extractAll(destPath);
        } catch (net.lingala.zip4j.exception.ZipException e) {
            LOGGER.error("Failed to extract the zip file " + output + " to the folder " + destPath + " :" + e);
        }

        /** 3. create results */
        ProductReviewComputation reviewController = new ProductReviewComputation(OUTPUTSOURCE);
        File folder = new File(destPath);
        File[] listOfFiles = folder.listFiles();


        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info(listOfFiles[i].getName() + " is being processed!");
                }
                String sourceFile = INPUTSOURCE + "//" + listOfFiles[i].getName();
                reviewController.run(sourceFile);
                listOfFiles[i].deleteOnExit();
            }
        }

        long endTime = System.currentTimeMillis();
        long duree = endTime - startTime;
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Passed time to generate client reviews: " + duree);
        }
    }
}
