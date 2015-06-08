<@tag_cms_lookup name="pageCode"; pageCode>
if(m){
  <#if pageCode == "error">
    m.contextData['OmniVars.ErrorMessage'] = 'APPLICATION ERROR';
    m.contextData['OmniVars.Error'] = 'APPLICATION ERROR';
    m.contextData['OmniVars.ErrorID'] = 'APPLICATION_ERROR';
  <#else>
    m.contextData['OmniVars.ErrorMessage'] = '404 ERROR';
    m.contextData['OmniVars.Error'] = '404 ERROR';
    m.contextData['OmniVars.ErrorID'] = '404_ERROR';
  </#if>
}
</@tag_cms_lookup>