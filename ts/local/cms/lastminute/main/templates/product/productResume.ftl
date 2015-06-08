<#assign
  productDisplayable = context.lookup("smallProductDisplayable")!productDisplayable
  flyAndDrive = context.lookup("flyAndDrive")!false
  seoData = context.lookup("seoData") !''
  stayType = productDisplayable.stayType!''
  brandData = context.lookup("brandData")!''
/>

<div class="colCenter">
  <h1>${productDisplayable.title}</h1>
  <div id="price-block-waitingDom" class="price-block">
    <span class="waitingDom">
      <img class="loader" src="/shared-cs/lastminute-catalog/images/ajax-loader.gif" alt="loader">
    </span>
  </div>
  <div id="price-block" style="display:none" class="price-block" itemprop="offerDetails" itemscope itemtype="http://data-vocabulary.org/Offer-aggregate">
    <#if (productDisplayable.getPromoPercentage())?has_content &&
      (productDisplayable.getPromoPercentage() > 5)>
        <div class="economy">&eacute;conomie de ${productDisplayable.promoPercentage}%<span> </span></div>
        <div class="price" itemprop="lowPrice">${productDisplayable.promoPrice}</div>
        <div class="euro">&euro;*</div>
        <div class="ttc">ttc <strong>/pers</strong></div>
        <div class="actual-price" itemprop="highPrice">au lieu de ${productDisplayable.basePrice}&euro;</div>
    <#else>
        <div class="economy visibleHidden">&eacute;conomie de 23%<span> </span></div>
        <div class="price" itemprop="lowPrice" id="finalPrice">${productDisplayable.basePrice}</div>
        <div class="euro">&euro;*</div>
        <div class="ttc">ttc <strong>/pers</strong></div>
        <div class="actual-price visibleHidden">au lieu de 9999&euro;</div>
    </#if>
    <meta itemprop="currency" content="EUR">
    <a href="#table-departure" class="btn-pink">voir les prix</a>
  </div>
  <div class="next-departure">Prochains d&eacute;p. &agrave; ce tarif :
    <strong>${productDisplayable.baseDepartureCity}, le ${productDisplayable.baseDepartureDate}</strong>
  </div>
</div>

<div class="colRight">
  <div class="description-block">
    <input id="catalogRef" type="hidden" value="${productDisplayable.reference?lower_case}" name="catalogRef" />
    <div class="title-stay">
      ${stayType} <#if seoData.getDestinationCountry()?has_content>${seoData.destinationCountry}</#if>
    </div>

    <#if (productDisplayable.getDescriptions())?has_content>
      <ul>
        <#list productDisplayable.getDescriptions() as description>
          <li>${description}</li>
        </#list>
      </ul>
    </#if>

    <#if (productDisplayable.getDescriptionButtom())?has_content>
      <strong class="slogan">${productDisplayable.descriptionButtom}</strong>
    </#if>
   <#if productDisplayable.getToCode() = "LASTMINUTE">
     <img class="veryFlexy" src="/shared-cs/lastminute-catalog/images/very-flexy.png" alt="">
   </#if>
  </div>
  <div class="departure-block">
    <p>
      <strong>villes de d&eacute;part :</strong>
      <#import "/lib/directives.ftl" as check>
      <@check.checkCharacterNumber characterNumber="22" characterValue="${productDisplayable.getDepartureCities()}" checkType="city"; cities>
        <#if cities == "general">
          Plusieurs villes de d&eacute;part possibles
        <#else>
          ${cities}
        </#if>
      </@check.checkCharacterNumber>
    </p>

    <#if !flyAndDrive>
      <p>
        <strong>Dur&eacute;e du s&eacute;jour :</strong>
         <#if (productDisplayable.getDurations())?has_content>
           <#if (productDisplayable.getDurations()?size > 3)>
                Plusieurs dur&eacute;es de s&eacute;jour
           <#else>
            <#list productDisplayable.getDurations() as duration>
             <#if duration_has_next>
               ${duration.nights} nuits /
              <#else>
               ${duration.nights} nuits
              </#if>
            </#list>
          </#if>
        </#if>
      </p>
    </#if>
    <#if productDisplayable?has_content && productDisplayable.getPensions()?has_content>
      <@tag_cms_writeMessage key="${productDisplayable.getPensions()}" fileName="pension.properties";pension >
        <#if pension != ""><p><strong>Pension :</strong> ${pension}</#if></p>
      </@tag_cms_writeMessage>
    </#if>
  </div>

  <div id="hieuroi">display lightbox</div>

  <div id="lightbox" class="hidden">
    <p>Fermer</p>
      <div id="content">
          C'est quelque chose C'est quelque chose C'est quelque chose C'est quelque chose
      </div>
  </div>

  <div class="help-phone tablet-mini-hidden">
    <span> </span>
    <p>
      besoin d&rsquo;aide pour r&eacute;server ?
      <br>appelez le
      <strong>
        <#if brandData.brandName == 'CGOS'> 0892 707 200 <#else> 0 892 68 61 00 </#if>
      </strong>
      <em>(0.34 &euro; TTC / min)</em>
      <br>r&eacute;f&eacute;rence produit : <strong>${productDisplayable.id}</strong>
    </p>
  </div>



  <#if brandData.brandName == 'lastminute' || brandData.brandName == 'CGOS'  || brandData.brandName == 'SELECTOUR'>
  <#if productDisplayable.ratingImageUrl?has_content && productDisplayable.rating?has_content && productDisplayable.num_reviews?has_content>
  <div class="customer-reviews">
    <div class="title">Avis clients :</div>
    <div class="advisor-block">
      <div class="img-block">
        <a href="#details-customerReviews"><img alt="" src="${productDisplayable.ratingImageUrl}"></a>
      </div>
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
      <p>Bas&eacute; sur <strong>${productDisplayable.num_reviews}</strong> &eacute;valuations des clients</p>
    </div>
    <a class="link-blue" href="#details-customerReviews">afficher les &eacute;valuations</a>
  </div>
  <#--
    <#import "/lib/includeStaticFileDirective.ftl" as engine>
    <@engine.includeStaticFile fileName="/shared/cs/web/lastminute-catalog/reviews/${productDisplayable.reference?lower_case}/reviewSummary.html";insertedFile>
      ${insertedFile}
    </@engine.includeStaticFile>
  -->
  </#if>
  </#if>
</div>
