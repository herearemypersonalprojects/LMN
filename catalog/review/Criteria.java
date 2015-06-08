package com.travelsoft.lastminute.catalog.review;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * Review criteria.
 *
 */
public class Criteria {
    /** List of notes on different type of criteria. */
    private List<ReviewNote> criterionNote = new ArrayList<ReviewNote>();



    /**
     * Returns the criterionNote.
     *
     * @return {@link List<ReviewNote>}
     */
    public List<ReviewNote> getReviewNote() {
        return criterionNote;
    }



    /**
     * Sets the criterionNote.
     *
     * @param criterionNote The criterionNote to set.
     */
    public void setReviewNote(List<ReviewNote> criterionNote) {
        this.criterionNote = criterionNote;
    }
}
