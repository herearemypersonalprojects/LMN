package com.travelsoft.lastminute.catalog.review;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import net.lingala.zip4j.core.ZipFile;


/**
 *
 * Util to download file from a Ftp server.
 *
 */
public class FTPClientModel {
    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(FTPClientModel.class);


    /**
     *
     * Method to init a folder containing customized data.
     *
     * @param folder the directory's name
     */
    public static void resetDir(String folder) {
        File dir = new File(folder);
        if (!dir.exists()) {
            dir.mkdir();
        } else {
            for (int i = 0; i < dir.listFiles().length; i++) {
                dir.listFiles()[i].delete();
            }
        }
    }

    /**
    *
    * Method to create a folder containing customized data.
    *
    * @param folder the directory's name
    */
    public static void createDir(String folder) {
        File dir = new File(folder);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }
    /**
    *
    * Method to save file to disk.
    *
    * @param input given source.
    * @param output destination.
    * @param bufferSize amount
    */
    private static void copy(InputStream input, OutputStream output, int bufferSize) {
        try {
            byte[] buf = new byte[bufferSize];
            int n = input.read(buf);
            while (n >= 0) {
                output.write(buf, 0, n);
                n = input.read(buf);
            }
            output.flush();
        } catch (IOException e) {
            LOGGER.error("Error to write file: " + e.getMessage());
        }
    }
    /**
    *
    * Method to pass authentificated server.
    *
    */
    static class MyAuthenticator extends Authenticator {
       /** login. */
        private String username;
       /** pwd. */
        private String password;

       /**
        *
        * Constructor.
        *
        * @param user given login
        * @param pass given password
        */
        public MyAuthenticator(String user, String pass) {
            username = user;
            password = pass;
        }

       /**
        *
        * Overriding method.
        *
        * @see java.net.Authenticator#getPasswordAuthentication()
        * @return password
        */
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password.toCharArray());
        }
    }
    /**
     *
     * Method to get and unzip file from the server.
     *
     * @param server the server's url
     * @param username the user
     * @param password the pwd
     * @param srcDir the folder on the server that contains the filename
     * @param fileName the file to be copied
     * @param zipFileFullPath the full path of the destination
     * @param folderToSave the folder to unzip XML file
     */
    public static void getAndUnzipFile(String server,
        String username, String password, String srcDir,
        String fileName, String zipFileFullPath, String folderToSave) {

        resetDir(folderToSave);

        Authenticator.setDefault(new MyAuthenticator(username, password));
        try {
            URL url = new URL(server + fileName + ".zip");
            InputStream in = (InputStream) url.getContent();
            FileOutputStream out = new FileOutputStream(zipFileFullPath);
            copy(in, out, 1024);
            out.close();

        } catch (MalformedURLException e) {
            LOGGER.error("Can not find the url " + e.getMessage());
        } catch (IOException e) {
            LOGGER.error("Can not establish a connection to the given url: " + e.getMessage());
        }

        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(zipFileFullPath);
            zipFile.extractAll(folderToSave);
        } catch (net.lingala.zip4j.exception.ZipException e) {
            LOGGER.error("Failed to unzip the file " + zipFileFullPath + " :" + e.getMessage());
        }

        File file = new File(zipFileFullPath);
        if (file.exists()) {
            file.delete();
        }
    }
    /**
     *
     * Constructor.
     *
     * @param server the server's url
     * @param username the login
     * @param password the password
     * @param srcDir the folder in the server that contain the file to be retrieved
     * @param src the filename to be retrieved
     * @param dest the full path of saved file in local server
     */
    public static void getFile(String server,
        String username, String password, String srcDir, String src, String dest) {
        try {
            FTPClient ftp = new FTPClient();
            ftp.connect(server);
            ftp.login(username, password);

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Connected to " + server + ".");
                LOGGER.info(ftp.getReplyString());
            }

            try {
                int reply = ftp.getReplyCode();
                if (!FTPReply.isPositiveCompletion(reply)) {
                    throw new Exception("Connect failed: " + ftp.getReplyString());
                }
                try {
                    ftp.enterLocalPassiveMode();
                    if (!ftp.setFileType(FTP.BINARY_FILE_TYPE)) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Setting binary file type failed.");
                        }
                    }
                    transferFile(ftp, srcDir, src, dest);
                } catch (Exception e) {
                    LOGGER.error("Failed to transfer file " + src + " from the server " + server + " :" + e);
                } finally {
                    if (!ftp.logout()) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Logout failed.");
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Failed to transfer file " + src + " from the server " + server + " :" + e);
            } finally {
                ftp.disconnect();
            }
        } catch (Exception e) {
            LOGGER.error("Failed to transfer file " + src + " from the server " + server + " :" + e);
        }
    }
    /**
     *
     * Method to transfer FTP file.
     *
     * @param ftp sd
     * @param srcDir sd
     * @param src sd
     * @param dest sd
     * @throws Exception sd
     */
    private static void transferFile(FTPClient ftp, String srcDir, String src, String dest) throws Exception {
        long fileSize = 0;
        fileSize = getFileSize(ftp, srcDir, src);
        System.out.println("fileSize: " + fileSize);
        if (!(fileSize == 0)) {
            InputStream is = retrieveFileStream(ftp, src);
            downloadFile(is, fileSize, dest);
            is.close();
        } else
            // no such files
            if (!ftp.completePendingCommand()) {
                throw new Exception("Pending command failed: " + ftp.getReplyString());
            }
    }

    /**
     *
     * Method to get file stream.
     *
     * @param ftp sd
     * @param filePath sd
     * @return InputStream sd
     * @throws Exception sd
     */
    private static InputStream retrieveFileStream(FTPClient ftp, String filePath) throws Exception {
        InputStream is = ftp.retrieveFileStream(filePath);
        int reply = ftp.getReplyCode();
        if (is == null || (!FTPReply.isPositivePreliminary(reply) && !FTPReply.isPositiveCompletion(reply))) {
            throw new Exception(ftp.getReplyString());
        }
        return is;
    }

    /**
     *
     * Method to download file from ftp.
     *
     * @param is sd
     * @param fileSize sd
     * @param outputPath sd
     * @return file sd
     * @throws Exception sd
     */
    private static byte[] downloadFile(InputStream is, long fileSize, String outputPath) throws Exception {
        FileOutputStream  os = new FileOutputStream(outputPath);
        byte[] buffer = new byte[(int) fileSize];
        int readCount;
        while ((readCount = is.read(buffer)) > 0) {
            os.write(buffer, 0, readCount);
        }
        System.out.println("buffer = " + buffer);
        return buffer;
    }
    /**
     *
     * Method to get the size of a certain file on the server.
     *
     * @param ftp sd
     * @param srcDir sd
     * @param fileName sd
     * @return long sd
     * @throws Exception sd
     */
    private static long getFileSize(FTPClient ftp, String srcDir, String fileName) throws Exception {
        long fileSize = 0;
        ftp.changeWorkingDirectory(srcDir);
        System.out.println("Working directory: " + ftp.printWorkingDirectory());
        FTPFile[] files = ftp.listFiles(fileName);
        if (files.length == 1 && files[0].isFile()) {
            fileSize = files[0].getSize();
        }
        System.out.println(fileName + " has the size: " + fileSize);
        return fileSize;
    }

}
