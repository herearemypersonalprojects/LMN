<#if previewSummary??>
  <#if 0 < previewSummary.nbReviews>
      <strong>Note clients :</strong>
      <div class="note">${previewSummary.getStringNote()}</div>
      <div class="max">/${previewSummary.maxiScore}</div>
  </#if>
</#if>