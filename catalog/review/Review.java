/*
 * Copyright Travelsoft, 2014.
 */
package com.travelsoft.lastminute.catalog.review;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Product review.
 */
public class Review {
    /** Review ID. */
    private int identify;
    /** Client's surname. */
    private String lastName;
    /** Client's name. */
    private String firstName;
    /** Client's age. */
    private String age;
    /** Client's context (alone, pairs,familly, etc). */
    private String context;
    /** Review's date. */
    private Date date;
    /** Review's score. */
    private float score;
    /** Maximal score on the review scale. */
    private float maxiScore;
    /** Positive point. */
    private String posPoint;
    /** Negative point. */
    private String negPoint;
    /** List of notes on different criteria. */
    private List<NoteList> noteList = new ArrayList<NoteList>();

    /**
     * Returns the date.
     *
     * @return {@link String}
     */
    public String getDateString() {
        String d = "";
        if (date != null) {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            d = df.format(date);
        }
        return d;
    }
     /**
     * Returns the lastName.
     *
     * @return {@link String}
     */
    public String getLastName() {
        return lastName;
    }


    /**
     * Returns the firstName.
     *
     * @return {@link String}
     */
    public String getFirstName() {
        return firstName;
    }


    /**
     * Returns the age.
     *
     * @return {@link String}
     */
    public String getAge() {
        return age;
    }


    /**
     * Returns the context.
     *
     * @return {@link String}
     */
    public String getContext() {
        return context;
    }


    /**
     * Returns the date.
     *
     * @return {@link Date}
     */
    public Date getDate() {
        return date;
    }


    /**
     * Returns the score.
     *
     * @return {@link float}
     */
    public float getScore() {
        return score;
    }

    /**
     * Returns the globalScore.
     *
     * @return {@link String}
     */
    public String getStringScore() {
        DecimalFormat df = new DecimalFormat("#.#");
        return df.format(score);
    }

    /**
     * Returns the maxiScore.
     *
     * @return {@link float}
     */
    public float getMaxiScore() {
        return maxiScore;
    }


    /**
     * Returns the posPoint.
     *
     * @return {@link String}
     */
    public String getPosPoint() {
        return posPoint;
    }


    /**
     * Returns the negPoint.
     *
     * @return {@link String}
     */
    public String getNegPoint() {
        return negPoint;
    }


    /**
     * Returns the noteList.
     *
     * @return {@link List<NoteList>}
     */
    public List<NoteList> getNoteList() {
        return noteList;
    }


    /**
     * Sets the lastName.
     *
     * @param lastName The lastName to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    /**
     * Sets the firstName.
     *
     * @param firstName The firstName to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    /**
     * Sets the age.
     *
     * @param age The age to set.
     */
    public void setAge(String age) {
        this.age = age;
    }


    /**
     * Sets the context.
     *
     * @param context The context to set.
     */
    public void setContext(String context) {
        this.context = context;
    }


    /**
     * Sets the date.
     *
     * @param date The date to set.
     */
    public void setDate(Date date) {
        this.date = date;
    }


    /**
     * Sets the score.
     *
     * @param score The score to set.
     */
    public void setScore(float score) {
        this.score = score;
    }


    /**
     * Sets the maxiScore.
     *
     * @param maxiScore The maxiScore to set.
     */
    public void setMaxiScore(float maxiScore) {
        this.maxiScore = maxiScore;
    }


    /**
     * Sets the posPoint.
     *
     * @param posPoint The posPoint to set.
     */
    public void setPosPoint(String posPoint) {
        this.posPoint = posPoint;
    }


    /**
     * Sets the negPoint.
     *
     * @param negPoint The negPoint to set.
     */
    public void setNegPoint(String negPoint) {
        this.negPoint = negPoint;
    }


    /**
     * Sets the noteList.
     *
     * @param noteList The noteList to set.
     */
    public void setNoteList(List<NoteList> noteList) {
        this.noteList = noteList;
    }




    /**
     * Returns the identify.
     *
     * @return {@link int}
     */
    public int getIdentify() {
        return identify;
    }




    /**
     * Sets the identify.
     *
     * @param identify The identify to set.
     */
    public void setIdentify(int identify) {
        this.identify = identify;
    }
}
