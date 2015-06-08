/*
 * Copyright Travelsoft, 2014.
 */
package com.travelsoft.lastminute.catalog.review;



/**
 * Review note.
 *
 */
public class ReviewNote {
    /** Average score. */
    private String value;
    /** The name of criterion. */
    private String criterionName;
    /** Criterion's title. */
    private String title;
    /** Client's answer for this criterion. */
    private String answer;
    /** Maximal score on the review note scale. */
    private float maxiScore;
    /** Criterion's type. */
    private String type;


    /**
     * Returns the criterionName.
     *
     * @return {@link String}
     */
    public String getCriterionName() {
        return criterionName;
    }


    /**
     * Returns the title.
     *
     * @return {@link String}
     */
    public String getTitle() {
        return title;
    }


    /**
     * Returns the answer.
     *
     * @return {@link String}
     */
    public String getAnswer() {
        return answer;
    }


    /**
     * Returns the maxiScore.
     *
     * @return {@link String}
     */
    public float getMaxiScore() {
        return maxiScore;
    }


    /**
     * Returns the type.
     *
     * @return {@link String}
     */
    public String getType() {
        return type;
    }


    /**
     * Sets the criterionName.
     *
     * @param criterionName The criterionName to set.
     */
    public void setCriterionName(String criterionName) {
        this.criterionName = criterionName;
    }


    /**
     * Sets the title.
     *
     * @param title The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }


    /**
     * Sets the answer.
     *
     * @param answer The answer to set.
     */
    public void setAnswer(String answer) {
        this.answer = answer;
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
     * Sets the type.
     *
     * @param type The type to set.
     */
    public void setType(String type) {
        this.type = type;
    }




    /**
     * Returns the value.
     *
     * @return {@link String}
     */
    public String getValue() {
        return value;
    }




    /**
     * Sets the value.
     *
     * @param value The value to set.
     */
    public void setValue(String value) {
        this.value = value;
    }
}
