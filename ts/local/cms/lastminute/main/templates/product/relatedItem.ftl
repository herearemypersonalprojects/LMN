<@tag_cms_lookup name="relatedSmallProductDisplayable"; productDisplayable>
<#assign productUrl = productDisplayable.seoUrl?replace(productDisplayable.id + '/', productDisplayable.id + '#')!'' />

<div class="item-youLike">
  <a href="/${productUrl}"><img alt="" class="lazyOwl" data-src="${productDisplayable.imageUrl}"></a>
  <div class="titleItem-youLike">${productDisplayable.title}</div>

  <p>tout compris<#if productDisplayable.baseDepartureDate?exists>, le ${productDisplayable.baseDepartureDate}, d&eacute;part de</#if>
    <#if productDisplayable.getDepartureCities()?exists>
      <#import "/lib/directives.ftl" as check>
      <@check.checkCharacterNumber characterNumber="22" characterValue="${productDisplayable.getDepartureCities()}" checkType="city"; cities>
        <#if cities == "general">
          plusieurs villes
        <#else>
          ${cities}
        </#if>
      </@check.checkCharacterNumber>
      </#if>
  </p>
  <#--
  <div class="rating-bar">
    <strong>note :</strong>
    <div class="rating-bar_off">
      <span class="rating-bar_on"  style = "width: 60px"> </span>
    </div>
    <strong class="paddingLeftx10">(7 avis)</strong>
  </div>
  -->
  <#import "/lib/includeStaticFileDirective.ftl" as engine>
  <@engine.includeStaticFile fileName="/shared/cs/web/lastminute-catalog/reviews/${productDisplayable.id?lower_case}/relatedProductRate.html";insertedFile>
    ${insertedFile}
  </@engine.includeStaticFile>
  <#if productDisplayable.baseDepartureDate?exists>
    <div class="price-youLike">
      <strong>${productDisplayable.basePrice} &euro;<span>*</span></strong>
      <p>*prix TTC / pers</p>
    </div>
  </#if>
  <div class="link align-right"><a href="/${productUrl}" class="link-blueBottom">en savoir plus</a></div>
</div>
</@tag_cms_lookup>