<div id="email-lightbox" class="lightbox">
  <div class="heading">
    <strong class="title">Partage par email</strong>
  </div>
  <div class="entry">
    <form id="share-product-form" class="email-form validate" method="post" action="#">
      <fieldset>
        <input type="hidden" id="productId" name="pid" value="" />
        <input type="hidden" id="productName" name="pName" value="" />
        <input type="hidden" id="usePrintCss" name="usePrintCss" value="true" />

        <div class="row">
          <input type="text" id="ipt_email_friend" name="toAddress" class="required email" placeholder="email du destinataire" />
        </div>
        <div class="row">
          <input type="text" id="ipt_name" name="fromName" class="required civilname" placeholder="votre nom" />
        </div>
        <div class="row">
          <input type="text" id="ipt_email" name="fromAddress" class="required email" placeholder="votre email" />
        </div>
        <div id="note-container">
          <div class="note">Tous les champs doivent être remplis et valides.</div>
          <div id="popin-loader">en cours</div>
          <div id="popin-message"></div>
        </div>
        <div id="email-popin-buttons" class="row">
          <input type="submit" class="submit" value="ok" />
          <input type="reset" class="reset" value="annuler" />
        </div>
      </fieldset>
    </form>
  </div>
</div>
