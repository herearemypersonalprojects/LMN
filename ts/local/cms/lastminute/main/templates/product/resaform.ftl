<#assign
  disponibilities = context.lookup("disponibilties")!disponibilties
  bookingInfo = context.lookup("bookingInfo")!bookingInfo
  selectOptionList = context.lookup("selectOptionList")!selectOptionList
  x = 4
  y = 11
  toName = context.lookup("toName")!''
  toAddress = context.lookup("toAddress")!''
  toPostaleCode = context.lookup("toPostaleCode")!''
  toCity = context.lookup("toCity")!''
  toCountry = context.lookup("toCountry")!''
  partnerIdValue = context.lookup("partnerId")!''
  omnitureSource = context.lookup("omniture_source")!''
  tripType = context.lookup("tripType")!''
  hotelStars = context.lookup("hotelStars")!''
  depMonth = context.lookup("depMonth")!''
  destCityLabel = context.lookup("destCityLabel")!''
  brandData = context.lookup("brandData")!''
  flyAndDrive = context.lookup("flyAndDrive")!false
  productDisplayable = context.lookup("smallProductDisplayable")!productDisplayable
  receptifName=context.lookup("receptifName")!''
  receptifTel=context.lookup("receptifTel")!''
  receptifEmail=context.lookup("receptifEmail")!''
  receptifAddress=context.lookup("receptifAddress")!''
  transferLabel=context.lookup("transferLabel")!''
  transferPrice=context.lookup("transferPrice")!'0'
  mealPlanLabel=context.lookup("mealPlanLabel")!''
  mealPlanPrice=context.lookup("mealPlanPrice")!'0'

/>
<#import "/lib/directives.ftl" as util>
<div id="book-stay" class="book-stay">
  <h2>r&eacute;server ce s&eacute;jour</h2>
  <div id="reserve-block-waitingDom" class="contain-book">
    <span class="waitingDom">
      <img class="loader" src="/shared-cs/lastminute-catalog/images/ajax-loader.gif" alt="loader">
    </span>
  </div>

  <div id="contain-book" style="display:none" class="contain-book">
    <!-- reserve form -->
    <#if brandData.getBrandName() = 'lastminute'>
      <form class="reserve-form" id="reservationForm" action=<@util.retrieveConfigValue key="taglib_ReservationUrl"/> >
    <#else>
      <form class="reserve-form" id="reservationForm" action=<@util.retrieveConfigValue key="brand_taglib_ReservationUrl"/> >
    </#if>
      <input id="catalogCode" type="hidden" value="${bookingInfo.catalogCode}" name="catalogCode" />
      <input type="hidden" value="${bookingInfo.toCode}" name="toCode" />
      <input type="hidden" value="${bookingInfo.provider}" name="provider" />
      <input name="productUrl" id="productUrl" value="" type="hidden" />

      <#if brandData.getBrandName() != 'SELECTOUR'>
        <#if productDisplayable.getToCode()="LASTMINUTE" >
          <input name="reservationProfile.channel.code" id="channel" value="PREPACKAGE" type="hidden" />
        <#else>
          <@tag_cms_lookup name="channel";channel>
            <#if flyAndDrive>
              <input name="reservationProfile.channel.code" id="channel" value="FLYANDDRIVE" type="hidden" />
            <#else>
              <input name="reservationProfile.channel.code" id="channel" value="${channel}" type="hidden" />
            </#if>
          </@tag_cms_lookup>
        </#if>
      <#else>
        <#assign defaultResConfigCode = context.lookup("defaultResConfigCode")!'' />
        <#if defaultResConfigCode = ''>
         <input name="reservationProfile.channel.code" id="channel" value="SELECTOURB2B" type="hidden" />
        <#else>
          <input name="reservationProfile.channel.code" id="channel" value="SELECTOURB2B_INT" type="hidden" />
        </#if>
      </#if>

      <#if brandData.getBrandName() = 'lastminute'>
       <input type="hidden" value="<@util.retrieveConfigValue key="MB_URL"/>product.cms?pid=${bookingInfo.catalogCode}&showdesc=true" id="productDetailsUrl1" name="productDetailsUrl">
      <#else>
       <input type="hidden" value="<@util.retrieveConfigValue key="${brandData.getBrandName()}_MB_URL"/>product.cms?pid=${bookingInfo.catalogCode}&showdesc=true" id="productDetailsUrl1" name="productDetailsUrl">
      </#if>

      <#assign productPromoUrl = context.lookup("productPromoUrl")!'' />
      <#assign promoLabel = context.lookup("promoLabel")!'' />

      <h3><span class="block-numbers">1</span>Compl&eacute;tez le nombre de voyageurs :</h3>
      <div class="item firstChild">
        <ul class="spinner-wrap">
          <li><label for="spinner_room-1_pax-adults" class="spinner-label">Adultes</label></li>
          <li>
            <div class="container spinner" data-spinner-min="1" data-spinner-max="9">
              <div class="block-btn less spinner-remove disabled"></div>
              <input type="text" id="spinner_room-1_pax-adults" class="spinner-value hashUrlParam" value="1" readonly="readonly">
              <div class="overlay hidden"> </div>
              <div class="block-btn plus spinner-add"></div>
            </div>
          </li>
        </ul>
      </div>
      <#if bookingInfo.forbiddenChild = false>
      <div class="item">
        <ul class="spinner-wrap">
          <li><label for="spinner_room-1_pax-childs" class="spinner-label hashUrlParam">Enfants (2-11 ans)</label></li>
          <li id="nbChildren">
            <div class="container spinner" data-spinner-min="0" data-spinner-max="5">
              <div class="block-btn less spinner-remove disabled"></div>
              <input type="text" id="spinner_room-1_pax-childs" class="spinner-value" value="0" readonly="readonly">
              <div class="overlay hidden"> </div>
              <div class="block-btn plus spinner-add"></div>
            </div>
          </li>
        </ul>
      </div>
      </#if>
      <#if bookingInfo.forbiddenBaby = false>
      <div class="item">
        <ul class="spinner-wrap">
          <li><label for="spinner_room-1_pax-babys" class="spinner-label hashUrlParam">B&eacute;b&eacute;s (< de 2 ans) </label></li>
          <li>
            <div class="container spinner" data-spinner-min="0" data-spinner-max="3">
              <div class="block-btn less spinner-remove disabled"></div>
              <input type="text" id="spinner_room-1_pax-babys" class="spinner-value" value="0" readonly="readonly">
              <div class="overlay hidden"></div>
              <div class="block-btn plus spinner-add"></div>
            </div>
          </li>
        </ul>
      </div>
      </#if>

      <div class="clearBlocks"> </div>
      <#list 0..x as i>
      <div class="row2 ageChildrenBack" style="display: none;" id="ageChildrenBack${i}">
        <label for="ageChildren${i}">${i + 1}<#if i == 0>er<#else>&egrave;me</#if> enfant - &acirc;ge &agrave; la date de retour</label>
        <select id="ageChildren${i}" class="resaOption hashUrlParam">
          <#list 2..y as j>
            <option value="${j}">${j}</option>
          </#list>
        </select>
        <span>an(s)</span>
      </div>
      </#list>

      <div class="clearBlocks"> </div>
      <h3><span class="block-numbers">2</span>Personnalisez votre s&eacute;jour :</h3>
      <div class="departure-month item firstChild">
        <label for="">Mois de d&eacute;part</label>
        <select id="slt_departureMonth_resa" class="resaOption hashUrlParam">
          <#if (selectOptionList.getDepartureDates())?has_content>
            <#list selectOptionList.getDepartureDates() as date>
              <option value="${date.code}" <#if date.getSelected()>selected="selected" class="defaultValue"</#if> >${date.label}</option>
            </#list>
          </#if>
        </select>
      </div>
      <div class="departure-city item">
        <label for="">Ville de d&eacute;part</label>
        <select id="slt_city" class="resaOption hashUrlParam">
          <#if (selectOptionList.getDepartureCities())?has_content>
            <#list selectOptionList.getDepartureCities() as city>
             <#if (city.getSelected())?has_content>
              <option value="${city.code}" selected="selected" class="defaultValue">${city.label}</option>
             <#else>
               <option value="${city.code}">${city.label}</option>
             </#if>
            </#list>
          </#if>
        </select>
      </div>
      <div class="departure-duration item">
        <label for="">Dur&eacute;e de s&eacute;jour</label></li>
        <select id="slt_time" class="resaOption hashUrlParam">
          <#if (selectOptionList.getDepartureDuration())?has_content>
            <#list selectOptionList.getDepartureDuration() as duration>
             <#if (duration.getSelected())?has_content>
              <option value="${duration.code}" selected="selected" class="defaultValue">
                <#if flyAndDrive>
                  ${duration.label?substring(0,duration.label?index_of('/'))}
                <#else>
                  ${duration.label}
                </#if>
              </option>
             <#else>
              <option value="${duration.code}">
                <#if flyAndDrive>
                  ${duration.label?substring(0,duration.label?index_of('/'))}
                <#else>
                  ${duration.label}
                </#if>
              </option>
             </#if>
            </#list>
          </#if>
        </select>

      <#--
        <@tag_cms_lookup name="7plus1">
          <div class="offers">
            <a href="#" class="unlink"><span class="ico-info"> </span> <strong>2&egrave;me semaine pour 1 &euro;</strong><br> s&eacute;lectionnez 15 jours / 14 nuits</a>
          </div>
        </@tag_cms_lookup>
      -->

        <#if productPromoUrl != '' && promoLabel != ''>
          <div class="offers">
            ${promoLabel} <a href="${productPromoUrl}" class="button">Cliquez ici</a>
          </div>
        </#if>
      </div>

      <@tag_cms_lookup name="userProfile";userProfile>
        <#assign firstName = userProfile.firstName!''/>
        <#assign lastName = userProfile.lastName!''/>
        <#assign civility = userProfile.personalTitle!''/>
        <#assign email = userProfile.mail!''/>
        <#assign phoneNumber = userProfile.phoneNumber!''/>
        <#assign street = userProfile.street!''/>
        <#assign city = userProfile.city!''/>
        <#assign postalCode = userProfile.postalCode!''/>
        <#if civility != ''>
          <input type="hidden" name="agent.civility" value="${civility}" />
        </#if>
        <#if lastName != ''>
          <input type="hidden" name="agent.lastName" value="${lastName}" />
        </#if>
        <#if firstName != ''>
          <input type="hidden" name="agent.firstName" value="${firstName}" />
        </#if>
        <#if email != ''>
          <input type="hidden" name="agent.email" value="${email}" />
        </#if>
        <#if phoneNumber != ''>
          <input type="hidden" name="client.tel1" value="${phoneNumber}" />
        </#if>
        <#if street != ''>
          <input type="hidden" name="client.facturationStreet" value="${street}" />
        </#if>
        <#if postalCode != ''>
          <input type="hidden" name="client.facturationPostalCode" value="${postalCode}" />
        </#if>
        <#if city != ''>
          <input type="hidden" name="client.facturationCity" value="${city}" />
        </#if>
      </@tag_cms_lookup>

      <@tag_cms_lookup name="user";user>
        <#assign userCode = user.code!''/>
        <#if userCode != ''><input type="hidden" name="reservationProfile.organization.code" value="${userCode}" /></#if>
      </@tag_cms_lookup>
      <#assign complementaryValue = '' />

      <#assign complementaryValue = 'toName=${toName!}' />

       <#if toAddress != ''>
        <#assign complementaryValue = complementaryValue + '&toAddress=${toAddress}' />
      </#if>
       <#if toPostaleCode != ''>
        <#assign complementaryValue = complementaryValue + '&toPostaleCode=${toPostaleCode}' />
      </#if>
       <#if toCity != ''>
        <#assign complementaryValue = complementaryValue + '&toCity=${toCity}' />
      </#if>
       <#if toCountry != ''>
        <#assign complementaryValue = complementaryValue + '&toCountry=${toCountry}' />
      </#if>
      <#if brandData.getBrandName() != ''>
        <#assign complementaryValue = complementaryValue + '&brand=${brandData.getBrandName()}' />
      </#if>
      <#if tripType != ''>
        <#assign complementaryValue = complementaryValue + '&tripType=${tripType}' />
      </#if>
      <#if hotelStars != ''>
        <#assign complementaryValue = complementaryValue + '&hotelStars=${hotelStars}' />
      </#if>
      <#if depMonth != ''>
        <#assign complementaryValue = complementaryValue + '&depMonth=${depMonth}' />
      </#if>
      <#if destCityLabel != ''>
        <#assign complementaryValue = complementaryValue + '&destCityLabel=${destCityLabel}' />
      </#if>
      <#if receptifName != ''>
        <#assign complementaryValue = complementaryValue + '&receptifName=${receptifName}' />
      </#if>
      <#if receptifTel != ''>
        <#assign complementaryValue = complementaryValue + '&receptifTel=${receptifTel}' />
      </#if>
      <#if receptifEmail != ''>
        <#assign complementaryValue = complementaryValue + '&receptifEmail=${receptifEmail}' />
        <input type="hidden" name="agencyEmail" value="${receptifEmail}" />

      </#if>
      <#if receptifAddress != ''>
        <#assign complementaryValue = complementaryValue + '&receptifAddress=${receptifAddress}' />
      </#if>
      <#if transferLabel != ''>
        <input id ="pp_transfer" type="hidden" name="complementaryOption[0].id" value="0" />
        <input type="hidden" name="complementaryOption[0].code" value="TRS" />
        <input type="hidden" name="complementaryOption[0].type" value="SUPP" />
        <input type="hidden" name="complementaryOption[0].description" value="${transferLabel}" />
        <input type="hidden" name="complementaryOption[0].quantityMax" value="1" />
        <input type="hidden" name="complementaryOption[0].quantityMin" value="1" />
        <input id="pp_transfer_price" type="hidden" name="complementaryOption[0].inventoryPrice.value" value="${transferPrice}" />
        <#assign complementaryValue = complementaryValue + '&transferLabel=${transferLabel}' />
      </#if>
      <#if mealPlanLabel != ''>
        <input type="hidden" name="complementaryOption[1].id" value="0" />
        <input type="hidden" name="complementaryOption[1].code" value="MEAL" />
        <input type="hidden" name="complementaryOption[1].type" value="SUPP" />
        <input type="hidden" name="complementaryOption[1].description" value="${mealPlanLabel}" />
        <input type="hidden" name="complementaryOption[1].quantityMax" value="1" />
        <input type="hidden" name="complementaryOption[1].quantityMin" value="1" />
        <input type="hidden" name="complementaryOption[1].inventoryPrice.value" value="${mealPlanPrice}" />
      </#if>

      <#if complementaryValue != ''>
        <input type="hidden" id="complementaryParameters" name="complementaryParameters" value="${complementaryValue}" />
      </#if>
    </form>
  </div>
</div>

<!-- ******** BEGIN SUMMARY-BOOKSTAY ************************************** -->
<div class="summary-bookStay">

</div>
<!-- END SUMMARY-BOOKSTAY -->
<!-- ******** BEGIN TABLE-DEPARTURE ************************************** -->
<#include "/product/calendarResa.ftl">
<!-- END TABLE-DEPARTURE -->

<div class="description-infos" id="descriptionProduct">
  <p><strong>Le saviez vous ?</strong> si vous r&eacute;servez &agrave; + de 30 jours du d&eacute;part, seul un acompte de 30% sera pr&eacute;lev&eacute;, le solde sera automatiquement pr&eacute;lev&eacute; sur votre carte bancaire 30 jours avant le d&eacute;part</p>

  <#if (productSupDisplayable.getExperts())?has_content>
    <div class="block-infos">
      <h3>l'avis de notre expert: Afin d'évaluer cet établissement de façon transparente,
        nous avons recueilli l'avis de notre expert</h3>
      <p>${expert.content}</p>
    </div>
  </#if>

  <#if (productDisplayable.getImagePub())?has_content || (productDisplayable.getUriPub())?has_content || (productDisplayable.getContentPub())?has_content>
    <div class="block-infos">
      <#if (productDisplayable.getImagePub())?has_content>
        <img src="${productDisplayable.getImagePub()}" alt="">
      </#if>
      ${productDisplayable.getContentPub()}
    </div>
  </#if>

  <div class="block-infos priceAlert">
    <div class="alert-price"><span> </span>alertes prix <span class="arrow"> </span></div>
    <p class="alertPrice">Inscrivez-vous &Agrave; notre <strong>Alerte Prix</strong>, si vous souhaitez &ecirc;tre inform&eacute;s des &eacute;volutions du prix de ce s&eacute;jour</p>
    <a target="_blank" href="<@util.retrieveConfigValue key="ALERTE_PRICE_URL"/>?ids=${bookingInfo.toCode}&urls=${productDisplayable.imageUrl}&r=http://${brandData.brandServerName}/${productDisplayable.seoUrl}&tol=${productDisplayable.toCode}&idfrom=3" class="link-pink">Je m'inscris !</a>
  </div>

</div>