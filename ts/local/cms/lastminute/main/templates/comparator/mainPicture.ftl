<#assign
  productDisplayable = context.lookup("smallProductDisplayable")!productDisplayable
/>

<td class="col-${productDisplayable.params}">
  <div class="image-wrap table-middle equalized-row">
    <a href="/${productDisplayable.seoUrl}"><img src="${productDisplayable.imageUrl}" alt=""></a>
    <#if (productDisplayable.getPicto())?has_content>
      <img class="lastMinute" src="${productDisplayable.picto.code}" alt="">
    </#if>
  </div>
</td>
