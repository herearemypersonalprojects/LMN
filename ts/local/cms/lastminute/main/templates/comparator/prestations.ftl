<#assign
  productDisplayable = context.lookup("smallProductDisplayable")!productDisplayable
/>

<td class="col-${productDisplayable.params}">
  <div class="table-middle ">
    <ul>
      <#if (productDisplayable.getDescriptions())?has_content>
        <#list productDisplayable.getDescriptions() as description>
          <li>${description}</li>
        </#list>
      </#if>
    </ul>
  </div>
</td>
