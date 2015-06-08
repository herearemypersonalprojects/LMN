<#assign totalResultsNumber = context.lookup("totalResultsNumber")!2000/>
<input type="hidden" name="totalNumberInput" value="${totalResultsNumber}" id="totalNumberInput" />

<form id="sortedForm" action="serp.cms" method="GET">
  <div class="sort-selector">
      <ul>
          <li>Trier par :</li>
          <li><span  name="st" id="favoris" value="base_price_coeff" class="selected">top des ventes</span> |</li>
          <li><span  name="st" id="croissant" value="base_price">prix croissant</span> |</li>
          <li><span  name="st" id="dcroissant" value="base_price_desc">prix d&eacute;croissant</span> |</li>
          <li><span  name="st" id="dtoiles" value="c.cat">nombre d'&eacute;toiles</span></li>
      </ul>
      <ul>
          <li>Afficher :</li>
          <li><span name="nbDisplay" class="selected">20</span> |</li>
          <li><span name="nbDisplay">40</span></li>
      </ul>
  </div>
</form>