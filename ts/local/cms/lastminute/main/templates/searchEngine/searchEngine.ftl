<#import "/lib/directives.ftl" as engine>
<#assign currentPage = context.lookup("currentPage")!'' />
<@tag_cms_lookup name="searchEngine"; searchEngine>
  <form id="search" class="residence-form" action="serp.cms" method="GET">
    <@tag_cms_lookup name="queryString"; queryString>
      <input type="hidden" name="queryString" value="${queryString}" id="queryString" />
    </@tag_cms_lookup>
    <@tag_cms_lookup name="brandData"; brandData>
      <input type="hidden" name="brandName" value="${brandData.brandName}" id="brandName" />
    </@tag_cms_lookup>
    <fieldset>
      <h2>séjour</h2>
      <div class="holder">
        <label for="slt_city">ville de départ :</label>
        <select id="slt_city" name="s_dpci">
          <option value="" >peu m'importe</option>
            <@tag_cms_lookup name="topDepartureCities"; topDepartureCities>
              <#list topDepartureCities.getDepartureCities() as topDepartureCity>
                  <option value="${topDepartureCity.code}"><#if topDepartureCity.code != '-1'>${topDepartureCity_index}.</#if> ${topDepartureCity.label?lower_case}</option>
              </#list>
            </@tag_cms_lookup>
            <option value="-1"></option>
            <option value="-1">toutes les villes</option>
            <option value="-1"></option>
          <#list searchEngine.getDepartureCity() as departureCity>
            <@engine.preselectValue requestParameter="s_dpci" currentValue=departureCity.getCode() renderValue="selected=selected"; isSelected>
                <option value="${departureCity.getCode()}" ${isSelected} >${departureCity.getLabel()?lower_case}</option>
            </@engine.preselectValue>
          </#list>
        </select>
      </div>
      
      
      <div class="holder">
        <div>      
          <label for="slt_destination">destination(s) :</label>
        </div>
        <div>
          <a class="notSelectDest linkSelectDest" href="#" style="display:none;">tout décocher</a>
        </div>
        <div id="selectedDest" style="display:none;">
          <span><!-- leave empty --></span>
        </div>
        <div>
          <a class="linkSelectDest" id="add_destination" href="#" style="display:none;">ajouter une destination</a>
        </div>
      <div >
        <select id="slt_destination" name="s_c.de">
          <option value="indifferent">peu m’importe</option>
          <@tag_cms_lookup name="topDestination"; topDestination>
              <#list topDestination.getDestination() as topDestination>
                  <option id="dest_${topDestination.label?lower_case}" class="noSelectDestination" value="${topDestination.code}" ><#if topDestination.code != '-1'>${topDestination_index}.</#if> ${topDestination.label?lower_case}</option>
              </#list>
            </@tag_cms_lookup>
            <option value="-1"></option>
            <option value="-1">toutes les destinations</option>
            <option value="-1"></option>
          <#list searchEngine.getCriterion() as criterion>
            <#if "de"=criterion.getCode()>
              <#list criterion.getValue() as critValue>
                <@engine.preselectValue requestParameter="s_c.de" currentValue=critValue.getCode() renderValue="selected=selected"; isSelected>
                  <#if critValue.getCode()?index_of(".") = -1>
                    <option id="dest_${critValue.getLabel()?lower_case}" value="${critValue.getCode()}" ${isSelected}>${critValue.getLabel()?lower_case}</option>
                  <#else>
                    <option id="dest_${critValue.getLabel()?lower_case}" value="${critValue.getCode()}" ${isSelected}>&nbsp;&nbsp;- ${critValue.getLabel()?lower_case}</option>
                  </#if>
                </@engine.preselectValue>
              </#list>
            </#if>
          </#list>
        </select>
      </div>
      </div>
      
      <div class="row">
        <div class="col">
          <label for="slt_departureDay">jour :</label>
          <select class="slt_departureDay" name="s_dd">
              <option value="">tous</option>
          </select>
        </div>
        <div class="col">
          <label for="slt_departureMonth">mois :</label>
          <select class="slt_departureMonth" name="s_dmy">
             <option value="">tous</option>
          </select>
        </div>
        <div class="col">
          <div class="datepicker-holder">
            <input type="text" class="datepicker" />
          </div>
        </div>
      </div>
      <div class="holder ad">
        <label for="slt_aj">flexibilité :</label>
       <select id="slt_aj" name="s_aj">
          <@engine.preselectValue requestParameter="s_aj" currentValue="0" renderValue="selected=selected"; isSelected>
            <option value="0" ${isSelected} >0 jours</option>
          </@engine.preselectValue>
          <@engine.preselectValue requestParameter="s_aj" currentValue="1" renderValue="selected=selected"; isSelected>
            <option value="1" ${isSelected} >1 jours</option>
          </@engine.preselectValue>
           <@engine.preselectValue requestParameter="s_aj" currentValue="2" renderValue="selected=selected" defaultValue="true"; isSelected>
            <option value="2" ${isSelected}>2 jours</option>
          </@engine.preselectValue>
           <@engine.preselectValue requestParameter="s_aj" currentValue="3" renderValue="selected=selected"; isSelected>
            <option value="3" ${isSelected}>3 jours</option>
          </@engine.preselectValue>
           <@engine.preselectValue requestParameter="s_aj" currentValue="4" renderValue="selected=selected"; isSelected>
            <option value="4" ${isSelected}>4 jours</option>
          </@engine.preselectValue>
          <@engine.preselectValue requestParameter="s_aj" currentValue="5" renderValue="selected=selected"; isSelected>
            <option value="5" ${isSelected}>5 jours</option>
          </@engine.preselectValue>
           <@engine.preselectValue requestParameter="s_aj" currentValue="6" renderValue="selected=selected"; isSelected>
            <option value="6" ${isSelected}>6 jours</option>
          </@engine.preselectValue>
           <@engine.preselectValue requestParameter="s_aj" currentValue="7" renderValue="selected=\"selected\""; isSelected>
            <option value="7" ${isSelected}>7 jours</option>
          </@engine.preselectValue>
        </select>
      </div>
      <div class="holder">
        <label for="slt_time">durée du séjour :</label>
        <select id="slt_time" name="s_minMan">
          <@engine.preselectValue requestParameter="s_minMan" currentValue="" renderValue="selected=selected" defaultValue="true"; isSelected>
            <option value="" ${isSelected}>peu m'importe</option>
          </@engine.preselectValue>
           <@engine.preselectValue requestParameter="s_minMan" currentValue="1,5" renderValue="selected=selected"; isSelected>
              <option value="1,5" ${isSelected}>Week-ends / Courts Séjours (1-5 nuits)</option>
          </@engine.preselectValue>
          <@engine.preselectValue requestParameter="s_minMan" currentValue="6,9" renderValue="selected=selected"; isSelected>
            <option value="6,9" ${isSelected}>Une Semaine (6-9 nuits)</option>
          </@engine.preselectValue>
          <@engine.preselectValue requestParameter="s_minMan" currentValue="10,16" renderValue="selected=selected"; isSelected>
            <option value="10,16" ${isSelected}>Deux Semaines (10-16 nuits)</option>
          </@engine.preselectValue>
        </select>
      </div>
      <span class="btn"><span><input type="submit" value="recherchez" /></span></span>
    </fieldset>
  </form>
</@tag_cms_lookup>
