/*
 * Copyright Travelsoft, 2014.
 */
package com.travelsoft.lastminute.catalog.review;

import java.io.FileReader;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;



/**
 * Using XStream tool to convert
 * the product reviews in XML format to Java classes.
 *
 */
public class XStreamModel {
    /**
     *
     * Method to get review Java class from xml file.
     *
     * @param fileName a string
     * @throws Exception when the file is not found
     * @return a class of product reviews
     */
    public static ProductReviews getProductReviews(String fileName) throws Exception {
        /** read xml file. */
        FileReader fileReader = new FileReader(fileName);

        /** initialize the stream tool. */
        XStream xstream = new XStream();


        /** Mapping from the Produit XML to ProductReviews class */
        xstream.alias("Produit", ProductReviews.class);
        xstream.aliasField("code", ProductReviews.class, "code");
        xstream.aliasField("nom", ProductReviews.class, "title");
        xstream.aliasField("pays", ProductReviews.class, "country");
        xstream.aliasField("nbAvis", ProductReviews.class, "nbReviews");
        xstream.aliasField("noteGloable", ProductReviews.class, "globalScore");
        xstream.aliasField("noteMini", ProductReviews.class, "miniScore");
        xstream.aliasField("noteMax", ProductReviews.class, "maxiScore");
        xstream.aliasField("moyMini", ProductReviews.class, "avgMini");
        xstream.aliasField("moyMax", ProductReviews.class, "avgMax");
        xstream.aliasField("Criteres", ProductReviews.class, "criteria");

        xstream.addImplicitCollection(ProductReviews.class, "reviews");

        /** Mapping from Criteres XML to the Criteria class */
        xstream.alias("Criteres", Criteria.class);
        xstream.addImplicitCollection(Criteria.class, "criterionNote");

        /** Mapping from Avis XML to the Review class */
        xstream.alias("Avis", Review.class);
        xstream.aliasField("nom", Review.class, "lastName");
        xstream.aliasField("prenom", Review.class, "firstName");
        xstream.aliasField("age", Review.class, "age");
        xstream.aliasField("contexte", Review.class, "context");
        xstream.aliasField("date", Review.class, "date");
        xstream.aliasField("note", Review.class, "score");
        xstream.aliasField("noteMax", Review.class, "maxiScore");
        xstream.aliasField("pointPos", Review.class, "posPoint");
        xstream.aliasField("pointneg", Review.class, "negPoint");

        xstream.addImplicitCollection(Review.class, "noteList");

        /** Mapping from Notes XML to NoteList class */
        xstream.alias("Notes", NoteList.class);
        xstream.addImplicitCollection(NoteList.class, "reviewNotes");

        /** Mapping from Note XML to ReviewNote class */
        xstream.alias("note", ReviewNote.class);
        xstream.aliasField("Valeur", ReviewNote.class, "value");
        xstream.aliasField("NoteMax", ReviewNote.class, "maxiScore");
        xstream.aliasField("Critere", ReviewNote.class, "criterionName");
        xstream.aliasField("Nom", ReviewNote.class, "title");
        xstream.aliasField("critere", ReviewNote.class, "criterionName");
        xstream.aliasField("nom", ReviewNote.class, "title");
        xstream.aliasField("reponse", ReviewNote.class, "answer");
        xstream.aliasField("type", ReviewNote.class, "type");

        /** Using DateConverter */
        xstream.registerConverter(new DateConverter("yyyy-MM-dd", new String[] {}));

        ProductReviews productReviews = (ProductReviews) xstream.fromXML(fileReader);

        return productReviews;
    }
}
