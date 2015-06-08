<#import "/directives.ftl" as util>
<@tag_cms_lookup name="departure"; departure>
  <#assign pPrice = departure.DProduct.basePrice>
  <#if departure.DProduct.promoPrice??>
    <#assign pPrice = departure.DProduct.promoPrice>
  </#if>
  <#assign depDate = context.lookup('dayDepartures').departureDate?string("yyyy-MM-dd")>
  <#assign depCity = context.lookup('depCity')!>
  <#assign duration = departure.avail.durationInDays + "-" + departure.avail.durationInNights>

  <li data="/voyage/${departure.DProduct.id}/${depCity}/${duration}/product.html?depDate=${depDate}&dur=${duration}">
    <div class="meta">
      <#if departure.DProduct.promoPercentage??>
        <span class="disc">- ${departure.DProduct.promoPercentage}<span>%</span></span>
      <#else>
        <span class="status">derni&egrave;res places</span>
      </#if>
      <h2><a href="#">${departure.DProduct.title}</a></h2>
      <#if departure.DProduct.destinationTitle??>
        <h3>
          <a href="#">
            ${departure.DProduct.destinationTitle?cap_first}
            <#if departure.DProduct.destinationCity??> - ${departure.DProduct.destinationCity?cap_first}</#if>
          </a>
        </h3>
      </#if>
    </div>
    <div class="entry">
      <div class="price-box">
        <span class="alt">&agrave; partir de</span>
        <div class="row">
          <span class="price <#if departure.DProduct.promoPercentage??>red</#if>">${pPrice}<sup>&euro;</sup></span>
          TTC / <br />pers.
        </div>
      </div>
      <a href="#" class="btn-add" data="${departure.DProduct.id}#${departure.DProduct.title}#${pPrice}#${departure.avail.durationInDays}-${departure.avail.durationInNights}">add</a>
      <a href="#" class="btn-email" data="${departure.DProduct.id}#${departure.DProduct.title}">email</a>
      <div class="text-box">
        <p>Au d&eacute;part de <@tag_cms_writeMessage key="${depCity}" fileName="cityLabels.properties" /></p>
        <p>
          <@tag_cms_writeMessage key="${departure.DProduct.pensions}" fileName="pension.properties"; pension>
            <#if pension != "">${pension}</#if>
          </@tag_cms_writeMessage>
        </p>
      </div>
      <div class="displayMsgFavorite hidden"></div>
    </div>
  </li>
</@tag_cms_lookup>