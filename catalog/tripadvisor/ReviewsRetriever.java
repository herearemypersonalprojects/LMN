package com.travelsoft.lastminute.catalog.tripadvisor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONValue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.travelsoft.lastminute.catalog.review.FTPClientModel;
import com.travelsoft.lastminute.catalog.review.ReviewUtils;
import com.travelsoft.lastminute.catalog.util.Util;
import com.travelsoft.lastminute.data.Address_obj;
import com.travelsoft.lastminute.data.Ancestor;
import com.travelsoft.lastminute.data.Ancestors;
import com.travelsoft.lastminute.data.Award;
import com.travelsoft.lastminute.data.Awards;
import com.travelsoft.lastminute.data.Category;
import com.travelsoft.lastminute.data.CollectionReview;
import com.travelsoft.lastminute.data.CollectionReviews;
import com.travelsoft.lastminute.data.Data;
import com.travelsoft.lastminute.data.Images;
import com.travelsoft.lastminute.data.Place;
import com.travelsoft.lastminute.data.PlaceList;
import com.travelsoft.lastminute.data.Ranking_data;
import com.travelsoft.lastminute.data.Review;
import com.travelsoft.lastminute.data.ReviewRatingCount;
import com.travelsoft.lastminute.data.Subcategory;
import com.travelsoft.lastminute.data.Subratings;
import com.travelsoft.lastminute.data.TripAdvisorReviews;
import com.travelsoft.lastminute.data.Trip_types;
import com.travelsoft.lastminute.data.User;
import com.travelsoft.lastminute.data.User_location;


/**
 *
 * Get TripAdvisor reviews.
 *
 */
public class ReviewsRetriever {
    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(ReviewsRetriever.class);

    /** save reviews to file. */
    private static final String SAVED_FILE = "/var/data/static/lastminute/shared/cs/web/lastminute-catalog/tripAdvisorReviews.results";

    /**
    *
    * Method to convert a buffer data to string type.
    *
    * @param rd given buffer
    * @return string
    */
    private String readAll(BufferedReader rd) {
        StringBuilder sb = new StringBuilder();
        try {
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return sb.toString();
    }
   /**
    *
    * Method to get json from a given url.
    *
    * @param url tripadvisor address
    * @return json object
    */
    public JSONObject getJsonData(String url) {
        InputStream is = null;
        JSONObject json = null;
        try {
            is = new URL(url).openStream();
            try {
                InputStreamReader isd = new InputStreamReader(is, Charset.forName("UTF-8"));
                BufferedReader rd = new BufferedReader(isd);
                String jsonText = readAll(rd);
                json = new JSONObject(jsonText);
            } catch (JSONException e) {
                is.close();
            }
        } catch (IOException e) {
            LOGGER.debug("TripAdvisor reviews are not found from the given url" + url + ":" + e.getMessage());
        }
        return json;
    }



    /**
     *
     * Method to save file to disk.
     *
     * @param input given source.
     * @param output destination.
     * @param bufferSize amount
     */
    private void copy(InputStream input, OutputStream output, int bufferSize) {
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
     * Method to get and save TripAdvisor reviews.
     *
     * @return map
     */
    @SuppressWarnings("unchecked")
    public Map<String, Place> getTripAdvisorReviews() {

        Map<String, Place> map = null;
        try {
            File file = new File(SAVED_FILE);
            FileInputStream f = new FileInputStream(file);
            ObjectInputStream s = new ObjectInputStream(f);
            map = (HashMap<String, Place>) s.readObject();
            s.close();
        } catch (IOException e) {
            LOGGER.error("Error while trying to read reviews from file " + SAVED_FILE + ": " + e.getMessage());
        } catch (ClassNotFoundException e) {
            LOGGER.error(e.getMessage());
        }
        return map;
    }

    /**
     *
     * Method be called by servlet to get and save reviews.
     *
     * @param source the given server (url).
     * @param fileName filename
     * @return number of imported reviews
     */
    public int getAndSaveTripAdvisorReviews(String source, String fileName) {
        Map<String, Place> map = getXMLReviews(source, fileName);
        try {
            File file = new File(SAVED_FILE);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream f = new FileOutputStream(file);
            ObjectOutputStream s = new ObjectOutputStream(f);
            s.writeObject(map);
            s.flush();
            s.close();
        } catch (IOException e) {
            LOGGER.error("Error while trying to save reviews to file: " + SAVED_FILE + e.getMessage());
        }
        return map.size();
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
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Requesting Host  : " + getRequestingHost());
                LOGGER.debug("Requesting Port  : " + getRequestingPort());
                LOGGER.debug("Requesting Prompt : " + getRequestingPrompt());
                LOGGER.debug("Requesting Protocol: " + getRequestingProtocol());
                LOGGER.debug("Requesting Scheme : " + getRequestingScheme());
                LOGGER.debug("Requesting Site  : " + getRequestingSite());
            }
            return new PasswordAuthentication(username, password.toCharArray());
        }
    }

    /**
     *
     * Method to import reviews and store in a local repository..
     *
     * @param server the server's url
     * @param username the username
     * @param password the password
     * @param srcDir the folder in the server
     * @param fileName the file to be retrieved from the server
     * @param zipFileFullPath the full path of zip file
     * @param folderToSave the folder destination to save the retrieved file
     * @param content the xml input's content
     * @return number of products retrieved
     */
    public int importTripAdvisorReviews(String server,
        String username, String password, String srcDir,
        String fileName, String zipFileFullPath, String folderToSave, StringWriter content) {
        PlaceList placeList = this.getReviewCollectionFeeds(
            server, username, password, srcDir, fileName, zipFileFullPath, folderToSave, content);
        int num = 0;
        for (int i = 0; i < placeList.getPlaceCount(); i++) {
            Place product = placeList.getPlace(i);
            /** create one folder for each product */
            String path = folderToSave + product.getPARTNERID();
            FTPClientModel.resetDir(path);
            List<Map<String, String>> json = new LinkedList<Map<String, String>>();
            /** create review files indepently each others */
            CollectionReviews reviews = product.getCollectionReviews();
            int crawableReviewsCount = 0;
            String crawableReviewsList = "";
            for (int j = 0; j < reviews.getCollectionReviewCount(); j++) {
                CollectionReview review = reviews.getCollectionReview(j);
                num++;
                Map<String, String> m = new LinkedHashMap<String, String>();
                String crawable = "";
                m.put("id", String.valueOf(j));
                m.put("date", review.getPublishedDate());
                m.put("note", review.getRating());

                if (!review.getCrawable().equals("true")) {
                    crawable = " nocrawable";
                    json.add(json.size(), m);
                } else {
                    crawable = " crawable";
                    json.add(0, m);
                    crawableReviewsCount++;
                    if (crawableReviewsCount <= 5) {
                        crawableReviewsList = crawableReviewsList + m.get("id") + ":";
                    }
                }
                Map<String, Object> dataModel = new HashMap<String, Object>();
                dataModel = new HashMap<String, Object>();
                dataModel.put("review", review);
                String ftlFolder = "/var/data/static/lastminute/shared/ts/local/cms/lastminute/main/templates/product/";
                String ftlFile = "reviewCollectionItem.ftl";
                String htmlFile = path + "/" + String.valueOf(j) + ".html";
                ReviewUtils.ftlToHtml(ftlFolder, ftlFile, htmlFile, dataModel);
            }
            if (crawableReviewsCount >= 5) {
                /** create json file that indicate the first 5 reviews 'crawable' */
                try {
                    FileWriter txtFile = new FileWriter(path + "//crawableReviewsList.txt");
                    txtFile.write(crawableReviewsList);
                    txtFile.flush();
                    txtFile.close();

                    FileWriter file = new FileWriter(path + "//crawableReviews.json");
                    file.write(JSONValue.toJSONString(json));
                    file.flush();
                    file.close();
                } catch (IOException e) {
                    LOGGER.error("Failed to create json file " + path + "//crawableReviews.json(txt) :" + e);
                }
            }
        }
        return num;
    }

    /**
     *
     * Method to get Review Collection Feeds from the TripAdvisor server.
     *
     * @param server the server's url
     * @param username the username
     * @param password the password
     * @param srcDir the folder in the server
     * @param fileName the file to be retrieved from the server
     * @param zipFileFullPath the full path of zip file
     * @param folderToSave the folder destination to save the retrieved file
     * @param content the XML input's content
     * @return a list of products' reviews
     */
    public PlaceList getReviewCollectionFeeds(String server,
        String username, String password, String srcDir,
        String fileName, String zipFileFullPath, String folderToSave, StringWriter content) {
        PlaceList placeList = new PlaceList();

        /** get xml file from the Tripadvisor server */
        if (StringUtils.isNotBlank(Util.getConfigValue("TRIPADVISOR_ISEXISTINGFEEDSXML"))
                        && Util.getConfigValue("TRIPADVISOR_ISEXISTINGFEEDSXML").equals("false")) {
            FTPClientModel.getAndUnzipFile(server, username, password, srcDir, fileName, zipFileFullPath, folderToSave);
        }


        /** load and process xml reviews data */
        File xmlFile = new File(folderToSave + fileName);
        if (xmlFile.exists()) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(xmlFile);
                content.append(String.valueOf(xmlFile.length()));
                Element element = doc.getDocumentElement();
                /** each place correspond to one or many product */
                NodeList productList = element.getElementsByTagName("Place");
                for (int i = 0; i < productList.getLength(); i++) {
                    if (productList.item(i) instanceof Element) {
                        Element p = (Element) productList.item(i);
                        /** may be many products have same reviews */
                        NodeList pIds = p.getElementsByTagName("PartnerId");

                        for (int pCount = 0; pCount < pIds.getLength(); pCount++) {
                            /** create a new product */
                            Place place = new Place();
                            place.setPARTNERID(pIds.item(pCount).getTextContent());
                            /** get all reviews for this product */
                            int existReviews = p.getElementsByTagName("Reviews").getLength();
                            if (existReviews > 0) {
                                CollectionReviews collectionReviews = new CollectionReviews();
                                NodeList reviews = ((Element) p.getElementsByTagName("Reviews").item(0))
                                                .getElementsByTagName("Review");
                                for (int rCount = 0; rCount < reviews.getLength(); rCount++) {
                                    CollectionReview collectionReview = new CollectionReview();

                                    Element review = (Element) reviews.item(rCount);
                                    collectionReview.setCrawable(this.getString(review, "Crawlable"));
                                    collectionReview.setRating(this.getString(review, "Rating"));
                                    collectionReview.setTitle(this.getString(review, "Title"));
                                    collectionReview.setText(this.getString(review, "Text"));
                                    collectionReview.setPublishedDate(this.getDate(review, "DatePublished"));
                                    if (review.getElementsByTagName("Author").getLength() > 0) {
                                        Element e = (Element) review.getElementsByTagName("Author").item(0);
                                        collectionReview.setLocation(this.getString(e, "Location"));
                                        collectionReview.setAuthor(this.getString(e, "AuthorName"));
                                    }
                                    collectionReviews.addCollectionReview(collectionReview);
                                }
                                place.setCollectionReviews(collectionReviews);
                            }
                            placeList.addPlace(place);
                        }
                    }
                }
            } catch (MalformedURLException e) {
                LOGGER.error("The given url is not found: " + e.getMessage());
            } catch (FileNotFoundException e) {
                LOGGER.error("The xml file is not found: " + e.getMessage());
            } catch (IOException e) {
                LOGGER.error("Unicode error: " + e.getMessage());
            } catch (ParserConfigurationException e) {
                LOGGER.error("Error when parse xml content: " + e.getMessage());
            } catch (SAXException e) {
                LOGGER.error("Can not extract xml content: " + e.getMessage());
            }
        }


        return placeList;
    }
    /**
     *
     * Method to get review notes from zip file.
     *
     * @param source given adresse to get file
     * @param fileName zip file
     * @return Map<String, Place> list of review infos
     */
    public Map<String, Place> getXMLReviews(String source, String fileName) {
        Map<String, Place> map = new HashMap<String, Place>();
        try {
            String tmpFile = "tripadvisor" + System.currentTimeMillis() + ".zip";
            URL url = new URL(source);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStream in = connection.getInputStream();
            FileOutputStream out = new FileOutputStream(tmpFile);
            copy(in, out, 1024);
            out.close();

            ZipFile zipFile = new ZipFile(tmpFile);
            ZipEntry zipEntry = zipFile.getEntry(fileName);
            InputStream inputStream = zipFile.getInputStream(zipEntry);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(inputStream);
            inputStream.close();

            Element element = doc.getDocumentElement();
            NodeList nodes = element.getElementsByTagName("Place");

            for (int i = 0; i < nodes.getLength(); i++) {
                Node n = nodes.item(i);
                if (n instanceof Element ) {

                    NodeList productList = ((Element) n).getElementsByTagName("PARTNERID");
                    if (productList.getLength() > 0) {
                        for (int j = 0; j < productList.getLength(); j++) {
                            Place place = new Place();
                            String key = productList.item(j).getTextContent();
                            place.setAvgrating(((Element) n).getAttribute("avgrating"));
                            place.setReviewcnt(((Element) n).getAttribute("reviewcnt"));
                            place.setRatingimage(((Element) n).getElementsByTagName("Url").item(0).getTextContent());
                            map.put(key, place);
                        }
                    }
                }
            }

            String testedId = Util.getConfigValue("TRIPADVISOR_PARTNERID_TEST");
            String replacedId = Util.getConfigValue("TRIPADVISOR_PARTNERID_REPLACED");
            if (StringUtils.isNotBlank(testedId) && StringUtils.isNotBlank(replacedId)) {
                if (map.containsKey(replacedId)) {
                    map.put(testedId, map.get(replacedId));
                }
            }

            zipFile.close();
            File f = new File(tmpFile);
            f.delete();


        } catch (MalformedURLException e) {
            LOGGER.error("The given url is not found: " + e.getMessage());
        } catch (FileNotFoundException e) {
            LOGGER.error("The zip file is not found: " + e.getMessage());
        } catch (IOException e) {
            LOGGER.error("Can not connect to the TripAdvisor server: " + e.getMessage());
        } catch (ParserConfigurationException e) {
            LOGGER.error("Error when parse xml content: " + e.getMessage());
        } catch (SAXException e) {
            LOGGER.error("Can not extract xml content: " + e.getMessage());
        }



        return map;
    }

    /**
     *
     * Method to get list of crawable review files.
     *
     * @param sourceJson path to json file
     * @return list of filenames
     */
    public String getReviewFilesList(String sourceJson) {
        String lst = "";
        File file = new File(sourceJson);
        if (file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(sourceJson));
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.getProperty("line.separator"));
                    line = br.readLine();
                }
                lst = sb.toString();
                br.close();
            } catch (IOException e) {
                LOGGER.debug("Failed to read file : " + sourceJson);
            }
        }
        return lst;
    }
    /**
     *
     * Method to initialize reviews data.
     *
     * @param reviewsUrl tripadvior json url
     * @param opinionsUrl opion
     * @param awardsUrl traveler's choice
     * @return data
     */
    public TripAdvisorReviews getReviews(String reviewsUrl, String opinionsUrl, String awardsUrl) {
        TripAdvisorReviews reviews = null;
        try {
            //JSONObject ro = this.getJsonData(reviewsUrl);
            JSONObject ro = Util.getJson(reviewsUrl);
            reviews =  new TripAdvisorReviews();
            if (ro == null) {
                return reviews;
            }
            /* 1. set review ratings */
            if (ro.has("reviewRatingCount")) {
                ReviewRatingCount reviewRatingCount = new ReviewRatingCount();
                JSONObject json = ro.getJSONObject("reviewRatingCount");
                reviewRatingCount.setExcellent(this.getInt(json, "5"));
                reviewRatingCount.setVery_good(this.getInt(json, "4"));
                reviewRatingCount.setAverage(this.getInt(json, "3"));
                reviewRatingCount.setPoor(this.getInt(json, "2"));
                reviewRatingCount.setTerrible(this.getInt(json, "1"));

                reviews.setReviewRatingCount(reviewRatingCount);
            }

            /* 2. percent recommended */
            reviews.setPercent_recommended(this.getInt(ro, "percent_recommended"));

            /* 3. location */
            reviews.setLocation_string(this.getString(ro, "location_string"));

            /* 4. write review */
            reviews.setWrite_review(this.getString(ro, "write_review"));

            /* 5. address object */
            if (ro.has("address_obj")) {
                JSONObject json = ro.getJSONObject("address_obj");
                Address_obj address = new Address_obj();
                address.setStreet1(this.getString(json, "street1"));
                address.setStreet2(this.getString(json, "street2"));
                address.setCity(this.getString(json, "city"));
                address.setState(this.getString(json, "state"));
                address.setCountry(this.getString(json, "country"));
                address.setPostalcode(this.getString(json, "postalcode"));

                reviews.setAddress_obj(address);
            }

            /* 6. distance  */
            reviews.setDistance(this.getString(ro, "distance"));

            /* 7. description */
            reviews.setDescription(this.getString(ro.get("description")));

            /* 8. name */
            reviews.setName(this.getString(ro, "name"));

            /* 9.  ancestors*/
            if (ro.has("ancestors")) {
                Ancestors ancestors = new Ancestors();
                JSONArray array = ro.getJSONArray("ancestors");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject json = array.getJSONObject(i);
                    Ancestor ancestor = new Ancestor();
                    ancestor.setLevel(this.getString(json, "level"));
                    ancestor.setName(this.getString(json, "name"));
                    ancestor.setLocation_id(this.getString(json, "location_id"));
                    ancestors.addAncestor(ancestor);
                }
                reviews.setAncestors(ancestors);
            }

            /* 10. longitude */
            reviews.setLongitude(this.getFloat(ro, "longitude"));

            /* 11. photo_count */
            reviews.setPhoto_count(this.getInt(ro, "photo_count"));

            /* 13. see_all_photos */
            reviews.setSee_all_photos(this.getString(ro, "see_all_photos"));

            /* 14. web_url */
            reviews.setWeb_url(this.getString(ro, "web_url"));

            /* 15. ratingImageUrl */
            reviews.setRatingImageUrl(this.getString(ro, "ratingImageUrl"));

            /* 16. location_id */
            reviews.setLocation_id(this.getString(ro, "location_id"));

            /* 17. category */
            if (ro.has("category")) {
                Category category = new Category();
                JSONObject json = ro.getJSONObject("category");
                category.setKey(this.getString(json, "key"));
                category.setName(this.getString(json, "name"));
                reviews.setCategory(category);
            }

            /* 18. price*/
            reviews.setPrice(this.getString(ro, "price"));

            /* 19. subratings*/
            if (ro.has("subratings")) {
                Subratings subratings = new Subratings();
                JSONObject json = ro.getJSONObject("subratings");
                subratings.setSleep_quality(this.getString(json, "Literie"));
                subratings.setValue(this.getString(json, "Rapport qualité / prix"));
                subratings.setService(this.getString(json, "Service"));
                subratings.setRooms(this.getString(json, "Chambres"));
                subratings.setCleanliness(this.getString(json, "Propreté"));
                subratings.setLocation(this.getString(json, "Emplacement"));
                reviews.setSubratings(subratings);
            }

            /* 20. address*/
            reviews.setAddress(this.getString(ro, "address"));

            /* 21. trip_types*/
            if (ro.has("trip_types")) {
                Trip_types tripTypes = new Trip_types();
                JSONObject json = ro.getJSONObject("trip_types");
                tripTypes.setCouples(getString(json, "En couple"));
                tripTypes.setBusiness(getString(json, "Professionnel"));
                tripTypes.setFamily(getString(json, "En famille"));
                tripTypes.setSolo_travel(getString(json, "Voyage solo"));
                reviews.setTrip_types(tripTypes);
            }

            /* 22. num_reviews*/
            reviews.setNum_reviews(this.getInt(ro, "num_reviews"));

            /* 23. subcategory*/
            if (ro.has("subcategory")) {
                Subcategory subcategory = new Subcategory();
                JSONObject json = ro.getJSONObject("subcategory");
                subcategory.setKey(this.getString(json, "key"));
                subcategory.setName(this.getString(json, "name"));
                reviews.setSubcategory(subcategory);
            }

            /* 24. awards*/
            if (ro.has("awards")) {
                Awards awards = new Awards();
                JSONArray array = ro.getJSONArray("awards");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject json = array.getJSONObject(i);
                    Award award = new Award();
                    award.setAward_type(getString(json, "award_type"));
                    award.setYear(this.getInt(json, "year"));
                    if (json.has("images")) {
                        Images images = new Images();
                        images.setSmall(getString(json.getJSONObject("images"), "small"));
                        images.setLarge(getString(json.getJSONObject("images"), "large"));
                        award.setImages(images);
                    }
                    award.setDisplay_name(getString(json, "display_name"));
                    awards.addAward(award);
                }
                reviews.setAwards(awards);
            }

            /* 25. bearing*/
            reviews.setBearing(this.getString(ro.get("bearing")));

            /* 26. latitude*/
            reviews.setLatitude(this.getFloat(ro, "latitude"));

            /* 27. rating*/
            reviews.setRating(this.getFloat(ro, "rating"));

            /* 28. ranking*/
            reviews.setRanking(this.getString(ro, "ranking"));

            /* 29. ranking_data*/
            if (ro.has("ranking_data")) {
                Ranking_data rankingData = new Ranking_data();
                JSONObject json = ro.getJSONObject("ranking_data");
                rankingData.setGeo_location_id(this.getString(json, "geo_location_id"));
                rankingData.setGeo_location_name(this.getString(json, "geo_location_name"));
                rankingData.setRanking_out_of(this.getInt(json, "ranking_out_of"));
                rankingData.setRanking(this.getInt(json, "ranking"));
                rankingData.setRanking_category(getString(json, "ranking_category"));
                reviews.setRanking_data(rankingData);
            }

            /*********************************************
             * OPINIONS ARRAY
             */
            //ro = this.getJsonData(opinionsUrl);
            ro = Util.getJson(opinionsUrl);
            if (ro == null) {
                return reviews;
            }
            Data data = new Data();
            if (ro.has("data")) {
                JSONArray array = ro.getJSONArray("data");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject json = array.getJSONObject(i);
                    Review review = new Review();

                    review.setId(getString(json, "id"));
                    review.setLang(getString(json, "lang"));
                    review.setLocation_id(getString(json, "location_id"));
                    review.setPublished_date(this.getDate(json, "published_date"));
                    review.setRating(getString(json, "rating"));
                    review.setType(getString(json, "type"));
                    review.setRatingImageUrl(getString(json, "ratingImageUrl"));
                    review.setUrl(getString(json, "url"));
                    review.setTrip_type(this.getString(json, "trip_type"));
                    review.setTravel_date(getString(json, "travel_date"));
                    review.setText(getString(json, "text"));

                    User user = new User();
                    if (json.has("user")) {
                        JSONObject userJson = json.getJSONObject("user");
                        user.setUsername(this.getString(userJson, "username"));
                        if (userJson.has("user_location")) {
                            User_location userLocation = new User_location();
                            JSONObject userLocationJson = userJson.getJSONObject("user_location");
                            userLocation.setId(this.getString(userLocationJson, "id"));
                            userLocation.setName(this.getString(userLocationJson, "name"));
                            user.setUser_location(userLocation);
                        }
                        review.setUser(user);
                    }

                    review.setTitle(getString(json, "title"));

                    Subratings dataSubratings = new Subratings();
                    if (json.has("subratings")) {
                        JSONObject dataSubratingsJson = json.getJSONObject("subratings");
                        dataSubratings.setSleep_quality(this.getString(dataSubratingsJson, "Literie"));
                        dataSubratings.setService(this.getString(dataSubratingsJson, "Service"));
                        dataSubratings.setValue(this.getString(dataSubratingsJson, "Rapport qualité / prix"));
                        dataSubratings.setRooms(this.getString(dataSubratingsJson, "Chambres"));
                        dataSubratings.setCleanliness(this.getString(dataSubratingsJson, "Propreté"));
                        dataSubratings.setLocation(this.getString(dataSubratingsJson, "Emplacement"));
                        review.setSubratings(dataSubratings);
                    }

                    data.addReview(review);
                }
                reviews.setData(data);
            }


            /*********************************************
             * AWARDS (TRAVELER'S CHOICE)
             */
            //ro = this.getJsonData(awardsUrl);
            ro = Util.getJson(awardsUrl);
            if (ro == null) {
                return reviews;
            }
            if (ro.has("data")) {
                Awards travelerChoiceAwards = new Awards();
                JSONArray array = ro.getJSONArray("data");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject json = array.getJSONObject(i);
                    Award award = new Award();
                    award.setAward_type(getString(json, "award_type"));
                    award.setYear(this.getInt(json, "year"));
                    if (json.has("images")) {
                        Images images = new Images();
                        images.setSmall(getString(json.getJSONObject("images"), "small"));
                        images.setLarge(getString(json.getJSONObject("images"), "large"));
                        award.setImages(images);
                    }
                    award.setDisplay_name(getString(json, "display_name"));
                    travelerChoiceAwards.addAward(award);
                }
                reviews.setAwards(travelerChoiceAwards);
            }
        } catch (JSONException e) {
            LOGGER.error("Failed to retrieve TripAdvisor reviews: " + e.getMessage());
            return reviews;
        }


        return reviews;
    }

    /**
     *
     * Method to get string value from xml element object.
     *
     * @param e element object
     * @param tag tagnamel
     * @return string
     */
    private String getString(Element e, String tag) {
        String v = "";
        NodeList list = e.getElementsByTagName(tag);
        if (list.getLength() > 0) {
            v = list.item(0).getTextContent();
        }
        return v;
    }
    /**
     *
     * Method to get and convert to French date type.
     *
     * @param e XML element object.
     * @param tag tagname
     * @return date in string
     */
    private String getDate(Element e, String tag) {
        String v = getString(e, tag);
        return this.getDate(v);
    }

    /**
     *
     * Method to convert string to French date.
     *
     * @param sDate string
     * @return date in string
     */
    private String getDate(String sDate) {
        String v = sDate;
        if (v.indexOf("T") > 0) {
            v = v.substring(0, v.indexOf("T"));
            try {

                SimpleDateFormat formater = new SimpleDateFormat("EEEE, d MMM yyyy");
                SimpleDateFormat dmyFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = dmyFormat.parse(v);
                v = formater.format(date).toString();
                v = v.substring(0, 3) + "." + v.substring(v.indexOf(",") + 1);
            } catch (ParseException e) {
                LOGGER.error("The date is not correctly formatted! " + e.getMessage());
            }
        }
        return v;
    }
    /**
     *
     * Method to get and convert to French date type.
     *
     * @param json a json object
     * @param tag tagname
     * @return french date in string
     */
    private String getDate(JSONObject json, String tag) {
        String v = this.getString(json, tag);
        return this.getDate(v);
    }
    /**
     *
     * Method check and get value from a json tag.
     *
     * @param json given json object
     * @param tag tagname
     * @return string
     */
    public String getString(JSONObject json, String tag) {
        String v = "";
        if (json.has(tag)) {
            v = this.getString(json.get(tag));
        }
        return v;
    }


    /**
     *
     * Method to check and return a string value.
     *
     * @param v value
     * @return string
     */
    private String getString(Object v) {
        String r = "";
        try {
            if (v != null && v.getClass().equals(String.class) && StringUtils.isNotBlank((String) v)) {
                r = (String) v;
            }
        } catch (Exception e) {
            LOGGER.error("Number Format Exception for InputString " + e);
        }
        return r;
    }

    /**
     *
     * Method to check and return a float value.
     *
     * @param json a given json object.
     * @param tag tagname
     * @return float
     */
    public float getFloat(JSONObject json, String tag) {
        float v = 0.0f;
        if (json.has(tag)) {
            v = this.getFloat(json.get(tag));
        }
        return v;
    }

    /**
     *
     * Method to check and return a float value.
     *
     * @param v value.
     * @return float
     */
    private float getFloat(Object v) {
        float newV = 0.0f;
        try {
            if (v != null) {
                if (v.getClass().equals(Float.class)) {
                    newV = (Float) v;
                } else if (v.getClass().equals(String.class) && StringUtils.isNotBlank((String) v)) {
                    newV = Float.valueOf((String) v);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Number Format Exception for InputString " + e);
        }
        return newV;
    }

    /**
     *
     * Method to check and return an int value.
     *
     * @param json a given json object
     * @param tag tagname
     * @return integer
     */
    public int getInt(JSONObject json, String tag) {
        int v = 0;
        if (json.has(tag)) {
            v = this.getInt(json.get(tag));
        }
        return v;
    }
    /**
     *
     * Method to check and return an integer value.
     *
     * @param v value
     * @return int
     */
    private int getInt(Object v) {
        int newV = 0;
        try {
            if (v != null) {
                if (v.getClass().equals(Integer.class)) {
                    newV = (Integer) v;
                } else if (v.getClass().equals(String.class) && StringUtils.isNotBlank((String) v)) {
                    newV = Integer.valueOf((String) v);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Number Format Exception for InputString " + e);
        }
        return newV;
    }


    /**
     * Method to test.
     *
     * @param args args
     */
    public static void main(String[] args) {
        String url =         "http://api.tripadvisor.com/api/partner/1.0/location/89575?key=6AB7FA15364F478FB896531F8735D5DB";
        String opinionsUrl = "http://api.tripadvisor.com/api/partner/1.0/location/258705/reviews?key=6AB7FA15364F478FB896531F8735D5DB";
        String awardsUrl =   "http://api.tripadvisor.com/api/partner/1.0/location/1516481/awards?key=6AB7FA15364F478FB896531F8735D5DB";

        String source = "http://feedserver.tripadvisor.com/lastminuteE4BE2C56.out/lastminuteXmlExport-output.xml.zip";
        //String fileName = "lastminuteXmlExport-output.xml";
        /*
        TripAdvisorReviews tripAdvisorReviews = new ReviewsRetriever().getReviews(url, opinionsUrl, awardsUrl);

        System.out.println(url);


        new ReviewsRetriever().getXMLReviews(source, fileName);
        */
        String v = "2014-05-10T14:13:38-0400";
        v = v.substring(0, v.indexOf("T"));
        try {

            SimpleDateFormat formater = new SimpleDateFormat("EEEE, d MMM yyyy");
            SimpleDateFormat dmyFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = dmyFormat.parse(v);
            v = formater.format(date).toString();
            v = v.substring(0, 3) + "." + v.substring(v.indexOf(",") + 1);
        } catch (ParseException e) {
            LOGGER.error("The date is not correctly formatted! " + e.getMessage());
        }
        System.out.println(v);

        //System.out.println(new ReviewsRetriever().getTripAdvisorReviews().get("1787").getAvgrating());

        /** TEST REVIEW COLLECTION FEEDS */
        String server = "https://feedserver.tripadvisor.com/rcp/lastminute/";
        String username = "lastminute";
        String password = "BE6AE3A40AD3";
        String srcDir = "";
        String fileName = "RCP_ReviewsDaily_lastminute_20140522.xml";
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String d = dateFormat.format(date).toString();
        fileName = "RCP_ReviewsDaily_lastminute_" + d + ".xml";

        String tripAdviorFolder = "C://var//data//static//lastminute//shared//cs//web//lastminute-catalog//";
        String zipFileFullPath = tripAdviorFolder + fileName + ".zip";
        String folderToSave = tripAdviorFolder + "reviews//";

        /*
        PlaceList placeList = new ReviewsRetriever().getReviewCollectionFeeds(server,
            username, password, srcDir, fileName, zipFileFullPath, folderToSave, new StringWriter());

        System.out.println("Number of reviews: " + placeList.getPlaceCount());
        System.out.println(placeList.getPlace(0).getCollectionReviews().getCollectionReview(0).getAuthor());


        StringWriter content = new StringWriter();
        new ReviewsRetriever().importTripAdvisorReviews(server,
            username, password, srcDir, fileName, zipFileFullPath, folderToSave, content);

        System.out.println(content.toString());

        String jsonSource = "/var/data/static/lastminute/shared/cs/web/lastminute-catalog/reviews/1787/crawableReviewsList.txt";
        System.out.println(new ReviewsRetriever().getReviewFilesList(jsonSource));


        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String d = dateFormat.format(date).toString();
        System.out.println(d);
        */
    }
}
