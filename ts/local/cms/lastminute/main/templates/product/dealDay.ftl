<@tag_cms_lookup name="dealDayDisplayable"; dealDayDisplayable>
<#assign destinationCountry = dealDayDisplayable.destinationTitle!''
      title = dealDayDisplayable.title!''
     productUrl = dealDayDisplayable.seoUrl!'#' />
  <div id="blkSpecialOffer" class="blkSpecialOffer">
    <div class="actionsTitle open">OFFRE IMBATTABLE !
      <span id="arrowLink">
      </span>
      <span data-toggle-text="afficher,masquer" class="link">
        masquer
      </span>
    </div>

    <div class="inner-special-offer hidden">
        <div class="item-offer">
            <a href="/${productUrl}?intcmp=offre_imbattable">
                <img alt="" src="${dealDayDisplayable.getImageUrl()}">
                <div class="middle">
                    <strong>En exclusivit&eacute;</strong>
                    <p class="title">${destinationCountry} - ${title}</p>
                    <#if (dealDayDisplayable.getPromoPercentage())?has_content>
                      <#if (dealDayDisplayable.getPromoPercentage() != 0)>
                        <p class="percentage">${dealDayDisplayable.promoPercentage} % de r&eacute;duction</p>
                      </#if>
                    </#if>
                </div>
                <div class="right">
                    <p>&agrave; partir de</p>
                    <#if (dealDayDisplayable.getPromoPercentage())?has_content>
                       <#if (dealDayDisplayable.getPromoPercentage() != 0)>
                          <p class="priceOffer">${dealDayDisplayable.promoPrice} &euro;*</p>
                          <p class="oldPrice">au lieu de ${dealDayDisplayable.basePrice} &euro;</p>
                        <#else>
                          <p class="priceOffer">${dealDayDisplayable.basePrice} &euro;*</p>
                        </#if>
                    <#else>
                        <p class="priceOffer">${dealDayDisplayable.basePrice} &euro;*</p>
                    </#if>
                </div>
                <div class="arrow"> </div>
            </a>
        </div>
    </div>
  </div>
</@tag_cms_lookup>
