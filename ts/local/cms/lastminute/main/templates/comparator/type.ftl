<#assign
  compareDisplayable = context.lookup("compareProductDisplayable")!compareDisplayable
/>

<td class="col-${compareDisplayable.index} shadowBottom">
  <div class="stay table-middle odd equalized-row">
    <#if compareDisplayable.getTypeFomules()?size = 0>
      ...
    <#else>
      <#list compareDisplayable.getTypeFomules() as type>
        <#if type_has_next>
          ${type}<br />
        <#else>
          ${type}
        </#if>
      </#list>
    </#if>
  </div>
</td>
