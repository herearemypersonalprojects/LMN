  <#assign   productDisplayable = context.lookup("smallProductDisplayable")!productDisplayable />
  <#assign   brandData = context.lookup("brandData")!'' />
  <#assign myEnvironment = context.lookup("myEnvironment") />
  <div class="wrapper">

    <@tag_cms_renderContainer code="rightBarProductContainer" />

    <@tag_cms_renderContainer code="cgosVidationCodeContainer" />
    <!-- ******** BEGIN BOOKSTAY **************************************-->
    <#if myEnvironment.getAttribute("showdesc") != 'true'>
      <@tag_cms_renderContainer code="resaformContainer" />
    </#if>

    <@tag_cms_renderContainer code="b2bContainer" />

    <@tag_cms_renderContainer code="personalInfoContainer" />

    <!-- END BOOKSTAY -->
    <!-- ******** BEGIN DESCRIPTION-INFOS ************************************** -->
    <div class="description-infos" id="descriptionProduct">
      <@tag_cms_renderContainer code="productDescriptionContainer" />
      <@tag_cms_renderContainer code="customerReviewsContainer" />
    </div>

    <!-- END DESCRIPTION-INFOS -->



    <div class="help-phone bottom">
      <span> </span>
      <p>besoin d&rsquo;aide pour r&eacute;server ? appelez le <strong>
      <#if brandData.brandName == 'CGOS'>
      0892 707 200
      <#else>
      0 892 68 61 00
      </#if>
      </strong> <em>(0.34 &euro; TTC / min)</em> r&eacute;f&eacute;rence produit : <strong>${productDisplayable.id}</strong></p>
    </div>

    <@tag_cms_renderContainer code="productReferenceContainer" />

    <@tag_cms_renderContainer code="middAdsContainer" />
  </div>
