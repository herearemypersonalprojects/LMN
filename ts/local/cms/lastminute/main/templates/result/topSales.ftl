<#assign
  adsIndex = context.lookup("adsIndex")!1
  nbExpertList = context.lookup("nbExpertList")!0
  showExpertBlock = context.lookup("showExpertBlock")!false
  productDisplayable = context.lookup("smallProductDisplayable")!productDisplayable
  productIndex = context.lookup("publishedProductIndex")!productIndex
  flyAndDrive = context.lookup("flyAndDrive")!false
  brandData = context.lookup("brandData")!''
  productUrl = productDisplayable.seoUrl?replace(productDisplayable.id + '/', productDisplayable.id + '#')!''
/>

<!-- MONITEUR_OK - do not remove this comment -->
<#if !(brandData.brandName == "CGOS" && productDisplayable.getToCode() = "LASTMINUTE")>
<#if productDisplayable.getDepartureCities()??>
  <div class="search-item topSales">
    <div class="colLeft">
      <h3><a href="/${productUrl}">${productDisplayable.title}</a></h3>
      <#if '' != productDisplayable.imageUrl>
        <div class="image-wrap">
          <a href="/${productUrl}"><img src="${productDisplayable.imageUrl}" alt="${productDisplayable.title}"></a>
          <#if (productDisplayable.getPicto())?has_content>
            <img class="lastMinute" alt="${productDisplayable.picto.label}" src="${productDisplayable.picto.code}" />
          </#if>
        </div>
      </#if>
      <div class="description">
        <@tag_cms_lookup name="seoData"; seoData>
          <#assign destinationCountry = productDisplayable.destinationTitle!'' />
          <#assign destinationCity = productDisplayable.destinationCity!'' />
          <#assign stayType = seoData.stayType!'' />

          <div>
            <h2 class="title stayType">${stayType} ${destinationCountry} <#if destinationCountry != '' && destinationCity != ''> | </#if> ${destinationCity}</h2>
          </div>
          <ul>
            <#if (productDisplayable.getDescriptions())?has_content>
              <#list productDisplayable.getDescriptions() as description>
                <li>${description}</li>
              </#list>
            </#if>
          </ul>
        </@tag_cms_lookup>
      </div>
      <div class="option">
        <ul>
          <#if !flyAndDrive>
            <li><strong>dur&eacute;e du s&eacute;jour :</strong>
              <#if (productDisplayable.getDurations())?has_content>
                <#if (productDisplayable.getDurations()?size > 3)>
                  Plusieurs dur&eacute;es de s&eacute;jour
                <#else>
                  <#list productDisplayable.getDurations() as duration>
                    <#if duration_has_next>${duration.nights} nuits / <#else>${duration.nights} nuits</#if>
                  </#list>
                </#if>
              </#if>
            </li>
          </#if>

          <li class="date"><strong>prochain d&eacute;p. &agrave; ce tarif :</strong>
            ${productDisplayable.baseDepartureCity}, le ${productDisplayable.baseDepartureDate}
          </li>

          <li><strong>d&eacute;part de :</strong>
            <#import "/lib/directives.ftl" as check>
            <@check.checkCharacterNumber characterNumber="22" characterValue="${productDisplayable.getDepartureCities()}" checkType="city"; cities>
              <#if cities == "general">
                Plusieurs villes de d&eacute;part
              <#else>
                ${cities}
              </#if>
            </@check.checkCharacterNumber>
          </li>

          <@tag_cms_writeMessage key="${productDisplayable.getPensions()}" fileName="pension.properties";pension >
            <#if pension != "">
              <li title="${pension}"><strong>pension :</strong>
                ${pension}
              </li>
            </#if>
          </@tag_cms_writeMessage>
        </ul>
      </div>

      <#if (productDisplayable.getDescriptionButtom())?has_content>
        <div class="clearBlocks"> </div>
        <div class="slogan">
          <strong>${productDisplayable.descriptionButtom}</strong>
        </div>
      </#if>
    </div>

    <div class="colRight">
      <div class="top-sales">Top des ventes</div>

      <div class="add-compare">
        <input value="${productDisplayable.id}" type="checkbox" alt="" id="${productDisplayable.id}">
        <label for="${productDisplayable.id}">ajouter au comparateur</label>
      </div>

      <div class="price-block">
        <#if (productDisplayable.getPromoPercentage())?has_content
               && (productDisplayable.getPromoPercentage() != 0)>
          <a href="/${productUrl}" rel="nofollow">
            <div class="economy">&eacute;conomie de ${productDisplayable.promoPercentage}%<span> </span></div>
            <div class="price">${productDisplayable.promoPrice}</div>
            <div class="euro">&euro;*</div>
            <div class="ttc">ttc <strong>/pers</strong></div>
            <div class="actual-price">au lieu de ${productDisplayable.basePrice}&euro;</div>
            <span class="arrow"> </span>
          </a>
        <#else>
          <a href="/${productUrl}" rel="nofollow">
            <div class="economy visibleHidden">&nbsp;</div>
            <div class="price">${productDisplayable.basePrice}</div>
            <div class="euro">&euro;*</div>
            <div class="ttc">ttc <strong>/pers</strong></div>
            <span class="arrow"> </span>
            <#if productDisplayable.getToCode() = "LASTMINUTE">
				<img class="veryFlexy" src="/shared-cs/lastminute-catalog/images/very-flexy.png" alt="">
            </#if>
          </a>
        </#if>
      </div>
      <div class="customer-block">
        <div class="customer-rating">
          <#if productDisplayable.ratingImageUrl?has_content && productDisplayable.rating?has_content>
            <div class="advisor-block">
              <img src="${productDisplayable.ratingImageUrl}" alt="">
              <div class="mention-rating">
                <#assign ratingNum = productDisplayable.rating?number>
                <#if (ratingNum > 4)>
                  Excellent,
                <#elseif (ratingNum > 3)>
                  Tr&egrave;s bien,
                <#elseif (ratingNum > 2)>
                  Moyen,
                <#elseif (ratingNum > 1)>
                  M&eacute;diocre,
                <#elseif (ratingNum >= 0)>
                  Horrible,
                </#if>
                  ${productDisplayable.rating}
              </div>
            </div>
          </#if>
          <#--
          <#import "/lib/includeStaticFileDirective.ftl" as engine>
          <@engine.includeStaticFile fileName="/shared/cs/web/lastminute-catalog/reviews/${productDisplayable.reference?lower_case}/itemNote.html";insertedFile>
            ${insertedFile}
          </@engine.includeStaticFile>
          -->
          <div class="infos-block">
            <div class="infos">
              Info et r&eacute;servation au
              <strong>
                <#if brandData.brandName == 'CGOS'>
                  0892 707 200
                <#else>
                  0 892 68 61 00
                </#if>
                <sup>(1)</sup>
              </strong>
            </div>
            <div class="ref">R&eacute;f. : ${productDisplayable.id}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</#if>
</#if>
<#if showExpertBlock == true>
  <#if adsIndex < nbExpertList>
    <#if adsIndex == productIndex>
      <@tag_cms_renderContainer code="middAdsContainer" />
    </#if>
  </#if>
</#if>
