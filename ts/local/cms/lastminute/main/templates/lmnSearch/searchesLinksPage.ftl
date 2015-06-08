<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>

  </head>

  <body>

    <@tag_cms_lookup name="departureCities"; departureCities>
      <#list 0..departureCities.getCitiesCount()-1 as i>
        <a href="/lastMinuteSearch.cms?depCity=${departureCities.getCities(i)}">recherche au d&eacute;part de ${departureCities.getCities(i)}</a><br />
      </#list>
    </@tag_cms_lookup>

  </body>
</html>