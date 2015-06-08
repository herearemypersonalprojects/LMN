/*
 *
 * Copyright Travelsoft, 2013.
 */
package com.travelsoft.lastminute.catalog.review;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * <p>Titre : ReviewDateComparator.</p>
 * Description : Fill the clients' opinion.
 * <p>Copyright : Copyright (c) 2013</p>
 * <p>Company : Travelsoft</p>
 * @author Quoc-Anh Le
 * @version
 *
 */
class ReviewDateComparator implements Comparator<Review> {

    /**
     *
     * Overriding method.
     *
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     * @param o1 sd
     * @param o2 sd
     * @return 0 if they are egal or 1 if the first is newer than the second
     */
    public int compare(Review o1, Review o2) {
        if (o1.getDate().compareTo(o2.getDate()) > 0) {
            return 1;
        } else if (o1.getDate().compareTo(o2.getDate()) < 0) {
            return -1;
        }
        return 0;
    }

}
/**
 *
 * OldestReviewDateComparator.
 *
 */
class OldestReviewDateComparator implements Comparator<Review> {
    /**
    *
    * Overriding method.
    *
    * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
    * @param o1 sd
    * @param o2 sd
    * @return 0 if they are egal or 1 if the first is newer than the second
    */
    public int compare(Review o1, Review o2) {
        if (o1.getDate().compareTo(o2.getDate()) > 0) {
            return 1;
        } else if (o1.getDate().compareTo(o2.getDate()) < 0) {
            return -1;
        }
        return 0;
    }
}
/**
*
* LatestReviewDateComparator.
*
*/
class LatestReviewDateComparator implements Comparator<Review> {
    /**
    *
    * Overriding method.
    *
    * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
    * @param o1 sd
    * @param o2 sd
    * @return 0 if they are egal or 1 if the first is newer than the second
    */
    public int compare(Review o1, Review o2) {
        if (o1.getDate().compareTo(o2.getDate()) < 0) {
            return 1;
        } else if (o1.getDate().compareTo(o2.getDate()) > 0) {
            return -1;
        }
        return 0;
    }
}
/**
 *
 * ReviewNoteComparator.
 *
 */
class ReviewNoteComparator implements Comparator<Review> {
    /**
    *
    * Overriding method.
    *
    * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
    * @param o1 sd
    * @param o2 sd
    * @return 0 if they are egal or 1 if the first is newer than the second
    */
    public int compare(Review o1, Review o2) {
        if (o1.getScore() > o2.getScore()) {
            return 1;
        } else if (o1.getScore() < o2.getScore()) {
            return -1;
        }
        return 0;
    }
}
/**
*
* LowestReviewNoteComparator.
*
*/
class LowestReviewNoteComparator implements Comparator<Review> {
    /**
    *
    * Overriding method.
    *
    * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
    * @param o1 sd
    * @param o2 sd
    * @return 0 if they are egal or 1 if the first is newer than the second
    */
    public int compare(Review o1, Review o2) {
        if (o1.getScore() > o2.getScore()) {
            return 1;
        } else if (o1.getScore() < o2.getScore()) {
            return -1;
        }
        return 0;
    }
}
/**
*
* BestReviewNoteComparator.
*
*/
class BestReviewNoteComparator implements Comparator<Review> {
    /**
    *
    * Overriding method.
    *
    * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
    * @param o1 sd
    * @param o2 sd
    * @return 0 if they are egal or 1 if the first is newer than the second
    */
    public int compare(Review o1, Review o2) {
        if (o1.getScore() < o2.getScore()) {
            return 1;
        } else if (o1.getScore() > o2.getScore()) {
            return -1;
        }
        return 0;
    }
}
/**
 *
 * ReviewSorter.
 *
 */
public class ReviewSorter {
    /**
     *
     * Method to get the latest review.
     *
     * @param pReview all review.
     *
     * @return list of review
     */
    public static List<Review> getLastestReviews(ProductReviews pReview) {
        List<Review> lastestReviewList = pReview.getReviews();
        Collections.sort(lastestReviewList, new LatestReviewDateComparator());
        return lastestReviewList;
    }

    /**
    *
    * Method to get a number of the latest review.
    *
    * @param pReview all review.
    *
    * @return list of review
    */
    public static List<Review> getLastestNbReviews(ProductReviews pReview) {
        List<Review> lastestReviewList = pReview.getReviews();
        Collections.sort(lastestReviewList, new ReviewDateComparator());
        return getNbReviews(lastestReviewList, pReview.getNbReviews(), true);
    }

    /**
    *
    * Method to get the oldest review.
    *
    * @param pReview all review.
    *
    * @return list of review
    */
    public static List<Review> getOldestReviews(ProductReviews pReview) {
        List<Review> lastestReviewList = pReview.getReviews();
        Collections.sort(lastestReviewList, new OldestReviewDateComparator());
        return lastestReviewList;
    }
    /**
    *
    * Method to get a number of the oldest review.
    *
    * @param pReview all review.
    *
    * @return list of review
    */
    public static List<Review> getOldestNbReviews(ProductReviews pReview) {
        List<Review> lastestReviewList = pReview.getReviews();
        Collections.sort(lastestReviewList, new ReviewDateComparator());
        return getNbReviews(lastestReviewList, pReview.getNbReviews(), false);
    }
    /**
    *
    * Method to get the best review.
    *
    * @param pReview all review.
    *
    * @return list of review
    */
    public static List<Review> getBestReviews(ProductReviews pReview) {
        List<Review> bestReviewList = pReview.getReviews();
        Collections.sort(bestReviewList, new BestReviewNoteComparator());
        return bestReviewList;
    }
    /**
    *
    * Method to get a number of the best review.
    *
    * @param pReview all review.
    *
    * @return list of review
    */
    public static List<Review> getBestNbReviews(ProductReviews pReview) {
        List<Review> bestReviewList = pReview.getReviews();
        Collections.sort(bestReviewList, new ReviewNoteComparator());
        return getNbReviews(bestReviewList, pReview.getNbReviews(), true);
    }
    /**
    *
    * Method to get the lowest review.
    *
    * @param pReview all review.
    *
    * @return list of review
    */
    public static List<Review> getLowestReviews(ProductReviews pReview) {
        List<Review> bestReviewList = pReview.getReviews();
        Collections.sort(bestReviewList, new LowestReviewNoteComparator());
        return bestReviewList;
    }
    /**
    *
    * Method to get a number of the lowest review.
    *
    * @param pReview all review.
    *
    * @return list of review
    */
    public static List<Review> getLowestNbReviews(ProductReviews pReview) {
        List<Review> bestReviewList = pReview.getReviews();
        Collections.sort(bestReviewList, new ReviewNoteComparator());
        return getNbReviews(bestReviewList, pReview.getNbReviews(), false);
    }
    /**
     *
     * Method to get num of reviews from the given list.
     *
     * @param givenList the given list.
     * @param num number of reviews
     * @param invert from the top or from the bottom of the array.
     * @return list of reviews.
     */
    private static List<Review> getNbReviews(List<Review> givenList, int num, boolean invert) {
        List<Review> al = new ArrayList<Review>();
        int n = givenList.size() > num ? num : givenList.size();
        if (invert) {
            for (int i = givenList.size() - 1; i >= givenList.size() - n; i--) {
                al.add(givenList.get(i));
            }
        } else {
            for (int i = 0; i < n; i++) {
                al.add(givenList.get(i));
            }
        }
        return al;
    }

}
