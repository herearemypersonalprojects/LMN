<#import "/lib/directives.ftl" as util>
<#assign dayInWeek>
    <@util.formatDate format="E" />
</#assign>
<#assign hourInDay >
    <@util.formatDate format="HH" />
</#assign>
<#if dayInWeek == 'dim.' || dayInWeek == 'sam.' || hourInDay?number &lt; 18 || hourInDay?number &gt; 19> 
    <#assign isFlashSaleTime = false /> 
<#else>
    <#assign isFlashSaleTime = true /> 
</#if>