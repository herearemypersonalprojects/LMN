<#assign myEnvironment = context.lookup("myEnvironment") />
<#if myEnvironment.getAttribute("showdesc") != 'true'>
  <div class="details-youLike">
    <h2>vous aimerez aussi</h2>
    <div id="slideshowBottom" class="owl-carousel">
      <@tag_cms_renderContainer code="relatedCartoucheContainer" />
    </div>
  </div>
</#if>
