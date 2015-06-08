<#assign
  compareDisplayable = context.lookup("compareProductDisplayable")!compareDisplayable
  productDisplayable = context.lookup("smallProductDisplayable")!productDisplayable
/>

<td class="cell-content grey">
  <div class="date-info">
    <span>${compareDisplayable.baseDispoDate}</span>
    <strong>${productDisplayable.destinationCities}</strong>
    <span>
      <#if (productDisplayable.getDurations())?has_content>
        <#list productDisplayable.getDurations() as duration>
          <#if duration_has_next>
            ${duration.nights}nuits/
          <#else>
            ${duration.nights}nuits
          </#if>
        </#list>
      </#if>
    </span>
  </div>
</td>
<td class="indent">&nbsp;</td>