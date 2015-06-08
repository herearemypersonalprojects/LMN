
<@tag_cms_lookup name="departure"; departure>
  <#assign idxProduct = context.lookup("idx")!0
           depCity = context.lookup("depCity")!'' />

  <#assign trackingDepCityCode>
    <@tag_cms_writeMessage key="${depCity}" fileName="intcmpCityLabels.properties" />
  </#assign>

  <#assign productUrl = 'http://voyage.lastminute.com/' + departure.seoUrl + '/' + departure.DProduct.id + '?intcmp=dm_' + trackingDepCityCode + '#city=' + depCity/>

  <tr class="gen_tableRow <#if (idxProduct % 2) == 0>None<#else>odd</#if>">
    <td class="tabcell_1"><a href="${productUrl}"><strong>${departure.avail.departureDate.toDate()?string("EEE dd/MM")}</strong></a></td>
    <td class="tabcell_2"><a href="${productUrl}"><strong><#if departure.DProduct.destinationTitle??>${departure.DProduct.destinationTitle?cap_first}</#if></strong></a></td>
    <td class="tabcell_3"><a href="${productUrl}"><span><#if departure.DProduct.destinationCity??>${departure.DProduct.destinationCity?cap_first}</#if></span></a></td>
    <td class="tabcell_4">
      <a href="${productUrl}"><span>
        ${departure.DProduct.title}
        <@tag_cms_writeMessage key="${departure.DProduct.pensions}" fileName="pension.properties"; pension>
          <#if pension != "">, ${pension}</#if>
        </@tag_cms_writeMessage>
      </span></a>
    </td>
    <td class="tabcell_5">
      <a href="${productUrl}">
        <span class="gen_bubble_bkg">
          <span class="gen_bubble_border">
            <span class="gen_bubble_txt">
              <#if departure.DProduct.promoPercentage??>- ${departure.DProduct.promoPercentage} %<#else>derni&egrave;res places</#if>
            </span>
          </span>
        </span>
      </a></td>
    <td class="gen_tab_price">
      <span> d&egrave;s </span><a href="${productUrl}"><strong class="price_prod"><#if departure.DProduct.promoPrice??>${departure.DProduct.promoPrice}<#else>${departure.DProduct.basePrice}</#if> &euro;*</strong></a><br />
    </td>
    <td class="tab_btn"><a href="${productUrl}"><img src="http://cdn.lastminute.com/site/4x8_fonk_flex.gif?skin=frfr.lastminute.com" /></a></td>
  </tr>
</@tag_cms_lookup>