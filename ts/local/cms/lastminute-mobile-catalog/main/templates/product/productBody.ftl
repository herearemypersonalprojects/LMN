<#assign productExists = context.lookup('product')??>
<#if productExists>
  <#assign product = context.lookup('product')>
  <#assign duration = context.lookup('pDuration')!>
</#if>

<#if productExists>
  <section class="slider">
      <div class="flexslider">
          <ul class="slides">
              <#list 0..product.diaporamaItemsUrls.size()-1 as i>
                <li <#if i == 0>class="active"</#if>><img src="${product.diaporamaItemsUrls.get(i)}" alt="diaporama produit - image ${i + 1}" /></li>
              </#list>
          </ul>
      </div>
  </section>

  <section class="about-box">
    <div class="price-col">
      <#if product.discount??><span class="disc">${product.discount}<span>%</span></span></#if>
      <span class="alt">&agrave; partir de</span><span class="price">${product.price?number}<sup>&euro;</sup></span><p>TTC / pers.</p>
      <#if product.crossedPrice??><span class="alt2">au lieu de <mark>${product.crossedPrice}&euro;</mark></span></#if>
    </div>
    <div class="text-box">
      <h2>${product.title}</h2>
      <h3>${product.location}</h3>
      <div class="entry">
        <div class="btns">
          <a href="#" id="btn-add" class="btn-add" data="${product.product.code}#${product.title}#${product.price?number}#${duration}">add</a>
          <a href="#" id="btn-share" class="btn-email" data="${product.product.code}#${product.title}">email</a>
        </div>
        <div class="text">
          <p>${product.nbNights} nuits</p>
          <@tag_cms_writeMessage key="pension.${product.mealPlanCode}" fileName="messages.properties";pension>
            <#if pension != ""><p>${pension}</p></#if>
          </@tag_cms_writeMessage>
        </div>
        <div class="displayMsgFavorite hidden"></div>
      </div>
    </div>
  </section>

  <@tag_cms_renderContainer code="resaFormContainer" />

  <div class="promo-box">
    <div class="add-block">R&eacute;f&eacute;rence produit <strong>${product.product.code}</strong></div>
    <div class="text-box">
      <strong class="title">BESOIN D'AIDE POUR R&Eacute;SERVER ? Appeler l'assistance </strong>
      <a href="tel:0892051523" class="btn-phone">0 892 05 15 23 <span>0,34&euro; TTC / min</span></a>
    </div>
  </div>

  <ul class="text-list accordion">

    <#if (product.editoDescriptions?has_content)>
      <li class="active">
        <a href="#" class="opener"><span>descriptif du s&eacute;jour</span></a>
        <div class="slide">
          <div class="slide-holder">
            <article>
              <#list product.editoDescriptions as description>
                <h3>${description.title}</h3>
                <p>${description.content}</p>
              </#list>
              <a href="#header">haut de page</a>
            </article>
          </div>
        </div>
      </li>
    </#if>

    <#if (product.editoIncludes?has_content || product.editoInformations?has_content)>
      <li>
        <a href="#" class="opener"><span>conditions tarifaires</span></a>
        <div class="slide">
          <div class="slide-holder">
            <article>
              <#if (product.editoIncludes?has_content)>
                <#list product.editoIncludes as include>
                  <h3>${include.title}</h3>
                  <p>${include.content}</p>
                </#list>
              </#if>
              <#if (product.editoInformations?has_content)>
                <#list product.editoInformations as information>
                  <h3>${information.title}</h3>
                  <p>${information.content}</p>
                </#list>
              </#if>
              <a href="#header">haut de page</a>
            </article>
          </div>
        </div>
      </li>
    </#if>

    <#if (product.passengersInfos?has_content || product.formalities?has_content)>
      <li>
        <a href="#" class="opener"><span>informations voyageurs</span></a>
        <div class="slide">
          <div class="slide-holder">
            <article>
              <#if (product.passengersInfos?has_content)>
                <#list product.passengersInfos as pInformation>
                  <h3>${pInformation.title}</h3>
                  <p>${pInformation.content}</p>
                </#list>
              </#if>
              <#if (product.formalities?has_content)>
                <#list product.formalities as formality>
                  <h3>${formality.title}</h3>
                  <p>${formality.content}</p>
                </#list>
              </#if>
              <a href="#header">haut de page</a>
            </article>
          </div>
        </div>
      </li>
    </#if>

    <#if (product.flightsInfos?has_content)>
      <li>
        <a href="#" class="opener"><span>informations vols</span></a>
        <div class="slide">
          <div class="slide-holder">
            <article>
              <#list product.flightsInfos as flightInfo>
                <h3>${flightInfo.title}</h3>
                <p>${flightInfo.content}</p>
              </#list>
              <a href="#header">haut de page</a>
            </article>
          </div>
        </div>
      </li>
    </#if>

  </ul>

<#else>
  <div class="info-section">
    <p>D&eacute;sol&eacute;, cette offre n'est plus disponible.</p>
  </div>
</#if>

