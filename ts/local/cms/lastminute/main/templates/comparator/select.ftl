<#assign
  showLight = context.lookup("showLight")!showLight
  showCheckedImage = context.lookup("showCheckedImage")!showCheckedImage
  criterionValueDisplayable = context.lookup("criterionValueDisplayable")!criterionValueDisplayable
  productIndex = context.lookup("productIndex")!productIndex
/>

<#import "/lib/directives.ftl" as resource>

<td class="col-${productIndex}">
  <#if showLight == true>
    <div class="table-middle optionsTab odd equalized-row">
  <#else>
    <div class="table-middle optionsTab even equalized-row">
  </#if>
    ${criterionValueDisplayable.label}
  <#if showCheckedImage == true>
    <span></span>
  </#if>
</td>

