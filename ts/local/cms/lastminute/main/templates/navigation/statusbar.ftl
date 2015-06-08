<#assign currentPage = context.lookup("currentPage")!'serp' />
<ul class="status-bar">

  <#if currentPage = 'serp'><li class="active step1"><#else><li class="step1"></#if>
   <p><span><strong>1</strong> choisir un voyage</span></p></li>

  <#if currentPage = 'product'><li class="active step2"><#else><li class="step2"></#if>
  <p><span><strong>2</strong> produit</span></p></li>

  <li class="step3"><p><span><strong>3</strong> devis</span></p></li>
  <li class="step4"><p><span><strong>4</strong> coordonnées</span></p></li>
  <li class="step5"><p><span><strong>5</strong> paiement</span></p></li>
  <li class="step6"><p><span><strong>6</strong> confirmation</span></p></li>
</ul>