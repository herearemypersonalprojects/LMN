<!-- SiteCatalyst code version: H.23.8. -->
<#assign currentPage = context.lookup("currentPage")!'serp' />
<#assign omniture = context.lookup("omnitureStats") />

<script language="JavaScript" type="text/javascript"><!--
s_server="${omniture.serverName}";
s_cookieDomainPeriods="${omniture.cookieDomainPeriods}";
s_prop11="devlastminuteqa";
s_prop3="Orchestra Holidays";
s_eVar3="Orchestra Holidays";
s_prop4="Voyages";
s_eVar4="Voyages";
s_prop22="Travel";
s_eVar22="Travel";
s_prop21="${omniture.url}";
s_prop24="Not Registered Users";
s_eVar24="Not Registered Users";
s_prop27="Orchestra";
s_eVar27="Orchestra";
s_prop30="Holidays :: ${omniture.currentHour}";
s_eVar30="Holidays :: ${omniture.currentHour}";
s_prop47="Orchestra Holidays";
s_eVar47="Orchestra Holidays";

// on prend la valeur du cookie,
// afin d'envoyer le bon partnerId meme si la page est en cache
<#if (omniture.getPartnerId())?has_content>
s_prop1=$.cookie("partnerId");
s_eVar2=$.cookie("partnerId");
s_eVar16=$.cookie("partnerId");
</#if>

<#if (omniture.getUserId())?has_content>
s_prop23="${omniture.userId}";
s_eVar23="${omniture.userId}";
</#if>
<#if currentPage = 'serp'>
s_prop6="${omniture.resultsNmb}";
s_event3="${omniture.resultsNmb}";
s_events="event22";
s_pageName="Holidays: Orchestra Search Results";
s_prop19="Holidays: Orchestra Search Results";
s_eVar49="Holidays: Orchestra Search Results";
s_channel="Search Results";
  <#if (omniture.getDestLabel())?has_content>
s_prop5="Holidays Search: ${omniture.destLabel}";
s_eVar5="Holidays Search: ${omniture.destLabel}";
  <#else>
s_prop5="Holidays Search: Unspecified Destination";
s_eVar5="Holidays Search: Unspecified Destination";
  </#if>
  <#if (omniture.getSortLabel())?has_content>
    <#if (omniture.getCurrentPageNmb())?has_content>
s_eVar19="${omniture.sortLabel} - Page ${omniture.currentPageNmb}"
    </#if>
  </#if>
<#elseif currentPage = 'errorPage'>
s_events="event2";
<#if (omniture.getErrorCode())?has_content>
s_pageName="Holidays:Error ${omniture.errorCode}";
s_prop19="Holidays:Error ${omniture.errorCode}";
s_eVar49="Holidays:Error ${omniture.errorCode}";
s_prop16="Holidays:Error ${omniture.errorCode}";
</#if>
s_channel="Error";
<#elseif currentPage = 'product' || currentPage = 'productEmail'>
s_events="prodView";
s_pageName="Holidays: Orchestra Product Details";
s_prop19="Holidays: Orchestra Product Details";
s_eVar49="Holidays: Orchestra Product Details";
s_channel="Product";
  <#if (omniture.getProducts())?has_content>
s_products="<#list omniture.getProducts() as product>Holidays;${product.id}<#if product_has_next>,</#if></#list>";
s_eVar18="${omniture.toCode}";
  </#if>
<#elseif currentPage = 'compare'>
s_events="prodView";
s_pageName="Holidays: Orchestra Product Compare";
s_prop19="Holidays: Orchestra Product Compare";
s_eVar49="Holidays: Orchestra Product Compare";
s_channel="Product";
  <#if (omniture.getProducts())?has_content>
s_products="<#list omniture.getProducts() as product>Holidays;${product.id}<#if product_has_next>,</#if></#list>";
s_eVar52="Holidays :: Products Compared :: <#list omniture.getProducts() as product>${product.id}<#if product_has_next>|</#if></#list>";
  </#if>
s_prop18="${omniture.toCode}";
s_eVar18="${omniture.toCode}";
</#if>
<#if currentPage = 'serp' || currentPage = 'errorPage'>
  <#if (omniture.getDepDateWithFlex())?has_content>
s_prop8="${omniture.depDateWithFlex}";
s_eVar8="${omniture.depDateWithFlex}";
  </#if>
  <#if (omniture.getLeadTime())?has_content>
s_prop9="${omniture.leadTime}";
s_eVar9="${omniture.leadTime}";
  </#if>
  <#if (omniture.getDepCityLabel())?has_content>
s_prop14="Holidays :: Departure Point :: ${omniture.depCityLabel}";
s_eVar14="Holidays :: Departure Point :: ${omniture.depCityLabel}";
  </#if>
  <#if (omniture.getStars())?has_content>
s_prop33="Holidays :: Star Rating :: ${omniture.stars}";
s_eVar33="Holidays :: Star Rating :: ${omniture.stars}";
  </#if>
  <#if (omniture.getMaxPrice())?has_content>
s_prop34="Holidays :: Min Price - Max Price :: 0 - ${omniture.maxPrice}";
s_eVar34="Holidays :: Min Price - Max Price :: 0 - ${omniture.maxPrice}";
  </#if>
  <#if (omniture.getFormulaLabel())?has_content>
s_prop36="Holidays :: Board Basis :: ${omniture.formulaLabel}";
s_eVar36="Holidays :: Board Basis :: ${omniture.formulaLabel}";
  </#if>
  <#if (omniture.getRegionLabel())?has_content>
s_prop43="Holidays :: Region :: ${omniture.regionLabel}";
s_eVar43="Holidays :: Region :: ${omniture.regionLabel}";
  </#if>
  <#if (omniture.getTripTypeLabel())?has_content>
s_prop51="Holidays :: Type of trip :: ${omniture.tripTypeLabel}";
s_eVar51="Holidays :: Type of trip :: ${omniture.tripTypeLabel}";
  </#if>
</#if>
<#if context.lookup("displayAlternatifProducts")??>
s_prop16="Holidays:no availability";
</#if>

--></script>
<script src="/shared-cs/lastminute-catalog/js/s_code.js"></script>
<!-- SiteCatalyst code version: H.23.8. -->
