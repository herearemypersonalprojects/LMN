<#if previewSummary??>
  <#if 0 < previewSummary.nbReviews>
    <div class="rating-bar">
      <strong>note :</strong>
      <div class="rating-bar_off">
        <span class="rating-bar_on"  style = "width: ${previewSummary.rate}px"> </span>
      </div>
      <strong class="paddingLeftx10">(${previewSummary.nbReviews} avis)</strong>
    </div>
  </#if>
</#if>