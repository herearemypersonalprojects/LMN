<#assign
  productDisplayable = context.lookup("smallProductDisplayable")!productDisplayable
/>

<td class="col-${productDisplayable.params}">
  <div class="table-middle equalized-row">
    <#if (productDisplayable.getDurations())?has_content>
      <#if (productDisplayable.getDurations()?size > 6)>
        Plusieurs dur&eacute;es de s&eacute;jour
      <#else>
        <#list productDisplayable.getDurations() as duration>
          <#if duration_has_next>
             ${duration.nights} nuits /
          <#else>
            ${duration.nights} nuits
          </#if>
        </#list>
      </#if>
    </#if>
  </div>
</td>
