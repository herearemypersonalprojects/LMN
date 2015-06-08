<#include "config.ftl">
<#assign
  productDisplayable = context.lookup("smallProductDisplayable")!productDisplayable
  productIndex = context.lookup("publishedProductIndex")!productIndex
  selectedAvailable = context.lookup("landingPageSelectedAvailable")!''
  selectedAvailableDaysOffset = context.lookup("landingPageSelectedAvailableDaysOffset")!''
  productUrl = productDisplayable.seoUrl?replace(productDisplayable.id + '/', productDisplayable.id + '#')!''
  landingPageProductSearchResponsebis = context.lookup("landingPageProductSearchResponse")!''
  destination = context.lookup("destination")!productDisplayable.destinationTitle
/>

<#if productIndex = 1>
<div id="offres_lp">
</#if>
  <div class="lp_offer"<#if productIndex = landingPageProductSearchResponsebis.getProductsCount()> id="last"</#if>>
      <div class="lp_prod_title">
          <strong>${productDisplayable.title}</strong>
      </div>
      <div class="details_image">
          <a href="http://front.lastminute.recette.orchestra-platform.com/${productUrl}?intcmp=lp_${destination}">
  <img alt="${productDisplayable.title}" src="${productDisplayable.imageUrl}"/>
          </a>
      </div>
      <div class="lp_details">
          <ul class="lp_details_ul">
              <#if (productDisplayable.getDescriptions())?has_content>
                  <li class="lp_text">${productDisplayable.getDescriptions(0)}</li>
              </#if>
              <li class="lp_text">
                  <strong>de : </strong>
                  <#import "/lib/directives.ftl" as check>
                  <@check.checkCharacterNumber characterNumber="22" characterValue="${productDisplayable.getDepartureCities()}" checkType="city"; cities>
                      <#if cities == "general">
                        Plusieurs villes de d&eacute;part possibles
                      <#else>
                        ${cities}
                      </#if>
                   </@check.checkCharacterNumber>
              </li>

              <#if productDisplayable?has_content && productDisplayable.getPensions()?has_content>

                  <@tag_cms_writeMessage key="${productDisplayable.getPensions()}" fileName="pension.properties";pension >
                    <#if pension != "">
                      <li class="lp_text">
                        <strong>formule : </strong>${pension}</li>
                      </li>
                    </#if>
                  </@tag_cms_writeMessage>

              </#if>

          </ul>
          <div class="lp_prices">
              <ul class="lp_prices_list">
                  <li class="text_price">&agrave;  partir de </li>
                  <li class="lp_text_price">
                      <a href="http://front.lastminute.recette.orchestra-platform.com/${productUrl}?intcmp=lp_${destination}">
                      <#if (productDisplayable.getPromoPercentage())?has_content>
                            <#if productDisplayable.getPromoPercentage() != 0>
                              <strong>${productDisplayable.promoPrice}&euro;*</strong>
                          <#else>
                                <strong>${productDisplayable.basePrice}&euro;*</strong>
                          </#if>
                      <#else>
                            <strong>${productDisplayable.basePrice}&euro;*</strong>
                      </#if>
                      </a>
                  </li>
                  <li class="text_price_2">(TTC par personne)</li>
              </ul>
              <#if (selectedAvailable?has_content)>
                <div class="lp_price_more">Tarif en derni&egrave;re minute: d&egrave;s
                    <strong>${selectedAvailable.ttcPrice}&euro;*</strong>TTC
                </div>
            </#if>
          </div>
          <div class="lp_next_date">
              <ul class="lp_next_list">
                  <li class="lp_next_1">Prochain d&eacute;part &agrave;  ce tarif :</li>
                  <li class="lp_next_2">${productDisplayable.baseDepartureCity}, le ${productDisplayable.baseDepartureDate}</li>
              </ul>
          </div>
          <div class="lp_fr_button">
              <a href="http://front.lastminute.recette.orchestra-platform.com/${productUrl}?intcmp=lp_${destination}">r&eacute;server</a>
          </div>
      </div>
  </div>
<#if productIndex = landingPageProductSearchResponsebis.getProductsCount()>
</div>
</#if>

