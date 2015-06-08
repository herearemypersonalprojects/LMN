/*
 * Copyright Travelsoft, 2014.
 */
package com.travelsoft.lastminute.catalog.review;

import com.travelsoft.lastminute.data.Produit;





/**
 * .
 *
 */
public class Main {

    /** Place to get XML inputs. */
    private static final String INPUTSOURCE =
                    "C:\\Documents\\LastMinuteResponsive\\";
    /** Place to save created reviews. */
    private static final String OUTPUTSOURCE =
                    "C://var//data//static//lastminute//shared//cs//web//lastminute-catalog//reviews";

    /**
     * Method to start.
     *
     * @param args ag
     * @throws Exception exception
     */
    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();

        Produit produit = ReviewsLoadingTool.loadReviews(INPUTSOURCE + "avis_h037.xml", "UTF-8", Produit.class);

        System.out.println(produit.getCriteres().getCritereNote(0).getValeur());
        System.out.println(produit.getAvis(0).getNotes().getAvisNote(0).getReponse());

        ProductReviewComputation reviewController = new ProductReviewComputation(OUTPUTSOURCE);

        for (int i = 0; i < 1; i++) {
            String sourceFile = INPUTSOURCE + "avis_h037.xml";
            reviewController.run(sourceFile);
        }

        long endTime = System.currentTimeMillis();
        long duree = endTime - startTime;
        System.out.println("Passed time: " + duree);
    }

}
