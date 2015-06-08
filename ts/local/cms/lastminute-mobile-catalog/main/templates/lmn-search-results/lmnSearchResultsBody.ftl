<#import "/directives.ftl" as util>
<#assign depCity = context.lookup('depCity')!>
<#assign noDeparturesFound = context.lookup('noDeparturesFound')!false>

<@tag_cms_renderContainer code="shareProductFormContainer" />

<div class="results-container slidable">
<div class="container-content">
  <div id="lmn-search-filter-form-container">
    <form id="lmn-search-form" class="lmn-search-form validate" method="post" action="#">
      <fieldset>
        <div class="row">
          <label for="slt-dep-city">Ville de d&eacute;part :</label>
          <select id="slt-dep-city" name="slt-dep-city" class="required">
            <@tag_cms_lookup name="lmnDepCities"; lmnDepCities>
              <#list 0..lmnDepCities.getCitiesCount()-1 as i>
                <option value="${lmnDepCities.getCities(i)}" <#if lmnDepCities.getCities(i) == depCity>selected</#if>><@tag_cms_writeMessage key="${lmnDepCities.getCities(i)}" fileName="cityLabels.properties" /></option>
              </#list>
            </@tag_cms_lookup>
          </select>
        </div>
      </fieldset>
    </form>
  </div>

  <@tag_cms_renderContainer code="dayDeparturesContainer" />

  <#if noDeparturesFound>
    <div class="info-section">
      <p>D&eacute;sol&eacute;, aucun d&eacute;part de derni&egrave;re minute n'est actuellement disponible depuis <@tag_cms_writeMessage key="${depCity}" fileName="cityLabels.properties" />.</p>
    </div>
  </#if>
</div>
</div>