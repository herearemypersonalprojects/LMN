<#assign
  disponibilities = context.lookup("disponibilties")!disponibilties
  showDispoEconomyColumn = context.lookup("showDispoEconomyColumn")!false
  productDisplayable = context.lookup("smallProductDisplayable")!productDisplayable
/>

<#import "/lib/directives.ftl" as resource>
<#if (disponibilities.getDisponibilityDisplayable())?has_content>
    <thead>
        <tr>
          <td class="col1">départ le</td>
          <td class="col2">retour le</td>
          <td class="col3">départ de</td>
          <td class="col4">durée</td>
          <td class="col5">
            prix TTC*
            <br />
            <span class="head5_small">par pers.</span>
          </td>
          <#if showDispoEconomyColumn == true>
            <td class="col6">réduction</td>
          </#if>
          <td class="col7"> &nbsp;
          </td>
        </tr>
    </thead>
    <tbody>
    <#list disponibilities.getDisponibilityDisplayable() as disponibilityDisplayable>
      <#if disponibilityDisplayable_index % 2 == 0 >
       <tr class="content_line None">
      <#else>
       <tr class="content_line odd">
      </#if>
        <td class="col1"><span class="sortable_cell" style="display:none;">${disponibilityDisplayable.getSignature().substring(3,10)}</span>${disponibilityDisplayable.departureDate}</td>
        <td class="col2">${disponibilityDisplayable.returnDate}</td>
        <td class="col3"><span class="sortable_cell" style="display:none;">${disponibilityDisplayable.departureCityCode}</span>${disponibilityDisplayable.departureCity}</td>
        <td class="col4"><span class="sortable_cell" style="display:none;">${disponibilityDisplayable.getSignature().substring(11).replace('-',',')}</span>${disponibilityDisplayable.duration}</td>
        <td class="col5"><strong class="pink_text">${disponibilityDisplayable.getPrice().toString()}€</strong></td>
        <#if showDispoEconomyColumn == true>
          <#if (disponibilityDisplayable.getEconomyPrice())?has_content>
            <td class="col6">
              <#if disponibilityDisplayable.getPercentage()?has_content>-${disponibilityDisplayable.getPercentage()}% soit </#if>
                -${disponibilityDisplayable.getEconomyPrice().toString()}€
            </td>
          <#else>
            <td class="col6">-</td>
          </#if>
        </#if>
        <td class="col7">
            <#assign nbNight= disponibilityDisplayable.getSignature().substring(13, 14) />
            <a class="flash_button" href="http://voyage.lastminute.com/${productDisplayable.getSeoUrl()}?intcmp=flash_promo#aj=0&date=${disponibilityDisplayable.getSignature().substring(0,10)}&city=${disponibilityDisplayable.departureCityCode}&duration=${nbNight},${nbNight}" title="voir l'offre">voir l'offre</a>
        </td>
      </tr>
    </#list>
    </tbody>
</#if>

