<#import "/directives.ftl" as util>
<#assign topSalesOrder = context.lookup('topSalesOrder')!false>
<#assign defaultDepDate = context.lookup('defaultDepDate')>

<div class="results-container">
  <div id="filter-form-container">
    <span class="title-text">Trier les offres par :</span>
    <ul id="orderControls" class="controls-btn checkbox-list">
      <li><input type="checkbox" id="topSalesChx" value="Top des ventes" <#if topSalesOrder>checked</#if>/><label for="topSalesChx">Top des ventes</label></li>
      <li><input type="checkbox" id="priceChx" value="Prix croissants" <#if !topSalesOrder>checked</#if>/><label for="priceChx">Prix croissants</label></li>
    </ul>
    <span class="title-text">Options de recherche :</span>
    <@tag_cms_renderContainer code="searchFormContainer" />
  </div>
  <div id="filter-form-opener" class="result-text"><strong>Modifier votre recherche</strong></div>
  <div class="search-info">
    <div class="title"><span class="desti"></span> à partir de <span class="city"></span></div>
    <div class="detail"></div>
  </div>

  <ul id="result-list" class="result-list" data="defaultDepDate=${defaultDepDate?string('dd/MM/yyyy')}">
    <#-- BEGIN hidden model product bloc -->
    <li class="product-item" style="display: none;">
      <div class="holder">
        <div class="price-box"> </div>
        <div class="text-box">
          <h2><a href="#"><#-- title --></a></h2>
          <h3><a href="#"><#-- country - city --></a></h3>
          <div class="entry">
            <div class="btns">
              <a href="#" class="btn-add">add</a>
              <#-- ou si déjà enregistré a href="#" class="btn-add alt">add</a-->
              <a href="#" class="btn-email">email</a>
            </div>
            <img class="image" src="" height="137" width="127" />
            <div class="text">
              <div class="units"><p><#-- X nuits --></p></div>
              <p class="pension"><#-- Pension complète --></p>
            </div>
            <div class="displayMsgFavorite hidden"></div>
          </div>
        </div>
      </div>
      <div class="slide">
        <div class="action-box">
          <ul> </ul>
        </div>
      </div>
    </li>
    <#-- END hidden model product bloc -->
  </ul>

  <div id="searchLoader">Chargement en cours...</div>

  <div id="searchNoResults" class="info-section hidden">
    <p>D&eacute;sol&eacute;, aucun produit n'a &eacute;t&eacute; trouv&eacute;.</p>
    <p>Nous vous invitons &agrave; effectuer une nouvelle recherche en &eacute;largissant vos crit&egrave;res.</p>
  </div>

  <div id="searchError" class="info-section hidden">
    <p>D&eacute;sol&eacute;, une erreur est survenue pendant le chargement de nouveaux r&eacute;sultats.</p>
    <p>Veuillez v&eacute;rifier que votre connectivit&eacute; internet n'est pas limit&eacute;e.</p>
  </div>

</div>
