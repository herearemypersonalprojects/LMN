<#--
<#if previewSummary??>
  <#if 0 < previewSummary.nbReviews>
-->
    <div class="customer-reviews">
      <div class="title">Avis clients :</div>
      <div class="advisor-block">
        <div class="img-block">
          <img src="http://back-lastminute.dev.internal.travelsoft.fr/admin/TS/fckUserFiles/Image/tripAdvisor-1.png" alt="">
          <img src="http://back-lastminute.dev.internal.travelsoft.fr/admin/TS/fckUserFiles/Image/tripAdvisor-2.png" alt="">
        </div>
        <div class="mention-rating">Très bien, 3.5</div>
        <p>Basé sur <strong>92</strong> évaluations des clients</p>
      </div>
      <a href="#" class="link-blue">afficher les évaluations</a>

      <#--
        <p>Note moyenne : (${previewSummary.nbReviews} avis)</p>
        <div class="rating-bar">
         <span id="externalProductCode" class="hidden">${previewSummary.code}</span>
          <span id="reviewSummaryBarValue1" class="hidden">${previewSummary.getStringNote()}</span>
          <span id="reviewSummaryBarValue2" class="hidden">${previewSummary.maxiScore}</span>

          <#assign noteRate = (previewSummary.globalScore/previewSummary.maxiScore)*114 />
          <#assign noteStr = "" + noteRate />

          <div  class="rating-bar_off">
            <span id = "reviewSummaryBar" style="width: ${noteStr?replace(",", ".")}px;" class="rating-bar_on"> </span>
          </div>
        </div>
        <div class="rating-block">
          <p><strong>${previewSummary.getStringNote()}</strong>/${previewSummary.maxiScore}</p>
          <a href="#details-customerReviews" class="link-black">voir les notes</a>
        </div>
      -->
    </div>
<#--  
  </#if>
</#if>
-->