<@tag_cms_lookup name="seoData"; seoData>
  <#assign destinationCountry = seoData.destinationCountry!'' />
  <#assign destinationCity = seoData.destinationCity!'' />
  <#assign stayType = seoData.stayType!'' />
  <#assign destination = '' />
  <#assign productName = seoData.productName!'' />
  <#assign seoProductName = seoData.seoProductName!'' />
  <#assign rewrite = seoData.rewrite!false />
  <#assign brandData = context.lookup("brandData")!'' />
  <#assign queryWithDynamicParameters = context.lookup("queryWithDynamicParameters")!false />

  <#if brandData.brandName == 'lastminute'>
    <#assign  brandName = 'lastminute.com' />
  <#elseif brandData.brandName == 'CDISCOUNT'>
    <#assign  brandName = 'CDISCOUNT' />
  <#elseif brandData.brandName == 'SELECTOUR'>
     <#assign  brandName = 'SELECTOUR' />
  <#elseif brandData.brandName == 'CITROEN'>
    <#assign  brandName = 'CITROËN MULTICITY' />
  <#elseif brandData.brandName == 'CGOS'>
    <#assign  brandName = 'le C.G.O.S' />
  </#if>

  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <#if brandData.brandName == 'lastminute'>
    <meta name="google-site-verification" content="osLaXy1bmT5F-ptnLLVk9KAALbgRTVTC8jq84CuGNRg" />
  </#if>
  <#if productName != ''>
    <title>${productName} - ${brandName}</title>
    <meta name="description" content="Consultez nos avis clients, nos photos et Profitez dès maintenant de nos Offres Promotionnelles ${productName} - ${brandName}" />

    <@tag_cms_lookup name="smallProductDisplayable"; smallProductDisplayable>
        <!-- Canonical URL for product page. -->
        <meta name="canonical" content="http://${brandData.brandServerName}/${seoProductName}/${smallProductDisplayable.id}" />
    </@tag_cms_lookup>

  <#else>
    <#if destinationCity != ''>
      <#assign destination = destinationCity />
    <#else>
      <#assign destination = destinationCountry />
    </#if>
    <#if stayType != ''>
    <#assign stayTypeCapFirst = stayType?cap_first />
    </#if>
    <title><#if stayType == ''>Voyage<#else>${stayTypeCapFirst}</#if> ${destination} - Réservez votre <#if stayType == ''>Voyage<#else>${stayType}</#if> ${destination} pas cher avec ${brandName}</title>
    <meta name="description" content="Vous souhaitez changer d’Air? Profitez dès maintenant de nos offres <#if stayType == ''>Voyage<#else>${stayType}</#if> ${destination}
    et Faites des économies avec ${brandName}. Exclusivité : Payez – et Partez +" />

    <#if seoData.rewrite && stayType!='' >
      <!-- Canonical URL for stay type search (sejour, croisiere, etc). -->
      <meta name="canonical" content="http://${brandData.brandServerName}/${stayType}" />
    <#elseif seoData.rewrite && destinationCountry!='' && destinationCity=='' >
      <!-- Canonical URL for country search (maroc, tunisie, etc). -->
      <meta name="canonical" content="http://${brandData.brandServerName}/voyage-${destinationCountry}" />
    <#elseif seoData.rewrite && destinationCountry!='' && destinationCity!='' >
      <!-- Canonical URL for country and city search (tunisie/tunis, etc). -->
      <meta name="canonical" content="http://${brandData.brandServerName}/voyage-${destinationCountry}/${destinationCity}" />
    </#if>

  </#if>

  <#if !queryWithDynamicParameters && seoData.rewrite && brandName == 'lastminute.com' >
    <meta content="index,follow" name="robots" />
    <meta content="index,follow" name="googlebot" />
  <#elseif queryWithDynamicParameters>
    <meta content="noindex,follow" name="robots" />
    <meta content="noindex,follow" name="googlebot" />
  <#else>
    <meta content="noindex,nofollow" name="robots" />
    <meta content="noindex,nofollow" name="googlebot" />
  </#if>

</@tag_cms_lookup>

