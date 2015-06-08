<#import "/lib/directives.ftl" as engine>
<#assign
  currentPage = context.lookup("currentPage")!''
  resultsNumber = context.lookup("resultsNumber")!1
  show = context.lookup("showDestinationInfo")!false
  title = context.lookup("title")!''
  stayType = context.lookup("seoData.stayType") !''
  mainPicture = context.lookup("mainPicture")!''
/>


<@tag_cms_lookup name="searchEngine"; searchEngine>

  <@tag_cms_lookup name="queryString"; queryString>
    <input type="hidden" name="queryString" value="${queryString}" id="queryString" />
  </@tag_cms_lookup>

  <@tag_cms_lookup name="brandData"; brandData>
    <input type="hidden" name="brandName" value="${brandData.brandName}" id="brandName" />
  </@tag_cms_lookup>

  <form id="searchForm" class="residence-form" action="serp.cms" method="GET">
    <div class="search-filters">
      <#if (show == true && mainPicture != "")>
        <div class="media-img">
          <div class="bg-image" style="background-image: url(${mainPicture});"> </div>
        </div>
      <#else>
        <div class="media-img">
          <div class="bg-image" style="background-image: url(http://back-lastminute.orchestra-platform.com/admin/TS/fckUserFiles/Content_Image/Visuels_SEO/VISUELS_RESPONSIVE/Visuels_destinations/Thailande.jpg);"> </div>
        </div>
      </#if>
      <div class="inner-block clearfix">
        <@tag_cms_lookup name="seoData"; seoData>
          <#assign destinationCountry = seoData.destinationCountry!'' />
          <#if destinationCountry != ''>
            <#--<h1 id="dest_voyage"> </h1>-->
            <h1>
              <@tag_cms_lookup name="seoData";seoData>
                <#assign stayType = seoData.stayType!'' />
                <#if stayType != ''>${stayType}
                <#else>
                  <#if title?has_content>
                    voyage
                  </#if>
                </#if>
              </@tag_cms_lookup>
              ${title}
            </h1>
          </#if>
        </@tag_cms_lookup>
        <p id="tobehidden"> <strong> ... </strong>  </p>
        <p id="resultInfo" class="hidden"><strong>${resultsNumber}</strong> produit(s) correspondent &agrave; votre recherche sur un total de
        <label id="totalNumberLabel">${resultsNumber}</label> produits</p>
        <p id="noResultInfo" class="hidden"> <strong  style="font-size: 13px;"> </strong></p>

        <div class="selectBlock destination">
          <div class="content-destination">
            <div class="addDestinationBlock">
              <ul id = "selectedDests">
                <li class="title"><label for="destination">Destination(s) :</label></li>
              </ul>
            </div>
          </div>
          <select id="slt_destination" class="custom-select-box" name="s_c.de">
            <option value="">peu m'importe</option>
            <@tag_cms_lookup name="topDestination"; topDestination>
              <#list topDestination.getDestination() as topDestination>
                <option id="dest_${topDestination.label?lower_case}" class="noSelectDestination"
                value="${topDestination.code}" ><#if topDestination.code != '-1'>${topDestination_index}.</#if>
                ${topDestination.label?lower_case}</option>
              </#list>
            </@tag_cms_lookup>
            <option value="-1"></option>
            <option value="-1">toutes les destinations</option>
            <option value="-1"></option>
            <#list searchEngine.getCriterion() as criterion>
              <#if "de"=criterion.getCode()>
                <#list criterion.getValue() as critValue>
                  <@engine.preselectValue requestParameter="s_c.de" currentValue=critValue.getCode() renderValue=""; isSelected>
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
          <div id="destAdd" class="addDestinationBlock" style="display:none">
            <a id="addMoreDestBtn" href="#">ajouter une destination<span class="ico-bg"><i class="icon--plus">+</i></span></a>
          </div>
        </div>

        <div class="selectBlock">
          <label for="slt_city">Ville de d&eacute;part :</label>
          <select id="slt_city" class="custom-select-box" name="s_dpci">
            <option value="" >peu m'importe</option>
              <@tag_cms_lookup name="topDepartureCities"; topDepartureCities>
                <#list topDepartureCities.getDepartureCities() as topDepartureCity>
                  <option value="${topDepartureCity.code}">
                    <#if topDepartureCity.code != '-1'>
                      ${topDepartureCity_index}.</#if> ${topDepartureCity.label?lower_case}
                  </option>
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

        <div class="selectBlock dayDeparture">
          <label for="dayDeparture">Date de d&eacute;part :</label>
          <div class="content-select-departureDate">
            <div class="selectDate">
              <select id="dayDeparture" class="custom-select-box" name="s_dd">
                <option value="">tous</option>
                <option value="">tous 1</option>
                <option value="">tous 2</option>
                <option value="">tous</option>
                <option value="">tous</option>
                <option value="">tous</option>
              </select>
            </div>
            <div class="selectDate last">
              <select id="monthDeparture" class="custom-select-box" name="s_dmy">
                <option value="">tous</option>
              </select>
            </div>
            <div class="content-calendar">
              <span class="picto-calendar">
                <input type="text" class="datepicker hidden" />
              </span>
            </div>
          </div>
        </div>

        <div class="selectBlock flexibility">
          <label for="flexibility">Flexibilit&eacute; :</label>
          <select id="slt_aj" class="custom-select-box middle" name="s_aj">
            <@engine.preselectValue requestParameter="s_aj" currentValue="0" renderValue="selected=selected"; isSelected>
              <option value="0" ${isSelected} >0 jour</option>
            </@engine.preselectValue>
            <@engine.preselectValue requestParameter="s_aj" currentValue="1" renderValue="selected=selected"; isSelected>
              <option value="1" ${isSelected} >1 jour</option>
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

        <div class="selectBlock lengthStay">
          <label for="lengthStay">Dur&eacute;e du s&eacute;jour :</label>
          <select id="slt_time" class="custom-select-box middle" name="s_minMan">
            <@engine.preselectValue requestParameter="s_minMan" currentValue="" renderValue="selected=selected" defaultValue="true"; isSelected>
              <option value="" ${isSelected}>peu m'importe</option>
            </@engine.preselectValue>
             <@engine.preselectValue requestParameter="s_minMan" currentValue="1,5" renderValue="selected=selected"; isSelected>
                <option value="1,5" ${isSelected}>Week-ends / Courts S&eacute;jours (1-5 nuits)</option>
            </@engine.preselectValue>
            <@engine.preselectValue requestParameter="s_minMan" currentValue="6,9" renderValue="selected=selected"; isSelected>
              <option value="6,9" ${isSelected}>Une Semaine (6-9 nuits)</option>
            </@engine.preselectValue>
            <@engine.preselectValue requestParameter="s_minMan" currentValue="10,16" renderValue="selected=selected"; isSelected>
              <option value="10,16" ${isSelected}>Deux Semaines (10-16 nuits)</option>
            </@engine.preselectValue>
          </select>
        </div>

        <div class="buttonBlock">
          <button type="submit" value="recherchez" id="searchBtn" class="btn-search">Rechercher</button>
        </div>

      </div>
    </div>
  </form>
</@tag_cms_lookup>
