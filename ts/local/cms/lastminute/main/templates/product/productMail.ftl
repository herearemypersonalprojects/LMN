<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
<style type="text/css">

body {background-color:#fff; font:normal 13px/normal Arial, Helvetica, sans-serif; color:#000; margin:0; padding:0;}
h1 {margin:0 0 5px;}
h2 {font-size:17px; margin:0;}
h3 { font-size:13px; color:#ec008c; margin:10px 0 5px; }

p {font-size:13px; margin:0 0 10px;}
a {color:#ec008c;}
table.wrap0 {width:768px; margin:10px auto; border-collapse:separate;} /* cellspacing property into html code  */
table.wrap0 td.wrap2 { background:#f2f2f2; padding:20px; }

</style>
</head>
<body>
  <table class="wrap0">
    <tr>
      <td>
        <h1>
          <#if brandName == 'lastminute' || brandName == 'SELECTOUR' || brandName == 'CGOS'>
            <img moz-do-not-send="true" alt="Lastminute" src="http://cdn.lastminute.com/site/logo_droit_rose.gif?skin=frfr.lastminute.com"/>
          <#elseif brandName == 'CITROEN'>
            <img moz-do-not-send="true" alt="CITROEN" src="http://www.multicity.citroen.fr/fileadmin/templates/images/multicity-citroen.jpg"/>
          <#elseif brandName == 'CDISCOUNT'>
            <img moz-do-not-send="true" alt="CDISCOUNT" src="http://i4.cdscdn.com/struct/i1/common/logo_cdiscount_voyages.png"/>
          </#if>
        </h1>
      </td>
    </tr>
    <tr>
      <td class="wrap2">
        <p>
        Bonjour,
        ${fromName} a pens&eacute; que vous seriez intéress&eacute;(e) par le produit dont vous trouverez les d&eacute;tails dans la fiche ci-jointe.
        </p>

        <#if emailText??>
        <h3>Voici son message :</h3>
        ${emailText}
        </#if>
      </td>
    </tr>
  </table>
</body>
</html>
