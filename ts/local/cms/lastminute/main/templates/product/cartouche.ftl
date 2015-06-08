<#assign
  showExpertBlock = context.lookup("showExpertBlock")!false
  productDisplayable = context.lookup("smallProductDisplayable")!productDisplayable
  brandData = context.lookup("brandData")!''
  productUrl = productDisplayable.seoUrl?replace(productDisplayable.id + '/', productDisplayable.id + '#')!''
/>
<#if productDisplayable.getDepartureCities()??>
  <div class="holder">
    <div class="result">
      <!-- MONITEUR_OK - do not remove this comment -->
      <a class="photo-link" href="/${productUrl}">
          <#if '' != productDisplayable.imageUrl>
            <img src="${productDisplayable.imageUrl}" width="241" height="170" alt="${productDisplayable.title}" class="alignleft" />
          </#if>
      </a>
      <div class="description">
        <@tag_cms_lookup name="seoData"; seoData>
          <#assign destinationCountry = productDisplayable.destinationTitle!'' />
          <#assign destinationCity = productDisplayable.destinationCity!'' />
          <#assign stayType = seoData.stayType!'' />
            <div class="heading stayType">
              <h2> ${stayType} ${destinationCountry} <#if destinationCountry != '' && destinationCity != ''>-</#if> ${destinationCity}</h2>
              <#import "/lib/directives.ftl" as resource>
              <#if showExpertBlock>
                <img src=<@resource.retrieveResource canonicalAddress="images" fileName="heading-text" fileExtension="gif"/> alt="" />
              </#if>
            </div>
          </@tag_cms_lookup>
        <div class="description-holder">
          <div class="text-block">
            <div class="title">
              <a href="/${productUrl}"><h4>${productDisplayable.title}</h4></a>
            </div>
           <#if brandData.brandName == 'lastminute' || brandData.brandName == 'CGOS'  || brandData.brandName == 'SELECTOUR'>
                <script type="text/javascript">
                   <!--
                     document.write('<script language="JavaScript" type="text/javascript" src="http://avis.lastminute.com/?ctrl=rating&act=jsproductopinion&prodid=${productDisplayable.reference}"></script>');
                   //-->
                </script>
            </#if>
            <#if (productDisplayable.getDescriptions())?has_content>
             <ul class="list">
              <#list productDisplayable.getDescriptions() as description>
                <li>${description}</li>
              </#list>
             </ul>
            </#if>
            <ul>
              <li title="${productDisplayable.getDepartureCities()}"><strong>ville de départ :</strong>
                <#import "/lib/directives.ftl" as check>
                <@check.checkCharacterNumber characterNumber="22" characterValue="${productDisplayable.getDepartureCities()}" checkType="city"; cities>
                  <#if cities == "general">
                    Plusieurs villes de départ possibles
                  <#else>
                    ${cities}
                  </#if>
                </@check.checkCharacterNumber>
              <#--
              </li>
              <li title="<#if (productDisplayable.getDurations())?has_content><#list productDisplayable.getDurations() as duration><#if duration_has_next>${duration.nights} nuits / <#else>${duration.nights} nuits</#if></#list></#if>">
              <strong>durée(s) du séjour :</strong>
              <#if (productDisplayable.getDurations())?has_content>
                 <#if (productDisplayable.getDurations()?size > 3)>
                      Plusieurs durées de séjour
                 <#else>
                  <#list productDisplayable.getDurations() as duration>
                      <#if duration_has_next>${duration.nights} nuits / <#else>${duration.nights} nuits</#if>
                  </#list>
                 </#if>
              </#if>
              </li>
              -->
              <@tag_cms_writeMessage key="${productDisplayable.getPensions()}" fileName="pension.properties";pension >
                <#if pension != "">
                  <li title="${pension}"><strong>pension :</strong>
                    ${pension}
                  </li>
                </#if>
             </@tag_cms_writeMessage>
            </ul>
            <#if (productDisplayable.getDescriptionButtom())?has_content>
            <strong class="slogan">${productDisplayable.descriptionButtom}</strong>
            </#if>
            <div class="nextStartZone">
              <span class="label">Prochain départ à ce tarif : </span>
              <span class="nextStart">${productDisplayable.baseDepartureCity}, le ${productDisplayable.baseDepartureDate}</span>
            </div>
          </div>
          <div class="price-block">
            <div class="price">
              <p>à partir de</p>
              <#if (productDisplayable.getPromoPercentage())?has_content>
               <#if (productDisplayable.getPromoPercentage() != 0)>
                  <div class="row">
                    <strong class="promo">${productDisplayable.promoPrice}<span class="asterisk">*</span></strong>
                    <span>${productDisplayable.basePrice}€</span>
                  </div>
                  <em>(TTC par personne)</em>
                  <span class="tooltip"><span>soit ${productDisplayable.diffencePrice} € économisés!</span></span>
                  <#if (productDisplayable.getPromoPercentage() > 5)>
            <span class="discount">- ${productDisplayable.promoPercentage} %</span>
          </#if>
                <#else>
                   <div class="row">
                    <strong class="promo">${productDisplayable.basePrice}<span class="asterisk">*</span></strong>
                   </div>
                   <em>(TTC par personne)</em>
                </#if>
              <#else>
                <div class="row">
                  <strong>${productDisplayable.basePrice}<span class="asterisk">*</span></strong>
                </div>
                <em>(TTC par personne)</em>
              </#if>
            </div>
            <strong>Réf. : ${productDisplayable.id} </strong>
            <a href="/${productUrl}" class="btn"><span>continuer <span class="ico_arrow-180">&nbsp;</span></span></a>
          </div>
        </div>
      </div>
      <div class="callcenterinfo">info et réservation au <span class="phoneNumber">0 892 68 61 00<sup>(1)</sup><span></div>
      <#if (productDisplayable.getPicto())?has_content>
        <img class="image-flash" src="${productDisplayable.picto.code}" width="106" height="20" alt="${productDisplayable.picto.label}" />
      </#if>

       <@tag_cms_renderContainer code="videoLinkContainer" />
    </div>
    <input type="checkbox" class="check comparatorCheckBox" id="pid_${productDisplayable.id}" />
  </div>
</#if>