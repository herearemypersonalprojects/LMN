<#assign resultsNumber = context.lookup("resultsNumber")!1/>
<#assign totalResultsNumber = context.lookup("totalResultsNumber")!2000/>
<@tag_cms_lookup name="catIdValue"; catIdValue>
<div class="pushMessage">${catIdValue}</div>
</@tag_cms_lookup>
<div class="info-block">
  <div class="heading">
    <span>${resultsNumber}</span>
    <h4 class="tte_serp">produit(s) correspondent à votre recherche</h4>
    <div class="heading-text">
      <p>sur un total de <strong class="total">
      	<#assign totalResultsNumber = context.lookup("totalResultsNumber")/>
      	<#if totalResultsNumber??>
      		${totalResultsNumber}
      	<#else>
      		<@tag_cms_writeMessage key="resultsNumber" fileName="resultsNumber.properties" /></strong>
      	</#if>
     	</strong>
      </p>
    </div>
  </div>
  <@tag_cms_renderContainer code="destinationDescContainer" />
  <div class="conteiner">
    <div class="holder">
      <form class="select-form" action="#">
        <fieldset>
          <label class="title">trier par :</label>
          <div class="row">
            <input type="radio" name="st" id="favoris" value="base_price_coeff"/>
            <label>top des ventes</label>
          </div>
          <div class="row">
            <input type="radio" name="st" id="croissant" value="base_price"/>
            <label>prix croissant</label>
          </div>
          <div class="row">
            <input type="radio" name="st" id="dcroissant" value="base_price_desc"/>
            <label>prix décroissant</label>
          </div>
          <div class="row">
            <input type="radio" name="st" id="dtoiles" value="c.cat"/>
            <label>nbre d’étoiles</label>
          </div>
        </fieldset>
      </form>
    </div>
  </div>
</div>
