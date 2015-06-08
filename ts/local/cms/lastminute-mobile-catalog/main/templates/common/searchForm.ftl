<#import "/directives.ftl" as util>

<form id="search-form" action="/voyage/search-results.html" method="get" class="search-form validate">
  <fieldset>
    <div class="note">Erreur de saisie d&eacute;tect&eacute;e. Veuillez corriger les champs indiqu&eacute;s en rouge.</div>
    <ul>
      <#if context.lookup('pageCode')! == 'searchResults'><li style="display:none"></li></#if>

      <li>
        <label for="slt-desti" class="ico1">Destination</label>
        <select id="slt-desti" name="slt-desti" class="required">
          <option value="*">peu m'importe</option>

          <@tag_cms_lookup name="topDestination"; topDestination>
            <#list topDestination.getDestination() as topDestination>
              <#if topDestination.code != '-1'>
                <option value="${topDestination.code}">${topDestination_index}. ${topDestination.label?lower_case}</option>
              </#if>
            </#list>
          </@tag_cms_lookup>

          <@tag_cms_lookup name="searchEngine"; searchEngine>
            <#list searchEngine.getCriterion() as criterion>
              <#if "de"=criterion.getCode()>
                <#list criterion.getValue() as critValue>
                  <#if critValue.getCode()?index_of(".") = -1>
                    <option value="${critValue.getCode()}">${critValue.getLabel()?lower_case}</option>
                  <#else>
                    <option value="${critValue.getCode()}">&nbsp;&nbsp;- ${critValue.getLabel()?lower_case}</option>
                  </#if>
                </#list>
              </#if>
            </#list>
          </@tag_cms_lookup>
        </select>
      </li>

      <li>
        <label for="slt-ori" class="ico2">Ville de d&eacute;part</label>
        <select id="slt-ori" name="slt-ori" class="required">
          <@tag_cms_lookup name="topDepartureCities"; topDepartureCities>
            <#list topDepartureCities.getDepartureCities() as topDepartureCity>
              <#if topDepartureCity.code != '-1'>
                <option value="${topDepartureCity.code}" <#if topDepartureCity.code == 'PAR'>selected="selected"</#if>>${topDepartureCity_index}. ${topDepartureCity.label?lower_case}</option>
              </#if>
            </#list>
          </@tag_cms_lookup>

          <@tag_cms_lookup name="searchEngine"; searchEngine>
            <#list searchEngine.getDepartureCity() as departureCity>
              <option value="${departureCity.getCode()}">${departureCity.getLabel()?lower_case}</option>
            </#list>
          </@tag_cms_lookup>
        </select>
      </li>

      <li>
        <label for="ipt-date" class="ico3">Date de d&eacute;part</label>
        <span class="text"><input type="text" class="with-dp required date defaultInvalid" id="ipt-date" name="ipt-date" /></span>
      </li>

      <li>
        <label for="slt-flex" class="ico5">Flexibilit&eacute;</label>
        <select id="slt-flex" name="slt-flex" class="required">
          <option value="0">0 jour</option>
          <option value="2">+/- 2 jours</option>
          <option value="7" selected>+/- 1 semaine</option>
        </select>
      </li>

      <li>
        <label for="slt-dur" class="ico4">Dur&eacute;e du s&eacute;jour</label>
        <select id="slt-dur" name="slt-dur" class="required">
          <option value="1,5">1 - 5 nuits</option>
          <option value="6,9" selected>6 - 9 nuits</option>
          <option value="10,16">10 - 16 nuits</option>
        </select>
      </li>
    </ul>

    <div class="row">
      <input type="submit" class="submit" value="rechercher<#if context.lookup('pageCode')! == 'index'> les offres</#if>" />
    </div>

  </fieldset>
</form>