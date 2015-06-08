<#assign
  disponibilities = context.lookup("disponibilties")!disponibilties
  selectOptionList = context.lookup("selectOptionList")!selectOptionList
/>
<#import "/lib/directives.ftl" as util>

<div class="white_bkg">
    <div class="col month">
      <label>mois</label>
      <select class="select_sort">
          <#if (selectOptionList.getDepartureDates())?has_content>
            <#list selectOptionList.getDepartureDates() as date>
              <option value="${date.code}" <#if date.getSelected()>selected="selected" class="defaultValue"</#if> >${date.label}</option>
            </#list>
          </#if>
      </select>
    </div>
    <div class="col city">
      <label>ville de départ</label>
      <select class="select_sort">
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
    <div class="col duration">
      <label>durée</label>
      <select class="select_sort">
        <#if (selectOptionList.getDepartureDuration())?has_content>
              <#list selectOptionList.getDepartureDuration() as duration>
               <#if (duration.getSelected())?has_content>
                <option value="${duration.code}" selected="selected" class="defaultValue">${duration.label}</option>
               <#else>
                <option value="${duration.code}">${duration.label}</option>
               </#if>
              </#list>
            </#if>
      </select>
    </div>
    <table cellspacing="0" cellpadding="0">
      <#include "/flashSale/flashSaleDispoCalendar.ftl">
    </table>
</div>
