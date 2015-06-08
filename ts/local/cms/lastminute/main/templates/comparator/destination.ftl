<#assign
  productDisplayable = context.lookup("smallProductDisplayable")!productDisplayable
/>

<td class="col-${productDisplayable.params}">
  <div class="title table-up equalized-row">
    <#if productDisplayable.destinationTitle?has_content>
      <strong>${productDisplayable.destinationTitle}</strong>
    <#else>
      <strong> ... </strong>
    </#if>
    ${productDisplayable.title}
  </div>
</td>
