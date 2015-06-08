

<div id="share-product">
  <!--div class="container-content"-->

    <div class="title">Partage par email de l'offre: <span id="shared-product-title"></span></div>

  <!--div class="entry"-->
    <form id="share-product-form" class="email-form validate" method="post" action="#">
      <fieldset>
        <input type="hidden" id="shared-product-id" name="pid" value="" />
        <input type="hidden" id="shared-product-name" name="pName" value="" />
        <input type="hidden" id="usePrintCss" name="usePrintCss" value="true" />

        <div class="row">
          <input type="text" id="ipt_email_friend" name="toAddress" class="required email" placeholder="email du destinataire" />
          <div class="error-msg">son adresse doit être remplie et valide</div>
        </div>
        <div class="row">
          <input type="text" id="ipt_name" name="fromName" class="required civilname" placeholder="votre nom" />
          <div class="error-msg">votre nom doit être rempli, et sans caractères spéciaux</div>
        </div>
        <div class="row">
          <input type="text" id="ipt_email" name="fromAddress" class="required email" placeholder="votre email" />
          <div class="error-msg">votre adresse doit être remplie et valide</div>
        </div>
        <div id="note-container">
          <div id="share-product-loader">en cours</div>
          <div id="share-product-message">
            <span id="message-content"></span>
            <a id="message-back-link" href="#">Retour</a>
          </div>
        </div>
        <div id="share-product-buttons" class="row">
          <input id="cancel-share-product" type="reset" class="reset" value="annuler" />
          <input type="submit" class="submit" value="ok" />
        </div>
      </fieldset>
    </form>
  <!--/div-->

  <!--/div-->
</div>