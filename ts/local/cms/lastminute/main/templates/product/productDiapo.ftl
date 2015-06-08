<#assign
  productSupDisplayable = context.lookup("productSupplementDisplayable")!productSupplementDisplayable
  productDisplayable = context.lookup("smallProductDisplayable")!productDisplayable
/>

<div class="colLeft">
  <div id="slideProduct" class="slide-product-wrap">
    <#if (productDisplayable.getPicto())?has_content>
      <div class="img-flash">
        <img alt="${productDisplayable.picto.label}" src="${productDisplayable.picto.code}" />
      </div>
    </#if>
    <@tag_cms_renderContainer code="videoLinkContainer" />
    <div id="slideshow" class="owl-product visibleHidden">
      <#if (productSupDisplayable.getDiaporama())?has_content>
        <#list productSupDisplayable.getDiaporama() as diapo>
          <div class="owl-product-item"><img src="${diapo}" alt="${productDisplayable.title}"></div>
        </#list>
      </#if>
    </div>
    <span class="arrow-left"> </span>
    <span class="arrow-right"> </span>
    <div class="slide-mask">
      <div id="slideProductPage">
        <#if (productSupDisplayable.getDiaporama())?has_content>
          <#list productSupDisplayable.getDiaporama() as diapo>
            <a data-slide-index="${diapo_index}" href=""><img src="${diapo}" alt="${productDisplayable.title}"></a>
          </#list>
        </#if>
      </div>
    </div>
  </div>
</div>
