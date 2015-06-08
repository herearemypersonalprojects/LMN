<#assign
  productSupDisplayable = context.lookup("productSupplementDisplayable")!productSupplementDisplayable
  productDisplayable = context.lookup("smallProductDisplayable")!productDisplayable
  publishedProduct = context.lookup("publishedProduct")!publishedProduct
  productUrl = context.lookup("productUrl")!'#'
  brandData = context.lookup("brandData")!''
/>
<#assign myEnvironment = context.lookup("myEnvironment") />

<#import "/lib/directives.ftl" as util>

<div class="sep"> </div>
<div id="details-stay" class="details-stay">
  <#if myEnvironment.getAttribute("showdesc") != 'true'>
    <div class="title">d&eacute;tails du s&eacute;jour</div>
  <#else>
    <input id="catalogCode" type="hidden" value="${productDisplayable.id}" name="catalogCode" />
  </#if>
  <#if (productSupDisplayable.getDescriptions())?has_content>
    <#list productSupDisplayable.getDescriptions() as description>
      <#if description_index == 0>
        <h2>${productDisplayable.title} ${myEnvironment.getAttribute("mlsdfklsfskdfjsdsfsd")}</h2>
        <#--<p>${description.content}</p>-->
        <#break>
      </#if>

    </#list>
  </#if>

  <#if (productSupDisplayable.getEquipement())?has_content>
    <div class="options">
      <div class="title">services et &eacute;quipements</div>
      <div class="contents">
        <#list productSupDisplayable.getEquipement() as equipement>
        <#assign mod = (equipement_index) - 4*(((equipement_index)/4)?int) />
        <#if mod = 0>
          <ul class="amenities">
        </#if>
            <li class="services">
              <img src="${equipement.code}" width="30" height="30" alt="${equipement.label}" />
              <span class="commodities">${equipement.label}</span>
            </li>
        <#if mod = 3>
          </ul>
        </#if>

        </#list>
      </div>
    </div>
  </#if>

  <#if (productSupDisplayable.getDescriptions())?has_content>
    <div class="options">
      <div class="title">description</div>
      <div class="contents">
        <#if (productSupDisplayable.getDescriptions())?has_content>
          <#list productSupDisplayable.getDescriptions() as description>
            <#if description_index == 0>
              <#--<h4>${productDisplayable.title}</h4>-->
              <div>${description.content}</div>
              <#break>
            </#if>
          </#list>
        </#if>
      </div>
      <div class="contents">
        <#list productSupDisplayable.getDescriptions() as description>
          <#if 0 < description_index>
            <h4>${description.title}</h4>
            <div>${description.content}</div>
          </#if>
        </#list>
      </div>
    </div>
  </#if>

  <#if (productSupDisplayable.getIncludes())?has_content>
    <div class="options">
      <div id="conditions-tarifaires" class="title">conditions tarifaires</div>
      <div class="contents">
        <#list productSupDisplayable.getIncludes() as include>
          <ul class="yourRequirements">
            <li class="up">${include.title}</li>
            <li>${include.content}</li>
          </ul>
        </#list>
      </div>
      <div class="contents">
        <#list productSupDisplayable.getInformation() as information>
            <h4>${information.title}</h4>
            <div>${information.content}</div>
        </#list>
      </div>
    </div>
  </#if>

<#--
  <#if (productSupDisplayable.getInformation())?has_content>
      <div class="options">
        <div id="supple-information" class="title">informations supplémentaires</div>http://jira.internal.travelsoft.fr/secure/attachment/43286/responsive_new_bookingpath_holidays_7.jpg
        <div class="contents">
          <#list productSupDisplayable.getInformation() as information>
              <h4>${information.title}</h4>
              <p>${information.content}</p>
          </#list>
        </div>
      </div>
  </#if>
-->

  <#if (productSupDisplayable.getPassengerInfo()?has_content || productSupDisplayable.getFormalityInfo()?has_content)>
    <div class="options">
      <div id="informations-voyageurs" class="title">informations voyageurs</div>
      <div class="contents">
        <#list productSupDisplayable.getFormalityInfo() as formalityInfo>
          <#if (formalityInfo.title)?has_content>
            <h4>${formalityInfo.title}</h4>
          </#if>
          <div>${formalityInfo.content}</div>
        </#list>

        <#list productSupDisplayable.getPassengerInfo() as passengerInfo>
          <#if (passengerInfo.title)?has_content>
            <h4>${passengerInfo.title}</h4>
          </#if>
          <div>${passengerInfo.content}</div>
        </#list>
      </div>
    </div>
  </#if>

  <#if (productSupDisplayable.getFlightsInfo())?has_content>
    <div class="options">
      <div id="informations-voyageurs" class="title">informations transports</div>
      <div class="contents">
        <#list productSupDisplayable.getFlightsInfo() as flightsInfo>
          <#if (flightsInfo.title )?has_content>
            <h4>${flightsInfo.title}</h4>
          </#if>
          <div>${flightsInfo.content}</div>
        </#list>
      </div>
    </div>
  </#if>
</div>

<#if (productSupDisplayable.getExperts())?has_content>
  <div id="details-stay" class="details-stay">
     <div class="text-conteiner">
        <h2>avis de notre expert</h2>
        <span class="tooltip">
          <span>
            Afin d'&eacute;valuer  cet &eacute;tablissement de fa&ccedil;on transparente,
            nous avons recueilli l'avis de notre expert
          </span>
        </span>

        <#list productSupDisplayable.getExperts() as expert>
           <p>${expert.content}</p>
        </#list>
     </div>
   </div>
</#if>

<#if myEnvironment.getAttribute("showdesc") != 'true'>
  <div class="content-up-page">
    <a href="#header">Haut de page</a>
  </div>
</#if>