<#assign
  partitioner = context.lookup("partitioner")!partitioner
/>

<#if partitioner.getLabel() == "-1">
  <td><span><a style="text-decoration:none;color:#4A82BB" href="${partitioner.getUrl()}">&laquo;</a></span></td>

<#elseif partitioner.getLabel() == "0">
  <td><span><a style="text-decoration:none;color:#4A82BB" href="${partitioner.getUrl()}">&raquo;</a></span></td>

<#else>
  <#if partitioner.isCurrent() == true>
    <td class="selected"><span><a style="text-decoration:none;color:#000000" href="#">${partitioner.getLabel()}</a></span></td>
  <#else>
    <td><span><a style="text-decoration:none;color:#4A82BB" href="${partitioner.getUrl()}">${partitioner.getLabel()}</a></span></td>
  </#if>
</#if>