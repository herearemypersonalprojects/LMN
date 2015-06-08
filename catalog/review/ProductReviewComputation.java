/*
 * Copyright Travelsoft, 2014.
 */
package com.travelsoft.lastminute.catalog.review;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONValue;


/**
 * Calculate product reviews from the XML file.
 *
 */
public class ProductReviewComputation {
    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(ProductReviewComputation.class);
    /** source file. */
    private String xmlFileName;
    /** Product reviews controller. */
    private ProductReviews productReviews;
    /** Place to save created reviews. */
    private String outputBaseFolder;
    /** Place to save created reviews. */
    private String outputFolder;

    /**
     *
     * Default Constructor.
     * @param outputFolder sd
     *
     */
    public ProductReviewComputation(String outputFolder) {
        this.outputBaseFolder = outputFolder;
    }
    /**
     *
     * start to process.
     *
     * @param sourceFile sd
     *
     */
    public void run(String sourceFile) {
        // 1. Convert xml to java class
        xmlFileName = sourceFile;

        try {
            productReviews = XStreamModel.getProductReviews(sourceFile);
            outputFolder = outputBaseFolder + "//" + productReviews.getCode().concat("//");

            // 2. Create a repertory to store results
            if (!createReviewsFolder()) {
                return;
            }
            // 3. Create review rates for 6 types of clients
            createReviewRates();

            // 4. Create an html summary to be inserted in the product's page
            createShortSummary();

            // 5. Create html files corresponding to each review
            createReviewItems();

            // 6. Create json files containing filtered reviews
            createFilteredReviews();

            // 7. Delete the review folder if there are less than 5 reviews
            verifyReviewFolder();
        } catch (Exception e) {
            LOGGER.error("Failed to process the file: " + xmlFileName + " due to " + e);
        }

    }

    /**
     *
     * Method to check whether there are less than 5 reviews.
     *
     */
    private void verifyReviewFolder() {
        String criteria = "5.html";
        File reviewFolder = new File(outputFolder + criteria);
        if (!reviewFolder.exists()) {
            File folder = new File(outputFolder);
            for (File file: folder.listFiles()) {
                if (!file.delete()) {
                    LOGGER.error("Failed to delete the file: " + file.getAbsolutePath());
                    return;
                }
            }
            if (!folder.delete()) {
                LOGGER.error("Failed to delete the folder: " + folder.getAbsolutePath());
                return;
            }
        }
    }

    /**
     *
     * Method to create json files containing filtered reviews.
     *
     */
    private void createFilteredReviews() {
        /** create json for lastest reviews */
        List<Review> reviewsList = ReviewSorter.getLastestReviews(productReviews);
        createJSonFile(reviewsList, outputFolder, outputFolder + "lastestReviews.json");
        /** create json for oldest reviews */
        reviewsList = ReviewSorter.getOldestReviews(productReviews);
        createJSonFile(reviewsList, outputFolder, outputFolder + "oldestReviews.json");
        /** create json for highest reviews */
        reviewsList = ReviewSorter.getBestReviews(productReviews);
        createJSonFile(reviewsList, outputFolder, outputFolder + "bestReviews.json");
        /** create json for lowest reviews */
        reviewsList = ReviewSorter.getLowestReviews(productReviews);
        createJSonFile(reviewsList, outputFolder, outputFolder + "lowestReviews.json");

    }

    /**
    *
    * Method to build json file.
    *
    * @param reviewList sd
    * @param folderName sd
    * @param saveToFile sd
    */
    private void createJSonFile(List<Review> reviewList, String folderName, String saveToFile) {
        List<Map<String, String>> aListJson = new LinkedList<Map<String, String>>();

        for (int i = 0; i < reviewList.size(); i++) {
            Review review = reviewList.get(i);

           /** create json containing a list of review */
            Map<String, String> m = new LinkedHashMap<String, String>();
            m.put("id", String.valueOf(review.getIdentify()));
            m.put("date", review.getDateString());
            m.put("note", String.valueOf(review.getScore()));

            aListJson.add(m);
        }


        try {
            FileWriter file = new FileWriter(saveToFile);
            file.write(JSONValue.toJSONString(aListJson));
            file.flush();
            file.close();
        } catch (IOException e) {
            LOGGER.error("Failed to create json file " + saveToFile + " :" + e);
        }
    }

    /**
     *
     * Method to create html files corresponding to each review.
     *
     */
    private void createReviewItems() {
        for (int i = 0; i < productReviews.getReviews().size(); i++) {
            Review review = productReviews.getReviews().get(i);
            review.setIdentify(i);

            Map<String, Object> dataModel = new HashMap<String, Object>();
            dataModel = new HashMap<String, Object>();
            dataModel.put("item", review);

            ReviewUtils.ftlToHtml(FixedVariables.TEMPLATE_FOLDER,
                FixedVariables.CUSTOMER_REVIEW_ITEMS,
                outputFolder + review.getIdentify() + ".html", dataModel);

        }
    }

    /**
     *
     * Method to create a short summary of review rating for product page.
     *
     */
    private void createShortSummary() {
        Map<String, Object> dataModel = new HashMap<String, Object>();

        int rate = (int) ((productReviews.getGlobalScore() / productReviews.getMaxiScore()) * 114);
        productReviews.setRate(rate);

        dataModel.put("previewSummary", productReviews);
        ReviewUtils.ftlToHtml(FixedVariables.TEMPLATE_FOLDER,
            FixedVariables.REVIEW_SUMMARY + ".ftl",
                            outputFolder + FixedVariables.REVIEW_SUMMARY + ".html", dataModel);

        ReviewUtils.ftlToHtml(FixedVariables.TEMPLATE_FOLDER,
            FixedVariables.RELATED_PRODUCT_RATE + ".ftl",
                            outputFolder + FixedVariables.RELATED_PRODUCT_RATE + ".html", dataModel);

        ReviewUtils.ftlToHtml(FixedVariables.TEMPLATE_FOLDER,
            FixedVariables.ITEM_NOTE + ".ftl",
                            outputFolder + FixedVariables.ITEM_NOTE + ".html", dataModel);

    }

    /**
     *
     * Method to create review rates for 6 types of clients.
     *
     */
    private void createReviewRates() {
        int nbReviewTravelFamily = 0;
        int nbReviewTravelFamilyChild = 0;
        int nbReviewTravelPair = 0;
        int nbReviewTravelFriend = 0;
        int nbReviewSenior = 0;
        int nbReviewTravelAlone = 0;
        for (int i = 0; i < productReviews.getReviews().size(); i++) {
            Review review = productReviews.getReviews().get(i);
            if (review.getContext().equalsIgnoreCase(FixedVariables.ENFAMILLE)) {
                nbReviewTravelFamily++;
            } else
            if (review.getContext().equalsIgnoreCase(FixedVariables.ENFAMILLEBEBE)) {
                nbReviewTravelFamilyChild++;
            } else
            if (review.getContext().equalsIgnoreCase(FixedVariables.ENCOUPLE)) {
                nbReviewTravelPair++;
            } else
            if (review.getContext().equalsIgnoreCase(FixedVariables.ENTREAMIS)) {
                nbReviewTravelFriend++;
            } else
            if (review.getContext().equalsIgnoreCase(FixedVariables.SENIORS)) {
                nbReviewSenior++;
            } else
            if (review.getContext().equalsIgnoreCase(FixedVariables.SEULES)) {
                nbReviewTravelAlone++;
            }
        }

        productReviews.setNbReviewSenior(nbReviewSenior);
        productReviews.setNbReviewTravelAlone(nbReviewTravelAlone);
        productReviews.setNbReviewTravelFamily(nbReviewTravelFamily);
        productReviews.setNbReviewTravelFamilyChild(nbReviewTravelFamilyChild);
        productReviews.setNbReviewTravelFriend(nbReviewTravelFriend);
        productReviews.setNbReviewTravelPair(nbReviewTravelPair);

        /** Create html files */
        createHtml(outputFolder + FixedVariables.HTML_GLOBAL, "");
        createHtml(outputFolder + FixedVariables.HTML_FAMILY, FixedVariables.ENFAMILLE);
        createHtml(outputFolder + FixedVariables.HTML_FAMILYBABY, FixedVariables.ENFAMILLEBEBE);
        createHtml(outputFolder + FixedVariables.HTML_TRAVELPAIR, FixedVariables.ENCOUPLE);
        createHtml(outputFolder + FixedVariables.HTML_SENIOR, FixedVariables.SENIORS);
        createHtml(outputFolder + FixedVariables.HTML_TRAVELFRIEND, FixedVariables.ENTREAMIS);
        createHtml(outputFolder + FixedVariables.HTML_TRAVELALONE, FixedVariables.SEULES);
    }

    /**
    *
    * createHTML_FAMILY ..
    *
    * @param outputFile file
    * @param context context
    */
    private void createHtml(String outputFile, String context) {
        ClientReviewController clientReviewController = new ClientReviewController();
        clientReviewController.reset(productReviews);
        float globalNote = 0;
        for (int i = 0; i < productReviews.getReviews().size(); i++) {
            Review review = productReviews.getReviews().get(i);
            if (review.getContext().equals(context) || "".equals(context)) {
                globalNote = globalNote + review.getScore();
                boolean hotelRecommend = false;
                String recommendType = "";
                for (int j = 0; j < review.getNoteList().get(0).getReviewNotes().size(); j++) {
                    ReviewNote note = review.getNoteList().get(0).getReviewNotes().get(j);
                    if (!note.getAnswer().equals(FixedVariables.NC)
                        && !"".equals(note.getAnswer().trim())) {
                        if (note.getCriterionName().equals(FixedVariables.HOTELRECOMMEND)) {
                            if (note.getAnswer().equals(FixedVariables.OUI)) {
                                hotelRecommend = true;
                            }
                        } else {
                            if (note.getCriterionName().equals(FixedVariables.HOTELCLIENTTYPE)) {
                                recommendType = note.getAnswer();
                            } else {
                                clientReviewController = setReviewClients(clientReviewController, note);
                            }
                        }

                    }
                }
                if (hotelRecommend && !"".equals(recommendType)) {
                    clientReviewController = setRecommend(clientReviewController,
                        recommendType, review.getScore() / review.getMaxiScore());
                }
            }
        }

        /** Create html file */
        clientReviewController.setHotelRecommend(context, globalNote);
        clientReviewController.compute();

        Map<String, Object> dataModel = new HashMap<String, Object>();
        dataModel.put("reviews", clientReviewController);
        ReviewUtils.ftlToHtml(FixedVariables.TEMPLATE_FOLDER,
            FixedVariables.PRODUCT_REVIEWS + ".ftl", outputFile, dataModel);

    }

    /**
    *
    * Method ..
    *
    * @param clientReviewController sd
    * @param type sd
    * @param score sd
    * @return clientReviewController;
    */
    private ClientReviewController setRecommend(ClientReviewController clientReviewController, String type, float score) {
        if (type.equalsIgnoreCase(FixedVariables.FAMILLES)) {
            clientReviewController.setRecommendFamilles(score);
        } else
        if (type.equalsIgnoreCase(FixedVariables.COUPLES)) {
            clientReviewController.setRecommendCouples(score);
        } else
        if (type.equalsIgnoreCase(FixedVariables.NESEPRONONCEPAS)) {
            clientReviewController.setRecommendAbsence(score);
        } else
        if (type.equalsIgnoreCase(FixedVariables.GROUPEAMIS)) {
            clientReviewController.setRecommendAmisGroupes(score);
        } else
        if (type.equalsIgnoreCase(FixedVariables.ETUDIANTS)) {
            clientReviewController.setRecommendEtudiants(score);
        } else
        if (type.equalsIgnoreCase(FixedVariables.PERSONNESAGEES)) {
            clientReviewController.setRecommendPersonnesAgees(score);
        } else
        if (type.equalsIgnoreCase(FixedVariables.JEUNESMARIES)) {
            clientReviewController.setRecommendJeunesMaries(score);
        } else
        if (type.equalsIgnoreCase(FixedVariables.CELIBATAIRES)) {
            clientReviewController.setRecommendCelibataires(score);
        }

        return clientReviewController;
    }
   /**
    *
    * Method ..
    *
    * @param note note
    * @param clientReviewController s
    * @return ClientReviewController sd
    */
    private ClientReviewController setReviewClients(ClientReviewController clientReviewController,
            ReviewNote note) {
       /** consider hotel rates */
        float rate = ReviewUtils.getFloat(note.getAnswer()) / note.getMaxiScore();
        if (rate <= 0.001) {
            return clientReviewController;
        }

        if (note.getCriterionName().equalsIgnoreCase(FixedVariables.SERVICE)) {
            clientReviewController.setHotelService(rate);
        } else
            if (note.getCriterionName().equalsIgnoreCase(FixedVariables.ENVIRON)) {
                clientReviewController.setHotelCadre(rate);
            } else
                if (note.getCriterionName().equalsIgnoreCase(FixedVariables.GEO)) {
                    clientReviewController.setHotelLocation(rate);
                } else
                    if (note.getCriterionName().equalsIgnoreCase(FixedVariables.HOUSING)) {
                        clientReviewController.setHotelChambre(rate);
                    } else
                        if (note.getCriterionName().equalsIgnoreCase(FixedVariables.RESTORATION)) {
                            clientReviewController.setHotelRestauration(rate);
                        } else
                            if (note.getCriterionName().equalsIgnoreCase(FixedVariables.ANIMATION)) {
            clientReviewController.setHotelActivites(rate);
        } else
        if ((note.getCriterionName().equalsIgnoreCase(FixedVariables.QUALITYPRICE))) {
            clientReviewController.setHotelPrix(rate);
        } else
        if ((note.getCriterionName().equalsIgnoreCase(FixedVariables.EQUIPEMENT))) {
            clientReviewController.setHotelEquipements(rate);
        } else
        if ((note.getCriterionName().equalsIgnoreCase(FixedVariables.KIDACTIVITY))) {
            clientReviewController.setHotelEnfantActivites(rate);
        } else
        if ((note.getCriterionName().equalsIgnoreCase(FixedVariables.EXCURSION))) {
            clientReviewController.setHotelExcursions(rate);
        } else
        if ((note.getCriterionName().equalsIgnoreCase(FixedVariables.FLY))) {
            clientReviewController.setHotelVol(rate);
        } else
        if ((note.getCriterionName().equalsIgnoreCase(FixedVariables.TRANSFERT))) {
            clientReviewController.setHotelTransfert(rate);
        } else
        if ((note.getCriterionName().equalsIgnoreCase(FixedVariables.STRINGER))) {
            clientReviewController.setHotelCorrespondantLocal(rate);
        }

        return clientReviewController;
    }

     /**
     *
     * Method to creat review folder.
     *
     *
     * @return boolean
     */
    private boolean  createReviewsFolder() {
        File productFolder = new File(outputFolder);
        if (!productFolder.exists()) {
            if (!productFolder.mkdir()) {
                return false;
            }
        } else {
            for (File file: productFolder.listFiles()) {
                if (!file.delete()) {
                    return false;
                }
            }
        }
        return true;
    }
}
