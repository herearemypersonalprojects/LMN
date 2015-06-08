<#assign
  productDisplayable = context.lookup("smallProductDisplayable")!productDisplayable
/>

<td class="col-${productDisplayable.params}">
  <div class="table-middle noPaddingTop">
    <div class="price-block">
      <#if (productDisplayable.getPromoPercentage())?has_content>
        <#if productDisplayable.getPromoPercentage() != 0>
          <div class="economy">&eacute;conomie de ${productDisplayable.promoPercentage}%<span> </span></div>
          <div class="price">${productDisplayable.promoPrice}</div>
          <div class="euro">&euro;*</div>
          <div class="ttc">ttc <strong>/pers</strong></div>
          <div class="actual-price">au lieu de  ${productDisplayable.basePrice}&euro;</div>
        <#else>
          <div class="economy visibleHidden">&eacute;conomie de<span> </span></div>
          <div class="price">${productDisplayable.basePrice}</div>
          <div class="euro">&euro;*</div>
          <div class="ttc">ttc <strong>/pers</strong></div>
        </#if>
      <#else>
        <div class="economy visibleHidden">&eacute;conomie de<span> </span></div>
        <div class="price">${productDisplayable.basePrice}</div>
        <div class="euro">&euro;*</div>
        <div class="ttc">ttc <strong>/pers</strong></div>
      </#if>
    </div>
  </div>
</td>
