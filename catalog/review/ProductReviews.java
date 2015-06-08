package com.travelsoft.lastminute.catalog.review;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * List of reviews for one product in only one file.
 *
 */
public class ProductReviews {
    /** Product's external code. */
    private String code;
    /** Product's title. */
    private String title;
    /** contry. */
    private String country;
    /** Rate. */
    private int rate;
    /** Number of reviews. */
    private int nbReviews;
    /** Average score. */
    private float globalScore;
    /** Minimal score in the review scale. */
    private float miniScore;
    /** Maximal score in the review scale. */
    private float maxiScore;
    /** Average minimal scores. */
    private float avgMini;
    /** Average maximal scores. */
    private float avgMax;
    /** List of criteria. */
    private Criteria criteria;
    /** List of reviews. */
    private List<Review> reviews = new ArrayList<Review>();

    /** Total number of reviews for family travel type. */
    private int nbReviewTravelFamily;
    /** Total number of reviews for family child travel type. */
    private int nbReviewTravelFamilyChild;
    /** Total number of reviews for pair travel type. */
    private int nbReviewTravelPair;
    /** Total number of reviews for friend travel type. */
    private int nbReviewTravelFriend;
    /** Total number of reviews for senior travel type. */
    private int nbReviewSenior;
    /** Total number of reviews for alone travel type. */
    private int nbReviewTravelAlone;
    /**
     * Returns the code.
     *
     * @return {@link String}
     */
    public String getCode() {
        return code;
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
     * Returns the country.
     *
     * @return {@link String}
     */
    public String getCountry() {
        return country;
    }


    /**
     * Returns the nbReviews.
     *
     * @return {@link int}
     */
    public int getNbReviews() {
        return nbReviews;
    }


    /**
     * Returns the globalScore.
     *
     * @return {@link String}
     */
    public String getStringNote() {
        DecimalFormat df = new DecimalFormat("#.#");
        return df.format(globalScore);
    }

    /**
     * Returns the globalScore.
     *
     * @return {@link float}
     */
    public float getGlobalScore() {
        return globalScore;
    }

    /**
     * Returns the miniScore.
     *
     * @return {@link float}
     */
    public float getMiniScore() {
        return miniScore;
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
     * Returns the avgMini.
     *
     * @return {@link float}
     */
    public float getAvgMini() {
        return avgMini;
    }


    /**
     * Returns the avgMax.
     *
     * @return {@link float}
     */
    public float getAvgMax() {
        return avgMax;
    }


    /**
     * Returns the criteria.
     *
     * @return {@link Criteria}
     */
    public Criteria getCriteria() {
        return criteria;
    }


    /**
     * Returns the reviews.
     *
     * @return {@link List<Review>}
     */
    public List<Review> getReviews() {
        return reviews;
    }


    /**
     * Sets the code.
     *
     * @param code The code to set.
     */
    public void setCode(String code) {
        this.code = code;
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
     * Sets the country.
     *
     * @param country The country to set.
     */
    public void setCountry(String country) {
        this.country = country;
    }


    /**
     * Sets the nbReviews.
     *
     * @param nbReviews The nbReviews to set.
     */
    public void setNbReviews(int nbReviews) {
        this.nbReviews = nbReviews;
    }


    /**
     * Sets the globalScore.
     *
     * @param globalScore The globalScore to set.
     */
    public void setGlobalScore(float globalScore) {
        this.globalScore = globalScore;
    }


    /**
     * Sets the miniScore.
     *
     * @param miniScore The miniScore to set.
     */
    public void setMiniScore(float miniScore) {
        this.miniScore = miniScore;
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
     * Sets the avgMini.
     *
     * @param avgMini The avgMini to set.
     */
    public void setAvgMini(float avgMini) {
        this.avgMini = avgMini;
    }


    /**
     * Sets the avgMax.
     *
     * @param avgMax The avgMax to set.
     */
    public void setAvgMax(float avgMax) {
        this.avgMax = avgMax;
    }


    /**
     * Sets the criteria.
     *
     * @param criteria The criteria to set.
     */
    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }


    /**
     * Sets the reviews.
     *
     * @param reviews The reviews to set.
     */
    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }




    /**
     * Returns the nbReviewTravelFamily.
     *
     * @return {@link int}
     */
    public int getNbReviewTravelFamily() {
        return nbReviewTravelFamily;
    }




    /**
     * Returns the nbReviewTravelFamilyChild.
     *
     * @return {@link int}
     */
    public int getNbReviewTravelFamilyChild() {
        return nbReviewTravelFamilyChild;
    }




    /**
     * Returns the nbReviewTravelPair.
     *
     * @return {@link int}
     */
    public int getNbReviewTravelPair() {
        return nbReviewTravelPair;
    }




    /**
     * Returns the nbReviewTravelFriend.
     *
     * @return {@link int}
     */
    public int getNbReviewTravelFriend() {
        return nbReviewTravelFriend;
    }




    /**
     * Returns the nbReviewSenior.
     *
     * @return {@link int}
     */
    public int getNbReviewSenior() {
        return nbReviewSenior;
    }




    /**
     * Returns the nbReviewTravelAlone.
     *
     * @return {@link int}
     */
    public int getNbReviewTravelAlone() {
        return nbReviewTravelAlone;
    }




    /**
     * Sets the nbReviewTravelFamily.
     *
     * @param nbReviewTravelFamily The nbReviewTravelFamily to set.
     */
    public void setNbReviewTravelFamily(int nbReviewTravelFamily) {
        this.nbReviewTravelFamily = nbReviewTravelFamily;
    }




    /**
     * Sets the nbReviewTravelFamilyChild.
     *
     * @param nbReviewTravelFamilyChild The nbReviewTravelFamilyChild to set.
     */
    public void setNbReviewTravelFamilyChild(int nbReviewTravelFamilyChild) {
        this.nbReviewTravelFamilyChild = nbReviewTravelFamilyChild;
    }




    /**
     * Sets the nbReviewTravelPair.
     *
     * @param nbReviewTravelPair The nbReviewTravelPair to set.
     */
    public void setNbReviewTravelPair(int nbReviewTravelPair) {
        this.nbReviewTravelPair = nbReviewTravelPair;
    }




    /**
     * Sets the nbReviewTravelFriend.
     *
     * @param nbReviewTravelFriend The nbReviewTravelFriend to set.
     */
    public void setNbReviewTravelFriend(int nbReviewTravelFriend) {
        this.nbReviewTravelFriend = nbReviewTravelFriend;
    }




    /**
     * Sets the nbReviewSenior.
     *
     * @param nbReviewSenior The nbReviewSenior to set.
     */
    public void setNbReviewSenior(int nbReviewSenior) {
        this.nbReviewSenior = nbReviewSenior;
    }




    /**
     * Sets the nbReviewTravelAlone.
     *
     * @param nbReviewTravelAlone The nbReviewTravelAlone to set.
     */
    public void setNbReviewTravelAlone(int nbReviewTravelAlone) {
        this.nbReviewTravelAlone = nbReviewTravelAlone;
    }




    /**
     * Returns the rate.
     *
     * @return {@link int}
     */
    public int getRate() {
        return rate;
    }




    /**
     * Sets the rate.
     *
     * @param rate The rate to set.
     */
    public void setRate(int rate) {
        this.rate = rate;
    }
}
