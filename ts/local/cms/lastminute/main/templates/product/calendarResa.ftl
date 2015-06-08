<#assign
  disponibilities = context.lookup("disponibilties")!disponibilties
  showDispoEconomyColumn = context.lookup("showDispoEconomyColumn")!false
  flyAndDrive = context.lookup("flyAndDrive")!false
/>
<#import "/lib/directives.ftl" as resource>
<div id="table-departure" style="display:none" class="table-departure">
  <table id="calendar">
   <#if (disponibilities.getDisponibilityDisplayable())?has_content>
    <thead>
      <tr>
        <th>d&eacute;part le</th>
        <th>retour le</th>
        <th>d&eacute;part de</th>
        <th>dur&eacute;e</th>
        <th class="lastChild">prix TTC/pers *</th>
      </tr>
    </thead>
    <tbody>
     <#list disponibilities.getDisponibilityDisplayable() as disponibilityDisplayable>

      <tr style="cursor: pointer" class="resaSubmit <#if disponibilityDisplayable_index % 2 == 0 >even<#else>odd</#if>">
        <td>${disponibilityDisplayable.departureDate}</td>
        <td>
          ${disponibilityDisplayable.returnDate}
          <span class="adjust hidden">${disponibilityDisplayable.adjustDate}</span>
        </td>
        <td>${disponibilityDisplayable.departureCity}</td>
        <td>
          <#if flyAndDrive>
            ${disponibilityDisplayable.duration?substring(0,disponibilityDisplayable.duration?index_of('/'))}
          <#else>
            ${disponibilityDisplayable.duration}
          </#if>
        </td>
        <td class="price lastChild">
          <#if (disponibilityDisplayable.getSupplementDescription())?has_content>
            <#if (disponibilityDisplayable.getSupplementDescription() == 'lowest')>
                <#if (disponibilityDisplayable.getEconomyPrice())?has_content>
                  <span class="old-price">${disponibilityDisplayable.price+disponibilityDisplayable.getEconomyPrice()} &euro;*</span>
                <#else>
                </#if>
                <span class="best-price">meilleur prix <span> </span></span>
                <span class="new-price">${disponibilityDisplayable.price} &euro;*</span>
            </#if>
          <#elseif (disponibilityDisplayable.getEconomyPrice())?has_content>
              <span class="old-price">${disponibilityDisplayable.price+disponibilityDisplayable.getEconomyPrice()} &euro;*</span>
              <span class="economy">-${disponibilityDisplayable.getPercentage()}% soit ${disponibilityDisplayable.getEconomyPrice()} &euro;<span> </span></span>
              <span class="new-price">${disponibilityDisplayable.price} &euro;*</span>
          <#else>
            <span class="new-price">${disponibilityDisplayable.price} &euro;*</span>
          </#if>
          <span class="arrow"> </span>
          <span class="btn hidden">
            <span id="booking_${disponibilityDisplayable.signature}">
              <input type="submit" value="" name="${disponibilityDisplayable.departureCityCode}" id="${disponibilityDisplayable.signature}" />
            </span>
            <img class='loader' src='<@resource.retrieveResource canonicalAddress="images" fileName="ajax-loader" fileExtension="gif"/>' style="display:none;" />
          </span>
        </td>
      </tr>

     </#list>
    </tbody>
   <#else>
    <script type="text/javascript">
      writeNoDispoMessage();
    </script>
   </#if>
  </table>
</div>
