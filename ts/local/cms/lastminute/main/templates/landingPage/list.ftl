<#include "config.ftl">
<#assign landingPageProductSearchResponse = context.lookup("landingPageProductSearchResponse")!''/>
<#assign queryString = context.lookup("queryString")!queryString/>

<#if !queryString?contains("access=all")>
    <#if landingPageProductSearchResponse?has_content>
        <#if !(landingPageProductSearchResponse.getProductsCount() &lt; minProductsNumber) && !(landingPageProductSearchResponse.getProductsCount() &gt; maxProductsNumber)>
            <@tag_cms_renderContainer code="cartoucheContainer" />
        </#if>
    </#if>
<#else>
    <@tag_cms_renderContainer code="cartoucheContainer" />
</#if>