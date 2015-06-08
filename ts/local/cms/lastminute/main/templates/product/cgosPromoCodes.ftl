<#assign
  brandData = context.lookup("brandData")!''
/>
<#if brandData.brandName == 'CGOS'>
  <form method="POST" action="/codePromoValidator" name="profile" id="cgosValidator">
    <input type="hidden" name="codeActive" value="false" id="codeActive" />

    <div class="blk-reduction-cgos" id="cgosCodeValidator">
      <h2>R&eacute;duction Libre &eacute;vasion - C.G.O.S - Prix affich&eacute;s hors r&eacute;duction</h2>
      <p>Num&eacute;ro C.G.O.S compos&eacute; de 7 chiffres et 1 lettre*</p>
      <div class="btn-reduction-cgos">
        <input type="text" name="Code Promo CGOS" id="promoCode"  value="">
        <button class="btn-green btn_valid" type="submit">valider</button>
        <a href="http://contact.cgos.info" class="promoPopup" target="_blank">Si n&eacute;cessaire, contactez votre correspondant C.G.O.S pour le conna&icirc;tre et v&eacute;rifier sa validit&eacute;</a>
      </div>
    </div>

    <div class="information-cgos">Indiquez ici-dessous votre num&eacute;ro C.G.O.S compos&eacute; de 7 chiffres et 1 lettre. Espacer d'un ; si plusieurs num&eacute;ros. Attention, si ce champ n'est pas renseign&eacute;, aucune r&eacute;duction ne vous sera accord&eacute;e et votre commande pourra &ecirc;tre refus&eacute;e. Vous recevrez par lettre ch&egrave;que le remboursement du montant de la r&eacute;duction le mois de d&eacute;part, apr&egrave;s validation de vos droits.</div>
 </form>
</#if>

