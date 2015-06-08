<#assign
  productDisplayable = context.lookup("smallProductDisplayable")!productDisplayable
/>

<td class="cell-content grey">
  <div class="description">
    <#if (productDisplayable.getDescriptionButtom())?has_content>
      <strong>« Un géant bien sympathique »</strong>
      <p>${productDisplayable.descriptionButtom}</p>
    </#if>
    <span>Réf. : ${productDisplayable.id} </span>
  </div>
</td>
<td class="indent">&nbsp;</td>