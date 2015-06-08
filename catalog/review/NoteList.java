package com.travelsoft.lastminute.catalog.review;

import java.util.ArrayList;
import java.util.List;



/**
*
* List of review notes.
*
*/
public class NoteList {
   /** for each review, there are a list of notes on different criteria. */
    private List<ReviewNote> reviewNotes = new ArrayList<ReviewNote>();



   /**
    * Returns the reviewNotes.
    *
    * @return {@link List<ReviewNote>}
    */
    public List<ReviewNote> getReviewNotes() {
        return reviewNotes;
    }



   /**
    * Sets the reviewNotes.
    *
    * @param reviewNotes The reviewNotes to set.
    */
    public void setReviewNotes(List<ReviewNote> reviewNotes) {
        this.reviewNotes = reviewNotes;
    }
}