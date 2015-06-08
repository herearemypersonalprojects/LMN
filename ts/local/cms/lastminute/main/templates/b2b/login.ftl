<@tag_cms_lookup name="displayb2bBlock"; displayb2bBlock>
  <#assign userProfile = context.lookup("userProfile")!''/>
  <#if userProfile = ''>
    <div id="frm_login">
      <form id="b2blogin" name="profile" action="/login" method="POST">
        <#assign currentPageUrl = context.lookup("currentPageUrl")!''/>
        <input type="hidden" name="currentPageUrl" class="currentPage"/>
        <input type="hidden" name="action" value="connect" />
        <@tag_cms_lookup name="errorMessage">
          <ul class="msg_error"><li>${errorMessage}</li></ul>
        </@tag_cms_lookup>
        <h3 class="tte_loginForm">Bienvenue sur votre outil offline</h3>
        <ul class="inputList">
          <li>
            <label for="ipt_login">Identifiant : </label>
            <input type="text" title="login" id="ipt_login" name="login">
          </li>
          <li>
            <label class="pass" for="psd_login">Mot de passe : </label>
            <input type="password" title="password" id="psd_login" name="password">
          </li>
        </ul>
        <button type="submit" class="btn_valid" href="#">Connexion</button>
      </form>
    </div>
  </#if>
</@tag_cms_lookup>


