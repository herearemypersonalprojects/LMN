<@tag_cms_lookup name="userProfile">
  <#assign currentPageUrl = context.lookup("currentPageUrl")!''/>
  <#assign firstName = userProfile.firstName!''/>
  <#assign lastName = userProfile.lastName!''/>
  <#assign civility = userProfile.personalTitle!''/>
    <form action="/login" name="b2bdeconnexion" id="b2bdeconnexion">
      <input type="hidden" name="currentPageUrl" class="currentPage"/>
      <input type="hidden" name="action" value="deconnect" />
      <div id="frm_login">
          <button class="btn-pink btn_valid" type="submit" href="#">Déconnexion</button>
          <#if civility != '' || firstName != '' ||  lastName != '' >
            <span class="usrName">Bienvenue ${civility} ${lastName} ${firstName}</span>
          </#if>
        </form>
      </div>
    </form>

</@tag_cms_lookup>