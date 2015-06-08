<#assign myEnvironment = context.lookup("myEnvironment") />

<#if myEnvironment.getAttribute("showdesc") != 'true'>
  <div class="product-item-block">
    <@tag_cms_renderContainer code="mainContentProductContainer" />
  </div>
</#if>
<div class="wrap-master">
  <@tag_cms_renderContainer code="descriptionProductContainer" />
</div>