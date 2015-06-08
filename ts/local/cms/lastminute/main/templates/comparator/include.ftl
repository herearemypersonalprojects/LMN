<#assign
  compareDisplayable = context.lookup("compareProductDisplayable")!compareDisplayable
/>

<td class="col-${compareDisplayable.index} shadowBottom">
  <div class="stay table-middle odd equalized-row">
    <ul>
      <#if (compareDisplayable.getIncludes())?has_content>
        <#list compareDisplayable.getIncludes() as include>
          <li>${include.content}</li>
        </#list>
      </#if>
    </ul>
  </div>
</td>
