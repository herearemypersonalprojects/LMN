package com.travelsoft.lastminute.catalog.review;

import java.text.DecimalFormat;



/**
 * ReviewRateController.
 * .
 *
 */
public class ClientReviewController {

    /** */
    private  int nbReview;
    /** */
    private  float noteGloable;
    /** */
    private  float noteMax;
    /** */
    private  int nbReviewTravelFamily;
    /** */
    private  int nbReviewTravelFamilyChild;
    /** */
    private  int nbReviewTravelPair;
    /** */
    private  int nbReviewTravelFriend;
    /** */
    private  int nbReviewSenior;
    /** */
    private  int nbReviewTravelAlone;
    /** */
    private  int nbCurReview;

    /**
     * Concernant le séjour du client.
     * */
    /** Note moyen pour le service/le personnel de l'hôtel. */
    private  float hotelService;
    /** Note moyen pour le cadre de l'hôtel. */
    private  float hotelCadre;
    /** Note moyen pour la situation géographique de l'hôtel. */
    private  float hotelLocation;
    /** Note moyen pour la chambre/logement. */
    private  float hotelChambre;
    /** Note moyen pour la restauration. */
    private  float hotelRestauration;
    /** Note moyen pour l'animation/les activités. */
    private  float hotelActivites;
    /** Note moyen pour le rapport qualité/prix. */
    private  float hotelPrix;
    /** Note moyen pour les équippements de l'hôtel (piscine, salle de sport...). */
    private  float hotelEquipements;
    /** Note moyen pour les activités pour enfants. */
    private  float hotelEnfantActivites;
    /** Note moyen pour les excursions. */
    private  float hotelExcursions;


    /** Note moyen pour le service/le personnel de l'hôtel. */
    private  int nbHotelService;
    /** Note moyen pour le cadre de l'hôtel. */
    private  int nbHotelCadre;
    /** Note moyen pour la situation géographique de l'hôtel. */
    private  int nbHotelLocation;
    /** Note moyen pour la chambre/logement. */
    private  int nbHotelChambre;
    /** Note moyen pour la restauration. */
    private  int nbHotelRestauration;
    /** Note moyen pour l'animation/les activités. */
    private  int nbHotelActivites;
    /** Note moyen pour le rapport qualité/prix. */
    private  int nbHotelPrix;
    /** Note moyen pour les équippements de l'hôtel (piscine, salle de sport...). */
    private  int nbHotelEquipements;
    /** Note moyen pour les activités pour enfants. */
    private  int nbHotelEnfantActivites;
    /** Note moyen pour les excursions. */
    private  int nbHotelExcursions;

    /**
     * Prestations complémentaires
     * */
    /** Note moyen pour le service de vol. */
    private  float hotelVol;
    /** Note moyen pour le service de transfert. */
    private  float hotelTransfert;
    /** Note moyen pour le service de correspondant local. */
    private  float hotelCorrespondantLocal;

    /** Note moyen pour le service de vol. */
    private  int intHotelVol;
    /** Note moyen pour le service de transfert. */
    private  int intHotelTransfert;
    /** Note moyen pour le service de correspondant local. */
    private  int intHotelCorrespondantLocal;

    /**
     * A quel type de clientèle nos clients recommandent ce séjour?
     * */
    /** Note moyen des recommends en familles. */
    private  float recommendFamilles;
    /** Note moyen des recommends en couple. */
    private  float recommendCouples;
    /** Note moyen des recommends des célibataires. */
    private  float recommendCelibataires;
    /** Note moyen des recommends des personnes agées. */
    private  float recommendPersonnesAgees;
    /** Note moyen des recommends des jeunes mariés. */
    private  float recommendJeunesMaries;
    /** Note moyen des recommends des amis/groupes. */
    private  float recommendAmisGroupes;
    /** Note moyen des recommends des étudiants. */
    private  float recommendEtudiants;
    /** Note moyen des absences de recommends. */
    private  float recommendAbsence;

    /** Note moyen des recommends en familles. */
    private  int intRecommendFamilles;
    /** Note moyen des intRecommends en couple. */
    private  int intRecommendCouples;
    /** Note moyen des intRecommends des célibataires. */
    private  int intRecommendCelibataires;
    /** Note moyen des intRecommends des personnes agées. */
    private  int intRecommendPersonnesAgees;
    /** Note moyen des intRecommends des jeunes mariés. */
    private  int intRecommendJeunesMaries;
    /** Note moyen des intRecommends des amis/groupes. */
    private  int intRecommendAmisGroupes;
    /** Note moyen des intRecommends des étudiants. */
    private  int intRecommendEtudiants;
    /** Note moyen des absences de recommends. */
    private  int intRecommendAbsence;


    /** Percentage (%) des clients recommanderaient ce séjour à un proche. */
    private  int hotelRecommend;


    /**
     *
     * Method to reset all variables.
     * @param a avis
     *
     */
    public  void reset(ProductReviews a) {
        nbReview = a.getNbReviews();
        noteGloable = a.getGlobalScore();
        noteMax = a.getMaxiScore();

        nbReviewTravelFamily = a.getNbReviewTravelFamily();
        nbReviewTravelFamilyChild = a.getNbReviewTravelFamilyChild();
        nbReviewTravelPair = a.getNbReviewTravelPair();
        nbReviewTravelFriend = a.getNbReviewTravelFriend();
        nbReviewSenior = a.getNbReviewSenior();
        nbReviewTravelAlone = a.getNbReviewTravelAlone();
    }

    /**
     *
     * Method to compute.
     *
     */
    public  void compute() {
        if (nbHotelService > 0) {
            hotelService = hotelService / nbHotelService;
        }
        if (nbHotelCadre > 0) {
            hotelCadre = hotelCadre / nbHotelCadre;
        }
        if (nbHotelLocation > 0) {
            hotelLocation = hotelLocation / nbHotelLocation;
        }
        if (nbHotelChambre > 0) {
            hotelChambre = hotelChambre / nbHotelChambre;
        }
        if (nbHotelRestauration > 0) {
            hotelRestauration = hotelRestauration / nbHotelRestauration;
        }
        if (nbHotelActivites > 0) {
            hotelActivites = hotelActivites / nbHotelActivites;
        }
        if (nbHotelPrix > 0) {
            hotelPrix = hotelPrix / nbHotelPrix;
        }
        if (nbHotelEquipements > 0) {
            hotelEquipements = hotelEquipements / nbHotelEquipements;
        }
        if (nbHotelEnfantActivites > 0) {
            hotelEnfantActivites = hotelEnfantActivites / nbHotelEnfantActivites;
        }
        if (nbHotelExcursions > 0) {
            hotelExcursions = hotelExcursions / nbHotelExcursions;
        }

        /** */
        if (intHotelVol > 0) {
            hotelVol = hotelVol / intHotelVol;
        }
        if (intHotelTransfert > 0) {
            hotelTransfert = hotelTransfert / intHotelTransfert;
        }
        if (intHotelCorrespondantLocal > 0) {
            hotelCorrespondantLocal = hotelCorrespondantLocal / intHotelCorrespondantLocal;
        }

        /** */
        if (intRecommendFamilles > 0) {
            recommendFamilles = recommendFamilles / intRecommendFamilles;
        }

        if (intRecommendCouples > 0) {
            recommendCouples = recommendCouples / intRecommendCouples;
        }
        if (intRecommendCelibataires > 0) {
            recommendCelibataires = recommendCelibataires / intRecommendCelibataires;
        }
        if (intRecommendPersonnesAgees > 0) {
            recommendPersonnesAgees = recommendPersonnesAgees / intRecommendPersonnesAgees;
        }
        if (intRecommendJeunesMaries > 0) {
             recommendJeunesMaries = recommendJeunesMaries / intRecommendJeunesMaries;
        }
        if (intRecommendAmisGroupes > 0) {
            recommendAmisGroupes = recommendAmisGroupes / intRecommendAmisGroupes;
        }
        if (intRecommendEtudiants > 0) {
            recommendEtudiants = recommendEtudiants / intRecommendEtudiants;
        }
        if (intRecommendAbsence > 0) {
            recommendAbsence = recommendAbsence / intRecommendAbsence;
        }

    }
    /**
     * Returns the hotelService.
     *
     * @return {@link float}
     */
    public  float getHotelService() {
        return hotelService;
    }



    /**
     * Returns the hotelCadre.
     *
     * @return {@link float}
     */
    public  float getHotelCadre() {
        return hotelCadre;
    }



    /**
     * Returns the hotelLocation.
     *
     * @return {@link float}
     */
    public  float getHotelLocation() {
        return hotelLocation;
    }



    /**
     * Returns the hotelChambre.
     *
     * @return {@link float}
     */
    public  float getHotelChambre() {
        return hotelChambre;
    }



    /**
     * Returns the hotelRestauration.
     *
     * @return {@link float}
     */
    public  float getHotelRestauration() {
        return hotelRestauration;
    }



    /**
     * Returns the hotelActivites.
     *
     * @return {@link float}
     */
    public  float getHotelActivites() {
        return hotelActivites;
    }



    /**
     * Returns the hotelPrix.
     *
     * @return {@link float}
     */
    public  float getHotelPrix() {
        return hotelPrix;
    }



    /**
     * Returns the hotelEquipements.
     *
     * @return {@link float}
     */
    public  float getHotelEquipements() {
        return hotelEquipements;
    }



    /**
     * Returns the hotelEnfantActivites.
     *
     * @return {@link float}
     */
    public  float getHotelEnfantActivites() {
        return hotelEnfantActivites;
    }



    /**
     * Returns the hotelExcursions.
     *
     * @return {@link float}
     */
    public  float getHotelExcursions() {
        return hotelExcursions;
    }



    /**
     * Returns the hotelVol.
     *
     * @return {@link float}
     */
    public  float getHotelVol() {
        return hotelVol;
    }



    /**
     * Returns the hotelTransfert.
     *
     * @return {@link float}
     */
    public  float getHotelTransfert() {
        return hotelTransfert;
    }



    /**
     * Returns the hotelCorrespondantLocal.
     *
     * @return {@link float}
     */
    public  float getHotelCorrespondantLocal() {
        return hotelCorrespondantLocal;
    }



    /**
     * Returns the recommendFamilles.
     *
     * @return {@link float}
     */
    public  float getRecommendFamilles() {
        return recommendFamilles;
    }



    /**
     * Returns the recommendCouples.
     *
     * @return {@link float}
     */
    public  float getRecommendCouples() {
        return recommendCouples;
    }



    /**
     * Returns the recommendCelibataires.
     *
     * @return {@link float}
     */
    public  float getRecommendCelibataires() {
        return recommendCelibataires;
    }



    /**
     * Returns the recommendPersonnesAgees.
     *
     * @return {@link float}
     */
    public  float getRecommendPersonnesAgees() {
        return recommendPersonnesAgees;
    }



    /**
     * Returns the recommendJeunesMaries.
     *
     * @return {@link float}
     */
    public  float getRecommendJeunesMaries() {
        return recommendJeunesMaries;
    }



    /**
     * Returns the recommendAmisGroupes.
     *
     * @return {@link float}
     */
    public  float getRecommendAmisGroupes() {
        return recommendAmisGroupes;
    }



    /**
     * Returns the recommendEtudiants.
     *
     * @return {@link float}
     */
    public  float getRecommendEtudiants() {
        return recommendEtudiants;
    }



    /**
     * Returns the recommendAbsence.
     *
     * @return {@link float}
     */
    public  float getRecommendAbsence() {
        return recommendAbsence;
    }



    /**
     * Returns the hotelRecommend.
     *
     * @return {@link int}
     */
    public  int getHotelRecommend() {
        return hotelRecommend;
    }



    /**
     * Sets the hotelService.
     *
     * @param s The hotelService to set.
     */
    public  void setHotelService(float s) {
        this.hotelService = this.hotelService + s;
        nbHotelService++;
    }

    /**
     * Sets the hotelService.
     *
     * @param s The hotelService to set.
     */
    public  void addHotelService(String s) {
        this.hotelService = this.hotelService + ReviewUtils.getFloat(s);
    }

    /**
     * Sets the hotelCadre.
     *
     * @param c The hotelCadre to set.
     */
    public  void setHotelCadre(float c) {
        this.hotelCadre = this.hotelCadre + c;
        nbHotelCadre++;
    }



    /**
     * Sets the hotelLocation.
     *
     * @param l The hotelLocation to set.
     */
    public  void setHotelLocation(float l) {
        this.hotelLocation = this.hotelLocation + l;
        nbHotelLocation++;
    }



    /**
     * Sets the hotelChambre.
     *
     * @param c The hotelChambre to set.
     */
    public  void setHotelChambre(float c) {
        this.hotelChambre = this.hotelChambre + c;
        nbHotelChambre++;
    }



    /**
     * Sets the hotelRestauration.
     *
     * @param r The hotelRestauration to set.
     */
    public  void setHotelRestauration(float r) {
        this.hotelRestauration = this.hotelRestauration + r;
        nbHotelRestauration++;
    }



    /**
     * Sets the hotelActivites.
     *
     * @param a The hotelActivites to set.
     */
    public  void setHotelActivites(float a) {
        this.hotelActivites = this.hotelActivites + a;
        nbHotelActivites++;
    }



    /**
     * Sets the hotelPrix.
     *
     * @param p The hotelPrix to set.
     */
    public  void setHotelPrix(float p) {
        this.hotelPrix = this.hotelPrix + p;
        nbHotelPrix++;
    }



    /**
     * Sets the hotelEquipements.
     *
     * @param e The hotelEquipements to set.
     */
    public  void setHotelEquipements(float e) {
        this.hotelEquipements = this.hotelEquipements + e;
        nbHotelEquipements++;
    }



    /**
     * Sets the hotelEnfantActivites.
     *
     * @param e The hotelEnfantActivites to set.
     */
    public  void setHotelEnfantActivites(float e) {
        this.hotelEnfantActivites = this.hotelEnfantActivites + e;
        nbHotelEnfantActivites++;
    }



    /**
     * Sets the hotelExcursions.
     *
     * @param e The hotelExcursions to set.
     */
    public  void setHotelExcursions(float e) {
        this.hotelExcursions = this.hotelExcursions + e;
        nbHotelExcursions++;
    }



    /**
     * Sets the hotelVol.
     *
     * @param v The hotelVol to set.
     */
    public  void setHotelVol(float v) {
        this.hotelVol = this.hotelVol + v;
        intHotelVol++;
    }



    /**
     * Sets the hotelTransfert.
     *
     * @param t The hotelTransfert to set.
     */
    public  void setHotelTransfert(float t) {
        this.hotelTransfert = this.hotelTransfert + t;
        intHotelTransfert++;
    }



    /**
     * Sets the hotelCorrespondantLocal.
     *
     * @param c The hotelCorrespondantLocal to set.
     */
    public  void setHotelCorrespondantLocal(float c) {
        this.hotelCorrespondantLocal = this.hotelCorrespondantLocal + c;
        intHotelCorrespondantLocal++;
    }



    /**
     * Sets the recommendFamilles.
     *
     * @param r The recommendFamilles to set.
     */
    public  void setRecommendFamilles(float r) {
        this.recommendFamilles = this.recommendFamilles + r;
        intRecommendFamilles++;
        hotelRecommend++;
    }



    /**
     * Sets the recommendCouples.
     *
     * @param r The recommendCouples to set.
     */
    public  void setRecommendCouples(float r) {
        this.recommendCouples = this.recommendCouples + r;
        intRecommendCouples++;
        hotelRecommend++;
    }



    /**
     * Sets the recommendCelibataires.
     *
     * @param r The recommendCelibataires to set.
     */
    public  void setRecommendCelibataires(float r) {
        this.recommendCelibataires = this.recommendCelibataires + r;
        intRecommendCelibataires++;
        hotelRecommend++;
    }



    /**
     * Sets the recommendPersonnesAgees.
     *
     * @param r The recommendPersonnesAgees to set.
     */
    public  void setRecommendPersonnesAgees(float r) {
        this.recommendPersonnesAgees = this.recommendPersonnesAgees + r;
        intRecommendPersonnesAgees++;
        hotelRecommend++;
    }



    /**
     * Sets the recommendJeunesMaries.
     *
     * @param r The recommendJeunesMaries to set.
     */
    public  void setRecommendJeunesMaries(float r) {
        this.recommendJeunesMaries = this.recommendJeunesMaries + r;
        intRecommendJeunesMaries++;
        hotelRecommend++;
    }



    /**
     * Sets the recommendAmisGroupes.
     *
     * @param r The recommendAmisGroupes to set.
     */
    public  void setRecommendAmisGroupes(float r) {
        this.recommendAmisGroupes = this.recommendAmisGroupes + r;
        intRecommendAmisGroupes++;
        hotelRecommend++;
    }



    /**
     * Sets the recommendEtudiants.
     *
     * @param r The recommendEtudiants to set.
     */
    public  void setRecommendEtudiants(float r) {
        this.recommendEtudiants = this.recommendEtudiants + r;
        intRecommendEtudiants++;
        hotelRecommend++;
    }

    /**
     * Sets the recommendEtudiants.
     *
     * @param r The recommendEtudiants to set.
     */
    public  void addRecommendEtudiants(float r) {
        this.recommendEtudiants = this.recommendEtudiants + r;
    }

    /**
     * Sets the recommendAbsence.
     *
     * @param r The recommendAbsence to set.
     */
    public  void setRecommendAbsence(float r) {
        this.recommendAbsence = this.recommendAbsence + r;
        intRecommendAbsence++;
        hotelRecommend++;
    }


    /**
     *
     * Method to compute recommend (%) of clients.
     *
     * @param context type of clients
     * @param globalNote average note
     */
    public  void setHotelRecommend(String context, float globalNote) {
        nbCurReview = nbReview;

        if (context.equalsIgnoreCase(FixedVariables.ENFAMILLE)) {
            nbCurReview = nbReviewTravelFamily;
            if (nbReviewTravelFamily > 0) {
                hotelRecommend = (hotelRecommend * FixedVariables.PERCENT) / nbReviewTravelFamily;
            }
        } else
        if (context.equalsIgnoreCase(FixedVariables.ENFAMILLEBEBE)) {
            nbCurReview = nbReviewTravelFamilyChild;
            if (nbReviewTravelFamilyChild > 0) {
                hotelRecommend = (hotelRecommend * FixedVariables.PERCENT) / nbReviewTravelFamilyChild;
            }
        } else
        if (context.equalsIgnoreCase(FixedVariables.ENCOUPLE)) {
            nbCurReview = nbReviewTravelPair;
            if (nbReviewTravelPair > 0) {
                hotelRecommend = (hotelRecommend * FixedVariables.PERCENT) / nbReviewTravelPair;
            }
        } else
        if (context.equalsIgnoreCase(FixedVariables.SENIORS)) {
            nbCurReview = nbReviewSenior;
            if (nbReviewSenior > 0) {
                hotelRecommend = (hotelRecommend * FixedVariables.PERCENT) / nbReviewSenior;
            }
        } else
        if (context.equalsIgnoreCase(FixedVariables.ENTREAMIS)) {
            nbCurReview = nbReviewTravelFriend;
            if (nbReviewTravelFriend > 0) {
                hotelRecommend = (hotelRecommend * FixedVariables.PERCENT) / nbReviewTravelFriend;
            }
        } else
        if (context.equalsIgnoreCase(FixedVariables.SEULES)) {
            nbCurReview = nbReviewTravelAlone;
            if (nbReviewTravelAlone > 0) {
                hotelRecommend = (hotelRecommend * FixedVariables.PERCENT) / nbReviewTravelAlone;
            }
        } else {
            if (nbReview > 0) {
                hotelRecommend = (hotelRecommend * FixedVariables.PERCENT) / nbReview;
            }
        }

        noteGloable = globalNote / nbCurReview;
    }
    /**
     * Sets the hotelRecommend.
     *
     * @param r The hotelRecommend to set.
     */
    public  void addHotelRecommend(int r) {
        this.hotelRecommend = this.hotelRecommend + r;
    }


    /**
     * Returns the nbHotelService.
     *
     * @return {@link int}
     */
    public  int getNbHotelService() {
        return nbHotelService;
    }





    /**
     * Returns the nbHotelCadre.
     *
     * @return {@link int}
     */
    public  int getNbHotelCadre() {
        return nbHotelCadre;
    }





    /**
     * Returns the nbHotelLocation.
     *
     * @return {@link int}
     */
    public  int getNbHotelLocation() {
        return nbHotelLocation;
    }





    /**
     * Returns the nbHotelChambre.
     *
     * @return {@link int}
     */
    public  int getNbHotelChambre() {
        return nbHotelChambre;
    }





    /**
     * Returns the nbHotelRestauration.
     *
     * @return {@link int}
     */
    public  int getNbHotelRestauration() {
        return nbHotelRestauration;
    }





    /**
     * Returns the nbHotelActivites.
     *
     * @return {@link int}
     */
    public  int getNbHotelActivites() {
        return nbHotelActivites;
    }





    /**
     * Returns the nbHotelPrix.
     *
     * @return {@link int}
     */
    public  int getNbHotelPrix() {
        return nbHotelPrix;
    }





    /**
     * Returns the nbHotelEquipements.
     *
     * @return {@link int}
     */
    public  int getNbHotelEquipements() {
        return nbHotelEquipements;
    }





    /**
     * Returns the nbHotelEnfantActivites.
     *
     * @return {@link int}
     */
    public  int getNbHotelEnfantActivites() {
        return nbHotelEnfantActivites;
    }





    /**
     * Returns the nbHotelExcursions.
     *
     * @return {@link int}
     */
    public  int getNbHotelExcursions() {
        return nbHotelExcursions;
    }







    /**
     * Sets the nbHotelService.
     *
     * @param n The nbHotelService to set.
     */
    public  void addNbHotelService(int n) {
        nbHotelService = nbHotelService + n;
    }




    /**
     * Returns the intHotelVol.
     *
     * @return {@link int}
     */
    public  int getIntHotelVol() {
        return intHotelVol;
    }





    /**
     * Returns the intHotelTransfert.
     *
     * @return {@link int}
     */
    public  int getIntHotelTransfert() {
        return intHotelTransfert;
    }





    /**
     * Returns the intHotelCorrespondantLocal.
     *
     * @return {@link int}
     */
    public  int getIntHotelCorrespondantLocal() {
        return intHotelCorrespondantLocal;
    }





    /**
     * Returns the intRecommendFamilles.
     *
     * @return {@link int}
     */
    public  int getIntRecommendFamilles() {
        return intRecommendFamilles;
    }





    /**
     * Returns the intRecommendCouples.
     *
     * @return {@link int}
     */
    public  int getIntRecommendCouples() {
        return intRecommendCouples;
    }





    /**
     * Returns the intRecommendCelibataires.
     *
     * @return {@link int}
     */
    public  int getIntRecommendCelibataires() {
        return intRecommendCelibataires;
    }





    /**
     * Returns the intRecommendPersonnesAgees.
     *
     * @return {@link int}
     */
    public  int getIntRecommendPersonnesAgees() {
        return intRecommendPersonnesAgees;
    }





    /**
     * Returns the intRecommendJeunesMaries.
     *
     * @return {@link int}
     */
    public  int getIntRecommendJeunesMaries() {
        return intRecommendJeunesMaries;
    }





    /**
     * Returns the intRecommendAmisGroupes.
     *
     * @return {@link int}
     */
    public  int getIntRecommendAmisGroupes() {
        return intRecommendAmisGroupes;
    }





    /**
     * Returns the intRecommendEtudiants.
     *
     * @return {@link int}
     */
    public  int getIntRecommendEtudiants() {
        return intRecommendEtudiants;
    }





    /**
     * Returns the intRecommendAbsence.
     *
     * @return {@link int}
     */
    public  int getIntRecommendAbsence() {
        return intRecommendAbsence;
    }






    /**
     * Sets the addIntRecommendEtudiants.
     *
     *
     */
    public  void addIntRecommendEtudiants() {
        intRecommendEtudiants++;
    }




    /**
     * Returns the nbReview.
     *
     * @return {@link int}
     */
    public  int getNbReview() {
        return nbReview;
    }





    /**
     * Returns the noteGloable.
     *
     * @return {@link float}
     */
    public  float getNoteGloable() {
        return noteGloable;
    }


    /**
     * Returns the noteGloable.
     *
     * @return {@link string}
     */
    public  String getGlobalNote() {
        DecimalFormat df = new DecimalFormat("#.#");
        return df.format(noteGloable);
    }

    /**
     * Returns the noteMax.
     *
     * @return {@link float}
     */
    public  float getNoteMax() {
        return noteMax;
    }





    /**
     * Returns the nbReviewTravelFamily.
     *
     * @return {@link int}
     */
    public  int getNbReviewTravelFamily() {
        return nbReviewTravelFamily;
    }





    /**
     * Returns the nbReviewTravelFamilyChild.
     *
     * @return {@link int}
     */
    public  int getNbReviewTravelFamilyChild() {
        return nbReviewTravelFamilyChild;
    }





    /**
     * Returns the nbReviewTravelPair.
     *
     * @return {@link int}
     */
    public  int getNbReviewTravelPair() {
        return nbReviewTravelPair;
    }





    /**
     * Returns the nbReviewTravelFriend.
     *
     * @return {@link int}
     */
    public  int getNbReviewTravelFriend() {
        return nbReviewTravelFriend;
    }





    /**
     * Returns the nbReviewSenior.
     *
     * @return {@link int}
     */
    public  int getNbReviewSenior() {
        return nbReviewSenior;
    }





    /**
     * Returns the nbReviewTravelAlone.
     *
     * @return {@link int}
     */
    public  int getNbReviewTravelAlone() {
        return nbReviewTravelAlone;
    }



    /**
     * Returns the nbCurReview.
     *
     * @return {@link int}
     */
    public int getNbCurReview() {
        return nbCurReview;
    }



    /**
     * Sets the nbCurReview.
     *
     * @param nbCurReview The nbCurReview to set.
     */
    public void setNbCurReview(int nbCurReview) {
        this.nbCurReview = nbCurReview;
    }



}
