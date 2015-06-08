<#import "/lib/directives.ftl" as resource>
<div id="popupSendMail">
  <h2 class="tte_popup">Envoi de la fiche produit</h2>
  <form class="frm_sendEmail" id="sendEmailForm" action="sendProductEmail" method="post">
      <div class="formZone">
        <input type="hidden" id="productId" name="pid" value="" />
        <input type="hidden" id="productName" name="pName" value="" />
        <input type="hidden" id="usePrintCss" name="usePrintCss" value="true" />
        <ul class="msg_error">
          <li id="ipt_email_friend_error"></li>
          <li id="ipt_name_error"></li>
          <li id="ipt_email_error"></li>
        </ul>

        <ul class="inputList">
          <li><label for="ipt_email_friend">Email du destinataire*</label><input type="text" name="toAddress" id="ipt_email_friend" needValidate="true" /></li>
          <li><label for="ipt_name">Votre nom*</label><input type="text" name="fromName" id="ipt_name" needValidate="true" /></li>
          <li><label for="ipt_email">Email de l'exp&eacute;diteur*</label><input type="text" name="fromAddress" id="ipt_email" needValidate="true"/></li>
          <li class="grandZone">
            <label for="tta_message">Message</label>
            <textarea name="emailText" id="tta_message" rows="5" cols="50" ></textarea>
          </li>
        </ul>
        <p class="buttonZone">
          <span class="msg_status" id="statusMsg"></span>
          <button id="sendProductEmail" type="button" class="btn-green" title="Envoyer" value="Envoyer">envoyer</button>
          <img class="loader" src="<@resource.retrieveResource canonicalAddress="images" fileName="ajax-loader" fileExtension="gif"/>" style="display:none;" alt="" />
        </p>

        <small class="obligation">Les champs marqu&eacute;s d'un * sont obligatoires.</small>
      </div>
  </form>
</div>
