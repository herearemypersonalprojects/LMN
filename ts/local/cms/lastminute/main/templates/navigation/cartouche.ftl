<#assign
  showExpertBlock = context.lookup("showExpertBlock")!false
  productDisplayable = context.lookup("smallProductDisplayable")!productDisplayable
/>
<#import "/lib/directives.ftl" as check>
<div class="holder">
  <div class="result">
    <!-- MONITEUR_OK - do not remove this comment -->
    <a class="photo-link" href="/${productDisplayable.seoUrl}">
        <#if '' != productDisplayable.imageUrl>
          <img src="${productDisplayable.imageUrl}" width="241" height="170" alt="${productDisplayable.title}" class="alignleft" />
        </#if>
    </a>
    <div class="description">
      <#assign stayType = productDisplayable.stayType!'' />
      <#if stayType != '' || productDisplayable.destinationTitle != ''  || showExpertBlock>
        <div class="heading">
          <h2>${stayType} ${productDisplayable.destinationTitle}</h2>
          <#if showExpertBlock>
            <img src="/shared-cs/images/heading-text.gif" width="64" height="22" alt="" />
          </#if>
        </div>
      </#if>
      <div class="description-holder">
        <div class="text-block">
          <div class="title">
            <a href="/${productDisplayable.seoUrl}"><h4>${productDisplayable.title}</h4></a>
          </div>
          <!--p>Note client  <a href="#">5/5</a> <a href="#" class="reviews">lire les avis</a></p-->
          <#if (productDisplayable.getDescriptions())?has_content>
           <ul class="list">
            <#list productDisplayable.getDescriptions() as description>
              <li>${description}</li>
            </#list>
           </ul>
          </#if>
          <ul>
            <li title="${productDisplayable.getDepartureCities()}"><strong>ville de départ :</strong>
              <@check.checkCharacterNumber characterNumber="22" characterValue="${productDisplayable.getDepartureCities()}" checkType="city"; cities>
                <#if cities == "general">
                  Plusieurs villes de départ possibles
                <#else>
                  ${cities}
                </#if>
              </@check.checkCharacterNumber>
            </li>
            <li title="<#if (productDisplayable.getDurations())?has_content><#list productDisplayable.getDurations() as duration><#if duration_has_next>${duration.nights} nuits / <#else>${duration.nights} nuits</#if></#list></#if>">
            <strong>durées du séjour :
            <#if (productDisplayable.getDurations())?has_content>
               <#if (productDisplayable.getDurations()?size > 3)>
                    Plusieurs durées de séjour
               <#else>
                <#list productDisplayable.getDurations() as duration>
                    <#if duration_has_next>${duration.nights} nuits / <#else>${duration.nights} nuits</#if>
                </#list>
               </#if>
            </#if>
            </strong></li>
            <li title="${productDisplayable.getPensions()}"><strong>pension :</strong>
              <@check.checkCharacterNumber characterNumber="22" characterValue="${productDisplayable.getPensions()}" checkType="pension"; pensions>
                ${pensions}
              </@check.checkCharacterNumber>
            </li>
          </ul>
          <#if (productDisplayable.getDescriptionButtom())?has_content>
          <strong class="slogan">${productDisplayable.descriptionButtom}</strong>
          </#if>
        </div>
        <div class="price-block">
          <div class="price">
            <p>à partir de</p>
            <#if (productDisplayable.getPromoPercentage())?has_content>
              <div class="row">
                <strong class="promo">${productDisplayable.promoPrice}<span class="asterisk">*</span></strong>
                <span>${productDisplayable.basePrice}€</span>
              </div>
              <em>( TTC par personne)</em>
              <span class="tooltip"><span>soit ${productDisplayable.diffencePrice} € économisés!</span></span>
              <span class="discount">- ${productDisplayable.promoPercentage} %</span>
            <#else>
              <div class="row">
                <strong>${productDisplayable.basePrice}<span class="asterisk">*</span></strong>
              </div>
              <em>( TTC par personne)</em>
            </#if>
          </div>
          <strong>Réf. : ${productDisplayable.reference} </strong>
          <a href="/${productDisplayable.seoUrl}" class="btn"><span>continuer</span></a>
        </div>
      </div>
    </div>
    <#if (productDisplayable.getPicto())?has_content>
      <img class="image-flash" src="/shared-cs/images/image-${productDisplayable.picto}.png" width="106" height="20" alt="" />
    </#if>
  </div>
  <input type="checkbox" class="check comparatorCheckBox" id="pid_${productDisplayable.id}" />
</div>
