<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>

  </head>

  <body>
    <a href="/voyage/index.html">index</a><br />
    <a href="/voyage/search-results.html">retour moteur</a><br />

    <@tag_cms_lookup name="lmnDepCities"; lmnDepCities>
      <#list 0..lmnDepCities.getCitiesCount()-1 as i>
        <a href="/voyage/${lmnDepCities.getCities(i)}/lmn-search-results.html">offres derni&egrave;re minute au d&eacute;part de <@tag_cms_writeMessage key="${lmnDepCities.getCities(i)}" fileName="cityLabels.properties" /></a><br />
      </#list>
    </@tag_cms_lookup>
  </body>
</html>