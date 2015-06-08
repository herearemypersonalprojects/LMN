<#if reviews??>
<div class="contents">
   <div class="block-outside">
      <div class="block-price">
         <div class="overall-rating">Note globale :</div>
         <div class="rating-block" id="noteGlobale">
            <strong>${reviews.getGlobalNote()}</strong>/${reviews.getNoteMax()}
         </div>
         <p class="noPadding" id="nbReview">${reviews.getNbCurReview()} clients ont noté ce séjour</p>
         <div class="rating-bar">
            <div class="rating-bar_off">
               <span class="hidden" id="noteGlobaleBarValue1">${reviews.getNoteGloable()}</span>
               <span class="hidden" id="noteGlobaleBarValue2">${reviews.getNoteMax()}</span>
               <span style="width: 60.3943px;" class="rating-bar_on" id="noteGlobaleBar" />
            </div>
         </div>
      </div>
      <div class="titleBold-black">évaluation par type de client :</div>
      <ul>
         <li>
            tous les clients
            <#if 0 < reviews.getNbReview()>
              <span id="nbReview" class="clientType" style="cursor: pointer">(${reviews.getNbReview()} avis)</span>
            <#else>
              (0 avis)
            </#if>
         </li>
         <li>
            en famille
            <#if 0 < reviews.getNbReviewTravelFamily()>
              <span id="nbReviewFamille" class="clientType" style="cursor: pointer">(${reviews.getNbReviewTravelFamily()} avis)</span>
            <#else>
              (0 avis)
            </#if>
         </li>
         <li>
            en famille avec enfants en bas âges
            <#if 0 < reviews.getNbReviewTravelFamilyChild()>
              <span id="nbReviewFamilleBebe" style="cursor: pointer" class="clientType">(${reviews.getNbReviewTravelFamilyChild()} avis)</span>
            <#else>
              (0 avis)
            </#if>
         </li>
         <li>
            en couple
            <#if 0 < reviews.getNbReviewTravelPair()>
              <span id="nbReviewCouple" style="cursor: pointer" class="clientType">(${reviews.getNbReviewTravelPair()} avis)</span>
            <#else>
              (0 avis)
            </#if>
         </li>
      </ul>
      <ul>
         <li>
            entre amis
            <#if 0 < reviews.getNbReviewTravelFriend()>
              <span id="nbReviewAmis" style="cursor: pointer" class="clientType">(${reviews.getNbReviewTravelFriend()} avis)</span>
            <#else>
              (0 avis)
            </#if>
         </li>

         <li>
            séniors
            <#if 0 < reviews.getNbReviewSenior()>
              <span id="nbReviewSeniors" style="cursor: pointer"  class="clientType">(${reviews.getNbReviewSenior()} avis)</span>
            <#else>
              (0 avis)
            </#if>
         </li>

         <#if 0 < reviews.getNbReviewTravelAlone()>
         <li>
            voyageurs seuls
            <#if 0 < reviews.getNbReviewTravelAlone()>
              <span id="nbReviewSeuls" style="cursor: pointer" class="clientType">(${reviews.getNbReviewTravelAlone()} avis)</span>
            <#else>
              (0 avis)
            </#if>
         </li>
         </#if>
      </ul>
      <div class="recommend">
         <strong id="nbRecommends">${reviews.getHotelRecommend()}%</strong>
         des clients recommanderaient ce séjour à un proche
      </div>
   </div>
   <div class="titleBold-black paddingLeftx10">Concernant votre séjour :</div>
   <ul class="list-rating firstChild">
      <li>
         Le service / le personnel de l'hôtel
         <div class="rating-bar">
            <div class="rating-bar_off">
               <span class="hidden" id="noteServiceBarValue">${reviews.getHotelService()}</span>
               <span style="width: 85.1084px;" class="rating-bar_on" id="noteServiceBar" />
            </div>
         </div>
      </li>
      <li>
         Le cadre de l'hôtel (décoration, environnement)
         <div class="rating-bar">
            <div class="rating-bar_off">
               <span class="hidden" id="noteCadreBarValue">${reviews.getHotelCadre()}</span>
               <span style="width: 82.4977px;" class="rating-bar_on" id="noteCadreBar" />
            </div>
         </div>
      </li>
      <li>
         La situation géographique de l'hôtel
         <div class="rating-bar">
            <div class="rating-bar_off">
               <span class="hidden" id="noteLocationBarValue">${reviews.getHotelLocation()}</span>
               <span style="width: 78.1466px;" class="rating-bar_on" id="noteLocationBar" />
            </div>
         </div>
      </li>
      <li>
         Votre chambre / logement
         <div class="rating-bar">
            <div class="rating-bar_off">
               <span class="hidden" id="noteChambreBarValue">${reviews.getHotelChambre()}</span>
               <span style="width: 83.542px;" class="rating-bar_on" id="noteChambreBar" />
            </div>
         </div>
      </li>
      <li>
         La restauration
         <div class="rating-bar">
            <div class="rating-bar_off">
               <span class="hidden" id="noteRestaurationBarValue">${reviews.getHotelRestauration()}</span>
               <span style="width: 76.058px;" class="rating-bar_on" id="noteRestaurationBar" />
            </div>
         </div>
      </li>
   </ul>
   <ul class="list-rating">
      <li>
         L'animation / les activités
         <div class="rating-bar">
            <div class="rating-bar_off">
               <span class="hidden" id="noteAnimationBarValue">${reviews.getHotelActivites()}</span>
               <span style="width: 73.8644px;" class="rating-bar_on" id="noteAnimationBar" />
            </div>
         </div>
      </li>
      <li>
         Le rapport qualité  / prix
         <div class="rating-bar">
            <div class="rating-bar_off">
               <span class="hidden" id="notePrixBarValue">${reviews.getHotelPrix()}</span>
               <span style="width: 84.9344px;" class="rating-bar_on" id="notePrixBar" />
            </div>
         </div>
      </li>
      <li>
         Les équipements de l'hôtel (piscine, salle de sport...)
         <div class="rating-bar">
            <div class="rating-bar_off">
               <span class="hidden" id="noteEquipmentBarValue">${reviews.getHotelEquipements()}</span>
               <span style="width: 83.6px;" class="rating-bar_on" id="noteEquipmentBar" />
            </div>
         </div>
      </li>
      <li>
         Les activités pour enfants
         <div class="rating-bar">
            <div class="rating-bar_off">
               <span class="hidden" id="noteActivitesBebeBarValue">${reviews.getHotelEnfantActivites()}</span>
               <span style="width: 76.5429px;" class="rating-bar_on" id="noteActivitesBebeBar" />
            </div>
         </div>
      </li>
      <li>
         Les excursions
         <div class="rating-bar">
            <div class="rating-bar_off">
               <span class="hidden" id="noteExcursionsBarValue">${reviews.getHotelExcursions()}</span>
               <span style="width: 81.1082px;" class="rating-bar_on" id="noteExcursionsBar" />
            </div>
         </div>
      </li>
   </ul>
   <div class="clearBlocks" />
   <div class="block-benefits close">
      <div class="titleBold-black paddingLeftx10">
         Prestations complémentaires :
         <span class="link-more">afficher/masquer les détails</span>
      </div>
      <div class="contents-benefits">
         <ul class="list-rating firstChild">
            <li>
               Vol :
               <div class="rating-bar">
                  <div class="rating-bar_off">
                     <span class="hidden" id="noteVolBarValue">${reviews.getHotelVol()}</span>
                     <span style="width: 75.7099px;" class="rating-bar_on" id="noteVolBar" />
                  </div>
               </div>
            </li>
            <li>
               Transfert :
               <div class="rating-bar">
                  <div class="rating-bar_off">
                     <span class="hidden" id="noteTransfertBarValue">${reviews.getHotelTransfert()}</span>
                     <span style="width: 82.9569px;" class="rating-bar_on" id="noteTransfertBar" />
                  </div>
               </div>
            </li>
            <li>
               Correspondant local :
               <div class="rating-bar">
                  <div class="rating-bar_off">
                     <span class="hidden" id="noteCorrespondantLocalBarValue">${reviews.getHotelCorrespondantLocal()}</span>
                     <span style="width: 81.7292px;" class="rating-bar_on" id="noteCorrespondantLocalBar" />
                  </div>
               </div>
            </li>
         </ul>
      </div>
   </div>
   <div class="clearBlocks" />
   <div class="block-guests-recommend close">
      <div class="guests-recommend titleBold-black paddingLeftx10">
         A quel type de clientèle nos clients recommandent ce séjour ? :
         <span class="link-more">afficher/masquer les détails</span>
      </div>
      <div class="contents-guests-recommend">
         <ul class="list-rating firstChild">
            <li>
               Familles :
               <div class="rating-bar">
                  <div class="rating-bar_off">
                     <span class="hidden" id="noteReFamValue">${reviews.getRecommendFamilles()}</span>
                     <span style="width: 62.0862px;" class="rating-bar_on" id="noteReFam" />
                  </div>
               </div>
            </li>
            <li>
               Couples :
               <div class="rating-bar">
                  <div class="rating-bar_off">
                     <span class="hidden" id="noteReCoupValue">${reviews.getRecommendCouples()}</span>
                     <span style="width: 57px;" class="rating-bar_on" id="noteReCoup" />
                  </div>
               </div>
            </li>
            <li>
               Célibataires :
               <div class="rating-bar">
                  <div class="rating-bar_off">
                     <span class="hidden" id="noteReCeliValue">${reviews.getRecommendCelibataires()}</span>
                     <span style="width: 0px;" class="rating-bar_on" id="noteReCeli" />
                  </div>
               </div>
            </li>
            <li>
               Séniors:
               <div class="rating-bar">
                  <div class="rating-bar_off">
                     <span class="hidden" id="noteReAgeValue">${reviews.getRecommendPersonnesAgees()}</span>
                     <span style="width: 67.3867px;" class="rating-bar_on" id="noteReAge" />
                  </div>
               </div>
            </li>
         </ul>
         <ul class="list-rating">
            <li>
               Jeunes mariés :
               <div class="rating-bar">
                  <div class="rating-bar_off">
                     <span class="hidden" id="noteReJeunValue">${reviews.getRecommendJeunesMaries()}</span>
                     <span style="width: 57.76px;" class="rating-bar_on" id="noteReJeun" />
                  </div>
               </div>
            </li>
            <li>
               Amis / groupes :
               <div class="rating-bar">
                  <div class="rating-bar_off">
                     <span class="hidden" id="noteReAmisValue">${reviews.getRecommendAmisGroupes()}</span>
                     <span style="width: 71.4943px;" class="rating-bar_on" id="noteReAmis" />
                  </div>
               </div>
            </li>
            <li>
               Etudiants :
               <div class="rating-bar">
                  <div class="rating-bar_off">
                     <span class="hidden" id="noteReEtuValue">${reviews.getRecommendEtudiants()}</span>
                     <span style="width: 74.1px;" class="rating-bar_on" id="noteReEtu" />
                  </div>
               </div>
            </li>
            <li>
               Ne se prononce pas :
               <div class="rating-bar">
                  <div class="rating-bar_off">
                     <span class="hidden" id="noteReAbsValue">${reviews.getRecommendAbsence()}</span>
                     <span style="width: 52.668px;" class="rating-bar_on" id="noteReAbs" />
                  </div>
               </div>
            </li>
         </ul>
      </div>
   </div>
</div>
</#if>