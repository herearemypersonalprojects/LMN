<#assign contextPath = context.lookup("contextPath")!''/>
<#assign isRequestWithSearchCriteria = context.lookup("isRequestWithSearchCriteria")!true/>
<#import "/lib/directives.ftl" as engine>
<@tag_cms_lookup name="searchBoxEngine"; searchEngine>
  <div id="main_search">
    <form name="holidaysSearch" id="search" action="${contextPath}/serp.cms" method="GET">
      <@tag_cms_lookup name="refinementQueryParameters"; refinementQueryParameters>
        <#list refinementQueryParameters.getCodeLabel() as codeLabel>
          <input type="hidden" name="s_${codeLabel.getCode()}" value="${codeLabel.getLabel()}" />
          <input type="hidden" name="aff_${codeLabel.getCode()}" value="${codeLabel.getLabel()}" />
        </#list>
      </@tag_cms_lookup>
      <input type="hidden" name="partnerId" value="0" id="orc_partnerId" />
      <input type="hidden" name="source" value="0" id="orc_source" />
      <@tag_cms_lookup name="b2b">
      	 <input type="hidden" name="user" value="b2b" />
      </@tag_cms_lookup>
      <fieldset class="halfLength">
        <label for="slt_destination" id="destinationListContainer" class="render_link">destination:
          <select name="s_c.de" id="slt_destination">
            <option value="" selected="selected">peu m'importe</option>
            <#if !isRequestWithSearchCriteria>
              <@tag_cms_lookup name="topDestination"; topDestination>
                <#list topDestination.getDestination() as topDestination>
                    <option value="${topDestination.code}"><#if topDestination.code != '-1'>${topDestination_index}.</#if> ${topDestination.label?lower_case}</option>
                </#list>
              <option value="-1"></option>
              <option value="-1">toutes les destinations</option>
              <option value="-1"></option>
              </@tag_cms_lookup>
            </#if>
            <#list searchEngine.getCriterion() as criterion>
              <#if "de"=criterion.getCode()>
                <#list criterion.getValue() as critValue>

                  <@engine.preselectValue requestParameter="s_c.de" currentValue=critValue.getCode() renderValue="selected=selected"; isSelected>
                    <#if critValue.getCode()?index_of(".") = -1>
                      <option value="${critValue.getCode()}" ${isSelected}>${critValue.getLabel()?lower_case}</option>
                    <#else>
                        <option value="${critValue.getCode()}" ${isSelected}>&nbsp;&nbsp;- ${critValue.getLabel()?lower_case}</option>
                    </#if>
                  </@engine.preselectValue>
                </#list>
              </#if>
           </#list>
          </select>
        </label>
        <!-- Ends -->
        <!-- Departure Airport -->
        <label for="slt_city">ville de départ :
          <select class="" name="s_dpci" id="slt_city">
            <option value="" >peu m'importe</option>
            <#if !isRequestWithSearchCriteria>
              <@tag_cms_lookup name="topDepartureCities"; topDepartureCities>
                <#list topDepartureCities.getDepartureCities() as topDepartureCity>
                     <option value="${topDepartureCity.code}"><#if topDepartureCity.code != '-1'>${topDepartureCity_index}.</#if> ${topDepartureCity.label?lower_case}</option>
                </#list>
              </@tag_cms_lookup>
              <option value="-1"></option>
              <option value="-1">toutes les villes</option>
              <option value="-1"></option>
            </#if>
            <#list searchEngine.getDepartureCity() as departureCity>
              <@engine.preselectValue requestParameter="s_dpci" currentValue=departureCity.getCode() renderValue="selected=selected"; isSelected>
                <option value="${departureCity.getCode()}" ${isSelected} >${departureCity.getLabel()?lower_case}</option>
              </@engine.preselectValue>
            </#list>
          </select>
        </label>
        <!-- Ends -->
      </fieldset>

      <fieldset id="dateGroup" class="clearFloat">
        <div id="datesContent" class="curvedContentBody clearFloat">
          <!-- Departure month -->
          <label for="slt_departureMonth">mois :
            <select id="slt_departureMonth" name="s_dmy"></select>
          </label>
          <!-- Ends -->
          <!-- Departure day -->
          <label for="slt_departureDay">jour :
            <select id="slt_departureDay" name="s_dd"></select>
          </label>

          <label class="datepicker-holder">
            <input type="text" class="datepicker" />
          </label>
          <!-- Ends -->
          <!-- Calendar icon  JavaScript -->

          <!-- Ends -->
          <!-- Give or take -->
          <label for="slt_aj">flexibilité :
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
          </label>
          <!-- Ends -->
          <!-- No. of nights -->
          <label for="slt_time">durée du séjour:
          <select name="s_minMan" id="slt_time" class="">
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
          </label>
           <@tag_cms_lookup name="b2b">

         	 <label for="ipt_productId">recherche par ID du produit : </label>
    		 <input type="text"id="ipt_productId" name="s_pid" />

  			 <label for="ipt_toName">recherche par nom du TO : </label>
  			 <input type="text"id="ipt_toName" name="s_to" />

		     <label for="ipt_hname">recherche par nom produit : </label>
		     <input type="text"id="ipt_hname" name="s_hname" />

		</@tag_cms_lookup>
          <!-- Ends -->
        </div>
      </fieldset>
      <input type="submit" id="holsSubmitButton" class="siteBtn" value="recherchez" />
      <div class="clearSmall"></div>
    </form>
    <!-- Ends -->
  </div>
</@tag_cms_lookup>
<@engine.retrieveResource canonicalAddress="js/lib" fileName="jquery.min" fileExtension="js";filePath>
  <script type="text/javascript" src="${contextPath}${filePath}"></script>
</@engine.retrieveResource>
<@engine.retrieveResource canonicalAddress="js/lib" fileName="jquery.ui.min" fileExtension="js";filePath>
  <script type="text/javascript" src="${contextPath}${filePath}"></script>
</@engine.retrieveResource>
<@engine.retrieveResource canonicalAddress="js" fileName="searchBox" fileExtension="js";filePath>
  <script type="text/javascript" src="${contextPath}${filePath}"></script>
</@engine.retrieveResource>